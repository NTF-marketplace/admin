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
) {

     private val adminAddress = "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65"
     private val transferEventSignature = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"



    fun deposit(wallet: String, chainType: ChainType, transactionHash: String): Mono<Void> {
        return saveTransfer(wallet, chainType, transactionHash)
            .doOnNext { transfer ->
                eventPublisher.publishEvent(AdminTransferCreatedEvent(this, transfer.toResponse()))
            }
            .then()
    }
    fun saveTransfer(wallet: String, chainType: ChainType, transactionHash: String): Flux<Transfer> {
        return infuraApiService.getNftTransfer(chainType, transactionHash)
            .flatMapMany { response ->
                Flux.fromIterable(response.result.logs)
                    .flatMap { it.toEntity(wallet, AccountType.DEPOSIT) }
            }
            .flatMap { transfer ->
                checkTransferExistenceAndSave(transfer)
            }
    }

    private fun checkTransferExistenceAndSave(transfer: Transfer): Mono<Transfer> {
        return transferRepository.findByWalletAndAccountTypeAndNftId(transfer.wallet, transfer.accountType, transfer.nftId!!)
            .flatMap { existingTransfer ->
                if (existingTransfer != null) {
                    Mono.empty()
                } else {
                    transferRepository.existsByWalletAndAccountTypeAndTransactionHashAndTimestampAfter(
                        transfer.wallet, AccountType.WITHDRAW.toString(), transfer.transactionHash, transfer.timestamp
                    ).flatMap { withdrawalExists ->
                        if (withdrawalExists) {
                            Mono.empty()
                        } else {
                            transferRepository.save(transfer)
                        }
                    }
                }
            }
    }

    private fun Transfer.toResponse() = AdminTransferResponse(
        id = this.id!!,
        walletAddress = this.wallet,
        nftId = this.nftId,
        timestamp =this.timestamp,
        accountType = this.accountType,
        transferType = this.transferType,
        balance = this.balance
    )


    fun InfuraTransferDetail.toEntity(wallet: String,accountType: AccountType): Mono<Transfer> {
        return Mono.just(this)
            .filter { it.topics.isNotEmpty() && it.topics[0] == transferEventSignature }
            .filter { it.topics.size >= 3 && parseAddress(it.topics[2]) == adminAddress.lowercase() && parseAddress(it.topics[1]) == wallet.lowercase() }
            .flatMap { log ->
                val transferType = if (log.topics.size > 3) TransferType.ERC721 else TransferType.ERC20
                when (transferType) {
                    TransferType.ERC721 -> {
                        val tokenId = BigInteger(log.topics[3].removePrefix("0x"), 16).toString()
                        nftRepository.findByTokenAddressAndTokenId(log.address, tokenId) // 없으면 nft서버가서 저장해와 ㅋ
                            .map { nft ->
                                Transfer(
                                    id = null,
                                    nftId = nft.id,
                                    wallet = wallet,
                                    timestamp =  Instant.now().toEpochMilli(),
                                    accountType = accountType.toString(),
                                    balance = null,
                                    transferType = transferType.toString(),
                                    transactionHash = log.transactionHash
                                )
                            }
                    }
                    else -> {
                        val balance = toBigDecimal(log.data)
                        Mono.just(
                            Transfer(
                                id = null,
                                nftId = null,
                                wallet = wallet,
                                timestamp =  Instant.now().toEpochMilli(),
                                accountType = accountType.toString(),
                                balance = balance,
                                transferType = transferType.toString(),
                                transactionHash = log.transactionHash
                            )
                        )
                    }
                }
            }
    }

    private fun parseAddress(address: String): String {
        return "0x" + address.substring(26).padStart(40, '0')
    }

    private fun toBigDecimal(balance: String): BigDecimal =
        BigInteger(balance.removePrefix("0x"), 16).toBigDecimal().divide(BigDecimal("1000000000000000000"))

}