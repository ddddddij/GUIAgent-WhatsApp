package com.example.whatsapp_sim.ui.screen.communities

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.domain.model.Community
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunitiesViewModel(private val communityRepository: CommunityRepository) : ViewModel() {

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    private val _showNewCommunitySheet = MutableStateFlow(false)
    val showNewCommunitySheet: StateFlow<Boolean> = _showNewCommunitySheet.asStateFlow()

    val newCommunityViewModel = NewCommunityViewModel()

    init {
        _communities.value = communityRepository.getCommunities()
    }

    fun onNewCommunityClick() {
        _showNewCommunitySheet.value = true
    }

    fun onNewCommunitySheetDismiss() {
        _showNewCommunitySheet.value = false
    }

    fun addCommunity(community: Community) {
        _communities.update { listOf(community) + it }
    }

    fun onMoreMenuClick() { /* reserved */ }
    fun onSeeAllClick(communityId: String) { /* reserved */ }
    fun onAnnouncementsClick(communityId: String) { /* reserved */ }
    fun onGeneralClick(communityId: String) { /* reserved */ }
}
