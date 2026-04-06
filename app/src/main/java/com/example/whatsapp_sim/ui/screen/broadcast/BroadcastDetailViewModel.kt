package com.example.whatsapp_sim.ui.screen.broadcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.repository.BroadcastStore
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.domain.model.BroadcastList
import com.example.whatsapp_sim.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BroadcastDetailViewModel(
    private val chatRepository: ChatRepositoryImpl,
    val broadcastList: BroadcastList
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(
        BroadcastStore.getMessages(broadcastList.id)
    )
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: MutableStateFlow<String> = _inputText

    val showSendButton: StateFlow<Boolean> = _inputText
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val content = _inputText.value.trim()
        if (content.isEmpty()) return
        BroadcastStore.sendMessage(broadcastList.id, content, chatRepository)
        _messages.value = BroadcastStore.getMessages(broadcastList.id)
        _inputText.value = ""
    }
}

class BroadcastDetailViewModelFactory(
    private val chatRepository: ChatRepositoryImpl,
    private val broadcastList: BroadcastList
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BroadcastDetailViewModel::class.java)) {
            return BroadcastDetailViewModel(chatRepository, broadcastList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
