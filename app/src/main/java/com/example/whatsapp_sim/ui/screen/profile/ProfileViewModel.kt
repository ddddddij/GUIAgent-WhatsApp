package com.example.whatsapp_sim.ui.screen.profile

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(private val repository: AccountRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<Account?>(null)
    val currentUser: StateFlow<Account?> = _currentUser.asStateFlow()

    private val _showAboutSheet = MutableStateFlow(false)
    val showAboutSheet: StateFlow<Boolean> = _showAboutSheet.asStateFlow()

    init {
        _currentUser.value = repository.getCurrentUser()
    }

    fun showAboutSheet() { _showAboutSheet.value = true }
    fun hideAboutSheet() { _showAboutSheet.value = false }

    fun updateAbout(newAbout: String) {
        _currentUser.value = repository.updateCurrentUserAbout(newAbout)
        _showAboutSheet.value = false
    }
}
