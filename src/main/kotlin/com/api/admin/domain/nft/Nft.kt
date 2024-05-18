package com.api.admin.domain.nft

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("nft")
class Nft(
    @Id val id : Long,
    val tokenId: String,
    val tokenAddress: String,
    val chainType: String,
    val nftName: String,
    val collectionName: String
) {
}