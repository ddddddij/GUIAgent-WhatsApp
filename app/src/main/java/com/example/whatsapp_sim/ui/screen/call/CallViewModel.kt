package com.example.whatsapp_sim.ui.screen.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.domain.model.Call
import com.example.whatsapp_sim.domain.model.CallResult
import com.example.whatsapp_sim.domain.model.CallType
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CallViewModel(
    val contactNames: List<String>,
    val avatarUrls: List<String?>,
    private val contactIds: List<String>,
    private val conversationId: String,
    val callType: CallType,
    private val callRepository: CallRepository,
    private val chatRepository: ChatRepositoryImpl
) : ViewModel() {

    /** Display name: single name or "A, B, ..." */
    val displayName: String = if (contactNames.size <= 2) {
        contactNames.joinToString(", ")
    } else {
        "${contactNames.take(2).joinToString(", ")}..."
    }

    /** For backwards compat — single contact scenarios */
    val contactName: String get() = displayName
    val avatarUrl: String? get() = avatarUrls.firstOrNull()

    private val callId = "call_${UUID.randomUUID()}"
    private val callStartedAt = System.currentTimeMillis()
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
    private var didFinalizeCall = false

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(false)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private val _isVideoEnabled = MutableStateFlow(callType == CallType.VIDEO)
    val isVideoEnabled: StateFlow<Boolean> = _isVideoEnabled.asStateFlow()

    private val _callDurationSeconds = MutableStateFlow(0)
    val callDurationSeconds: StateFlow<Int> = _callDurationSeconds.asStateFlow()

    init {
        createUnfinishedCallRecord()
    }

    fun incrementTimer() {
        _callDurationSeconds.value += 1
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun toggleVideo() {
        _isVideoEnabled.value = !_isVideoEnabled.value
    }

    fun hangUp() {
        finalizeCall()
    }

    override fun onCleared() {
        finalizeCall()
        super.onCleared()
    }

    private fun createUnfinishedCallRecord() {
        val call = Call(
            id = callId,
            conversationId = conversationId,
            contactIds = contactIds,
            callType = callType,
            callResult = CallResult.UNFINISHED,
            timestamp = timeFormat.format(Date(callStartedAt)),
            dateLabel = "Today",
            durationSeconds = 0,
            durationDisplay = "Calling...",
            isSelf = true
        )
        callRepository.addCall(call, displayName, contactIds.firstOrNull(), avatarUrls.firstOrNull())
    }

    private fun finalizeCall() {
        if (didFinalizeCall) return
        didFinalizeCall = true

        val duration = _callDurationSeconds.value
        val endedAt = System.currentTimeMillis()

        val durationDisplay = when {
            duration == 0 -> "0 sec"
            duration < 60 -> "$duration sec"
            duration < 3600 -> "${duration / 60} min"
            else -> "${duration / 3600}h ${(duration % 3600) / 60}m"
        }

        val call = Call(
            id = callId,
            conversationId = conversationId,
            contactIds = contactIds,
            callType = callType,
            callResult = CallResult.COMPLETED,
            timestamp = timeFormat.format(Date(callStartedAt)),
            dateLabel = "Today",
            durationSeconds = duration,
            durationDisplay = durationDisplay,
            isSelf = true
        )
        callRepository.updateCall(call)

        // Resolve or create conversation for the call message
        val resolvedConversationId = if (conversationId.isNotEmpty()) {
            conversationId
        } else if (contactNames.size == 1) {
            // Single contact — find or create 1:1 conversation
            val contact = chatRepository.getAllContacts().firstOrNull { it.displayName == contactNames.first() }
            if (contact != null) {
                chatRepository.findOrCreateConversationForContact(contact)
            } else {
                "conv_call_${UUID.randomUUID()}"
            }
        } else {
            // Multi contact — create a group conversation
            chatRepository.createGroupConversation(
                groupName = displayName,
                memberIds = contactIds,
                memberNames = contactNames
            )
        }

        val messageType = if (callType == CallType.VOICE) MessageType.VOICE_CALL else MessageType.VIDEO_CALL
        val message = Message(
            id = "msg_call_${UUID.randomUUID()}",
            conversationId = resolvedConversationId,
            senderId = "user_001",
            senderName = "JiayiDai",
            messageType = messageType,
            textContent = null,
            mediaUrl = null,
            messageStatus = MessageStatus.SENT,
            sentAt = endedAt,
            deliveredAt = endedAt,
            readAt = null,
            callResult = CallResult.COMPLETED,
            callDurationDisplay = durationDisplay
        )
        chatRepository.addMessage(message)
    }
}

class CallViewModelFactory(
    private val contactNames: List<String>,
    private val avatarUrls: List<String?>,
    private val contactIds: List<String>,
    private val conversationId: String,
    private val callType: CallType,
    private val callRepository: CallRepository,
    private val chatRepository: ChatRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallViewModel(
            contactNames, avatarUrls, contactIds, conversationId,
            callType, callRepository, chatRepository
        ) as T
    }
}
