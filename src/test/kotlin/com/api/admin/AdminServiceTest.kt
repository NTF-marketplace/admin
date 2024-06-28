package com.api.admin

import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransferType
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse
import com.api.admin.rabbitMQ.sender.RabbitMQSender
import com.api.admin.service.InfuraApiService
import com.api.admin.service.TransferService
import com.api.admin.service.Web3jService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import java.time.Instant

@SpringBootTest
class AdminServiceTest(
    @Autowired private val transferService: TransferService,
    @Autowired private val rabbitMQSender: RabbitMQSender,
    @Autowired private val infuraApiService: InfuraApiService,
    @Autowired private val web3jService: Web3jService,
) {

//    @Test
//    fun test() {
//        val res = transferService.getNftOwner(ChainType.POLYGON_MAINNET,"0xa3784fe9104fdc0b988769fba7459ece2fb36eea","0")
//        println(res)
//    }

    @Test
    fun getNftTransferDetail() {

        val res = infuraApiService.getNftTransfer(ChainType.POLYGON_MAINNET,"0x55fa4495f983e9f162b39b3df4dec8ebcff9aa05daee7b051c680ccfb49422a6").block()
        println(res.toString())
    }

    @Test
    fun saveTransfer() {
        transferService.saveTransfer("0x01b72b4aa3f66f213d62d53e829bc172a6a72867",ChainType.POLYGON_MAINNET,"0x55fa4495f983e9f162b39b3df4dec8ebcff9aa05daee7b051c680ccfb49422a6",AccountType.DEPOSIT).next().block()
    }

    @Test
    fun test1() {
        val address = "0x0000000000000000000000009bdef468ae33b09b12a057b4c9211240d63bae65"
        val result = parseAddress(address)
        println(result)
        println(result == "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65")

    }

    @Test
    fun deposit() {
        transferService.getTransferData("0x01b72b4aa3f66f213d62d53e829bc172a6a72867",ChainType.POLYGON_MAINNET,"0x55fa4495f983e9f162b39b3df4dec8ebcff9aa05daee7b051c680ccfb49422a6",AccountType.DEPOSIT)
            .block()

        Thread.sleep(100000)
    }

    private fun parseAddress(address: String): String {
        return "0x" + address.substring(26).padStart(40, '0')
    }

    @Test
    fun sendMessage() {
        val response = AdminTransferResponse(
            id= 1L,
            walletAddress = "0x01b72b4aa3f66f213d62d53e829bc172a6a72867",
            nftId = 1L,
            timestamp =  Instant.now().toEpochMilli(),
            accountType = AccountType.DEPOSIT,
            transferType = TransferType.ERC721,
            balance = null,
            chainType = ChainType.POLYGON_MAINNET
        )
        rabbitMQSender.transferSend(response)
        Thread.sleep(10000)
    }

    @Test
    fun sendMatic() {
//        web3jService.createTransaction(
//            "e9769d3c00032a83d703e03630edbfc3cb634b40b92e38ab2890d5e37f21bb15",
//            "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65",
//            BigInteger("1000000000000000000")
//            )

        val transactionData = web3jService.createTransaction("e9769d3c00032a83d703e03630edbfc3cb634b40b92e38ab2890d5e37f21bb15",
            "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65",
            BigInteger("1000000000000000000"),
            ChainType.POLYGON_MAINNET
            )
        val response = infuraApiService.getSend(ChainType.POLYGON_MAINNET,transactionData).block()
        println(response)
    }

    @Test
    fun infuraTest() {
        val res =infuraApiService.getTransactionCount(ChainType.POLYGON_MAINNET,"0x01b72b4aa3f66f213d62d53e829bc172a6a72867").block()
        println(res.toString())
    }


    @Test
    fun erc1155Test() {
        // val apiKey = "98b672d2ce9a4089a3a5cb5081dde2fa"
        // val privateKey = "e9769d3c00032a83d703e03630edbfc3cb634b40b92e38ab2890d5e37f21bb15"
        // val web3j = Web3j.build(HttpService("https://polygon-mainnet.infura.io/v3/$apiKey"))
        // val credentials = Credentials.create(privateKey)
        //
        // val contractAddress = "0xe7900239E9332060dC975ED6F0cc5F0129D924cf"
        // val fromAddress = "0x01b72b4aa3f66f213d62d53e829bc172a6a72867"
        // val toAddress = "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65"
        // val tokenId = BigInteger("5")
        // val amount =BigInteger("1")
        // val chainType = ChainType.POLYGON_MAINNET
        // val res = web3jService.createERC1155TransactionData(
        //     web3j,
        //     credentials,
        //     contractAddress,
        //     fromAddress,
        //     toAddress,
        //     tokenId,
        //     amount,
        //     chainType
        //     )
        //
        // println("transaction DAta : " + res)
        val res = "0xf9012c1d8506fc23ac2983030d4094e7900239e9332060dc975ed6f0cc5f0129d924cf80b8c4de6c65ff00000000000000000000000001b72b4aa3f66f213d62d53e829bc172a6a728670000000000000000000000009bdef468ae33b09b12a057b4c9211240d63bae650000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000000820136a0c09f6b44990413c3a81950b014780bd9b2c480f65319922959aeb38df13f2105a014b6764cc0cf8baa3ffb7d82930a1887aa2b014cb4d77bf846e04084583ce138"
        val response = infuraApiService.getSend(ChainType.POLYGON_MAINNET,res).block()
        println(response)
    }


}