package com.api.admin.service

import com.api.admin.domain.nft.Nft
import com.api.admin.service.dto.NftResponse
import com.api.admin.service.dto.NftResponse.Companion.toEntity
import com.api.admin.domain.nft.NftRepository
import com.api.admin.enums.ChainType
import com.api.admin.service.dto.NftRequest
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class NftService(
    private val nftRepository: NftRepository,
    private val nftApiService: NftApiService,
) {

    fun save(response: NftResponse): Mono<Void> {
        return nftRepository.findById(response.id)
            .hasElement()
            .flatMap { exists ->
                if (!exists) {
                    nftRepository.insert(response.toEntity())
                        .onErrorResume(DuplicateKeyException::class.java) {
                            Mono.empty()
                        }
                        .then()
                } else {
                    Mono.empty()
                }
            }
    }

    fun findByNft(address: String, tokenId: String, chainType: ChainType): Mono<Nft> {
        return nftRepository.findByTokenAddressAndTokenIdAndChainType(address, tokenId, chainType)
            .switchIfEmpty(
                nftApiService.getNftSave(
                    NftRequest(
                        address,
                        tokenId,
                        chainType
                    )
                ).flatMap {
                    nftRepository.insert(it.toEntity())
                        .onErrorResume(DuplicateKeyException::class.java) {
                            nftRepository.findByTokenAddressAndTokenIdAndChainType(address, tokenId, chainType)
                        }
                }
            )
    }

}