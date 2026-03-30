package com.example.whatsapp_sim.domain.model

data class Status(
    val id: String,
    val ownerUserId: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val contentText: String?,
    val contentImageUrl: String?,
    val isViewed: Boolean,
    val viewedAt: Long?,
    val publishedAt: Long,
    val expiresAt: Long
)
