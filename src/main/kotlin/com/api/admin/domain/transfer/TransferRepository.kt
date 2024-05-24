package com.api.admin.domain.transfer

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface TransferRepository : ReactiveCrudRepository<Transfer,Long> {
    fun findByWalletAndAccountTypeAndNftId(wallet:String, accountType:String,nftId:Long) : Mono<Transfer>
    fun existsByWalletAndAccountTypeAndTransactionHashAndTimestampAfter(wallet: String,accountType: String,transactionHash: String,timestamp:Long) : Mono<Boolean>
}