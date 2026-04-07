package com.example.whatsapp_sim.ui.screen.calls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.domain.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class ContactGroup(
    val letter: String,
    val contacts: List<Contact>
)

data class InviteContact(
    val name: String,
    val phoneNumber: String
)

class NewCallViewModel(
    private val contactStore: RuntimeContactStore
) : ViewModel() {

    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedContactIds = MutableStateFlow<Set<String>>(emptySet())

    val totalCount: StateFlow<Int> = _allContacts
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val selectedCount: StateFlow<Int> = selectedContactIds
        .combine(_allContacts) { ids, _ -> ids.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // "Start a call" section: first 2 app-user contacts, always shown
    val startCallContacts: StateFlow<List<Contact>> = _allContacts
        .map { contacts -> contacts.filter { it.isAppUser }.take(2) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // All app-user contacts grouped by first letter, never filtered
    val groupedContacts: StateFlow<List<ContactGroup>> = _allContacts
        .map { contacts ->
            val appUsers = contacts.filter { it.isAppUser }
            appUsers
                .sortedWith(Comparator { a, b ->
                    val aLetter = letterOf(a.displayName)
                    val bLetter = letterOf(b.displayName)
                    if (aLetter == "#" && bLetter != "#") return@Comparator -1
                    if (aLetter != "#" && bLetter == "#") return@Comparator 1
                    a.displayName.compareTo(b.displayName, ignoreCase = true)
                })
                .groupBy { letterOf(it.displayName) }
                .map { (letter, contacts) -> ContactGroup(letter, contacts) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // The contact id to scroll to (first fuzzy match among appUsers), null when query is blank
    val scrollTargetContactId: StateFlow<String?> = _allContacts
        .combine(searchQuery) { contacts, query ->
            if (query.isBlank()) return@combine null
            val q = query.trim().lowercase()
            contacts.filter { it.isAppUser }
                .sortedBy { it.displayName.lowercase() }
                .firstOrNull { it.displayName.lowercase().contains(q) || it.phone.contains(q) }
                ?.id
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Invite contacts: non-app-user contacts, always shown
    val inviteContacts: StateFlow<List<InviteContact>> = _allContacts
        .map { contacts ->
            contacts.filter { !it.isAppUser }
                .map { InviteContact(it.displayName, it.phone) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refreshContacts()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onContactToggled(contactId: String) {
        selectedContactIds.update { ids ->
            if (contactId in ids) ids - contactId else ids + contactId
        }
    }

    fun onCancelClick() {
        selectedContactIds.value = emptySet()
        searchQuery.value = ""
    }

    fun refreshContacts() {
        _allContacts.value = contactStore.getAllContacts()
    }

    fun onNewCallLinkClick() { /* reserved */ }
    fun onCallANumberClick() { /* reserved */ }
    fun onNewContactClick() { /* reserved */ }
    fun onScheduleCallClick() { /* reserved */ }
    fun onInviteClick(contact: InviteContact) { /* reserved */ }

    /** Returns currently selected contacts in order */
    fun getSelectedContacts(): List<Contact> {
        val ids = selectedContactIds.value
        return _allContacts.value.filter { it.id in ids }
    }

    private fun letterOf(name: String): String {
        val first = name.firstOrNull() ?: return "#"
        return if (first.isLetter() && first.code < 128) first.uppercaseChar().toString() else "#"
    }
}
