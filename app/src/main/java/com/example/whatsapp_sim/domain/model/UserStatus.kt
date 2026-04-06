package com.example.whatsapp_sim.domain.model

data class UserStatusReaction(val emoji: String, val count: Int)

data class UserStatus(
    val id: String,
    val senderName: String,
    val preview: String,           // text caption shown in list & detail
    val timeLabel: String,         // e.g. "10:32 AM"
    val isViewed: Boolean = false,
    val bgColor: Long = 0xFF128C7E, // background color for text-only status
    val likeCount: Int = 0,
    val userLiked: Boolean = false,
    val emojiReactions: List<UserStatusReaction> = emptyList(),
    val userReaction: String? = null
)
