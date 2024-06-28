package com.api.admin.service

import com.api.admin.enums.ChainType
import org.springframework.stereotype.Service
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.math.BigInteger

@Service
class Web3jService(
    private val infuraApiService: InfuraApiService,
) {

    private val apiKey = "98b672d2ce9a4089a3a5cb5081dde2fa"
    private val privateKey = "e9769d3c00032a83d703e03630edbfc3cb634b40b92e38ab2890d5e37f21bb15"
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

    fun createTransaction(privateKey:String,recipientAddress: String, amount: BigInteger,chainType: ChainType): String {
        val web3j = Web3j.build(HttpService("https://polygon-mainnet.infura.io/v3/$apiKey"))
        val credentials = Credentials.create(privateKey)
        return createERC20TransactionData(web3j, credentials, recipientAddress, amount,chainType)

    }

    fun createERC20TransactionData(web3j: Web3j,
                                   credentials: Credentials,
                                   recipientAddress: String,
                                   amountInWei: BigInteger,
                                   chainType: ChainType
    ): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(15000)
        val chainId = getChainId(chainType)
        val rawTransaction = RawTransaction.createEtherTransaction(
            nonce, gasPrice, gasLimit, recipientAddress, amountInWei
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
        return Numeric.toHexString(signedMessage)
    }

    fun createERC721TransactionData(web3j: Web3j,
                                    credentials: Credentials,
                                    contractAddress: String,
                                    fromAddress: String,
                                    toAddress: String,
                                    tokenId: BigInteger,
                                    chainType: ChainType,
    ): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(15000)
        val chainId = getChainId(chainType)

        val function = Function(
            "safeTransferFrom",
            listOf(
                Address(fromAddress),
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
        return Numeric.toHexString(signedMessage)
    }

    fun createERC1155TransactionData(
        web3j: Web3j,
        credentials: Credentials,
        contractAddress: String,
        fromAddress: String,
        toAddress: String,
        tokenId: BigInteger,
        amount: BigInteger,
        chainType: ChainType
    ): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(200000)
        val chainId = getChainId(chainType)

        val function = Function(
            "safeTransferFrom",
            listOf(
                Address(credentials.address),
                Address(toAddress),
                Uint256(tokenId),
                Uint256(amount),
                Utf8String("") // empty data
            ),
            emptyList()
        )

        val encodedFunction = FunctionEncoder.encode(function)

        val rawTransaction = RawTransaction.createTransaction(
            nonce, gasPrice, gasLimit, contractAddress, encodedFunction
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
        return Numeric.toHexString(signedMessage)
    }
}