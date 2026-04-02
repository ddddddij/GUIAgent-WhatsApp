package com.example.whatsapp_sim.ui.screen.contactinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ContactInfoItem {
    MEDIA_LINKS_DOCS, STARRED,
    NOTIFICATIONS, CHAT_THEME, SAVE_TO_PHOTOS,
    DISAPPEARING_MESSAGES, ADVANCED_PRIVACY, ENCRYPTION,
    CONTACT_DETAILS, CREATE_GROUP,
    SHARE_CONTACT, ADD_TO_FAVORITES, ADD_TO_LIST, EXPORT_CHAT, CLEAR_CHAT
}

class ContactInfoViewModel(
    private val contactId: String,
    private val repository: ChatRepository
) : ViewModel() {

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    init {
        _contact.value = repository.getAllContacts().firstOrNull { it.id == contactId }
    }

    fun toggleLock() {
        _isLocked.value = !_isLocked.value
    }

    /** Returns conversationId to navigate to ChatDetailActivity */
    fun onMessageClick(): String {
        val contact = _contact.value ?: return ""
        return repository.findOrCreateConversationForContact(contact)
    }

    fun onAudioClick() { /* reserved */ }
    fun onVideoClick() { /* reserved */ }
    fun onSettingItemClick(item: ContactInfoItem) { /* reserved */ }
    fun onBlockClick() { /* reserved */ }
    fun onReportClick() { /* reserved */ }
}

class ContactInfoViewModelFactory(
    private val contactId: String,
    private val repository: ChatRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactInfoViewModel::class.java)) {
            return ContactInfoViewModel(contactId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
