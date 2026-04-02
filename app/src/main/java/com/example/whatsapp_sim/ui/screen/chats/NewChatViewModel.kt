package com.example.whatsapp_sim.ui.screen.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class NewChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    data class ContactGroup(
        val letter: String,
        val contacts: List<Contact>
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: MutableStateFlow<String> = _searchQuery

    private val _allContacts = MutableStateFlow(repository.getAllContacts().sortedBy { it.displayName.lowercase() })
    val allContacts: StateFlow<List<Contact>> = _allContacts.asStateFlow()

    private val _selfAccount = MutableStateFlow(repository.getCurrentUserAccount())
    val selfAccount: StateFlow<Account?> = _selfAccount.asStateFlow()

    val frequentContacts: StateFlow<List<Contact>> = combine(_allContacts, _searchQuery) { contacts, query ->
        contacts
            .take(3)
            .filter { contact -> matchesQuery(contact, query) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val groupedContacts: StateFlow<List<ContactGroup>> = combine(_allContacts, _searchQuery) { contacts, query ->
        contacts
            .filter { contact -> matchesQuery(contact, query) }
            .groupBy(::resolveGroupLetter)
            .toSortedMap(contactGroupComparator)
            .map { (letter, groupContacts) ->
                ContactGroup(
                    letter = letter,
                    contacts = groupContacts.sortedBy { it.displayName.lowercase() }
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onContactSelected(contact: Contact): String {
        return repository.findOrCreateConversationForContact(contact)
    }

    fun onCurrentUserSelected(): String {
        return repository.findOrCreateConversationForCurrentUser()
    }

    fun onNewGroupClick() = Unit

    fun onNewContactClick() = Unit

    fun onNewCommunityClick() = Unit

    fun onNewBroadcastClick() = Unit

    private fun matchesQuery(contact: Contact, query: String): Boolean {
        if (query.isBlank()) {
            return true
        }
        val normalizedQuery = query.trim().lowercase()
        return contact.displayName.lowercase().contains(normalizedQuery) ||
            contact.phone.lowercase().contains(normalizedQuery)
    }

    private fun resolveGroupLetter(contact: Contact): String {
        val firstChar = contact.displayName.trim().firstOrNull()?.uppercaseChar() ?: '#'
        return if (firstChar in 'A'..'Z') firstChar.toString() else "#"
    }

    companion object {
        private val contactGroupComparator = Comparator<String> { first, second ->
            when {
                first == second -> 0
                first == "#" -> -1
                second == "#" -> 1
                else -> first.compareTo(second)
            }
        }
    }
}
