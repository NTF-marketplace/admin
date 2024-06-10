package com.api.admin.util

import com.api.admin.enums.ChainType
import com.api.admin.enums.TokenType

object Utils {
    fun ChainType.toTokenType(): TokenType {
        return when (this) {
            ChainType.ETHEREUM_MAINNET -> TokenType.ETH
            ChainType.POLYGON_MAINNET -> TokenType.MATIC
            ChainType.POLYGON_AMOY -> TokenType.MATIC
            ChainType.ETHEREUM_SEPOLIA -> TokenType.ETH
            ChainType.ETHEREUM_HOLESKY -> TokenType.ETH
            else -> throw IllegalArgumentException("Unknown ChainType: $this")
        }
    }
}