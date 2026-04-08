package com.example.whatsapp_sim.ui.screen.groupinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.GroupDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GroupInfoItem {
    MEDIA_LINKS_DOCS, STARRED,
    NOTIFICATIONS, CHAT_THEME, SAVE_TO_PHOTOS,
    DISAPPEARING_MESSAGES, GROUP_PERMISSIONS, ADVANCED_PRIVACY, ENCRYPTION,
    ADD_TO_COMMUNITY, INVITE_VIA_LINK,
    ADD_TO_FAVORITES, ADD_TO_LIST, EXPORT_CHAT, CLEAR_CHAT,
    REPORT_GROUP
}

data class GroupInfoMemberUiModel(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val about: String?,
    val isAdmin: Boolean,
    val isSelf: Boolean,
    val contactId: String?
)

class GroupInfoViewModel(
    private val conversationId: String,
    private val repository: ChatRepositoryImpl
) : ViewModel() {

    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()

    private val _groupDetail = MutableStateFlow<GroupDetail?>(null)
    val groupDetail: StateFlow<GroupDetail?> = _groupDetail.asStateFlow()

    private val _members = MutableStateFlow<List<GroupInfoMemberUiModel>>(emptyList())
    val members: StateFlow<List<GroupInfoMemberUiModel>> = _members.asStateFlow()

    private val _selfAccount = MutableStateFlow<Account?>(null)
    val selfAccount: StateFlow<Account?> = _selfAccount.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _showAddMemberSheet = MutableStateFlow(false)
    val showAddMemberSheet: StateFlow<Boolean> = _showAddMemberSheet.asStateFlow()

    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog.asStateFlow()

    // All contacts for AddMemberSheet
    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())
    val allContacts: StateFlow<List<Contact>> = _allContacts.asStateFlow()

    private val _existingMemberIds = MutableStateFlow<List<String>>(emptyList())
    val existingMemberIds: StateFlow<List<String>> = _existingMemberIds.asStateFlow()

    init {
        _selfAccount.value = repository.getCurrentUserAccount()
        _allContacts.value = repository.getAllContacts()
        refreshState()
    }

    private fun refreshState() {
        val conversation = repository.getConversation(conversationId)
        val detail = repository.getGroupDetail(conversationId)
        _conversation.value = conversation
        _groupDetail.value = detail
        _isMuted.value = detail?.isMuted ?: false
        _isLocked.value = detail?.isLocked ?: false

        val memberIds = buildList {
            add("user_001")
            addAll(detail?.memberIds.orEmpty())
        }.distinct()
        _existingMemberIds.value = memberIds
        _members.value = memberIds.map { memberId ->
            val contact = repository.findContactByParticipantId(memberId)
            GroupInfoMemberUiModel(
                userId = memberId,
                displayName = repository.getParticipantDisplayName(memberId),
                avatarUrl = repository.getParticipantAvatarUrl(memberId),
                about = repository.getParticipantAbout(memberId),
                isAdmin = detail?.adminIds?.contains(memberId) == true,
                isSelf = memberId == "user_001",
                contactId = contact?.id
            )
        }
    }

    fun toggleMute() {
        repository.setGroupMuted(conversationId, !_isMuted.value)
        refreshState()
    }

    fun toggleLock() {
        repository.setGroupLocked(conversationId, !_isLocked.value)
        refreshState()
    }

    fun onAddClick() { _showAddMemberSheet.value = true }
    fun dismissAddMemberSheet() { _showAddMemberSheet.value = false }

    fun resolveMemberId(contact: Contact): String {
        return repository.resolveParticipantIdForContact(contact)
    }

    fun onAddMember(contact: Contact) {
        repository.addGroupMember(conversationId, contact)
        refreshState()
        _showAddMemberSheet.value = false
    }

    fun onExitGroupClick() { _showExitDialog.value = true }
    fun dismissExitDialog() { _showExitDialog.value = false }

    fun confirmExitGroup() {
        repository.removeConversation(conversationId)
        _showExitDialog.value = false
    }

    fun onSettingItemClick(item: GroupInfoItem) { /* Coming soon */ }
}

class GroupInfoViewModelFactory(
    private val conversationId: String,
    private val repository: ChatRepositoryImpl
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        GroupInfoViewModel(conversationId, repository) as T
}
