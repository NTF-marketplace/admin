package com.api.admin.service

import com.api.admin.enums.ChainType
import com.api.admin.service.dto.InfuraRequest
import com.api.admin.service.dto.InfuraTransferResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class InfuraApiService {

    private fun urlByChain(chainType: ChainType) : WebClient {
        val baseUrl = when (chainType) {
            ChainType.ETHEREUM_MAINNET -> "https://mainnet.infura.io"
            ChainType.POLYGON_MAINNET -> "https://polygon-mainnet.infura.io"
            ChainType.ETHREUM_GOERLI -> "https://goerli.infura.io"
            ChainType.ETHREUM_SEPOLIA -> "https://sepolia.infura.io"
            ChainType.POLYGON_MUMBAI -> "https://polygon-mumbai.infura.io"
        }
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }

    fun getNftTransfer(chainType: ChainType, transactionHash: String): Mono<InfuraTransferResponse> {
        val requestBody = InfuraRequest(method = "eth_getTransactionReceipt", params = listOf(transactionHash))
        val webClient = urlByChain(chainType)

        return webClient.post()
            .uri("/v3/98b672d2ce9a4089a3a5cb5081dde2fa")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(InfuraTransferResponse::class.java)

    }

    fun getSend(chainType: ChainType,signedTransactionData: String): Mono<String> {
        val requestBody = InfuraRequest(method = "eth_sendRawTransaction", params = listOf(signedTransactionData))
        val webClient = urlByChain(chainType)

        return webClient.post()
            .uri("/v3/98b672d2ce9a4089a3a5cb5081dde2fa")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)

    }
}