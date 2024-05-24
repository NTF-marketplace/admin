package com.api.admin

import com.api.admin.domain.nft.Nft

data class NftResponse(
    val id : Long,
    val tokenId: String,
    val tokenAddress: String,
    val chainType: String,
    val nftName: String,
    val collectionName: String
){
    companion object{
        fun NftResponse.toEntity() = Nft(
            id = this.id,
            tokenId = this.tokenId,
            tokenAddress = this.tokenAddress,
            chainType = this.chainType,
            nftName = this.nftName,
            collectionName = this.collectionName
        )
    }
}

