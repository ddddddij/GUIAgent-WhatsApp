package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class CommunityChannelType {
    GENERAL,
    ANNOUNCEMENTS
}

object CommunityChannelStore {

    private val messagesByChannelId = mutableMapOf<String, MutableList<Message>>()
    private val _changeVersion = MutableStateFlow(0)
    val changeVersion: StateFlow<Int> = _changeVersion.asStateFlow()

    private const val CURRENT_USER_ID = "user_001"
    private const val CURRENT_USER_NAME = "Alex Johnson"

    fun initialize(communities: List<Community>) {
        communities.forEach { community ->
            val generalChannelId = getChannelId(community.id, CommunityChannelType.GENERAL)
            val announcementsChannelId = getChannelId(community.id, CommunityChannelType.ANNOUNCEMENTS)
            if (messagesByChannelId[generalChannelId] == null) {
                messagesByChannelId[generalChannelId] = seedGeneralMessages(community).toMutableList()
            }
            if (messagesByChannelId[announcementsChannelId] == null) {
                messagesByChannelId[announcementsChannelId] = seedAnnouncementMessages(community).toMutableList()
            }
        }
    }

    fun getMessages(
        communityId: String,
        channelType: CommunityChannelType
    ): List<Message> {
        val channelId = getChannelId(communityId, channelType)
        return messagesByChannelId[channelId]
            ?.sortedBy { it.sentAt }
            ?: emptyList()
    }

    fun sendForwardedMessage(
        communityId: String,
        channelType: CommunityChannelType,
        textContent: String?,
        forwardedFrom: String?,
        forwardedChannelId: String?,
        forwardedImageResName: String?
    ) {
        val now = System.currentTimeMillis()
        val channelId = getChannelId(communityId, channelType)
        val message = Message(
            id = "community_fwd_${UUID.randomUUID()}",
            conversationId = channelId,
            senderId = CURRENT_USER_ID,
            senderName = CURRENT_USER_NAME,
            messageType = MessageType.FORWARDED_STATUS,
            textContent = textContent,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null,
            forwardedFrom = forwardedFrom,
            forwardedChannelId = forwardedChannelId,
            forwardedImageResName = forwardedImageResName
        )
        messagesByChannelId.getOrPut(channelId) { mutableListOf() }.add(message)
        _changeVersion.value += 1
    }

    fun sendMessage(
        communityId: String,
        channelType: CommunityChannelType,
        content: String
    ): Message {
        val now = System.currentTimeMillis()
        val channelId = getChannelId(communityId, channelType)
        val message = Message(
            id = "community_${UUID.randomUUID()}",
            conversationId = channelId,
            senderId = CURRENT_USER_ID,
            senderName = CURRENT_USER_NAME,
            messageType = when (channelType) {
                CommunityChannelType.GENERAL -> MessageType.TEXT
                CommunityChannelType.ANNOUNCEMENTS -> MessageType.COMMUNITY_ANNOUNCEMENT
            },
            textContent = content,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null
        )
        messagesByChannelId.getOrPut(channelId) { mutableListOf() }.add(message)
        _changeVersion.value += 1
        return message
    }

    fun getLatestMessage(
        communityId: String,
        channelType: CommunityChannelType
    ): Message? {
        return getMessages(communityId, channelType).maxByOrNull { it.sentAt }
    }

    fun getChannelId(communityId: String, channelType: CommunityChannelType): String {
        return "community_${communityId}_${channelType.name.lowercase()}"
    }

    private fun seedGeneralMessages(community: Community): List<Message> {
        val members = community.members.filter { it.membershipStatus.name == "JOINED" }
        val primaryMember = members.getOrNull(1) ?: members.firstOrNull()
        val secondaryMember = members.getOrNull(2) ?: members.firstOrNull()
        val baseTime = community.createdAt + 60 * 60 * 1000

        return buildList {
            primaryMember?.let { member ->
                add(
                    Message(
                        id = "general_${community.id}_1",
                        conversationId = getChannelId(community.id, CommunityChannelType.GENERAL),
                        senderId = member.userId,
                        senderName = member.displayName,
                        messageType = MessageType.TEXT,
                        textContent = "Welcome to ${community.name} General.",
                        mediaUrl = null,
                        messageStatus = MessageStatus.READ,
                        sentAt = baseTime,
                        deliveredAt = baseTime,
                        readAt = baseTime + 60_000
                    )
                )
            }
            secondaryMember?.let { member ->
                add(
                    Message(
                        id = "general_${community.id}_2",
                        conversationId = getChannelId(community.id, CommunityChannelType.GENERAL),
                        senderId = member.userId,
                        senderName = member.displayName,
                        messageType = MessageType.TEXT,
                        textContent = "Share updates, questions, and useful links here.",
                        mediaUrl = null,
                        messageStatus = MessageStatus.READ,
                        sentAt = baseTime + 25 * 60 * 1000,
                        deliveredAt = baseTime + 25 * 60 * 1000,
                        readAt = baseTime + 26 * 60 * 1000
                    )
                )
            }
        }
    }

    private fun seedAnnouncementMessages(community: Community): List<Message> {
        val admins = community.members.filter {
            it.membershipStatus.name == "JOINED" && (it.role.name == "OWNER" || it.role.name == "ADMIN")
        }
        val announcer = admins.firstOrNull() ?: community.members.firstOrNull()
        val baseTime = community.createdAt + 2 * 60 * 60 * 1000

        return buildList {
            announcer?.let { member ->
                add(
                    Message(
                        id = "announcement_${community.id}_1",
                        conversationId = getChannelId(community.id, CommunityChannelType.ANNOUNCEMENTS),
                        senderId = member.userId,
                        senderName = member.displayName,
                        messageType = MessageType.COMMUNITY_ANNOUNCEMENT,
                        textContent = "Welcome to ${community.name}. Official updates will be posted here.",
                        mediaUrl = null,
                        messageStatus = MessageStatus.READ,
                        sentAt = baseTime,
                        deliveredAt = baseTime,
                        readAt = baseTime + 60_000
                    )
                )
                add(
                    Message(
                        id = "announcement_${community.id}_2",
                        conversationId = getChannelId(community.id, CommunityChannelType.ANNOUNCEMENTS),
                        senderId = member.userId,
                        senderName = member.displayName,
                        messageType = MessageType.COMMUNITY_ANNOUNCEMENT,
                        textContent = "Please keep conversation in General and use Announcements for important notices only.",
                        mediaUrl = null,
                        messageStatus = MessageStatus.READ,
                        sentAt = baseTime + 35 * 60 * 1000,
                        deliveredAt = baseTime + 35 * 60 * 1000,
                        readAt = baseTime + 36 * 60 * 1000
                    )
                )
            }
        }
    }
}
