package com.api.admin

import com.api.admin.domain.nft.Nft
import com.api.admin.enums.ChainType

data class NftResponse(
    val id : Long,
    val tokenId: String,
    val tokenAddress: String,
    val chainType: ChainType,
){
    companion object{
        fun NftResponse.toEntity() = Nft(
            id = this.id,
            tokenId = this.tokenId,
            tokenAddress = this.tokenAddress,
            chainType = this.chainType,
        )
    }
}

