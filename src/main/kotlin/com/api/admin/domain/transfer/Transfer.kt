package com.api.admin.domain.transfer

import com.api.admin.enums.AccountType
import com.api.admin.enums.TransferType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("transfer")
data class Transfer(
    @Id val id: Long?,
    val nftId: Long?,
    val wallet: String,
    val timestamp: Long,
    val accountType: AccountType,
    val balance: BigDecimal?,
    val transferType: TransferType,
    val transactionHash: String,
) {

}