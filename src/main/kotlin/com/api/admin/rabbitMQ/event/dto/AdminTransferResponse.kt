package com.api.admin.rabbitMQ.event.dto


data class AdminTransferResponse(
    val id: Long,
    val walletAddress: String,
    val nftId: Long?,
    val timestamp: Long,
    val accountType: String
)
