package com.example.whatsapp_sim.domain.model

data class Chat(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val lastMessage: String,
    val lastMessageSender: String?,
    val lastMessageType: MessageType,
    val timestamp: String,
    val unreadCount: Int,
    val isPinned: Boolean,
    val isMuted: Boolean,
    val isGroup: Boolean,
    val isTyping: Boolean,
    val lastMessageStatus: MessageStatus?
)

enum class ChatFilter { ALL, UNREAD, GROUPS }
