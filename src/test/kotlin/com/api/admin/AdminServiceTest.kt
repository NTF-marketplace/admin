package com.api.admin

import com.api.admin.enums.ChainType
import com.api.admin.service.TransferService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AdminServiceTest(
    @Autowired private val transferService: TransferService,
) {

    @Test
    fun test() {
        val res = transferService.getNftOwner(ChainType.POLYGON_MAINNET,"0xa3784fe9104fdc0b988769fba7459ece2fb36eea","0")
        println(res)
    }
}