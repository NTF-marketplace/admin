package com.api.admin.rabbitMQ.event.dto

import com.api.admin.enums.TransferType
import java.math.BigDecimal

data class AdminTransferDetailResponse(
    val nftId: Long?,
    val transferType: TransferType,
    val balance: BigDecimal?,
)
