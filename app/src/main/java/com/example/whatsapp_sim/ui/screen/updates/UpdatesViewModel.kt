package com.example.whatsapp_sim.ui.screen.updates

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.UserStatusStore
import com.example.whatsapp_sim.data.repository.ChannelRepository
import com.example.whatsapp_sim.domain.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdatesViewModel(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _isChannelSectionExpanded = MutableStateFlow(true)
    val isChannelSectionExpanded: StateFlow<Boolean> = _isChannelSectionExpanded.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _followingState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val followingState: StateFlow<Map<String, Boolean>> = _followingState.asStateFlow()

    // Data lives in UserStatusStore (shared singleton)
    val myStatus get() = UserStatusStore.statuses.first { it.id == "my_status_001" }
    val recentStatuses get() = UserStatusStore.statuses.filter { it.id != "my_status_001" }

    init {
        refreshChannels()
    }

    fun toggleChannelSection() {
        _isChannelSectionExpanded.value = !_isChannelSectionExpanded.value
    }

    fun toggleFollow(channelId: String) {
        repository.toggleFollow(channelId)
        refreshChannels()
    }

    fun onMoreMenuClick() { /* Coming soon */ }
    fun onAddStatusClick() { /* Coming soon */ }

    private fun refreshChannels() {
        val channelList = repository.getChannels()
        _channels.value = channelList
        _followingState.value = channelList.associate { it.id to it.initiallyFollowing }
    }
}
