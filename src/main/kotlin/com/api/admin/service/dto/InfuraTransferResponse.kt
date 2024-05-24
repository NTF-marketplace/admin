package com.api.admin.service.dto

import java.math.BigDecimal
import java.math.BigInteger

data class InfuraTransferResponse(
    val jsonrpc: String,
    val id: String,
    val result: InfuraTransferResult,
)

data class InfuraTransferResult(
    val blockHash : String,
    val blockNumber: String,
    val contractAddress: String?,
    val cumulativeGasUsed: String?,
    val effectiveGasPrice: String?,
    val from: String,
    val gasUsed: String?,
    val logs:List<InfuraTransferDetail>,
    val logsBloom: String,
    val status: String,
    val to: String,
    val transactionHash: String,
    val transactionIndex: String,
    val type: String,

)

data class InfuraTransferDetail(
    val address: String,
    val blockHash: String,
    val blockNumber: String,
    val data: String,
    val logIndex: String,
    val removed: Boolean,
    val topics: List<String>,
    val transactionHash: String,
    val transactionIndex: String
)