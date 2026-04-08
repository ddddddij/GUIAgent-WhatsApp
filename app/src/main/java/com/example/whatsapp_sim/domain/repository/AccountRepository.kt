package com.example.whatsapp_sim.domain.repository

import com.example.whatsapp_sim.domain.model.Account

interface AccountRepository {
    fun getCurrentUser(): Account?
    fun updateCurrentUserAbout(about: String): Account?
}
