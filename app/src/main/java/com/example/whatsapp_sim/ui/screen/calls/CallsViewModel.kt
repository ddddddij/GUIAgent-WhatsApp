package com.example.whatsapp_sim.ui.screen.calls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.CallWithContact
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CallsViewModel(
    private val callRepository: CallRepository,
    contactStore: RuntimeContactStore
) : ViewModel() {

    val recentCalls: StateFlow<List<CallWithContact>> = callRepository.calls

    private val _showNewCallSheet = MutableStateFlow(false)
    val showNewCallSheet: StateFlow<Boolean> = _showNewCallSheet.asStateFlow()

    val newCallViewModel = NewCallViewModel(contactStore)

    fun onNewCallClick() {
        _showNewCallSheet.value = true
    }

    fun onNewCallSheetDismiss() {
        _showNewCallSheet.value = false
        newCallViewModel.onCancelClick()
    }

    fun onMoreMenuClick() { /* reserved */ }
    fun onSearchClick() { /* reserved */ }
    fun onAddFavoriteClick() { /* reserved */ }
    fun onCallItemClick(callId: String) { /* reserved */ }
    fun onCallDetailClick(callId: String) { /* reserved */ }
}
