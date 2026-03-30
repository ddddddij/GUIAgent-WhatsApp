package com.example.whatsapp_sim.domain.model

data class Account(
    val id: String,
    val phone: String,
    val displayName: String,
    val about: String,
    val avatarUrl: String?,
    val updatedAt: Long
)
