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
import kotlinx.coroutines.flow.map
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

    // Always show all frequent contacts (first 3), not filtered
    val frequentContacts: StateFlow<List<Contact>> = _allContacts
        .map { it.take(3) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Always show all contacts grouped, never filter them out
    val groupedContacts: StateFlow<List<ContactGroup>> = _allContacts
        .map { contacts ->
            contacts
                .groupBy(::resolveGroupLetter)
                .toSortedMap(contactGroupComparator)
                .map { (letter, groupContacts) ->
                    ContactGroup(
                        letter = letter,
                        contacts = groupContacts.sortedBy { it.displayName.lowercase() }
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // The contact id to scroll to (first fuzzy match), null when query is blank
    val scrollTargetContactId: StateFlow<String?> = combine(_allContacts, _searchQuery) { contacts, query ->
        if (query.isBlank()) return@combine null
        val q = query.trim().lowercase()
        contacts.firstOrNull { contact ->
            contact.displayName.lowercase().contains(q) || contact.phone.lowercase().contains(q)
        }?.id
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
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

    fun refreshContacts() {
        _allContacts.value = repository.getAllContacts().sortedBy { it.displayName.lowercase() }
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
