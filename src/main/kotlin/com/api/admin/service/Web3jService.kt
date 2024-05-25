package com.api.admin.service

import org.springframework.stereotype.Service
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
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

    fun createTransaction(privateKey:String,recipientAddress: String, amount: BigInteger): String {
        val web3j = Web3j.build(HttpService("https://polygon-mainnet.infura.io/v3/$apiKey"))
        val credentials = Credentials.create(privateKey)
        return createTransactionData(web3j, credentials, recipientAddress, amount)

    }

    fun createTransactionData(web3j: Web3j, credentials: Credentials, recipientAddress: String, amountInWei: BigInteger): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(21000)
        val chainId = 137L
        val rawTransaction = RawTransaction.createEtherTransaction(
            nonce, gasPrice, gasLimit, recipientAddress, amountInWei
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
        return Numeric.toHexString(signedMessage)
    }

    fun createERC721TransactionData(web3j: Web3j, credentials: Credentials, contractAddress: String, fromAddress: String, toAddress: String, tokenId: BigInteger): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(60000)
        val chainId = 137L

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
}