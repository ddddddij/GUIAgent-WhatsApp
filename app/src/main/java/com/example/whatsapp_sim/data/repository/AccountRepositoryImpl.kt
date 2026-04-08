package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.repository.AccountRepository

class AccountRepositoryImpl(private val assetsHelper: AssetsHelper) : AccountRepository {
    override fun getCurrentUser(): Account? = assetsHelper.loadAccounts().firstOrNull()

    override fun updateCurrentUserAbout(about: String): Account? {
        val accounts = assetsHelper.loadAccounts().toMutableList()
        val currentIndex = accounts.indexOfFirst { it.id == "user_001" }
        if (currentIndex < 0) {
            return null
        }

        val updatedAccount = accounts[currentIndex].copy(
            about = about,
            updatedAt = System.currentTimeMillis()
        )
        accounts[currentIndex] = updatedAccount
        assetsHelper.saveAccounts(accounts)
        return updatedAccount
    }
}
