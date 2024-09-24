package com.api.admin.controller.dto

import com.api.admin.enums.ChainType
import java.math.BigDecimal
import java.math.BigInteger

data class WithdrawERC20Request(
    val chainType: ChainType,
    val amount: BigDecimal,
    val accountLogId: Long,
    )
