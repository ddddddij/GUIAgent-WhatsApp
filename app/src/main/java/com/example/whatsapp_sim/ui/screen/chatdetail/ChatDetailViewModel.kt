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

private val demoImageAssetPaths = listOf(
    "image/Liverpool/IMG_2021.jpg",
    "image/Madrid/IMG_2032.jpg",
    "image/Manchester/IMG_2060.jpg",
    "image/Premier/IMG_2048.jpg",
    "image/Spotify/IMG_2009.jpg"
)

class ChatDetailViewModel(
    private val repository: ChatRepository,
    private val conversationId: String
) : ViewModel() {

    private val _conversation = MutableStateFlow(repository.getConversation(conversationId))
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()

    private val _contact = MutableStateFlow(repository.getContactForConversation(conversationId))
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    /** avatarUrl for the chat (works for both 1:1 and group chats) */
    val chatAvatarUrl: String? by lazy {
        repository.getAllChats().firstOrNull { it.id == conversationId }?.avatarUrl
    }

    /** Map of sender name -> avatarUrl for group chat member avatars */
    val contactAvatarMap: Map<String, String> by lazy {
        repository.getAllContacts()
            .filter { it.avatarUrl != null }
            .associate { it.displayName to it.avatarUrl!! }
    }

    private val _messages: MutableStateFlow<List<Message>>
    val messages: StateFlow<List<Message>>

    init {
        repository.markConversationAsRead(conversationId)
        _messages = MutableStateFlow(repository.getMessages(conversationId))
        messages = _messages.asStateFlow()
    }

    private val _inputText = MutableStateFlow("")
    val inputText: MutableStateFlow<String> = _inputText

    private var nextDemoImageIndex = 0

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

    fun onAddAttachmentClick() {
        sendDemoImage(caption = null)
    }

    fun onCameraClick() {
        sendDemoImage(caption = "Shot just now")
    }

    fun onMicClick() = Unit

    fun onLearnMoreClick() = Unit

    fun refreshMessages() {
        _messages.value = repository.getMessages(conversationId)
    }

    private fun sendDemoImage(caption: String?) {
        val imagePath = demoImageAssetPaths[nextDemoImageIndex % demoImageAssetPaths.size]
        nextDemoImageIndex += 1
        repository.sendImageMessage(conversationId, imagePath, caption)
        _messages.value = repository.getMessages(conversationId)
        _conversation.value = repository.getConversation(conversationId)
    }
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
