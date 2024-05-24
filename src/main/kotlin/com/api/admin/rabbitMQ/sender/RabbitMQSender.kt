package com.api.admin.rabbitMQ.sender

import com.api.admin.rabbitMQ.event.dto.AdminTransferResponse
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMQSender(
    private val rabbitTemplate: RabbitTemplate
) {

    fun transferSend(transfer: AdminTransferResponse) {
        rabbitTemplate.convertAndSend("transferExchange", "transferRoutingKey", transfer)
    }

}