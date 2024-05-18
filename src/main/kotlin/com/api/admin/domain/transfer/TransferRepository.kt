package com.api.admin.domain.transfer

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TransferRepository : ReactiveCrudRepository<Transfer,Long> {
}