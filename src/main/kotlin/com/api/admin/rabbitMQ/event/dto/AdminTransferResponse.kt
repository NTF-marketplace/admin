package com.api.admin.rabbitMQ.event.dto

import com.api.admin.domain.transfer.Transfer
import com.api.admin.domain.transferFailLog.TransferFailLog
import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransactionStatusType
import com.api.admin.enums.TransferType
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse.Companion.toResponse
import java.math.BigDecimal


data class AdminTransferResponse(
    val accountLogId: Long,
    val accountType: AccountType,
    val transferType: TransferType,
    val transactionStatusType: TransactionStatusType,
    val adminTransferDetailResponse: AdminTransferDetailResponse?
) {
    companion object {
        fun Transfer.toResponse(accountId: Long) = AdminTransferResponse(
            accountLogId = accountId,
            accountType = this.accountType,
            transferType = this.transferType,
            transactionStatusType = TransactionStatusType.SUCCESS,
            adminTransferDetailResponse =  AdminTransferDetailResponse(
                nftId = this.nftId,
                transferType =this.transferType,
                balance = this.balance
            )
        )

        fun TransferFailLog.toResponse(accountId: Long,accountType: AccountType) = AdminTransferResponse(
            accountLogId = accountId,
            accountType = accountType,
            transferType = this.transferType,
            transactionStatusType = TransactionStatusType.FAILURE,
            adminTransferDetailResponse = null

        )

    }
}
