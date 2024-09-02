package com.api.admin.service

import com.api.admin.domain.transferFailLog.TransferFailLog
import com.api.admin.domain.transferFailLog.TransferFailLogRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransferFailLogService(
    private val transferFailLogRepository: TransferFailLogRepository,
) {

    fun save(wallet: String, transactionHash: String?, message: String): Mono<Void> {
        return transferFailLogRepository.save(
            TransferFailLog(
                wallet = wallet,
                transactionHash = transactionHash,
                errorMessage = message
            )
        ).doOnSuccess {

        }.then()
    }
}