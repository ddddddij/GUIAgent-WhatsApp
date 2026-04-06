package com.example.whatsapp_sim.ui.screen.calls

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.CallWithContact
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallsViewModel(
    private val callRepository: CallRepository,
    contactStore: RuntimeContactStore
) : ViewModel() {

    private val _recentCalls = MutableStateFlow<List<CallWithContact>>(emptyList())
    val recentCalls: StateFlow<List<CallWithContact>> = _recentCalls

    private val _showNewCallSheet = MutableStateFlow(false)
    val showNewCallSheet: StateFlow<Boolean> = _showNewCallSheet.asStateFlow()

    val newCallViewModel = NewCallViewModel(contactStore)

    init {
        _recentCalls.value = callRepository.getRecentCalls()
    }

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
