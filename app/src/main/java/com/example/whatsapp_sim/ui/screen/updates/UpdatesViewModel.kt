package com.example.whatsapp_sim.ui.screen.updates

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.ChannelRepository
import com.example.whatsapp_sim.domain.model.Channel
import com.example.whatsapp_sim.domain.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpdatesViewModel : ViewModel() {

    private val repository = ChannelRepository()

    private val _isChannelSectionExpanded = MutableStateFlow(true)
    val isChannelSectionExpanded: StateFlow<Boolean> = _isChannelSectionExpanded.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _followingState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val followingState: StateFlow<Map<String, Boolean>> = _followingState.asStateFlow()

    // My own status that I posted
    val myStatus: UserStatus = UserStatus(
        id = "my_status_001",
        senderName = "Alex Johnson",
        preview = "Living the moment 🌅",
        timeLabel = "10:32 AM",
        isViewed = false
    )

    // Recent statuses from contacts
    val recentStatuses: List<UserStatus> = listOf(
        UserStatus(
            id = "status_002",
            senderName = "Sophia Chen",
            preview = "Weekend vibes ☀️",
            timeLabel = "9:15 AM",
            isViewed = false
        ),
        UserStatus(
            id = "status_003",
            senderName = "Marcus Johnson",
            preview = "Coffee time ☕",
            timeLabel = "8:47 AM",
            isViewed = true
        )
    )

    init {
        val channelList = repository.getChannels()
        _channels.value = channelList
        _followingState.value = channelList.associate { it.id to it.initiallyFollowing }
    }

    fun followingChannels(): List<Channel> =
        _channels.value.filter { _followingState.value[it.id] == true }

    fun toggleChannelSection() {
        _isChannelSectionExpanded.value = !_isChannelSectionExpanded.value
    }

    fun toggleFollow(channelId: String) {
        val current = _followingState.value.toMutableMap()
        current[channelId] = !(current[channelId] ?: false)
        _followingState.value = current
    }

    fun onMoreMenuClick() { /* Coming soon */ }
    fun onAddStatusCameraClick() { /* Coming soon */ }
    fun onAddStatusEditClick() { /* Coming soon */ }
    fun onAddStatusClick() { /* Coming soon */ }
    fun onExploreMoreClick() { /* Coming soon */ }
}

