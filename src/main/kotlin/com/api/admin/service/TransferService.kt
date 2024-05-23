package com.api.admin.service

import com.api.admin.controller.dto.ValidTransferRequest
import com.api.admin.domain.nft.NftRepository
import com.api.admin.domain.transfer.Transfer
import com.api.admin.domain.transfer.TransferRepository
import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.rabbitMQ.event.dto.AdminTransferCreatedEvent
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse
import com.api.admin.service.dto.InfuraTransferDetail
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
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


//    fun deposit(wallet: String, chainType: ChainType,transactionHash: String): Mono<Void> {
//        return Flux.fromIterable(requests)
//            .flatMap { request ->
//                nftRepository.findById(request.nftId)
//                    .flatMap { nft ->
//                        getNftOwner(request.chainType, nft.tokenAddress, nft.tokenId)
//                            .filterWhen { address -> Mono.just(address == adminAddress) }
//                            .flatMap { saveTransfer(nft.id, wallet, AccountType.DEPOSIT) }
//                            .doOnSuccess { eventPublisher.publishEvent(AdminTransferCreatedEvent(this, it.toResponse()))  }
//                    }
//            }
//            .then()
//    }

//    private fun Transfer.toResponse( ) = AdminTransferResponse(
//        id = this.id!!,
//        walletAddress = this.wallet,
//        nftId = this.nftId ,
//        timestamp = this.timestamp,
//        accountType = this.accountType
//    )

    fun saveTransfer(wallet: String, chainType: ChainType, transactionHash: String): Mono<Void> {
        return infuraApiService.getNftTransfer(chainType, transactionHash)
            .flatMapMany { response ->
                println(response.toString())
                Flux.fromIterable(response.result.logs)
                    .flatMap { it.toEntity(wallet) }
            }
            .flatMap { transfer ->
                transferRepository.save(transfer)
            }
            .then()
    }



    fun InfuraTransferDetail.toEntity(wallet: String): Mono<Transfer> {
        return Mono.just(this)
            .filter { it.topics.isNotEmpty() && it.topics[0] == transferEventSignature }
            .filter { it.topics.size >= 3 && parseAddress(it.topics[2]) == adminAddress.lowercase() && parseAddress(it.topics[1]) == wallet.lowercase() }
            .flatMap { log ->
                println("log: " + log.toString())
                val transferType = if (log.topics.size > 3) "ERC721" else "ERC20"

                when (transferType) {
                    "ERC721" -> {
                        val tokenId = BigInteger(log.topics[3].removePrefix("0x"), 16).toString()
                        nftRepository.findByTokenAddressAndTokenId(log.address, tokenId) // 없으면 nft서버가서 저장해와 ㅋ
                            .mapNotNull { nft ->
                                Transfer(
                                    id = null,
                                    nftId = nft.id,
                                    wallet = wallet,
                                    timestamp = System.currentTimeMillis(),
                                    accountType = "DEPOSIT",
                                    balance = null,
                                    transferType = transferType,
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
                                timestamp = System.currentTimeMillis(),
                                accountType = "DEPOSIT",
                                balance = balance,
                                transferType = transferType,
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