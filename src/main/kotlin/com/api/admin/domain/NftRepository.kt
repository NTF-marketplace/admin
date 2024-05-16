package com.api.admin.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface NftRepository : ReactiveCrudRepository<Nft,Long>{
}