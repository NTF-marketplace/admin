package com.api.admin.service

import com.api.admin.NftResponse
import com.api.admin.NftResponse.Companion.toEntity
import com.api.admin.domain.nft.NftRepository
import org.springframework.stereotype.Service

@Service
class NftService(
    private val nftRepository: NftRepository,
) {

    fun save(response: NftResponse) {
        nftRepository.findById(response.id).switchIfEmpty(
            nftRepository.save(response.toEntity())
        )
    }
}