package com.example.whatsapp_sim.ui.screen.broadcast

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.BroadcastStore
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.domain.model.BroadcastList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BroadcastListViewModel(
    private val contactStore: RuntimeContactStore,
    val chatRepository: ChatRepositoryImpl
) : ViewModel() {

    private val _lists = MutableStateFlow<List<BroadcastList>>(emptyList())
    val lists: StateFlow<List<BroadcastList>> = _lists.asStateFlow()

    private val _showNewBroadcastSheet = MutableStateFlow(false)
    val showNewBroadcastSheet: StateFlow<Boolean> = _showNewBroadcastSheet.asStateFlow()

    val newBroadcastViewModel = NewBroadcastViewModel(contactStore, chatRepository)

    init {
        refresh()
    }

    fun refresh() {
        _lists.value = BroadcastStore.lists
    }

    fun onNewBroadcastClick() {
        _showNewBroadcastSheet.value = true
    }

    fun onNewBroadcastSheetDismiss() {
        _showNewBroadcastSheet.value = false
    }

    fun onBroadcastCreated(broadcastList: BroadcastList) {
        _showNewBroadcastSheet.value = false
        refresh()
    }
}
