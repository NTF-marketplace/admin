package com.api.admin.rabbitMQ.receiver

import com.api.admin.service.dto.NftResponse
import com.api.admin.service.NftService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMQReceiver(
    private val nftService: NftService,
) {
    @RabbitListener(queues = ["nftQueue"])
    fun nftMessage(nft: NftResponse) {
        nftService.save(nft)
            .subscribe()

    }
}