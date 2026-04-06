package com.example.whatsapp_sim.ui.screen.communities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.repository.CommunityChannelStore
import com.example.whatsapp_sim.data.repository.CommunityChannelType
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CommunityChannelDetailViewModel(
    val community: Community,
    val channelType: CommunityChannelType
) : ViewModel() {

    val messages: StateFlow<List<Message>> = CommunityChannelStore.changeVersion
        .map {
            CommunityChannelStore.getMessages(community.id, channelType)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityChannelStore.getMessages(community.id, channelType)
        )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    val showSendButton: StateFlow<Boolean> = _inputText
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val title: String
        get() = when (channelType) {
            CommunityChannelType.GENERAL -> "General"
            CommunityChannelType.ANNOUNCEMENTS -> "Announcements"
        }

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val content = _inputText.value.trim()
        if (content.isEmpty()) {
            return
        }

        CommunityChannelStore.sendMessage(
            communityId = community.id,
            channelType = channelType,
            content = content
        )
        _inputText.value = ""
    }

    fun onVideoCallClick() = Unit

    fun onVoiceCallClick() = Unit

    fun onAddAttachmentClick() = Unit

    fun onCameraClick() = Unit

    fun onMicClick() = Unit

    fun onLearnMoreClick() = Unit
}

class CommunityChannelDetailViewModelFactory(
    private val community: Community,
    private val channelType: CommunityChannelType
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityChannelDetailViewModel::class.java)) {
            return CommunityChannelDetailViewModel(community, channelType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
