package com.example.whatsapp_sim.ui.screen.chatdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val CURRENT_USER_ID = "user_001"
private const val CURRENT_USER_NAME = "Alex Johnson"

class ChatDetailViewModel(
    private val repository: ChatRepository,
    private val conversationId: String
) : ViewModel() {

    private val _conversation = MutableStateFlow(repository.getConversation(conversationId))
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()

    private val _contact = MutableStateFlow(repository.getContactForConversation(conversationId))
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    private val _messages = MutableStateFlow(repository.getMessages(conversationId))
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: MutableStateFlow<String> = _inputText

    val showSendButton: StateFlow<Boolean> = _inputText
        .map { it.isNotBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val content = _inputText.value.trim()
        if (content.isEmpty()) {
            return
        }

        repository.sendMessage(conversationId, content)
        _messages.value = repository.getMessages(conversationId)
        _conversation.value = repository.getConversation(conversationId)
        _inputText.value = ""
    }

    fun onVideoCallClick() = Unit

    fun onVoiceCallClick() = Unit

    fun onAddAttachmentClick() = Unit

    fun onCameraClick() = Unit

    fun onMicClick() = Unit

    fun onLearnMoreClick() = Unit
}

class ChatDetailViewModelFactory(
    private val repository: ChatRepository,
    private val conversationId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(repository, conversationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
