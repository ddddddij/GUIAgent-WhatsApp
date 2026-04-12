package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.GroupDetail
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import com.example.whatsapp_sim.domain.repository.ChatRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatRepositoryImpl(
    private val assetsHelper: AssetsHelper,
    private val contactStore: RuntimeContactStore = RuntimeContactStore.getInstance(assetsHelper)
) : ChatRepository {

    private val currentUserId = "user_001" // JiayiDai
    private val currentUserName = "JiayiDai"

    private val fallbackGroupAvatarMap = mapOf(
        "SF Tech Squad" to "image/群聊头像/SF Tech Squad.jpg",
        "Weekend Hiking Crew" to "image/群聊头像/Weekend Hiking Crew.jpg",
        "Startup Ideas 💡" to "image/群聊头像/Startup Ideas.jpg",
        "Friday Night Plans 🍕" to "image/群聊头像/Friday Night Plans.jpg"
    )

    override fun getAllChats(): List<Chat> {
        ensureDataLoaded()
        val conversations = cachedConversations.map(::mergeConversationWithGroupDetail)
        val messages = cachedMessages.toList()
        val contacts = contactStore.getAllContacts()
        val groupDetailsByConversationId = cachedGroupDetails.associateBy { it.conversationId }

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
                    MessageType.FORWARDED_STATUS -> "📢 ${msg.forwardedFrom ?: "Channel post"}"
                    MessageType.COMMUNITY_ANNOUNCEMENT -> "📣 ${msg.textContent.orEmpty()}"
                    MessageType.VOICE_CALL -> "Voice call"
                    MessageType.VIDEO_CALL -> "Video call"
                }
            } ?: conv.lastMessagePreview.orEmpty()

            val lastMessageSender = if (conv.isGroupChat && lastMsg?.senderId != null && lastMsg.senderId != currentUserId) {
                lastMsg.senderName
            } else {
                null
            }

            val status = if (lastMsg?.senderId == currentUserId) lastMsg.messageStatus else null

            val contactAvatarUrl = if (!conv.isGroupChat) {
                contacts.firstOrNull { it.displayName == displayName }?.avatarUrl
            } else {
                groupDetailsByConversationId[conv.id]?.avatarUrl
                    ?: fallbackGroupAvatarMap[conv.groupName]
            }

            Chat(
                id = conv.id,
                name = displayName,
                avatarUrl = contactAvatarUrl,
                lastMessage = lastMessagePreview,
                lastMessageSender = lastMessageSender,
                lastMessageType = lastMsg?.messageType ?: MessageType.TEXT,
                timestamp = conv.lastMessageAt?.let(::formatTimestamp).orEmpty(),
                unreadCount = conv.unreadCount,
                isPinned = conv.id == "conv_004",
                isMuted = if (conv.isGroupChat) {
                    groupDetailsByConversationId[conv.id]?.isMuted ?: false
                } else {
                    false
                },
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
        return cachedConversations
            .firstOrNull { it.id == conversationId }
            ?.let(::mergeConversationWithGroupDetail)
    }

    override fun getMessages(conversationId: String): List<Message> {
        ensureDataLoaded()
        return cachedMessages
            .filter { it.conversationId == conversationId }
            .sortedBy { it.sentAt }
    }

    override fun markConversationAsRead(conversationId: String) {
        ensureDataLoaded()
        // Update unreadCount to 0 (runtime only, not persisted so restart restores original)
        cachedConversations = cachedConversations.map { conv ->
            if (conv.id == conversationId) conv.copy(unreadCount = 0) else conv
        }.toMutableList()
        // Mark all DELIVERED messages in this conversation as READ
        val now = System.currentTimeMillis()
        cachedMessages = cachedMessages.map { msg ->
            if (msg.conversationId == conversationId &&
                msg.senderId != currentUserId &&
                msg.messageStatus != MessageStatus.READ
            ) {
                msg.copy(messageStatus = MessageStatus.READ, readAt = now)
            } else {
                msg
            }
        }.toMutableList()
    }

    override fun getContactForConversation(conversationId: String): Contact? {
        ensureDataLoaded()
        val conversation = getConversation(conversationId) ?: return null
        if (conversation.isGroupChat) {
            return null
        }

        val contactName = conversation.participantNames.firstOrNull { it != currentUserName } ?: return null
        return contactStore.getAllContacts().firstOrNull { it.displayName == contactName }
    }

    override fun getAllContacts(): List<Contact> {
        ensureDataLoaded()
        return contactStore.getAllContacts()
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
        persistConversations()
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
        persistConversations()
        return newConversation.id
    }

    override fun createGroupConversation(groupName: String, memberIds: List<String>, memberNames: List<String>): String {
        ensureDataLoaded()
        val now = System.currentTimeMillis()
        val allIds = (listOf(currentUserId) + memberIds).distinct()
        val allNames = (listOf(currentUserName) + memberNames).distinct()
        val conversationId = "conv_group_$now"
        val newConversation = Conversation(
            id = conversationId,
            participantIds = allIds,
            participantNames = allNames,
            isGroupChat = true,
            groupName = groupName,
            lastMessagePreview = null,
            lastMessageAt = now,
            createdAt = now,
            creatorId = currentUserId,
            unreadCount = 0
        )
        cachedConversations.add(newConversation)
        cachedGroupDetails.add(
            GroupDetail(
                conversationId = conversationId,
                groupName = groupName,
                avatarUrl = null,
                memberIds = memberIds.distinct(),
                adminIds = listOf(currentUserId),
                description = null,
                createdBy = "you",
                createdAtDisplay = formatGroupCreatedAt(now),
                isMuted = false,
                isLocked = false
            )
        )
        persistGroupData()
        return conversationId
    }

    fun getGroupConversations(): List<Conversation> {
        ensureDataLoaded()
        return cachedConversations
            .filter { it.isGroupChat }
            .map(::mergeConversationWithGroupDetail)
    }

    fun getGroupDetail(conversationId: String): GroupDetail? {
        ensureDataLoaded()
        return cachedGroupDetails.firstOrNull { it.conversationId == conversationId }
    }

    fun setGroupMuted(conversationId: String, isMuted: Boolean): GroupDetail? {
        ensureDataLoaded()
        val updated = cachedGroupDetails.firstOrNull { it.conversationId == conversationId }
            ?.copy(isMuted = isMuted)
            ?: return null
        replaceGroupDetail(updated)
        return updated
    }

    fun setGroupLocked(conversationId: String, isLocked: Boolean): GroupDetail? {
        ensureDataLoaded()
        val updated = cachedGroupDetails.firstOrNull { it.conversationId == conversationId }
            ?.copy(isLocked = isLocked)
            ?: return null
        replaceGroupDetail(updated)
        return updated
    }

    fun addGroupMember(conversationId: String, contact: Contact): Conversation? {
        ensureDataLoaded()
        val participantId = resolveParticipantId(contact)
        val detail = cachedGroupDetails.firstOrNull { it.conversationId == conversationId } ?: return null
        if (detail.memberIds.contains(participantId)) {
            return getConversation(conversationId)
        }

        val updatedDetail = detail.copy(memberIds = detail.memberIds + participantId)
        replaceGroupDetail(updatedDetail)
        return getConversation(conversationId)
    }

    fun resolveParticipantIdForContact(contact: Contact): String {
        ensureDataLoaded()
        return resolveParticipantId(contact)
    }

    fun findContactByParticipantId(participantId: String): Contact? {
        ensureDataLoaded()
        return findContactByParticipantIdInternal(participantId)
    }

    fun getAccountById(accountId: String): Account? {
        ensureDataLoaded()
        return cachedAccounts.firstOrNull { it.id == accountId }
    }

    fun getParticipantDisplayName(participantId: String): String {
        ensureDataLoaded()
        return cachedAccounts.firstOrNull { it.id == participantId }?.displayName
            ?: findContactByParticipantIdInternal(participantId)?.displayName
            ?: participantId
    }

    fun getParticipantAvatarUrl(participantId: String): String? {
        ensureDataLoaded()
        return cachedAccounts.firstOrNull { it.id == participantId }?.avatarUrl
            ?: findContactByParticipantIdInternal(participantId)?.avatarUrl
    }

    fun getParticipantAbout(participantId: String): String? {
        ensureDataLoaded()
        return cachedAccounts.firstOrNull { it.id == participantId }?.about
    }

    /**
     * Directly add a pre-built message (e.g. forwarded status) to the cache.
     */
    fun addMessage(message: Message) {
        ensureDataLoaded()
        cachedMessages.add(message)
        cachedConversations = cachedConversations.map { conv ->
            if (conv.id == message.conversationId)
                conv.copy(lastMessagePreview = message.textContent ?: "[Forwarded]", lastMessageAt = message.sentAt)
            else conv
        }.toMutableList()
        persistChatData()
    }

    fun removeConversation(conversationId: String) {
        ensureDataLoaded()
        cachedConversations = cachedConversations.filter { it.id != conversationId }.toMutableList()
        cachedGroupDetails = cachedGroupDetails
            .filter { it.conversationId != conversationId }
            .toMutableList()
        persistGroupData()
    }

    fun updateConversation(conversation: Conversation) {
        ensureDataLoaded()
        cachedConversations = cachedConversations.map {
            if (it.id == conversation.id) conversation else it
        }.toMutableList()
        persistConversations()
    }

    /**
     * Fan out a broadcast message into each member's existing 1:1 conversation.
     * If the conversation doesn't exist yet, it is created.
     */
    fun sendBroadcastFanOut(memberId: String, memberName: String, content: String, sentAt: Long) {
        ensureDataLoaded()
        // Find or create 1:1 conversation with this member
        val conv = cachedConversations.firstOrNull { c ->
            !c.isGroupChat && c.participantNames.contains(memberName)
        } ?: run {
            val now = sentAt
            val newConv = Conversation(
                id = "conv_$now${memberId.takeLast(4)}",
                participantIds = listOf(currentUserId, memberId),
                participantNames = listOf(currentUserName, memberName),
                isGroupChat = false,
                groupName = null,
                lastMessagePreview = content,
                lastMessageAt = now,
                createdAt = now,
                creatorId = currentUserId,
                unreadCount = 1
            )
            cachedConversations.add(newConv)
            newConv
        }
        val fanOutMsg = Message(
            id = "fanout_${java.util.UUID.randomUUID()}",
            conversationId = conv.id,
            senderId = currentUserId,
            senderName = currentUserName,
            messageType = MessageType.TEXT,
            textContent = content,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = sentAt,
            deliveredAt = sentAt,
            readAt = null
        )
        cachedMessages.add(fanOutMsg)
        cachedConversations = cachedConversations.map { c ->
            if (c.id == conv.id) c.copy(lastMessagePreview = content, lastMessageAt = sentAt)
            else c
        }.toMutableList()
        persistChatData()
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

        appendMessage(message, content, now)

        return message
    }

    override fun sendImageMessage(conversationId: String, imagePath: String, caption: String?): Message {
        ensureDataLoaded()

        val now = System.currentTimeMillis()
        val message = Message(
            id = "local_${UUID.randomUUID()}",
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            messageType = MessageType.IMAGE,
            textContent = caption,
            mediaUrl = imagePath,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null
        )

        appendMessage(message, caption ?: "\uD83D\uDCF7 Photo", now)

        return message
    }

    private fun appendMessage(message: Message, conversationPreview: String, sentAt: Long) {
        cachedMessages.add(message)
        cachedConversations = cachedConversations.map { conversation ->
            if (conversation.id == message.conversationId) {
                conversation.copy(
                    lastMessagePreview = conversationPreview,
                    lastMessageAt = sentAt
                )
            } else {
                conversation
            }
        }.toMutableList()
        persistChatData()
    }

    private fun persistConversations() {
        assetsHelper.saveConversations(cachedConversations)
    }

    private fun persistGroupDetails() {
        assetsHelper.saveGroupDetails(cachedGroupDetails)
    }

    private fun persistGroupData() {
        persistConversations()
        persistGroupDetails()
    }

    private fun persistChatData() {
        assetsHelper.saveMessages(cachedMessages)
        assetsHelper.saveConversations(cachedConversations)
    }

    private fun ensureDataLoaded() {
        synchronized(lock) {
            if (isInitialized) {
                return
            }

            cachedConversations = assetsHelper.loadConversations().toMutableList()
            cachedMessages = assetsHelper.loadMessages().toMutableList()
            cachedAccounts = assetsHelper.loadAccounts()
            cachedGroupDetails = assetsHelper.loadGroupDetails().toMutableList()
            cachedConversations = cachedConversations
                .map(::mergeConversationWithGroupDetail)
                .toMutableList()
            isInitialized = true
        }
    }

    private fun resolveParticipantId(contact: Contact): String {
        val matchedAccount = cachedAccounts.firstOrNull { account ->
            account.displayName == contact.displayName || account.phone == contact.phone
        }
        return matchedAccount?.id ?: contact.id
    }

    private fun findContactByParticipantIdInternal(participantId: String): Contact? {
        val account = cachedAccounts.firstOrNull { it.id == participantId }
        return account?.let { resolvedAccount ->
            contactStore.getAllContacts().firstOrNull { contact ->
                contact.displayName == resolvedAccount.displayName || contact.phone == resolvedAccount.phone
            }
        } ?: contactStore.getAllContacts().firstOrNull { it.id == participantId }
    }

    private fun mergeConversationWithGroupDetail(conversation: Conversation): Conversation {
        if (!conversation.isGroupChat) {
            return conversation
        }

        val detail = cachedGroupDetails.firstOrNull { it.conversationId == conversation.id }
            ?: createGroupDetailFromConversation(conversation)

        val participantIds = (listOf(currentUserId) + detail.memberIds).distinct()
        val participantNames = participantIds.map(::resolveDisplayNameForId)

        return conversation.copy(
            participantIds = participantIds,
            participantNames = participantNames,
            groupName = detail.groupName,
            memberIds = detail.memberIds,
            adminIds = detail.adminIds,
            description = detail.description,
            createdBy = detail.createdBy,
            createdAtDisplay = detail.createdAtDisplay
        )
    }

    private fun createGroupDetailFromConversation(conversation: Conversation): GroupDetail {
        val detail = GroupDetail(
            conversationId = conversation.id,
            groupName = conversation.groupName ?: "Group",
            avatarUrl = fallbackGroupAvatarMap[conversation.groupName],
            memberIds = conversation.memberIds.ifEmpty {
                conversation.participantIds.filterNot { it == currentUserId }
            },
            adminIds = conversation.adminIds.ifEmpty { listOf(conversation.creatorId) },
            description = conversation.description,
            createdBy = conversation.createdBy,
            createdAtDisplay = conversation.createdAtDisplay.ifBlank {
                formatGroupCreatedAt(conversation.createdAt)
            },
            isMuted = false,
            isLocked = false
        )
        cachedGroupDetails.add(detail)
        persistGroupDetails()
        return detail
    }

    private fun syncConversationWithGroupDetail(conversationId: String, detail: GroupDetail) {
        cachedConversations = cachedConversations.map { conversation ->
            if (conversation.id == conversationId) {
                val participantIds = (listOf(currentUserId) + detail.memberIds).distinct()
                conversation.copy(
                    participantIds = participantIds,
                    participantNames = participantIds.map(::resolveDisplayNameForId),
                    groupName = detail.groupName,
                    memberIds = detail.memberIds,
                    adminIds = detail.adminIds,
                    description = detail.description,
                    createdBy = detail.createdBy,
                    createdAtDisplay = detail.createdAtDisplay
                )
            } else {
                conversation
            }
        }.toMutableList()
        persistGroupData()
    }

    private fun replaceGroupDetail(detail: GroupDetail) {
        cachedGroupDetails = cachedGroupDetails.map {
            if (it.conversationId == detail.conversationId) detail else it
        }.toMutableList()
        syncConversationWithGroupDetail(detail.conversationId, detail)
    }

    private fun resolveDisplayNameForId(participantId: String): String {
        return cachedAccounts.firstOrNull { it.id == participantId }?.displayName
            ?: findContactByParticipantIdInternal(participantId)?.displayName
            ?: participantId
    }

    private fun formatGroupCreatedAt(timestamp: Long): String {
        return SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
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
        private var cachedGroupDetails: MutableList<GroupDetail> = mutableListOf()
        private var cachedMessages: MutableList<Message> = mutableListOf()
        private var cachedAccounts: List<Account> = emptyList()
    }
}
