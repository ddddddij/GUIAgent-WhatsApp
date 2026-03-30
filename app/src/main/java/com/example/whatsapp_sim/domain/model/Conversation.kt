package com.example.whatsapp_sim.domain.model

data class Conversation(
    val id: String,
    val participantIds: List<String>,
    val participantNames: List<String>,
    val isGroupChat: Boolean,
    val groupName: String?,
    val lastMessagePreview: String?,
    val lastMessageAt: Long?,
    val createdAt: Long,
    val creatorId: String,
    val unreadCount: Int
)
