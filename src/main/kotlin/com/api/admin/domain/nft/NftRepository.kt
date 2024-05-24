package com.api.admin.domain.nft

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface NftRepository : ReactiveCrudRepository<Nft,Long>{

    fun findByTokenAddressAndTokenId(address:String,tokenId:String): Mono<Nft>
}