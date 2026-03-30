package com.example.whatsapp_sim.domain.model

data class Contact(
    val id: String,
    val phone: String,
    val displayName: String,
    val avatarUrl: String?,
    val isAppUser: Boolean,
    val contactStatus: ContactStatus,
    val inviteStatus: InviteStatus?,
    val invitedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
