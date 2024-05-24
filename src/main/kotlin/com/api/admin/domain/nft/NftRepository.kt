package com.api.admin.domain.nft

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface NftRepository : ReactiveCrudRepository<Nft,Long>{
}