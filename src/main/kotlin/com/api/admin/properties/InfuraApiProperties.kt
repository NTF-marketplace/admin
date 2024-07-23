package com.api.admin.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apikey")
data class InfuraApiProperties(
    val infura: String,
)
