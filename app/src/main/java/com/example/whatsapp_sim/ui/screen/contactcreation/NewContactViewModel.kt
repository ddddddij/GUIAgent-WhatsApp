package com.example.whatsapp_sim.ui.screen.contactcreation

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.domain.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ContactRegion(
    val name: String,
    val dialCode: String
)

data class NewContactUiState(
    val firstName: String = "",
    val lastName: String = "",
    val mobileNumber: String = "",
    val selectedRegion: ContactRegion = DEFAULT_REGION,
    val syncToPhone: Boolean = false
) {
    val canSubmit: Boolean
        get() = (firstName.isNotBlank() || lastName.isNotBlank()) && mobileNumber.isNotBlank()

    companion object {
        val DEFAULT_REGION = ContactRegion(name = "United States", dialCode = "+1")
    }
}

class NewContactViewModel(
    private val contactStore: RuntimeContactStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewContactUiState())
    val uiState: StateFlow<NewContactUiState> = _uiState.asStateFlow()

    val regions: List<ContactRegion> = listOf(
        NewContactUiState.DEFAULT_REGION,
        ContactRegion(name = "China", dialCode = "+86"),
        ContactRegion(name = "United Kingdom", dialCode = "+44"),
        ContactRegion(name = "Japan", dialCode = "+81")
    )

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun onMobileChanged(value: String) {
        _uiState.update { it.copy(mobileNumber = value) }
    }

    fun onRegionSelected(region: ContactRegion) {
        _uiState.update { it.copy(selectedRegion = region) }
    }

    fun onSyncToPhoneChanged(enabled: Boolean) {
        _uiState.update { it.copy(syncToPhone = enabled) }
    }

    fun createContact(): Contact? {
        val state = _uiState.value
        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()
        val mobileNumber = state.mobileNumber.trim()
        if ((firstName.isEmpty() && lastName.isEmpty()) || mobileNumber.isEmpty()) {
            return null
        }

        val displayName = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val formattedPhone = if (mobileNumber.startsWith("+")) {
            mobileNumber
        } else {
            "${state.selectedRegion.dialCode} $mobileNumber"
        }

        val contact = contactStore.addContact(
            displayName = displayName,
            phone = formattedPhone
        )
        reset()
        return contact
    }

    fun reset() {
        _uiState.value = NewContactUiState()
    }
}
