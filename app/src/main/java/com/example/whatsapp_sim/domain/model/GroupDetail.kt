package com.example.whatsapp_sim.domain.model

data class GroupDetail(
    val conversationId: String,
    val groupName: String,
    val avatarUrl: String?,
    val memberIds: List<String>,
    val adminIds: List<String>,
    val description: String?,
    val createdBy: String,
    val createdAtDisplay: String,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false
)
