package com.api.admin.util

import com.api.admin.enums.ChainType

object Util {
    fun getChainId(chain: ChainType): Long {
        val chain = when (chain) {
            ChainType.ETHEREUM_MAINNET -> 1L
            ChainType.POLYGON_MAINNET -> 137L
            ChainType.LINEA_MAINNET -> 59144L
            ChainType.LINEA_SEPOLIA -> 59140L
            ChainType.ETHEREUM_HOLESKY -> 1L
            ChainType.ETHEREUM_SEPOLIA -> 11155111L
            ChainType.POLYGON_AMOY -> 80002L
        }
        return chain
    }
}