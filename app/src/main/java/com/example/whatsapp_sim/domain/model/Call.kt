package com.example.whatsapp_sim.domain.model

data class Call(
    val id: String,
    val callerId: String,
    val calleeId: String,
    val calleeName: String,
    val callType: CallType,
    val callStatus: CallStatus,
    val startedAt: Long,
    val endedAt: Long?,
    val durationSeconds: Int
)
