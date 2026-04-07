package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.domain.model.BroadcastList
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import java.util.UUID

/**
 * Singleton in-memory store for broadcast lists.
 * Broadcast messages are stored per-list for display in BroadcastDetailScreen,
 * and are also fanned out to each member's individual 1:1 conversation.
 * No dedicated "broadcast conversation" is created in ChatRepositoryImpl.
 */
object BroadcastStore {

    private val _lists = mutableListOf<BroadcastList>()
    val lists: List<BroadcastList> get() = _lists.toList()

    // Messages keyed by broadcastList.id (not a chat conversationId)
    private val _messages = mutableMapOf<String, MutableList<Message>>()

    private const val CURRENT_USER_ID = "user_001"
    private const val CURRENT_USER_NAME = "JiayiDai"

    fun createBroadcastList(
        memberIds: List<String>,
        memberNames: List<String>
    ): BroadcastList {
        val now = System.currentTimeMillis()
        val broadcastList = BroadcastList(
            id = "broadcast_$now",
            memberIds = memberIds,
            memberNames = memberNames
        )
        _lists.add(broadcastList)
        return broadcastList
    }

    fun getMessages(broadcastId: String): List<Message> =
        _messages[broadcastId]?.sortedBy { it.sentAt } ?: emptyList()

    /**
     * Send a broadcast message:
     * 1. Store in this broadcast list's own history (for BroadcastDetailScreen).
     * 2. Fan out to each member's 1:1 conversation via ChatRepositoryImpl.
     *    This makes the message appear in each member's chat detail page.
     */
    fun sendMessage(
        broadcastId: String,
        content: String,
        chatRepository: ChatRepositoryImpl
    ): Message {
        val now = System.currentTimeMillis()
        val msg = Message(
            id = "bcast_${UUID.randomUUID()}",
            conversationId = broadcastId,
            senderId = CURRENT_USER_ID,
            senderName = CURRENT_USER_NAME,
            messageType = MessageType.TEXT,
            textContent = content,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null
        )
        _messages.getOrPut(broadcastId) { mutableListOf() }.add(msg)

        // Fan out to each member's 1:1 conversation
        val broadcastList = _lists.firstOrNull { it.id == broadcastId }
        broadcastList?.memberIds?.forEachIndexed { index, memberId ->
            val memberName = broadcastList.memberNames.getOrElse(index) { memberId }
            chatRepository.sendBroadcastFanOut(memberId, memberName, content, now)
        }

        return msg
    }
}
