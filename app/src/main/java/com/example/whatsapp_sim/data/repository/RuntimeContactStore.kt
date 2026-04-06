package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.ContactStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class RuntimeContactStore private constructor(
    initialContacts: List<Contact>
) {
    private val _contacts = MutableStateFlow(initialContacts)
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    fun getAllContacts(): List<Contact> = contacts.value

    fun addContact(
        displayName: String,
        phone: String,
        isAppUser: Boolean = true
    ): Contact {
        val now = System.currentTimeMillis()
        val contact = Contact(
            id = "contact_local_${UUID.randomUUID()}",
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
        return contact
    }

    companion object {
        @Volatile
        private var instance: RuntimeContactStore? = null

        fun getInstance(assetsHelper: AssetsHelper): RuntimeContactStore {
            return instance ?: synchronized(this) {
                instance ?: RuntimeContactStore(
                    initialContacts = assetsHelper.loadContacts()
                ).also { instance = it }
            }
        }
    }
}
