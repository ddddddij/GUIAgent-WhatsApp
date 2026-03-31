package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.repository.AccountRepository

class AccountRepositoryImpl(private val assetsHelper: AssetsHelper) : AccountRepository {
    override fun getCurrentUser(): Account? = assetsHelper.loadAccounts().firstOrNull()
}
