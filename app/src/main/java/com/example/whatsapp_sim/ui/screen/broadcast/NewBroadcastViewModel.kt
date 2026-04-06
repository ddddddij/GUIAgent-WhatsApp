package com.example.whatsapp_sim.ui.screen.broadcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.repository.BroadcastStore
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.domain.model.BroadcastList
import com.example.whatsapp_sim.domain.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class BroadcastContactGroup(
    val letter: String,
    val contacts: List<Contact>
)

class NewBroadcastViewModel(
    private val contactStore: RuntimeContactStore,
    private val chatRepository: ChatRepositoryImpl
) : ViewModel() {

    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedContactIds = MutableStateFlow<Set<String>>(emptySet())

    val selectedContacts: StateFlow<List<Contact>> = combine(_allContacts, selectedContactIds) { contacts, ids ->
        contacts.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isCreateEnabled: StateFlow<Boolean> = selectedContactIds
        .map { it.size >= 2 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val groupedContacts: StateFlow<List<BroadcastContactGroup>> = _allContacts
        .map { contacts ->
            contacts.filter { it.isAppUser }
                .sortedWith(Comparator { a, b ->
                    val aL = letterOf(a.displayName); val bL = letterOf(b.displayName)
                    if (aL == "#" && bL != "#") return@Comparator 1
                    if (aL != "#" && bL == "#") return@Comparator -1
                    a.displayName.compareTo(b.displayName, ignoreCase = true)
                })
                .groupBy { letterOf(it.displayName) }
                .map { (letter, contacts) -> BroadcastContactGroup(letter, contacts) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val scrollTargetContactId: StateFlow<String?> = combine(_allContacts, searchQuery) { contacts, query ->
        if (query.isBlank()) return@combine null
        val q = query.trim().lowercase()
        contacts.filter { it.isAppUser }
            .firstOrNull { it.displayName.lowercase().contains(q) || it.phone.contains(q) }
            ?.id
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        refreshContacts()
    }

    fun refreshContacts() {
        _allContacts.value = contactStore.getAllContacts()
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
        reset()
    }

    fun createBroadcastList(): BroadcastList {
        val ids = selectedContactIds.value
        val selected = _allContacts.value.filter { it.id in ids }
        return BroadcastStore.createBroadcastList(
            memberIds = selected.map { it.id },
            memberNames = selected.map { it.displayName }
        )
    }

    fun reset() {
        selectedContactIds.value = emptySet()
        searchQuery.value = ""
    }

    private fun letterOf(name: String): String {
        val first = name.firstOrNull() ?: return "#"
        return if (first.isLetter() && first.code < 128) first.uppercaseChar().toString() else "#"
    }
}
