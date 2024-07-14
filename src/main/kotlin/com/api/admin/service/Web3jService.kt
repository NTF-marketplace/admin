package com.api.admin.service

import com.api.admin.domain.nft.NftRepository
import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Numeric
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration

@Service
class Web3jService(
    private val infuraApiService: InfuraApiService,
    private val transferService: TransferService,
    private val nftRepository: NftRepository,
) {

    private val privateKey = "4ec9e64419547100af4f38d7ec57ba1de2d5c36a7dfb03f1a349b2c5b62ac0a9"

    private fun getChainId(chain: ChainType): Long {
        val chain = when (chain) {
            ChainType.ETHEREUM_MAINNET -> 1L
            ChainType.POLYGON_MAINNET -> 137L
            ChainType.LINEA_MAINNET -> 59144L
            ChainType.LINEA_SEPOLIA -> 59140L
            ChainType.ETHEREUM_HOLESKY -> 1L
            ChainType.ETHEREUM_SEPOLIA -> 11155111L
            ChainType.POLYGON_AMOY -> 80002L
        }
        return chain
    }


    fun createTransactionERC721(
        toAddress: String,
        nftId: Long,
    ): Mono<Void> {
        return nftRepository.findById(nftId).flatMap {
            val credentials = Credentials.create(privateKey)
             createERC721TransactionData(credentials, it.tokenAddress, toAddress, BigInteger(it.tokenId), it.chainType)
                 .flatMap { data ->
                     transferService.getTransferData(wallet = toAddress, chainType = it.chainType, transactionHash = data, accountType = AccountType.WITHDRAW)
                 }
        }.then()
    }

    fun createERC721TransactionData(
        credentials: Credentials,
        contractAddress: String,
        toAddress: String,
        tokenId: BigInteger,
        chainType: ChainType
    ): Mono<String> {
        return infuraApiService.getTransactionCount(chainType, credentials.address)
            .zipWith(infuraApiService.getGasPrice(chainType))
            .flatMap { tuple ->
                val nonce = BigInteger(tuple.t1.removePrefix("0x"), 16)
                val gasPrice = BigInteger(tuple.t2.removePrefix("0x"), 16)
                val gasLimit = BigInteger.valueOf(100000)
                val chainId = getChainId(chainType)

                val function = Function(
                    "safeTransferFrom",
                    listOf(
                        Address(credentials.address),
                        Address(toAddress),
                        Uint256(tokenId)
                    ),
                    emptyList()
                )

                val encodedFunction = FunctionEncoder.encode(function)

                val rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, gasLimit, contractAddress, encodedFunction
                )

                val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
                val signedTransactionData = Numeric.toHexString(signedMessage)

                infuraApiService.getSend(chainType, signedTransactionData)
            }
    }

//    fun createTransactionERC20(
//        recipientAddress: String,
//        amount: BigInteger,
//        chainType: ChainType
//    ): Mono<Void> {
//        val credentials = Credentials.create(privateKey)
//        return createERC20TransactionData(credentials, recipientAddress, amountToWei(BigDecimal(amount)), chainType)
//            .flatMap {
//                println("transactionLog :" + it)
//                transferService.getTransferData(recipientAddress,chainType,it,AccountType.WITHDRAW)
//            }
//            .then()
//    }

    fun createTransactionERC20(
        recipientAddress: String,
        amount: BigDecimal,
        chainType: ChainType
    ): Mono<Void> {
        val credentials = Credentials.create(privateKey)
        val weiAmount = amountToWei(amount)
        return createERC20TransactionData(credentials, recipientAddress, weiAmount, chainType)
            .flatMap { transactionHash ->
                println("transactionLog: $transactionHash")
                waitForTransactionReceipt(transactionHash, chainType)
                    .flatMap {
                        transferService.getTransferData(recipientAddress, chainType, transactionHash, AccountType.WITHDRAW)
                    }
            }
            .doOnError { e ->
                println("Error in createTransactionERC20: ${e.message}")
                e.printStackTrace()
            }
            .then()
    }


    fun createERC20TransactionData(
        credentials: Credentials,
        recipientAddress: String,
        amount: BigInteger,
        chainType: ChainType
    ): Mono<String> {
        return infuraApiService.getTransactionCount(chainType, credentials.address)
            .zipWith(infuraApiService.getGasPrice(chainType))
            .flatMap { tuple ->
                val nonce = BigInteger(tuple.t1.removePrefix("0x"), 16)
                val gasPrice = BigInteger(tuple.t2.removePrefix("0x"), 16)
                val gasLimit = BigInteger.valueOf(100000)
                val chainId = getChainId(chainType)

                val rawTransaction = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, gasLimit, recipientAddress, amount
                )

                val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
                val signedTransactionData = Numeric.toHexString(signedMessage)

                infuraApiService.getSend(chainType, signedTransactionData)
            }
    }

    fun amountToWei(amount: BigDecimal): BigInteger {
        val weiPerMatic = BigDecimal("1000000000000000000")
        return amount.multiply(weiPerMatic).toBigInteger()
    }



    fun waitForTransactionReceipt(transactionHash: String, chainType: ChainType, maxAttempts: Int = 5, attempt: Int = 1): Mono<String> {


        val objectMapper = ObjectMapper()
        println("Attempt $attempt for transaction $transactionHash")
        return infuraApiService.getTransactionReceipt(chainType, transactionHash)
            .flatMap { response ->
                println("Transaction receipt response: $response")
                val jsonNode: JsonNode = objectMapper.readTree(response)
                val resultNode = jsonNode.get("result")
                println("result Logic : $resultNode")
                if (resultNode != null && resultNode.has("status")) {
                    val status = resultNode.get("status").asText()
                    println("Transaction status: $status")
                    if (status == "0x1") {
                        println("Transaction $transactionHash succeeded")
                        Mono.just(transactionHash)
                    } else if (status == "0x0") {
                        println("Transaction $transactionHash failed")
                        Mono.error(IllegalStateException("Transaction failed"))
                    } else {
                        println("Transaction $transactionHash is pending")
                        if (attempt >= maxAttempts) {
                            Mono.error(IllegalStateException("Transaction was not successful after $maxAttempts attempts"))
                        } else {
                            Mono.delay(Duration.ofSeconds(5))
                                .flatMap { waitForTransactionReceipt(transactionHash, chainType, maxAttempts, attempt + 1) }
                        }
                    }
                } else {
                    println("Transaction receipt for $transactionHash not found on attempt $attempt")
                    if (attempt >= maxAttempts) {
                        Mono.error(IllegalStateException("Transaction receipt not found after $maxAttempts attempts"))
                    } else {
                        Mono.delay(Duration.ofSeconds(5))
                            .flatMap { waitForTransactionReceipt(transactionHash, chainType, maxAttempts, attempt + 1) }
                    }
                }
            }
            .doOnError { e ->
                println("Error while checking transaction receipt for $transactionHash: ${e.message}")
            }
    }


}