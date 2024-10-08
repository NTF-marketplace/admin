package com.api.admin.rabbitMQ.receiver

import com.api.admin.service.dto.NftResponse
import com.api.admin.service.NftService
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMQReceiver(
    private val nftService: NftService,
) {
    @RabbitListener(bindings = [QueueBinding(
        value = Queue(name = "", durable = "false", exclusive = "true", autoDelete = "true"),
        exchange = Exchange(value = "nftExchange", type = ExchangeTypes.FANOUT)
    )])
    fun nftMessage(nft: NftResponse) {
        nftService.save(nft)
            .subscribe()

    }
}