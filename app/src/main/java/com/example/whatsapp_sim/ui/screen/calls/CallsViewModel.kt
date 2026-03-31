package com.example.whatsapp_sim.ui.screen.calls

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.CallWithContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallsViewModel(private val callRepository: CallRepository) : ViewModel() {

    private val _recentCalls = MutableStateFlow<List<CallWithContact>>(emptyList())
    val recentCalls: StateFlow<List<CallWithContact>> = _recentCalls

    init {
        _recentCalls.value = callRepository.getRecentCalls()
    }

    fun onMoreMenuClick() { /* reserved */ }
    fun onNewCallClick() { /* reserved */ }
    fun onSearchClick() { /* reserved */ }
    fun onAddFavoriteClick() { /* reserved */ }
    fun onCallItemClick(callId: String) { /* reserved */ }
    fun onCallDetailClick(callId: String) { /* reserved */ }
}
