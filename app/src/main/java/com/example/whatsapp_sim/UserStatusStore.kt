package com.example.whatsapp_sim

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.UserStatus
import com.example.whatsapp_sim.domain.model.UserStatusReaction

/**
 * Shared runtime store backed by files/data/user_statuses.json.
 * Data is reset from assets on every fresh app process start by AssetsHelper.
 */
object UserStatusStore {

    private val _statuses = mutableStateOf<List<UserStatus>>(emptyList())
    private var assetsHelper: AssetsHelper? = null

    val statuses: List<UserStatus>
        get() = _statuses.value

    fun initialize(assetsHelper: AssetsHelper) {
        if (this.assetsHelper == null) {
            this.assetsHelper = assetsHelper
        }
        _statuses.value = assetsHelper.loadUserStatuses()
    }

    fun getStatus(id: String): UserStatus? = _statuses.value.firstOrNull { it.id == id }

    @Composable
    fun observeStatus(id: String): UserStatus? = _statuses.value.firstOrNull { it.id == id }

    @Composable
    fun observeAll(): List<UserStatus> = _statuses.value

    fun updateMyStatus(text: String, bgColor: Long, timeLabel: String) {
        mutateStatuses { statuses ->
            statuses.map { status ->
                if (status.id == "my_status_001") {
                    status.copy(preview = text, bgColor = bgColor, timeLabel = timeLabel, isViewed = false)
                } else {
                    status
                }
            }
        }
    }

    fun markViewed(id: String) {
        mutateStatuses { statuses ->
            statuses.map { status ->
                if (status.id == id) status.copy(isViewed = true) else status
            }
        }
    }

    fun toggleLike(id: String) {
        mutateStatuses { statuses ->
            statuses.map { status ->
                if (status.id != id) {
                    status
                } else if (status.userLiked) {
                    status.copy(likeCount = status.likeCount - 1, userLiked = false)
                } else {
                    status.copy(likeCount = status.likeCount + 1, userLiked = true)
                }
            }
        }
    }

    fun sendReaction(id: String, emoji: String) {
        mutateStatuses { statuses ->
            statuses.map { status ->
                if (status.id != id) {
                    status
                } else {
                    val previousReaction = status.userReaction
                    val reactionsAfterRemove = if (previousReaction != null) {
                        status.emojiReactions.mapNotNull { reaction ->
                            if (reaction.emoji == previousReaction) {
                                val nextCount = reaction.count - 1
                                if (nextCount > 0) reaction.copy(count = nextCount) else null
                            } else {
                                reaction
                            }
                        }
                    } else {
                        status.emojiReactions.toList()
                    }

                    val existingIndex = reactionsAfterRemove.indexOfFirst { it.emoji == emoji }
                    val updatedReactions = if (existingIndex >= 0) {
                        reactionsAfterRemove.toMutableList().also {
                            it[existingIndex] = it[existingIndex].copy(count = it[existingIndex].count + 1)
                        }
                    } else {
                        reactionsAfterRemove + UserStatusReaction(emoji, 1)
                    }

                    status.copy(emojiReactions = updatedReactions, userReaction = emoji)
                }
            }
        }
    }

    private fun mutateStatuses(transform: (List<UserStatus>) -> List<UserStatus>) {
        val updated = transform(_statuses.value)
        _statuses.value = updated
        assetsHelper?.saveUserStatuses(updated)
    }
}
