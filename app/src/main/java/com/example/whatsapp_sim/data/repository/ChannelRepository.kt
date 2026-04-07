package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.domain.model.Channel

class ChannelRepository {
    fun getChannels(): List<Channel> = listOf(
        Channel(
            id = "liverpool_fc",
            name = "Liverpool Football Club",
            followersCount = "18.4M followers",
            isVerified = true,
            avatarUrl = "image/Channel头像/liverpool.PNG",
            latestUpdate = "🔴 YNWA! Match day against Chelsea — 3:00 PM kick off. Come on Reds! 💪",
            updateTime = "2h ago",
            initiallyFollowing = true
        ),
        Channel(
            id = "real_madrid",
            name = "Real Madrid C.F.",
            followersCount = "67.7M followers",
            isVerified = true,
            avatarUrl = "image/Channel头像/C.F.jpg",
            latestUpdate = "⚽ Matchday preview: Real Madrid vs Atlético tonight at the Bernabéu. Hala Madrid!",
            updateTime = "5h ago",
            initiallyFollowing = true
        ),
        Channel("premier_league", "Premier League", "18.5M followers", true, "image/Channel头像/premier.PNG"),
        Channel("man_united", "Manchester United", "17M followers", true, "image/Channel头像/manchester.PNG"),
        Channel("spotify", "Spotify", "12.1M followers", true, "image/Channel头像/Spotify.jpg"),
        Channel("netflix", "Netflix", "24.6M followers", true, "image/Channel头像/Netflix.jpg")
    )
}
