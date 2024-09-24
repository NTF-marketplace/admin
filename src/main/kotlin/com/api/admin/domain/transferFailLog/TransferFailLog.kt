package com.api.admin.domain.transferFailLog

import com.api.admin.enums.TransferType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table("transfer_fail_log")
data class TransferFailLog(
    @Id val id: Long? = null,
    val wallet: String,
    val timestamp: Long? = System.currentTimeMillis(),
    val transactionHash: String?,
    val errorMessage: String,
    val transferType: TransferType,
    )
