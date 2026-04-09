package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.ContactStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RuntimeContactStore private constructor(
    private val assetsHelper: AssetsHelper
) {
    private val _contacts = MutableStateFlow(assetsHelper.loadContacts())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    fun getAllContacts(): List<Contact> = contacts.value

    fun addContact(
        displayName: String,
        phone: String,
        isAppUser: Boolean = true
    ): Contact {
        val now = System.currentTimeMillis()
        val contact = Contact(
            id = generateNextContactId(),
            phone = phone,
            displayName = displayName,
            avatarUrl = null,
            isOnline = false,
            isAppUser = isAppUser,
            contactStatus = ContactStatus.SAVED,
            inviteStatus = null,
            invitedAt = null,
            createdAt = now,
            updatedAt = now
        )
        _contacts.update { contacts ->
            (contacts + contact).sortedBy { it.displayName.lowercase() }
        }
        assetsHelper.saveContacts(_contacts.value)
        return contact
    }

    private fun generateNextContactId(): String {
        val maxIndex = _contacts.value
            .mapNotNull { contact ->
                contact.id
                    .removePrefix("contact_")
                    .takeIf { contact.id.startsWith("contact_") && it.all(Char::isDigit) }
                    ?.toIntOrNull()
            }
            .maxOrNull()
            ?: 0

        return "contact_${(maxIndex + 1).toString().padStart(3, '0')}"
    }

    companion object {
        @Volatile
        private var instance: RuntimeContactStore? = null

        fun getInstance(assetsHelper: AssetsHelper): RuntimeContactStore {
            return instance ?: synchronized(this) {
                instance ?: RuntimeContactStore(assetsHelper).also { instance = it }
            }
        }
    }
}
