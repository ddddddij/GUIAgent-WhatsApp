package com.example.whatsapp_sim.data.repository

import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.domain.model.Community

class CommunityRepository(private val assetsHelper: AssetsHelper) {
    fun getCommunities(): List<Community> = assetsHelper.loadCommunities()
}
