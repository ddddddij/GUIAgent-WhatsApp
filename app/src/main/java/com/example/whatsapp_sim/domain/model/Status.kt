package com.example.whatsapp_sim.domain.model

enum class StatusContentType {
    TEXT_ONLY,
    IMAGE_ONLY,
    IMAGE_WITH_TEXT,
    VIDEO_WITH_TEXT,
    MULTI_IMAGE
}

data class StatusReaction(
    val emoji: String,
    val count: Int
)

data class Status(
    val id: String,
    val channelId: String,
    val contentType: StatusContentType,
    val textContent: String?,
    val imageResName: String?,
    val timestamp: String,
    val dateLabel: String,
    val isEdited: Boolean,
    val reactions: List<StatusReaction>,
    val reactionCount: Int,
    val shareCount: Int,
    val hasVideo: Boolean,
    val videoDuration: String?,
    val imageCount: Int,
    val isMultiImage: Boolean,
    val userReaction: String? = null   // 用户已选择的表情，null 表示未选择
)
