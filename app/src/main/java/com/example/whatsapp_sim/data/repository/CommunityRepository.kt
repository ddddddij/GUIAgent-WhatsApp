package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.CommunityMember
import com.example.whatsapp_sim.domain.model.CommunityRole
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.MembershipStatus

class CommunityRepository(
    private val assetsHelper: AssetsHelper,
    private val contactStore: RuntimeContactStore = RuntimeContactStore.getInstance(assetsHelper)
) {
    private val currentUserId = "user_001"
    private val communities = assetsHelper.loadCommunities().toMutableList()
    private val accounts by lazy { assetsHelper.loadAccounts() }

    fun getCommunities(): List<Community> = communities.toList()

    fun getAllContacts(): List<Contact> = contactStore.getAllContacts()

    fun getCommunity(communityId: String): Community? {
        return communities.firstOrNull { it.id == communityId }
    }

    fun addCommunity(community: Community) {
        communities.add(0, community)
        persist()
    }

    fun toggleMute(communityId: String): Community? {
        val community = communities.firstOrNull { it.id == communityId } ?: return null
        val updatedCommunity = community
            .copy(isMuted = !community.isMuted)
        replaceCommunity(updatedCommunity)
        return updatedCommunity
    }

    fun setMute(communityId: String, isMuted: Boolean): Community? {
        val updatedCommunity = communities.firstOrNull { it.id == communityId }
            ?.copy(isMuted = isMuted)
            ?: return null
        replaceCommunity(updatedCommunity)
        return updatedCommunity
    }

    fun addMember(communityId: String, contact: Contact): Community? {
        val community = communities.firstOrNull { it.id == communityId } ?: return null
        val userId = resolveUserId(contact)
        val alreadyJoined = community.members.any { member ->
            member.userId == userId && member.membershipStatus == MembershipStatus.JOINED
        }
        if (alreadyJoined) {
            return community
        }

        val updatedMember = CommunityMember(
            userId = userId,
            displayName = resolveDisplayName(userId, contact),
            role = CommunityRole.MEMBER,
            membershipStatus = MembershipStatus.JOINED,
            joinedAt = System.currentTimeMillis()
        )
        val updatedCommunity = community.copy(members = community.members + updatedMember)
        replaceCommunity(updatedCommunity)
        return updatedCommunity
    }

    fun resolveUserId(contact: Contact): String {
        val matchedAccount = accounts.firstOrNull { account ->
            account.displayName == contact.displayName || account.phone == contact.phone
        }
        return matchedAccount?.id ?: contact.id
    }

    fun findContactByUserId(userId: String): Contact? {
        val account = accounts.firstOrNull { it.id == userId }
        return account?.let { resolvedAccount ->
            contactStore.getAllContacts().firstOrNull { contact ->
                contact.displayName == resolvedAccount.displayName || contact.phone == resolvedAccount.phone
            }
        } ?: contactStore.getAllContacts().firstOrNull { it.id == userId }
    }

    fun getDisplayName(userId: String): String {
        return accounts.firstOrNull { it.id == userId }?.displayName
            ?: findContactByUserId(userId)?.displayName
            ?: userId
    }

    fun getAvatarUrl(userId: String): String? {
        return accounts.firstOrNull { it.id == userId }?.avatarUrl
            ?: findContactByUserId(userId)?.avatarUrl
    }

    fun getAbout(userId: String): String? {
        return accounts.firstOrNull { it.id == userId }?.about
    }

    fun getCurrentUserId(): String = currentUserId

    private fun replaceCommunity(updatedCommunity: Community) {
        val index = communities.indexOfFirst { it.id == updatedCommunity.id }
        if (index == -1) {
            return
        }
        communities[index] = updatedCommunity
        persist()
    }

    private fun persist() {
        assetsHelper.saveCommunities(communities)
    }

    private fun resolveDisplayName(userId: String, fallbackContact: Contact): String {
        return accounts.firstOrNull { it.id == userId }?.displayName
            ?: fallbackContact.displayName
    }
}
