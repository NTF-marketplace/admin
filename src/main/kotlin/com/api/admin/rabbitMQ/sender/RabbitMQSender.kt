package com.api.admin.rabbitMQ.sender

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMQSender(
    private val rabbitTemplate: RabbitTemplate
) {

    fun depositSend(deposit: Long) {
        rabbitTemplate.convertAndSend("depositExchange", "depositRoutingKey", deposit)
    }

}