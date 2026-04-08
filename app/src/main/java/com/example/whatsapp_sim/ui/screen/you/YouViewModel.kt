package com.example.whatsapp_sim.ui.screen.you

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class YouMenuItem {
    INVITE_FRIEND, LISTS, BROADCAST, STARRED, LINKED_DEVICES,
    ACCOUNT, PRIVACY, CHATS, NOTIFICATIONS, STORAGE_AND_DATA
}

class YouViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<Account?>(null)
    val currentUser: StateFlow<Account?> = _currentUser.asStateFlow()

    init {
        refreshCurrentUser()
    }

    fun refreshCurrentUser() {
        _currentUser.value = repository.getCurrentUser()
    }

    fun onSearchClick() { /* Coming soon */ }
    fun onQrCodeClick() { /* Coming soon */ }
    fun onAvatarClick() { /* Coming soon */ }
    fun onStatusBubbleClick() { /* Coming soon */ }
    fun onMenuItemClick(item: YouMenuItem) { /* Coming soon */ }
}
