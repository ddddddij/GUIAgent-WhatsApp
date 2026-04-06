package com.example.whatsapp_sim.ui.screen.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NewGroupViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    data class ContactGroup(
        val letter: String,
        val contacts: List<Contact>
    )

    // Step 1 state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allContacts = MutableStateFlow(
        repository.getAllContacts().sortedBy { it.displayName.lowercase() }
    )

    private val _selectedContactIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedContactIds: StateFlow<Set<String>> = _selectedContactIds

    val selectedContacts: StateFlow<List<Contact>> = combine(_allContacts, _selectedContactIds) { contacts, ids ->
        contacts.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val groupedContacts: StateFlow<List<ContactGroup>> = _allContacts
        .map { contacts ->
            contacts
                .groupBy { contact ->
                    val first = contact.displayName.trim().firstOrNull()?.uppercaseChar() ?: '#'
                    if (first in 'A'..'Z') first.toString() else "#"
                }
                .toSortedMap(Comparator { a, b ->
                    when {
                        a == b -> 0
                        a == "#" -> 1
                        b == "#" -> -1
                        else -> a.compareTo(b)
                    }
                })
                .map { (letter, contacts) -> ContactGroup(letter, contacts) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // The contact id to scroll to (first fuzzy match), null when query is blank
    val scrollTargetContactId: StateFlow<String?> = combine(_allContacts, _searchQuery) { contacts, query ->
        if (query.isBlank()) return@combine null
        val q = query.trim().lowercase()
        contacts.firstOrNull { it.displayName.lowercase().contains(q) || it.phone.contains(q) }?.id
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Step 2 state
    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String> = _groupName

    private val _disappearingMessages = MutableStateFlow("Off")
    val disappearingMessages: StateFlow<String> = _disappearingMessages

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onContactToggled(contactId: String) {
        _selectedContactIds.update { ids ->
            if (contactId in ids) ids - contactId else ids + contactId
        }
    }

    fun onGroupNameChanged(name: String) {
        _groupName.value = name
    }

    fun onDisappearingMessagesChanged(value: String) {
        _disappearingMessages.value = value
    }

    fun createGroup(): String {
        val contacts = _allContacts.value.filter { it.id in _selectedContactIds.value }
        val memberIds = contacts.map { it.id }
        val memberNames = contacts.map { it.displayName }
        val name = _groupName.value.trim().ifBlank { "Group" }
        return repository.createGroupConversation(name, memberIds, memberNames)
    }

    fun refreshContacts() {
        _allContacts.value = repository.getAllContacts().sortedBy { it.displayName.lowercase() }
    }

    fun reset() {
        _searchQuery.value = ""
        _selectedContactIds.value = emptySet()
        _groupName.value = ""
        _disappearingMessages.value = "Off"
    }
}
