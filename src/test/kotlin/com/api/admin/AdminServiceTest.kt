package com.api.admin

import com.api.admin.enums.ChainType
import com.api.admin.rabbitMQ.sender.RabbitMQSender
import com.api.admin.service.InfuraApiService
import com.api.admin.service.TransferService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AdminServiceTest(
    @Autowired private val transferService: TransferService,
    @Autowired private val rabbitMQSender: RabbitMQSender,
    @Autowired private val infuraApiService: InfuraApiService,
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
        transferService.saveTransfer("0x01b72b4aa3f66f213d62d53e829bc172a6a72867",ChainType.POLYGON_MAINNET,"0x55fa4495f983e9f162b39b3df4dec8ebcff9aa05daee7b051c680ccfb49422a6").block()
    }

    @Test
    fun test1() {
        val address = "0x0000000000000000000000009bdef468ae33b09b12a057b4c9211240d63bae65"
        val result = parseAddress(address)
        println(result)
        println(result == "0x9bDeF468ae33b09b12a057B4c9211240D63BaE65")

    }

    private fun parseAddress(address: String): String {
        return "0x" + address.substring(26).padStart(40, '0')
    }


}