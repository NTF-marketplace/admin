package com.api.admin.domain

import reactor.core.publisher.Mono

interface NftRepositorySupport {
    fun insert(nft: Nft): Mono<Nft>
}