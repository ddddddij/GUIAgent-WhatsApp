package com.example.whatsapp_sim

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.example.whatsapp_sim.domain.model.UserStatus
import com.example.whatsapp_sim.domain.model.UserStatusReaction

/**
 * In-memory singleton store for all UserStatus entries.
 * Both UpdatesViewModel and UserStatusDetailActivity operate on this shared state.
 */
object UserStatusStore {

    private val _statuses = mutableStateOf(
        listOf(
            UserStatus(
                id = "my_status_001",
                senderName = "My status",
                preview = "Living the moment 🌅",
                timeLabel = "10:32 AM",
                isViewed = false,
                bgColor = 0xFF1A7A5E,
                likeCount = 0,
                userLiked = false
            ),
            UserStatus(
                id = "status_002",
                senderName = "Emily Chen",
                preview = "Weekend vibes ☀️",
                timeLabel = "9:15 AM",
                isViewed = false,
                bgColor = 0xFFD44000,
                likeCount = 5,
                userLiked = false,
                emojiReactions = listOf(
                    UserStatusReaction("❤️", 3),
                    UserStatusReaction("😍", 2)
                ),
                avatarUrl = "image/联系人头像/艺术.jpg"
            ),
            UserStatus(
                id = "status_003",
                senderName = "Marcus Davis",
                preview = "Coffee time ☕",
                timeLabel = "8:47 AM",
                isViewed = true,
                bgColor = 0xFF5C3317,
                likeCount = 2,
                userLiked = false,
                emojiReactions = listOf(UserStatusReaction("😂", 2)),
                avatarUrl = "image/联系人头像/运动.jpg"
            ),
            UserStatus(
                id = "status_004",
                senderName = "Olivia Brown",
                preview = "New chapter begins 📖",
                timeLabel = "Yesterday",
                isViewed = true,
                bgColor = 0xFF4A148C,
                likeCount = 12,
                userLiked = false,
                emojiReactions = listOf(
                    UserStatusReaction("🎉", 6),
                    UserStatusReaction("❤️", 4),
                    UserStatusReaction("👍", 2)
                ),
                avatarUrl = "image/联系人头像/小猫.jpg"
            )
        )
    )

    val statuses: List<UserStatus> get() = _statuses.value

    fun getStatus(id: String): UserStatus? = _statuses.value.firstOrNull { it.id == id }

    @Composable
    fun observeStatus(id: String): UserStatus? {
        // Reading _statuses.value inside Composable subscribes to recomposition
        return _statuses.value.firstOrNull { it.id == id }
    }

    @Composable
    fun observeAll(): List<UserStatus> = _statuses.value

    /** Replace My Status content (called after publishing a new text status). */
    fun updateMyStatus(text: String, bgColor: Long, timeLabel: String) {
        _statuses.value = _statuses.value.map { s ->
            if (s.id == "my_status_001")
                s.copy(preview = text, bgColor = bgColor, timeLabel = timeLabel, isViewed = false)
            else s
        }
    }

    fun markViewed(id: String) {
        _statuses.value = _statuses.value.map { s ->
            if (s.id == id) s.copy(isViewed = true) else s
        }
    }

    fun toggleLike(id: String) {
        _statuses.value = _statuses.value.map { s ->
            if (s.id != id) s
            else if (s.userLiked) s.copy(likeCount = s.likeCount - 1, userLiked = false)
            else s.copy(likeCount = s.likeCount + 1, userLiked = true)
        }
    }

    fun sendReaction(id: String, emoji: String) {
        _statuses.value = _statuses.value.map { s ->
            if (s.id != id) s
            else {
                val prev = s.userReaction
                val afterRemove = if (prev != null) {
                    s.emojiReactions.mapNotNull { r ->
                        if (r.emoji == prev) {
                            if (r.count - 1 > 0) r.copy(count = r.count - 1) else null
                        } else r
                    }
                } else s.emojiReactions.toList()
                val existing = afterRemove.indexOfFirst { it.emoji == emoji }
                val updated = if (existing >= 0) {
                    afterRemove.toMutableList().also {
                        it[existing] = it[existing].copy(count = it[existing].count + 1)
                    }
                } else {
                    afterRemove + UserStatusReaction(emoji, 1)
                }
                s.copy(emojiReactions = updated, userReaction = emoji)
            }
        }
    }
}
