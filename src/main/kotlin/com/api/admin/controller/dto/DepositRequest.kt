package com.api.admin.controller.dto

import com.api.admin.enums.ChainType

data class DepositRequest(
    val chainType: ChainType,
    val transactionHash: String,
)

