package com.example.whatsapp_sim.ui.screen.channelstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp_sim.data.repository.ChannelRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.StatusRepository
import com.example.whatsapp_sim.domain.model.Channel
import com.example.whatsapp_sim.data.repository.CommunityChannelStore
import com.example.whatsapp_sim.data.repository.CommunityChannelType
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChannelStatusViewModel(
    private val channelId: String,
    private val statusRepository: StatusRepository,
    private val chatRepository: ChatRepositoryImpl
) : ViewModel() {

    private val _channel = MutableStateFlow<Channel?>(null)
    val channel: StateFlow<Channel?> = _channel.asStateFlow()

    private val _statusPosts = MutableStateFlow<List<Status>>(emptyList())
    val statusPosts: StateFlow<List<Status>> = _statusPosts.asStateFlow()

    val isMuted = MutableStateFlow(false)
    val emojiSheetStatusId = MutableStateFlow<String?>(null)
    val shareSheetStatusId = MutableStateFlow<String?>(null)

    init {
        val allChannels = ChannelRepository().getChannels()
        _channel.value = allChannels.firstOrNull { it.id == channelId }
        _statusPosts.value = statusRepository.getStatusesByChannelId(channelId)
    }

    fun toggleMute() {
        isMuted.value = !isMuted.value
    }

    fun onEmojiClick(statusId: String) {
        emojiSheetStatusId.value = statusId
    }

    fun onShareClick(statusId: String) {
        shareSheetStatusId.value = statusId
    }

    fun sendReaction(statusId: String, emoji: String) {
        statusRepository.addReaction(statusId, emoji)
        // Refresh from repository so userReaction and counts are reflected
        _statusPosts.value = statusRepository.getStatusesByChannelId(channelId)
        dismissEmojiSheet()
    }

    /** Share to a contact: resolves real conversationId via findOrCreate. */
    fun shareStatusToContact(contact: Contact, statusId: String) {
        val conversationId = chatRepository.findOrCreateConversationForContact(contact)
        shareStatusTo(conversationId, statusId)
    }

    /** Share to a community: sends to the General channel via CommunityChannelStore. */
    fun shareStatusToCommunity(community: Community, statusId: String) {
        val status = _statusPosts.value.find { it.id == statusId } ?: return
        val channelName = _channel.value?.name ?: ""
        CommunityChannelStore.sendForwardedMessage(
            communityId = community.id,
            channelType = CommunityChannelType.GENERAL,
            textContent = status.textContent,
            forwardedFrom = channelName,
            forwardedChannelId = channelId,
            forwardedImageResName = status.imageResName
        )
        statusRepository.incrementShareCount(statusId)
    }

    fun shareStatusTo(targetConversationId: String, statusId: String) {
        val status = _statusPosts.value.firstOrNull { it.id == statusId } ?: return
        val channelName = _channel.value?.name ?: ""
        val now = System.currentTimeMillis()
        val msg = Message(
            id = "msg_$now",
            conversationId = targetConversationId,
            senderId = "user_001",
            senderName = "JiayiDai",
            messageType = MessageType.FORWARDED_STATUS,
            textContent = status.textContent,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = now,
            deliveredAt = now,
            readAt = null,
            forwardedFrom = channelName,
            forwardedImageResName = status.imageResName,
            forwardedChannelId = channelId
        )
        chatRepository.addMessage(msg)
        // Increment share count in-memory
        statusRepository.incrementShareCount(statusId)
        _statusPosts.value = statusRepository.getStatusesByChannelId(channelId)
        dismissShareSheet()
    }

    fun dismissEmojiSheet() {
        emojiSheetStatusId.value = null
    }

    fun dismissShareSheet() {
        shareSheetStatusId.value = null
    }
}

class ChannelStatusViewModelFactory(
    private val channelId: String,
    private val statusRepository: StatusRepository,
    private val chatRepository: ChatRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelStatusViewModel::class.java)) {
            return ChannelStatusViewModel(channelId, statusRepository, chatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
