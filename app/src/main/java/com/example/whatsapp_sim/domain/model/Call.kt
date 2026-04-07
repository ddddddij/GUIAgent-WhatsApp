package com.example.whatsapp_sim.domain.model

data class Call(
    val id: String,
    val conversationId: String = "",
    val contactIds: List<String> = emptyList(),
    val callType: CallType,
    val callResult: CallResult,
    val timestamp: String,
    val dateLabel: String,
    val durationSeconds: Int,
    val durationDisplay: String,
    val isSelf: Boolean
)
