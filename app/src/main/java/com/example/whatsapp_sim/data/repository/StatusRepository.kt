package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Status
import com.example.whatsapp_sim.domain.model.StatusReaction

class StatusRepository private constructor(private val assetsHelper: AssetsHelper) {

    // In-memory mutable copy so reactions can be updated at runtime
    private val _statuses: MutableList<Status> = assetsHelper.loadStatuses().toMutableList()

    fun getStatusesByChannelId(channelId: String): List<Status> =
        _statuses.filter { it.channelId == channelId }

    /** Add or increment a reaction on a status (in-memory only). */
    fun addReaction(statusId: String, emoji: String) {
        val index = _statuses.indexOfFirst { it.id == statusId }
        if (index < 0) return
        val status = _statuses[index]

        // If user already reacted with same emoji, do nothing
        if (status.userReaction == emoji) return

        // Remove count from previous user reaction if any
        val prevEmoji = status.userReaction
        val reactionsAfterRemove = if (prevEmoji != null) {
            status.reactions.mapNotNull { r ->
                if (r.emoji == prevEmoji) {
                    val newCount = r.count - 1
                    if (newCount > 0) r.copy(count = newCount) else null
                } else r
            }
        } else {
            status.reactions.toList()
        }

        // Add new reaction
        val existingIdx = reactionsAfterRemove.indexOfFirst { it.emoji == emoji }
        val updatedReactions = if (existingIdx >= 0) {
            reactionsAfterRemove.toMutableList().also {
                it[existingIdx] = StatusReaction(emoji, it[existingIdx].count + 1)
            }
        } else {
            reactionsAfterRemove + StatusReaction(emoji, 1)
        }

        val countDelta = if (prevEmoji != null) 0 else 1  // switching emoji doesn't change total
        _statuses[index] = status.copy(
            reactions = updatedReactions,
            reactionCount = status.reactionCount + countDelta,
            userReaction = emoji
        )
        assetsHelper.saveStatuses(_statuses)
    }

    /** Increment share count on a status (in-memory only). */
    fun incrementShareCount(statusId: String) {
        val index = _statuses.indexOfFirst { it.id == statusId }
        if (index < 0) return
        _statuses[index] = _statuses[index].copy(shareCount = _statuses[index].shareCount + 1)
        assetsHelper.saveStatuses(_statuses)
    }

    companion object {
        @Volatile
        private var instance: StatusRepository? = null

        fun getInstance(assetsHelper: AssetsHelper): StatusRepository {
            return instance ?: synchronized(this) {
                instance ?: StatusRepository(assetsHelper).also { instance = it }
            }
        }

        private val QUICK_EMOJIS = listOf("❤️", "💜", "👍", "🤢", "😂", "😮", "😢", "🎉")

        val ALL_EMOJIS = listOf(
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
            "😀", "😂", "😮", "😢", "😡", "😍", "🤣", "😊",
            "👍", "👎", "👏", "🙌", "🤝", "💪", "🙏", "👌",
            "🎉", "🎊", "🔥", "⭐", "💯", "✅", "❌", "⚡",
            "🐶", "🐱", "🦁", "🐻", "🐼", "🦊", "🐨", "🐯",
            "🍕", "🍔", "🎂", "🍰", "🎁", "🎵", "🎶", "🏆"
        )

        fun getQuickEmojis() = QUICK_EMOJIS
    }
}
