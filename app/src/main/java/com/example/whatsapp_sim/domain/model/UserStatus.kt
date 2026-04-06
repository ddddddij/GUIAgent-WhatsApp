package com.example.whatsapp_sim.domain.model

data class UserStatus(
    val id: String,
    val senderName: String,
    val preview: String,       // text or caption shown in list
    val timeLabel: String,     // e.g. "10:32 AM"
    val isViewed: Boolean = false
)
