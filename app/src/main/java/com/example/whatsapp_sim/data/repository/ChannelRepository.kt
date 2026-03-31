package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.domain.model.Channel

class ChannelRepository {
    fun getChannels(): List<Channel> = listOf(
        Channel("liverpool_fc", "Liverpool Football Club", "18.4M followers", true, null),
        Channel("real_madrid", "Real Madrid C.F.", "67.7M followers", true, null),
        Channel("premier_league", "Premier League", "18.5M followers", true, null),
        Channel("man_united", "Manchester United", "17M followers", true, null)
    )
}
