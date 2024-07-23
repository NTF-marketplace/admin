package com.api.admin.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "admin")
data class AdminInfoProperties(
    val address: String,
    val privatekey: String,
)
