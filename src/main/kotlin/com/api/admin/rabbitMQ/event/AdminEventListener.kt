package com.api.admin.rabbitMQ.event

import com.api.admin.rabbitMQ.event.dto.AdminTransferCreatedEvent
import com.api.admin.rabbitMQ.sender.RabbitMQSender
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AdminEventListener(
    private val provider : RabbitMQSender
) {

    @EventListener
    fun onTransferSend(event: AdminTransferCreatedEvent) {
        provider.transferSend(event.transfer)
    }
}