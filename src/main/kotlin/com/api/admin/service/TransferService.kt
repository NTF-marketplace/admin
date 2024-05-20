package com.api.admin.service

import com.api.admin.controller.dto.ValidTransferRequest
import com.api.admin.domain.nft.NftRepository
import com.api.admin.domain.transfer.Transfer
import com.api.admin.domain.transfer.TransferRepository
import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.rabbitMQ.event.dto.AdminTransferCreatedEvent
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse
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
import java.math.BigInteger
import java.time.Instant

@Service
class TransferService(
    private val nftRepository: NftRepository,
    private val transferRepository: TransferRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

     private val adminAddress = "0x01b72b4aa3f66f213d62d53e829bc172a6a72867"


    fun validTransfer(wallet: String, requests: List<ValidTransferRequest>): Mono<Void> {
        return Flux.fromIterable(requests)
            .flatMap { request ->
                nftRepository.findById(request.nftId)
                    .flatMap { nft ->
                        getNftOwner(request.chainType, nft.tokenAddress, nft.tokenId)
                            .filterWhen { address -> Mono.just(address == adminAddress) }
                            .flatMap { saveTransfer(nft.id, wallet, AccountType.DEPOSIT) }
                            .doOnSuccess { eventPublisher.publishEvent(AdminTransferCreatedEvent(this, it.toResponse()))  }
                    }
            }
            .then()
    }

    private fun Transfer.toResponse( ) = AdminTransferResponse(
        id = this.id!!,
        walletAddress = this.wallet,
        nftId = this.nftId,
        timestamp = this.timestamp,
        accountType = this.accountType
    )

    fun saveTransfer(nftId: Long, wallet: String,accountType: AccountType): Mono<Transfer> {
        val transfer = Transfer(
            id = null,
            wallet = wallet,
            nftId = nftId,
            timestamp = Instant.now().toEpochMilli(),
            accountType = accountType.toString()
        )
        return transferRepository.save(transfer)
    }

    //TODO("apiKey 캡슐화")
    fun getNftOwner(chainType: ChainType, contractAddress: String, tokenId: String): Mono<String?> {
        val web3 = Web3j.build(HttpService(chainType.baseUrl() + "/v3/98b672d2ce9a4089a3a5cb5081dde2fa"))
        val function = Function(
            "ownerOf",
            listOf(Uint256(BigInteger(tokenId))),
            listOf(object : TypeReference<Address>() {})
        )

        val encodedFunction = FunctionEncoder.encode(function)
        val transaction = Transaction.createEthCallTransaction(null, contractAddress, encodedFunction)

        return Mono.fromCallable {
            val ethCall = web3.ethCall(transaction, DefaultBlockParameterName.LATEST).send()
            val decode = FunctionReturnDecoder.decode(ethCall.value, function.outputParameters)
            if (decode.isEmpty()) null else decode[0].value as String
        }.retry(3)
    }

    fun ChainType.baseUrl(): String {
        return when(this){
            ChainType.ETHEREUM_MAINNET -> "https://mainnet.infura.io"
            ChainType.POLYGON_MAINNET -> "https://polygon-mainnet.infura.io"
            ChainType.ETHREUM_GOERLI -> "https://goerli.infura.io"
            ChainType.ETHREUM_SEPOLIA -> "https://sepolia.infura.io"
            ChainType.POLYGON_MUMBAI -> "https://polygon-mumbai.infura.io"
        }
    }
}