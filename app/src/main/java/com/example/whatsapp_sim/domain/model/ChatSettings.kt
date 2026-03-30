package com.example.whatsapp_sim.domain.model

data class ChatSettings(
    val userId: String,
    val wallpaperUrl: String?,
    val fontSizeScale: Float,
    val enterToSend: Boolean,
    val mediaAutoDownloadEnabled: Boolean,
    val archivedChatIds: List<String>,
    val mutedChatIds: List<String>,
    val updatedAt: Long
)
