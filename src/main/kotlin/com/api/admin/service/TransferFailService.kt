package com.api.admin.service

import com.api.admin.domain.transferFailLog.TransferFailLog
import com.api.admin.domain.transferFailLog.TransferFailLogRepository
import com.api.admin.enums.AccountType
import com.api.admin.enums.TransferType
import com.api.admin.rabbitMQ.event.dto.AdminTransferCreatedEvent
import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse.Companion.toResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransferFailService(
    private val transferFailLogRepository: TransferFailLogRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun save(accountId: Long,
             address: String,
             transactionHash: String?,
             message: String,
             transferType: TransferType,
             accountType: AccountType
    ): Mono<Void> {
        return transferFailLogRepository.save(
            TransferFailLog(
                wallet = address,
                transactionHash = transactionHash,
                errorMessage = message,
                transferType = transferType
            )
        ).doOnSuccess {
            eventPublisher.publishEvent(AdminTransferCreatedEvent(this, it.toResponse(accountId,accountType)))
        }.then()

    }
}