package com.api.admin.domain.nft

import reactor.core.publisher.Mono

interface NftRepositorySupport {
    fun insert(nft: Nft): Mono<Nft>
}