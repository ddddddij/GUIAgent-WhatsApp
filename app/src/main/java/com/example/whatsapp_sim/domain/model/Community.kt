package com.example.whatsapp_sim.domain.model

data class CommunityMember(
    val userId: String,
    val displayName: String,
    val role: CommunityRole,
    val membershipStatus: MembershipStatus,
    val joinedAt: Long
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val creatorId: String,
    val createdAt: Long,
    val members: List<CommunityMember>,
    val inviteLink: String
)
