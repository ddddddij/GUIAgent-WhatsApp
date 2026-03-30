package com.example.whatsapp_sim.domain.model

data class PrivacySettings(
    val userId: String,
    val lastSeenVisibility: PrivacyLevel,
    val profilePhotoVisibility: PrivacyLevel,
    val aboutVisibility: PrivacyLevel,
    val statusVisibility: PrivacyLevel,
    val readReceiptsEnabled: Boolean,
    val groupsAddPermission: PrivacyLevel,
    val updatedAt: Long
)
