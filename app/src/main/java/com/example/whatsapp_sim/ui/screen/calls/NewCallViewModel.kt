package com.example.whatsapp_sim.ui.screen.calls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.local.AssetsHelper
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

class NewCallViewModel(private val assetsHelper: AssetsHelper) : ViewModel() {

    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedContactIds = MutableStateFlow<Set<String>>(emptySet())

    val totalCount: StateFlow<Int> = _allContacts
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val selectedCount: StateFlow<Int> = selectedContactIds
        .combine(_allContacts) { ids, _ -> ids.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // "Start a call" section: first 2 contacts from allContacts, filtered by search
    val startCallContacts: StateFlow<List<Contact>> = _allContacts
        .combine(searchQuery) { contacts, query ->
            val top2 = contacts.filter { it.isAppUser }.take(2)
            if (query.isBlank()) top2
            else top2.filter { it.displayName.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // All app-user contacts grouped by first letter, filtered by search
    val groupedContacts: StateFlow<List<ContactGroup>> = _allContacts
        .combine(searchQuery) { contacts, query ->
            val appUsers = contacts.filter { it.isAppUser }
            val filtered = if (query.isBlank()) appUsers
            else appUsers.filter { it.displayName.contains(query, ignoreCase = true) }

            filtered
                .sortedWith(Comparator { a, b ->
                    val aLetter = letterOf(a.displayName)
                    val bLetter = letterOf(b.displayName)
                    // '#' sorts before letters
                    if (aLetter == "#" && bLetter != "#") return@Comparator -1
                    if (aLetter != "#" && bLetter == "#") return@Comparator 1
                    a.displayName.compareTo(b.displayName, ignoreCase = true)
                })
                .groupBy { letterOf(it.displayName) }
                .map { (letter, contacts) -> ContactGroup(letter, contacts) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Invite contacts: non-app-user contacts, filtered by search
    val inviteContacts: StateFlow<List<InviteContact>> = _allContacts
        .combine(searchQuery) { contacts, query ->
            val nonAppUsers = contacts.filter { !it.isAppUser }
            val filtered = if (query.isBlank()) nonAppUsers
            else nonAppUsers.filter { it.displayName.contains(query, ignoreCase = true) }
            filtered.map { InviteContact(it.displayName, it.phone) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        _allContacts.value = assetsHelper.loadContacts()
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

    fun onNewCallLinkClick() { /* reserved */ }
    fun onCallANumberClick() { /* reserved */ }
    fun onNewContactClick() { /* reserved */ }
    fun onScheduleCallClick() { /* reserved */ }
    fun onInviteClick(contact: InviteContact) { /* reserved */ }

    private fun letterOf(name: String): String {
        val first = name.firstOrNull() ?: return "#"
        return if (first.isLetter() && first.code < 128) first.uppercaseChar().toString() else "#"
    }
}
