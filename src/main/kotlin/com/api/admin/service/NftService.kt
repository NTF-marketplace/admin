package com.api.admin.service

import com.api.admin.service.dto.NftResponse
import com.api.admin.service.dto.NftResponse.Companion.toEntity
import com.api.admin.domain.nft.NftRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class NftService(
    private val nftRepository: NftRepository,
) {
    fun save(response: NftResponse): Mono<Void> {
        return nftRepository.findById(response.id)
            .flatMap {
                Mono.empty<Void>()
            }
            .switchIfEmpty(
                nftRepository.insert(response.toEntity()).then()
            )
    }


}