package com.example.whatsapp_sim.ui.screen.chats

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.ChatFilter
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class ChatsViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _allChats = MutableStateFlow<List<Chat>>(emptyList())
    val allChats: StateFlow<List<Chat>> = _allChats.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ChatFilter.ALL)
    val selectedFilter: StateFlow<ChatFilter> = _selectedFilter.asStateFlow()

    private val _filteredChats = MutableStateFlow<List<Chat>>(emptyList())
    val filteredChats: StateFlow<List<Chat>> = _filteredChats.asStateFlow()

    private val _totalUnreadCount = MutableStateFlow(0)
    val totalUnreadCount: StateFlow<Int> = _totalUnreadCount.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        val chats = repository.getAllChats()
        _allChats.value = chats
        _totalUnreadCount.value = chats.sumOf { it.unreadCount }
        applyFilter()
    }

    fun selectFilter(filter: ChatFilter) {
        _selectedFilter.value = filter
        applyFilter()
    }

    fun refreshChats() {
        loadChats()
    }

    private fun applyFilter() {
        val chats = _allChats.value
        _filteredChats.value = when (_selectedFilter.value) {
            ChatFilter.ALL -> chats
            ChatFilter.UNREAD -> chats.filter { it.unreadCount > 0 }
            ChatFilter.GROUPS -> chats.filter { it.isGroup }
        }
    }

    fun onChatItemClick(chatId: String) {
        // Coming soon
    }

    fun onNewChatClick() {
        // Coming soon
    }

    fun onCameraClick() {
        // Coming soon
    }

    fun onMoreMenuClick() {
        // Coming soon
    }

    fun findContactByName(name: String): Contact? {
        return repository.getAllContacts().firstOrNull { it.displayName == name }
    }
}
