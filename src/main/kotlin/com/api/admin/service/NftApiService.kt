package com.api.admin.service

import com.api.admin.properties.NftApiProperties
import com.api.admin.service.dto.NftRequest
import com.api.admin.service.dto.NftResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NftApiService(
     nftApiProperties: NftApiProperties,
) {


    private val webClient = WebClient.builder()
        .baseUrl(nftApiProperties.uri)
        .build()

    fun getNftSave(request: NftRequest): Mono<NftResponse> {
        return webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(NftResponse::class.java)
    }

}