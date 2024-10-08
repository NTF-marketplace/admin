package com.api.admin.domain.nft

import com.api.admin.enums.ChainType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("nft")
data class Nft(
    @Id val id : Long,
    val tokenId: String,
    val tokenAddress: String,
    val chainType: ChainType,
) {
}