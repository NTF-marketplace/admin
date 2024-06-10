package com.api.admin.service.dto

import java.math.BigDecimal

data class BinanceTickerPriceResponse(
    val symbol : String,
    val price: BigDecimal,
)

