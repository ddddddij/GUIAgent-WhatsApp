package com.example.whatsapp_sim.domain.repository

import com.example.whatsapp_sim.domain.model.Chat

interface ChatRepository {
    fun getAllChats(): List<Chat>
}
