package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.CommunityChannelStore
import com.example.whatsapp_sim.data.repository.CommunityChannelType
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.ui.screen.communities.CommunityChannelDetailScreen
import com.example.whatsapp_sim.ui.screen.communities.CommunityChannelDetailViewModel
import com.example.whatsapp_sim.ui.screen.communities.CommunityChannelDetailViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class CommunityChannelDetailActivity : ComponentActivity() {

    private val communityId: String by lazy {
        intent.getStringExtra(EXTRA_COMMUNITY_ID).orEmpty()
    }

    private val channelType: CommunityChannelType by lazy {
        val raw = intent.getStringExtra(EXTRA_CHANNEL_TYPE).orEmpty()
        runCatching { CommunityChannelType.valueOf(raw) }
            .getOrDefault(CommunityChannelType.GENERAL)
    }

    private val viewModel: CommunityChannelDetailViewModel by viewModels {
        val repository = CommunityRepository(AssetsHelper(this))
        val communities = repository.getCommunities()
        CommunityChannelStore.initialize(communities)
        val community = communities.first { it.id == communityId }
        CommunityChannelDetailViewModelFactory(
            community = community,
            channelType = channelType
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (communityId.isBlank()) {
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                CommunityChannelDetailScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish
                )
            }
        }
    }

    companion object {
        private const val EXTRA_COMMUNITY_ID = "communityId"
        private const val EXTRA_CHANNEL_TYPE = "channelType"

        fun createIntent(
            context: Context,
            communityId: String,
            channelType: CommunityChannelType
        ): Intent {
            return Intent(context, CommunityChannelDetailActivity::class.java).apply {
                putExtra(EXTRA_COMMUNITY_ID, communityId)
                putExtra(EXTRA_CHANNEL_TYPE, channelType.name)
            }
        }
    }
}
