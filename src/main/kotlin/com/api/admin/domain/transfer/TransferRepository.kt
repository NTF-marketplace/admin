package com.api.admin.domain.transfer

import com.api.admin.enums.AccountType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface TransferRepository : ReactiveCrudRepository<Transfer,Long> {
    fun findByWalletAndAccountTypeAndNftId(wallet:String, accountType:AccountType,nftId:Long) : Mono<Transfer>
    fun existsByWalletAndAccountTypeAndTransactionHashAndTimestampAfter(wallet: String,accountType: AccountType,transactionHash: String,timestamp:Long) : Mono<Boolean>


    fun existsByTransactionHash(transactionHash: String): Mono<Boolean>
}