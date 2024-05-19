package com.api.admin.rabbitMQ.event

import com.api.admin.rabbitMQ.sender.RabbitMQSender
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AdminEventListener(
    private val provider : RabbitMQSender
) {

    @EventListener
    fun onDepositSend(event: Long) {
        provider.depositSend(event)
    }
}