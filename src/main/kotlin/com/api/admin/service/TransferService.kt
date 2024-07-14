package com.api.admin.service

import com.api.admin.domain.nft.NftRepository
import com.api.admin.domain.transfer.Transfer
import com.api.admin.domain.transfer.TransferRepository
import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransferType
import com.api.admin.rabbitMQ.event.dto.AdminTransferCreatedEvent
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse
import com.api.admin.service.dto.InfuraTransferDetail
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

@Service
class TransferService(
    private val nftRepository: NftRepository,
    private val transferRepository: TransferRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val infuraApiService: InfuraApiService,
    private val nftService: NftService,
) {

    private val adminAddress = "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65"
    private val transferEventSignature = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
    private val nativeTransferEventSignature = "0xe6497e3ee548a3372136af2fcb0696db31fc6cf20260707645068bd3fe97f3c4"


    // fun accountTransfer(
    //     wallet: String,
    //     chainType: ChainType,
    //     transactionHash: String,
    //     accountType: AccountType,
    //     transferType: TransferType,
    // ) {
    //     return when(accountType) {
    //         AccountType.DEPOSIT -> getTransferData(wallet,chainType,transactionHash, accountType)
    //
    //     }
    // }

    fun getTransferData(
        wallet: String,
        chainType: ChainType,
        transactionHash: String,
        accountType: AccountType,
    ): Mono<Void> {
        return transferRepository.existsByTransactionHash(transactionHash)
            .flatMap {
                if (it) {
                    Mono.error(IllegalStateException("Transaction already exists"))
                } else {
                    saveTransfer(wallet, chainType, transactionHash, accountType)
                        .doOnNext { transfer ->
                            eventPublisher.publishEvent(AdminTransferCreatedEvent(this, transfer.toResponse()))
                        }
                        .then()
                }
            }
    }

    fun saveTransfer(wallet: String, chainType: ChainType, transactionHash: String, accountType: AccountType): Flux<Transfer> {
        return infuraApiService.getTransferLog(chainType, transactionHash)
            .flatMapMany { response ->
                Flux.fromIterable(response.result.logs)
                    .flatMap { it.toEntity(wallet, accountType,chainType) }
            }
            .flatMap { transfer -> transferRepository.save(transfer) }
    }

    private fun Transfer.toResponse() = AdminTransferResponse(
        id = this.id!!,
        walletAddress = this.wallet,
        nftId = this.nftId,
        timestamp = this.timestamp,
        accountType = this.accountType,
        transferType = this.transferType,
        balance = this.balance,
        chainType = this.chainType,
    )

    fun InfuraTransferDetail.toEntity(wallet: String, accountType: AccountType, chainType: ChainType): Mono<Transfer> {
        return Mono.just(this)
            .flatMap { log ->
                println("log : " + log.toString())
                when {
                    log.topics[0] == nativeTransferEventSignature ->
                        handleERC20Transfer(log, wallet, accountType, chainType,TransferType.NATIVE)
                    log.topics[0] == transferEventSignature && log.topics.size == 3 ->
                        handleERC20Transfer(log, wallet, accountType, chainType, TransferType.ERC20)
                    log.topics[0] == transferEventSignature && log.topics.size == 4 ->
                        handleERC721Transfer(log, wallet, accountType, chainType)
                    else -> Mono.empty()
                }
            }
    }

    private fun handleERC20Transfer(log: InfuraTransferDetail, wallet: String, accountType: AccountType, chainType: ChainType,transferType: TransferType): Mono<Transfer> {
        val from = parseAddress(log.topics[2])
        val to = parseAddress(log.topics[3])
        val amount = when (transferType) {
            TransferType.NATIVE -> parseNativeTransferAmount(log.data)
            TransferType.ERC20 -> toBigDecimal(log.data)
            else -> BigDecimal.ZERO
        }

        val isRelevantTransfer = when (accountType) {
            AccountType.DEPOSIT -> from.equals(wallet, ignoreCase = true) && to.equals(adminAddress, ignoreCase = true)
            AccountType.WITHDRAW -> from.equals(adminAddress, ignoreCase = true) && to.equals(wallet, ignoreCase = true)
        }

        return if (isRelevantTransfer) {
            Mono.just(
                Transfer(
                    id = null,
                    nftId = null,
                    wallet = wallet,
                    timestamp = Instant.now().toEpochMilli(),
                    accountType = accountType,
                    balance = amount,
                    transferType = TransferType.ERC20,
                    transactionHash = log.transactionHash,
                    chainType = chainType,
                )
            )
        } else {
            Mono.empty()
        }
    }



    private fun handleERC721Transfer(log: InfuraTransferDetail, wallet: String, accountType: AccountType, chainType: ChainType): Mono<Transfer> {
        val from = parseAddress(log.topics[1])
        val to = parseAddress(log.topics[2])
        val tokenId = BigInteger(log.topics[3].removePrefix("0x"), 16).toString()

        val isRelevantTransfer = when (accountType) {
            AccountType.DEPOSIT -> from.equals(wallet, ignoreCase = true) && to.equals(adminAddress, ignoreCase = true)
            AccountType.WITHDRAW -> from.equals(adminAddress, ignoreCase = true) && to.equals(wallet, ignoreCase = true)
        }

        return if (isRelevantTransfer) {
            nftService.findByNft(log.address, tokenId, chainType)
                .map { nft ->
                    Transfer(
                        id = null,
                        nftId = nft.id,
                        wallet = wallet,
                        timestamp = Instant.now().toEpochMilli(),
                        accountType = accountType,
                        balance = null,
                        transferType = TransferType.ERC721,
                        transactionHash = log.transactionHash,
                        chainType = chainType,
                    )
                }
        } else {
            Mono.empty()
        }
    }

    private fun parseAddress(address: String): String {
        return "0x" + address.substring(26).padStart(40, '0').toLowerCase()
    }

    private fun toBigDecimal(balance: String): BigDecimal =
        BigInteger(balance.removePrefix("0x"), 16).toBigDecimal().divide(BigDecimal("1000000000000000000"))

    private fun parseNativeTransferAmount(data: String): BigDecimal {
        val cleanData = data.removePrefix("0x")
        val amountHex = cleanData.substring(0, 64)
        return BigInteger(amountHex, 16).toBigDecimal().divide(BigDecimal("1000000000000000000"))
    }
}