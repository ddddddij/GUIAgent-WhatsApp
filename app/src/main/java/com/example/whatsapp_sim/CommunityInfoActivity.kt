package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.ui.screen.communityinfo.CommunityInfoScreen
import com.example.whatsapp_sim.ui.screen.communityinfo.CommunityInfoViewModel
import com.example.whatsapp_sim.ui.screen.communityinfo.CommunityInfoViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class CommunityInfoActivity : ComponentActivity() {

    private val communityId: String by lazy {
        intent.getStringExtra(EXTRA_COMMUNITY_ID).orEmpty()
    }

    private val repository by lazy {
        CommunityRepository(AssetsHelper(this))
    }

    private val viewModel: CommunityInfoViewModel by viewModels {
        CommunityInfoViewModelFactory(communityId, repository)
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
                CommunityInfoScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish
                )
            }
        }
    }

    companion object {
        private const val EXTRA_COMMUNITY_ID = "communityId"

        fun createIntent(context: Context, communityId: String): Intent {
            return Intent(context, CommunityInfoActivity::class.java).apply {
                putExtra(EXTRA_COMMUNITY_ID, communityId)
            }
        }
    }
}
