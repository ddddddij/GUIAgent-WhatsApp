package com.example.whatsapp_sim.domain.model

data class ConversationNotifOverride(
    val conversationId: String,
    val level: NotifLevel,
    val mutedUntil: Long?
)

data class NotificationSettings(
    val userId: String,
    val globalLevel: NotifLevel,
    val messageNotifEnabled: Boolean,
    val groupNotifEnabled: Boolean,
    val callNotifEnabled: Boolean,
    val notifToneUrl: String?,
    val vibrationEnabled: Boolean,
    val conversationOverrides: List<ConversationNotifOverride>,
    val updatedAt: Long
)
