package com.api.admin.domain.nft

import com.api.admin.enums.ChainType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface NftRepository : ReactiveCrudRepository<Nft,Long>, NftRepositorySupport{

    fun findByTokenAddressAndTokenId(address:String,tokenId:String): Mono<Nft>
    fun findByTokenAddressAndTokenIdAndChainType(address:String,tokenId:String,chainType: ChainType): Mono<Nft>
}