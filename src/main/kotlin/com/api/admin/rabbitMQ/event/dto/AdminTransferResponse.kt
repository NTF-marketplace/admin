package com.api.admin.rabbitMQ.event.dto

import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransferType
import java.math.BigDecimal


data class AdminTransferResponse(
    val id: Long,
    val walletAddress: String,
    val nftId: Long?,
    val timestamp: Long,
    val accountType: AccountType,
    val transferType: TransferType,
    val balance: BigDecimal?,
    val chainType: ChainType,
)
