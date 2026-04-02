package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import com.example.whatsapp_sim.domain.repository.ChatRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatRepositoryImpl(private val assetsHelper: AssetsHelper) : ChatRepository {

    private val currentUserId = "user_001" // Alex Johnson
    private val currentUserName = "Alex Johnson"

    override fun getAllChats(): List<Chat> {
        ensureDataLoaded()
        val conversations = cachedConversations.toList()
        val messages = cachedMessages.toList()

        return conversations.mapNotNull { conv ->
            val lastMsg = messages
                .filter { it.conversationId == conv.id }
                .maxByOrNull { it.sentAt }

            val displayName = if (conv.isGroupChat) {
                conv.groupName ?: "Group"
            } else {
                conv.participantNames.firstOrNull { it != currentUserName }
                    ?: conv.participantNames.firstOrNull()
                    ?: "Unknown"
            }

            val lastMessagePreview = lastMsg?.let { msg ->
                when (msg.messageType) {
                    MessageType.TEXT -> msg.textContent ?: ""
                    MessageType.IMAGE -> "\uD83D\uDCF7 Photo"
                    MessageType.AUDIO -> "\uD83C\uDFA4 Audio"
                    MessageType.DOCUMENT -> "\uD83D\uDCC4 Document"
                    MessageType.LOCATION -> "\uD83D\uDCCD Location"
                    MessageType.GIF -> "GIF"
                }
            } ?: conv.lastMessagePreview.orEmpty()

            val lastMessageSender = if (conv.isGroupChat && lastMsg?.senderId != null && lastMsg.senderId != currentUserId) {
                lastMsg.senderName
            } else {
                null
            }

            val status = if (lastMsg?.senderId == currentUserId) lastMsg.messageStatus else null

            Chat(
                id = conv.id,
                name = displayName,
                avatarUrl = null,
                lastMessage = lastMessagePreview,
                lastMessageSender = lastMessageSender,
                lastMessageType = lastMsg?.messageType ?: MessageType.TEXT,
                timestamp = conv.lastMessageAt?.let(::formatTimestamp).orEmpty(),
                unreadCount = conv.unreadCount,
                isPinned = conv.id == "conv_004",
                isMuted = conv.id == "conv_005",
                isGroup = conv.isGroupChat,
                isTyping = conv.id == "conv_003" && lastMsg != null,
                lastMessageStatus = status
            )
        }.sortedWith(
            compareByDescending<Chat> { it.isPinned }
                .thenByDescending { chat -> conversations.firstOrNull { it.id == chat.id }?.lastMessageAt ?: 0L }
        )
    }

    override fun getConversation(conversationId: String): Conversation? {
        ensureDataLoaded()
        return cachedConversations.firstOrNull { it.id == conversationId }
    }

    override fun getMessages(conversationId: String): List<Message> {
        ensureDataLoaded()
        return cachedMessages
            .filter { it.conversationId == conversationId }
            .sortedBy { it.sentAt }
    }

    override fun getContactForConversation(conversationId: String): Contact? {
        ensureDataLoaded()
        val conversation = getConversation(conversationId) ?: return null
        if (conversation.isGroupChat) {
            return null
        }

        val contactName = conversation.participantNames.firstOrNull { it != currentUserName } ?: return null
        return cachedContacts.firstOrNull { it.displayName == contactName }
    }

    override fun getAllContacts(): List<Contact> {
        ensureDataLoaded()
        return cachedContacts.toList()
    }

    override fun getCurrentUserAccount(): Account? {
        ensureDataLoaded()
        return cachedAccounts.firstOrNull { it.id == currentUserId } ?: cachedAccounts.firstOrNull()
    }

    override fun findOrCreateConversationForContact(contact: Contact): String {
        ensureDataLoaded()

        val existingConversation = cachedConversations.firstOrNull { conversation ->
            !conversation.isGroupChat &&
                conversation.participantNames.contains(contact.displayName)
        }
        if (existingConversation != null) {
            return existingConversation.id
        }

        val now = System.currentTimeMillis()
        val newConversation = Conversation(
            id = "conv_$now",
            participantIds = listOf(currentUserId, resolveParticipantId(contact)),
            participantNames = listOf(currentUserName, contact.displayName),
            isGroupChat = false,
            groupName = null,
            lastMessagePreview = "",
            lastMessageAt = null,
            createdAt = now,
            creatorId = currentUserId,
            unreadCount = 0
        )
        cachedConversations.add(newConversation)
        return newConversation.id
    }

    override fun findOrCreateConversationForCurrentUser(): String {
        ensureDataLoaded()

        val existingConversation = cachedConversations.firstOrNull { conversation ->
            !conversation.isGroupChat &&
                conversation.participantNames.all { it == currentUserName } &&
                conversation.participantIds.all { it == currentUserId }
        }
        if (existingConversation != null) {
            return existingConversation.id
        }

        val now = System.currentTimeMillis()
        val newConversation = Conversation(
            id = "conv_$now",
            participantIds = listOf(currentUserId),
            participantNames = listOf(currentUserName),
            isGroupChat = false,
            groupName = null,
            lastMessagePreview = "",
            lastMessageAt = null,
            createdAt = now,
            creatorId = currentUserId,
            unreadCount = 0
        )
        cachedConversations.add(newConversation)
        return newConversation.id
    }

    override fun sendMessage(conversationId: String, content: String): Message {
        ensureDataLoaded()

        val now = System.currentTimeMillis()
        val message = Message(
            id = "local_${UUID.randomUUID()}",
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            messageType = MessageType.TEXT,
            textContent = content,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null
        )

        cachedMessages.add(message)
        cachedConversations = cachedConversations.map { conversation ->
            if (conversation.id == conversationId) {
                conversation.copy(
                    lastMessagePreview = content,
                    lastMessageAt = now
                )
            } else {
                conversation
            }
        }.toMutableList()

        return message
    }

    private fun ensureDataLoaded() {
        synchronized(lock) {
            if (isInitialized) {
                return
            }

            cachedConversations = assetsHelper.loadConversations().toMutableList()
            cachedMessages = assetsHelper.loadMessages().toMutableList()
            cachedContacts = assetsHelper.loadContacts()
            cachedAccounts = assetsHelper.loadAccounts()
            isInitialized = true
        }
    }

    private fun resolveParticipantId(contact: Contact): String {
        val matchedAccount = cachedAccounts.firstOrNull { account ->
            account.displayName == contact.displayName || account.phone == contact.phone
        }
        return matchedAccount?.id ?: contact.id
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
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
            msgYear == nowYear && nowDoy - msgDoy == 1 -> {
                "Yesterday"
            }
            msgYear == nowYear && nowDoy - msgDoy < 7 -> {
                SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
            }
            else -> {
                SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
            }
        }
    }

    companion object {
        private val lock = Any()
        private var isInitialized = false
        private var cachedConversations: MutableList<Conversation> = mutableListOf()
        private var cachedMessages: MutableList<Message> = mutableListOf()
        private var cachedContacts: List<Contact> = emptyList()
        private var cachedAccounts: List<Account> = emptyList()
    }
}
