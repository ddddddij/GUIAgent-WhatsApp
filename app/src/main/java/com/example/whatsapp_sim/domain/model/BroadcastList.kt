package com.example.whatsapp_sim.domain.model

data class BroadcastList(
    val id: String,
    val memberIds: List<String>,
    val memberNames: List<String>  // excludes "You", shown separately in UI
)
