package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Call
import com.example.whatsapp_sim.domain.model.Contact
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CallWithContact(
    val call: Call,
    val contactName: String,
    val contactId: String?,
    val contactAvatarRes: Int?
)

class CallRepository(
    private val assetsHelper: AssetsHelper,
    private val contactStore: RuntimeContactStore = RuntimeContactStore.getInstance(assetsHelper)
) {

    private val currentUserId = "user_001"

    fun getRecentCalls(): List<CallWithContact> {
        val calls = assetsHelper.loadCalls()
        val accounts = assetsHelper.loadAccounts()
        val contacts = contactStore.getAllContacts()
        val accountMap = accounts.associateBy { it.id }

        return calls.map { call ->
            val otherPartyId = if (call.callerId == currentUserId) call.calleeId else call.callerId
            val account = accountMap[otherPartyId]
            val contactName = account?.displayName ?: "Unknown"
            val contactId = contacts.firstOrNull { it.displayName == contactName }?.id
            CallWithContact(
                call = call,
                contactName = contactName,
                contactId = contactId,
                contactAvatarRes = null
            )
        }
    }

    companion object {
        fun formatTimestamp(millis: Long): String {
            val callCal = Calendar.getInstance().apply { timeInMillis = millis }
            val nowCal = Calendar.getInstance()
            val callDay = callCal.get(Calendar.DAY_OF_YEAR)
            val nowDay = nowCal.get(Calendar.DAY_OF_YEAR)
            val callYear = callCal.get(Calendar.YEAR)
            val nowYear = nowCal.get(Calendar.YEAR)
            return when {
                callYear == nowYear && callDay == nowDay ->
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
                callYear == nowYear && callDay == nowDay - 1 ->
                    "Yesterday"
                else ->
                    SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(millis))
            }
        }
    }
}
