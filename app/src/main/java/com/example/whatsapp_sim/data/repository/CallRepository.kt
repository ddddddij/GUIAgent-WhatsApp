package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CallWithContact(
    val call: Call,
    val contactName: String,
    val contactId: String?,
    val contactAvatarRes: Int?,
    val contactAvatarUrl: String? = null
)

class CallRepository private constructor(
    private val assetsHelper: AssetsHelper,
    private val contactStore: RuntimeContactStore = RuntimeContactStore.getInstance(assetsHelper)
) {

    private val _calls = MutableStateFlow<List<CallWithContact>>(emptyList())
    val calls: StateFlow<List<CallWithContact>> = _calls.asStateFlow()
    private val rawCalls = mutableListOf<Call>()

    init {
        loadCalls()
    }

    private fun loadCalls() {
        rawCalls.clear()
        rawCalls.addAll(assetsHelper.loadCalls())
        val accounts = assetsHelper.loadAccounts()
        val contacts = contactStore.getAllContacts()
        val accountMap = accounts.associateBy { it.id }

        val callsWithContact = rawCalls.map { call ->
            val contactId = call.contactIds.firstOrNull()
            val account = contactId?.let { accountMap[it] }
            val contactName = account?.displayName ?: "Unknown"
            val contact = contacts.firstOrNull { it.displayName == contactName }
            CallWithContact(
                call = call,
                contactName = contactName,
                contactId = contact?.id,
                contactAvatarRes = null,
                contactAvatarUrl = contact?.avatarUrl
            )
        }
        _calls.value = callsWithContact
    }

    fun getRecentCalls(): List<CallWithContact> = _calls.value

    fun addCall(call: Call, displayName: String, displayContactId: String?, displayAvatarUrl: String?) {
        rawCalls.add(0, call)
        assetsHelper.saveCalls(rawCalls)
        val callWithContact = CallWithContact(
            call = call,
            contactName = displayName,
            contactId = displayContactId,
            contactAvatarRes = null,
            contactAvatarUrl = displayAvatarUrl
        )
        _calls.value = listOf(callWithContact) + _calls.value
    }

    fun updateCall(call: Call) {
        val rawIndex = rawCalls.indexOfFirst { it.id == call.id }
        if (rawIndex == -1) return

        rawCalls[rawIndex] = call
        assetsHelper.saveCalls(rawCalls)
        _calls.value = _calls.value.map { callWithContact ->
            if (callWithContact.call.id == call.id) {
                callWithContact.copy(call = call)
            } else {
                callWithContact
            }
        }
    }

    companion object {
        @Volatile
        private var instance: CallRepository? = null

        fun getInstance(assetsHelper: AssetsHelper): CallRepository {
            return instance ?: synchronized(this) {
                instance ?: CallRepository(assetsHelper).also { instance = it }
            }
        }
    }
}
