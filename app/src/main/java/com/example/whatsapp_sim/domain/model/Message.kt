package com.example.whatsapp_sim.domain.model

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val messageType: MessageType,
    val textContent: String?,
    val mediaUrl: String?,
    val messageStatus: MessageStatus,
    val sentAt: Long,
    val deliveredAt: Long?,
    val readAt: Long?,
    val forwardedFrom: String? = null,
    val forwardedImageResName: String? = null,
    val forwardedChannelId: String? = null,
    val callResult: CallResult? = null,
    val callDurationDisplay: String? = null
)
