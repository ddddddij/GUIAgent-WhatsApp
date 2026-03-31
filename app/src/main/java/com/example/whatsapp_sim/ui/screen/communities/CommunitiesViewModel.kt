package com.example.whatsapp_sim.ui.screen.communities

import androidx.lifecycle.ViewModel
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.domain.model.Community
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommunitiesViewModel(private val communityRepository: CommunityRepository) : ViewModel() {

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    init {
        _communities.value = communityRepository.getCommunities()
    }

    fun onMoreMenuClick() { /* reserved */ }
    fun onNewCommunityClick() { /* reserved */ }
    fun onSeeAllClick(communityId: String) { /* reserved */ }
    fun onAnnouncementsClick(communityId: String) { /* reserved */ }
    fun onGeneralClick(communityId: String) { /* reserved */ }
}
