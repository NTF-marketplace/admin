package com.api.admin.service.dto

import com.api.admin.enums.ChainType

data class NftRequest(
    val tokenAddress: String,
    val tokenId: String,
    val chainType: ChainType
)
