package com.api.admin.service

import com.api.admin.enums.TokenType
import com.api.admin.service.dto.BinanceTickerPriceResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class BinanceApiService {

    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()


    fun getTickerPrice(tokenType: TokenType): Mono<BinanceTickerPriceResponse> {
        return webClient.get()
            .uri{
                it.path("/api/v3/ticker/price")
                it.queryParam("symbol","${tokenType}USDT")
                it.build()
            }
            .retrieve()
            .bodyToMono(BinanceTickerPriceResponse::class.java)
    }



    companion object {
        private val baseUrl = "https://api.binance.com"
    }
}