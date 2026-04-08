package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Channel

class ChannelRepository private constructor(
    private val assetsHelper: AssetsHelper
) {
    private val channels = assetsHelper.loadChannels().toMutableList()

    fun getChannels(): List<Channel> = channels.toList()

    fun getChannel(channelId: String): Channel? = channels.firstOrNull { it.id == channelId }

    fun toggleFollow(channelId: String): Channel? {
        val index = channels.indexOfFirst { it.id == channelId }
        if (index < 0) return null
        val updated = channels[index].copy(
            initiallyFollowing = !channels[index].initiallyFollowing
        )
        channels[index] = updated
        persist()
        return updated
    }

    fun toggleMute(channelId: String): Channel? {
        val index = channels.indexOfFirst { it.id == channelId }
        if (index < 0) return null
        val updated = channels[index].copy(
            isNotificationMuted = !channels[index].isNotificationMuted
        )
        channels[index] = updated
        persist()
        return updated
    }

    private fun persist() {
        assetsHelper.saveChannels(channels)
    }

    companion object {
        @Volatile
        private var instance: ChannelRepository? = null

        fun getInstance(assetsHelper: AssetsHelper): ChannelRepository {
            return instance ?: synchronized(this) {
                instance ?: ChannelRepository(assetsHelper).also { instance = it }
            }
        }
    }
}
