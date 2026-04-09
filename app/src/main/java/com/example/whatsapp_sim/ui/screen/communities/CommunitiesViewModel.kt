package com.example.whatsapp_sim.ui.screen.communities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.CommunityChannelStore
import com.example.whatsapp_sim.data.repository.CommunityChannelType
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.domain.model.Community
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CommunitySectionUiModel(
    val community: Community,
    val announcementsPreview: String,
    val announcementsTime: String,
    val generalPreview: String,
    val generalTime: String
)

class CommunitiesViewModel(
    private val communityRepository: CommunityRepository,
    private val assetsHelper: AssetsHelper
) : ViewModel() {

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    private val _showNewCommunitySheet = MutableStateFlow(false)
    val showNewCommunitySheet: StateFlow<Boolean> = _showNewCommunitySheet.asStateFlow()

    val communitySections: StateFlow<List<CommunitySectionUiModel>> = combine(
        _communities,
        CommunityChannelStore.changeVersion
    ) { communities, _ ->
        communities.map { community ->
            val announcementsMessage = CommunityChannelStore.getLatestMessage(
                communityId = community.id,
                channelType = CommunityChannelType.ANNOUNCEMENTS
            )
            val generalMessage = CommunityChannelStore.getLatestMessage(
                communityId = community.id,
                channelType = CommunityChannelType.GENERAL
            )

            CommunitySectionUiModel(
                community = community,
                announcementsPreview = announcementsMessage?.textContent ?: "Ask members to start chatting",
                announcementsTime = announcementsMessage?.sentAt?.let(::formatCommunityPreviewTime).orEmpty(),
                generalPreview = generalMessage?.textContent ?: "Ask members to start chatting",
                generalTime = generalMessage?.sentAt?.let(::formatCommunityPreviewTime).orEmpty()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val newCommunityViewModel = NewCommunityViewModel()

    init {
        _communities.value = communityRepository.getCommunities()
        CommunityChannelStore.initialize(assetsHelper, _communities.value)
    }

    fun onNewCommunityClick() {
        _showNewCommunitySheet.value = true
    }

    fun onNewCommunitySheetDismiss() {
        _showNewCommunitySheet.value = false
    }

    fun addCommunity(community: Community) {
        communityRepository.addCommunity(community)
        CommunityChannelStore.initialize(assetsHelper, listOf(community))
        _communities.value = communityRepository.getCommunities()
    }

    fun onMoreMenuClick() { /* reserved */ }
    fun onSeeAllClick(communityId: String) { /* reserved */ }
    fun onAnnouncementsClick(communityId: String) { /* reserved */ }
    fun onGeneralClick(communityId: String) { /* reserved */ }

    private fun formatCommunityPreviewTime(timestamp: Long): String {
        val date = Date(timestamp)
        val msgCal = Calendar.getInstance().apply { time = date }
        val nowCal = Calendar.getInstance()

        val msgYear = msgCal.get(Calendar.YEAR)
        val msgDoy = msgCal.get(Calendar.DAY_OF_YEAR)
        val nowYear = nowCal.get(Calendar.YEAR)
        val nowDoy = nowCal.get(Calendar.DAY_OF_YEAR)

        return when {
            msgYear == nowYear && msgDoy == nowDoy -> {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
            msgYear == nowYear && nowDoy - msgDoy == 1 -> {
                "Yesterday"
            }
            msgYear == nowYear && nowDoy - msgDoy < 7 -> {
                SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
            }
            else -> {
                SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
            }
        }
    }
}
