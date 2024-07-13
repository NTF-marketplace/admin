package com.api.admin.service

import com.api.admin.enums.ChainType
import org.springframework.stereotype.Service
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

@Service
class Web3jService(
    private val infuraApiService: InfuraApiService,
) {

    private val apiKey = "98b672d2ce9a4089a3a5cb5081dde2fa"
    private val privateKey = "4ec9e64419547100af4f38d7ec57ba1de2d5c36a7dfb03f1a349b2c5b62ac0a9"
    private val adminAddress = "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65"

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
        contractAddress: String,
        toAddress: String,
        tokenId: BigInteger,
        chainType: ChainType
    ): Mono<String> {
        val credentials = Credentials.create(privateKey)
        return createERC721TransactionData(credentials, contractAddress, toAddress, tokenId, chainType)
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

    fun createTransactionERC20(
        recipientAddress: String,
        amount: BigInteger,
        chainType: ChainType
    ): Mono<String> {
        val credentials = Credentials.create(privateKey)
        return createERC20TransactionData(credentials, recipientAddress, amount, chainType)
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
                val gasLimit = BigInteger.valueOf(30000)
                val chainId = getChainId(chainType)

                val rawTransaction = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, gasLimit, recipientAddress, amount
                )

                val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
                val signedTransactionData = Numeric.toHexString(signedMessage)

                infuraApiService.getSend(chainType, signedTransactionData)
            }
    }

}