package com.example.whatsapp_sim.ui.screen.communityinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.MembershipStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CommunityInfoMemberUiModel(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val about: String?,
    val roleLabel: String?,
    val isSelf: Boolean,
    val contactId: String?
)

class CommunityInfoViewModel(
    private val communityId: String,
    private val repository: CommunityRepository
) : ViewModel() {

    private val _community = MutableStateFlow<Community?>(null)
    val community: StateFlow<Community?> = _community.asStateFlow()

    private val _members = MutableStateFlow<List<CommunityInfoMemberUiModel>>(emptyList())
    val members: StateFlow<List<CommunityInfoMemberUiModel>> = _members.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _showAddMemberSheet = MutableStateFlow(false)
    val showAddMemberSheet: StateFlow<Boolean> = _showAddMemberSheet.asStateFlow()

    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())
    val allContacts: StateFlow<List<Contact>> = _allContacts.asStateFlow()

    private val _existingMemberIds = MutableStateFlow<List<String>>(emptyList())
    val existingMemberIds: StateFlow<List<String>> = _existingMemberIds.asStateFlow()

    init {
        _allContacts.value = repository.getAllContacts()
        refreshState()
    }

    private fun refreshState() {
        val community = repository.getCommunity(communityId)
        _community.value = community
        _isMuted.value = community?.isMuted ?: false

        val joinedMembers = community?.members
            ?.filter { it.membershipStatus == MembershipStatus.JOINED }
            .orEmpty()
        _existingMemberIds.value = joinedMembers.map { it.userId }
        _members.value = joinedMembers.map { member ->
            val contact = repository.findContactByUserId(member.userId)
            CommunityInfoMemberUiModel(
                userId = member.userId,
                displayName = repository.getDisplayName(member.userId),
                avatarUrl = repository.getAvatarUrl(member.userId),
                about = repository.getAbout(member.userId),
                roleLabel = when (member.role.name) {
                    "OWNER" -> "Owner"
                    "ADMIN" -> "Admin"
                    else -> null
                },
                isSelf = member.userId == repository.getCurrentUserId(),
                contactId = contact?.id
            )
        }
    }

    fun toggleMute() {
        repository.setMute(communityId, !_isMuted.value)
        refreshState()
    }

    fun onAddClick() {
        _showAddMemberSheet.value = true
    }

    fun dismissAddMemberSheet() {
        _showAddMemberSheet.value = false
    }

    fun onAddMember(contact: Contact) {
        repository.addMember(communityId, contact)
        refreshState()
        _showAddMemberSheet.value = false
    }

    fun resolveMemberId(contact: Contact): String {
        return repository.resolveUserId(contact)
    }
}

class CommunityInfoViewModelFactory(
    private val communityId: String,
    private val repository: CommunityRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityInfoViewModel::class.java)) {
            return CommunityInfoViewModel(communityId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
