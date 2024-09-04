package com.api.admin.domain.transferFailLog

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TransferFailLogRepository: ReactiveCrudRepository<TransferFailLog,Long> {
}