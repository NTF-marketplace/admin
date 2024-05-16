package com.api.admin.rabbitMQ

import com.api.admin.NftResponse
import com.api.admin.service.NftService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MessageReceiver(
    private val nftService: NftService,
) {
    @RabbitListener(queues = ["nftQueue"])
    fun receiveMessage(nft: NftResponse) {
        nftService.save(nft)
    }
}