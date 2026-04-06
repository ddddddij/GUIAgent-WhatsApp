package com.example.whatsapp_sim.domain.model

data class Channel(
    val id: String,
    val name: String,
    val followersCount: String,
    val isVerified: Boolean,
    val avatarRes: Int?,
    val latestUpdate: String? = null,
    val updateTime: String? = null,
    val initiallyFollowing: Boolean = false,
    val isNotificationMuted: Boolean = false
)
