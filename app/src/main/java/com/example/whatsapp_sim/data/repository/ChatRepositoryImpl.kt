package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import com.example.whatsapp_sim.domain.repository.ChatRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatRepositoryImpl(private val assetsHelper: AssetsHelper) : ChatRepository {

    private val currentUserId = "user_001" // Alex Johnson

    override fun getAllChats(): List<Chat> {
        val conversations = assetsHelper.loadConversations()
        val messages = assetsHelper.loadMessages()

        return conversations.mapNotNull { conv ->
            val lastMsg = messages
                .filter { it.conversationId == conv.id }
                .maxByOrNull { it.sentAt }

            lastMsg?.let { msg ->
                val displayName = if (conv.isGroupChat) {
                    conv.groupName ?: "Group"
                } else {
                    conv.participantNames.firstOrNull { it != "Alex Johnson" } ?: "Unknown"
                }

                val lastMessagePreview = when (msg.messageType) {
                    MessageType.TEXT -> msg.textContent ?: ""
                    MessageType.IMAGE -> "📷 Photo"
                    MessageType.AUDIO -> "🎤 Audio"
                    MessageType.DOCUMENT -> "📄 Document"
                    MessageType.LOCATION -> "📍 Location"
                    MessageType.GIF -> "GIF"
                }

                val lastMessageSender = if (conv.isGroupChat && msg.senderId != currentUserId) {
                    msg.senderName
                } else null

                val status = if (msg.senderId == currentUserId) msg.messageStatus else null

                Chat(
                    id = conv.id,
                    name = displayName,
                    avatarUrl = null,
                    lastMessage = lastMessagePreview,
                    lastMessageSender = lastMessageSender,
                    lastMessageType = msg.messageType,
                    timestamp = formatTimestamp(msg.sentAt),
                    unreadCount = conv.unreadCount,
                    isPinned = conv.id == "conv_004",
                    isMuted = conv.id == "conv_005",
                    isGroup = conv.isGroupChat,
                    isTyping = conv.id == "conv_003",
                    lastMessageStatus = status
                )
            }
        }.sortedWith(compareByDescending<Chat> { it.isPinned }.thenByDescending { it.timestamp })
    }

    private fun formatTimestamp(millis: Long): String {
        val date = Date(millis)
        val msgCal = Calendar.getInstance().apply { time = date }
        val nowCal = Calendar.getInstance()

        val msgYear = msgCal.get(Calendar.YEAR)
        val msgDoy = msgCal.get(Calendar.DAY_OF_YEAR)
        val nowYear = nowCal.get(Calendar.YEAR)
        val nowDoy = nowCal.get(Calendar.DAY_OF_YEAR)

        return when {
            msgYear == nowYear && msgDoy == nowDoy -> {
                // Today: show time
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
            msgYear == nowYear && nowDoy - msgDoy == 1 -> {
                "Yesterday"
            }
            msgYear == nowYear && nowDoy - msgDoy < 7 -> {
                // This week: show day name
                SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
            }
            else -> {
                // Older: show month/day only (no year)
                SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
            }
        }
    }
}
