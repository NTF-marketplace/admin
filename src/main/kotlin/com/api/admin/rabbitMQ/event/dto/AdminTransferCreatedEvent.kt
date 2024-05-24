package com.api.admin.rabbitMQ.event.dto

import org.springframework.context.ApplicationEvent

data class AdminTransferCreatedEvent(val eventSource: Any, val transfer: AdminTransferResponse): ApplicationEvent(eventSource)
