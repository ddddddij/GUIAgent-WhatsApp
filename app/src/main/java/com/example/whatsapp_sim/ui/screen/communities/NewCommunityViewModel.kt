package com.example.whatsapp_sim.ui.screen.communities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DEFAULT_DESCRIPTION =
    "Hi everyone! This community is for members to chat in topic-based groups and get important announcements."

class NewCommunityViewModel : ViewModel() {

    val communityName: MutableStateFlow<String> = MutableStateFlow("")
    val communityDescription: MutableStateFlow<String> = MutableStateFlow(DEFAULT_DESCRIPTION)

    val isCreateEnabled: StateFlow<Boolean> = communityName
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onNameChanged(name: String) {
        communityName.value = name
    }

    fun onDescriptionChanged(desc: String) {
        communityDescription.value = desc
    }

    fun onCancelClick() {
        reset()
    }

    fun onSeeExamplesClick() { /* reserved */ }

    fun onAddPhotoClick() { /* reserved */ }

    fun onRefreshPhotoClick() { /* reserved */ }

    fun reset() {
        communityName.value = ""
        communityDescription.value = DEFAULT_DESCRIPTION
    }
}
