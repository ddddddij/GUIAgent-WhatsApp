package com.example.whatsapp_sim.domain.model

data class Channel(
    val id: String,
    val name: String,
    val followersCount: String,
    val isVerified: Boolean,
    val avatarRes: Int?,
    val latestUpdate: String? = null,  // latest post preview text
    val updateTime: String? = null,    // e.g. "2h ago"
    val initiallyFollowing: Boolean = false
)
