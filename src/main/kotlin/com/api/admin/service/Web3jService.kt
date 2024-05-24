package com.api.admin.service

import com.api.admin.enums.ChainType
import com.api.admin.wrapper.ERC20ABI
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Numeric
import java.math.BigInteger

@Service
class Web3jService(
    private val infuraApiService: InfuraApiService,
) {


    fun sendMatic() {
        val web3j = Web3j.build(HttpService("https://polygon-mainnet.infura.io/v3/98b672d2ce9a4089a3a5cb5081dde2fa"))
        val credentials = Credentials.create("e9769d3c00032a83d703e03630edbfc3cb634b40b92e38ab2890d5e37f21bb15")

        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
            .send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(21000)
        val recipientAddress = "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65"
        val amount = BigInteger("1000000000000000000")

        val rawTransaction = RawTransaction.createEtherTransaction(
            nonce, gasPrice, gasLimit, recipientAddress, amount
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)

        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send()
        val transactionHash = ethSendTransaction.transactionHash

        println("Transaction hash: $transactionHash")
    }

    fun createTransaction(privateKey:String,recipientAddress: String, amount: BigInteger): String{
        val web3j = Web3j.build(HttpService("https://polygon-mainnet.infura.io/v3/98b672d2ce9a4089a3a5cb5081dde2fa"))
        val credentials = Credentials.create(privateKey)
        return createTransactionData(web3j, credentials, recipientAddress, amount)

    }

    fun createTransactionData(web3j: Web3j, credentials: Credentials, recipientAddress: String, amountInWei: BigInteger): String {
        val nonce = web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send().transactionCount
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(21000)
        val chainId = 137L // 이더리움메인넷 1L
        val rawTransaction = RawTransaction.createEtherTransaction(
            nonce, gasPrice, gasLimit, recipientAddress, amountInWei
        )

        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
        return Numeric.toHexString(signedMessage)
    }
}