package com.api.admin.domain.transfer

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("transfer")
data class Transfer(
    @Id val id: Long?,
    val nftId: Long?,
    val wallet: String,
    val timestamp: Long,
    val accountType: String,
    val balance: BigDecimal?,
    val transferType: String,
    val transactionHash: String,
) {

}