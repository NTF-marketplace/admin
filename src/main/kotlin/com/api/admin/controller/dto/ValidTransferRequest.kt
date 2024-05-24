package com.api.admin.controller.dto

import com.api.admin.enums.ChainType

data class ValidTransferRequest(
    val chainType: ChainType,
    val nftId: Long,
)
