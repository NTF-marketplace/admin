package com.api.admin.domain.transfer

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("transfer")
class Transfer(
    @Id val id: Long?,
    val nftId: Long,
    val wallet: String,
    val timestamp: Long,
    val status: String
) {

}