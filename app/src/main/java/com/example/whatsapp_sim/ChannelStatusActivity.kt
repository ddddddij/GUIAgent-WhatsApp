package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.ChannelRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.data.repository.StatusRepository
import com.example.whatsapp_sim.ui.screen.channelstatus.ChannelStatusScreen
import com.example.whatsapp_sim.ui.screen.channelstatus.ChannelStatusViewModel
import com.example.whatsapp_sim.ui.screen.channelstatus.ChannelStatusViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class ChannelStatusActivity : ComponentActivity() {

    private val channelId: String by lazy {
        intent.getStringExtra(EXTRA_CHANNEL_ID).orEmpty()
    }

    private val assetsHelper by lazy { AssetsHelper(this) }

    private val chatRepository by lazy { ChatRepositoryImpl(assetsHelper) }

    private val channelRepository by lazy { ChannelRepository.getInstance(assetsHelper) }

    private val statusRepository by lazy { StatusRepository.getInstance(assetsHelper) }

    private val viewModel: ChannelStatusViewModel by viewModels {
        ChannelStatusViewModelFactory(channelId, channelRepository, statusRepository, chatRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (channelId.isBlank()) { finish(); return }

        enableEdgeToEdge()

        val contactStore = RuntimeContactStore.getInstance(assetsHelper)
        val allContacts = contactStore.getAllContacts()
        val frequentContacts = allContacts.take(3)

        val groups = chatRepository.getGroupConversations()

        val communities = CommunityRepository(assetsHelper).getCommunities()

        setContent {
            Whatsapp_simTheme {
                ChannelStatusScreen(
                    viewModel = viewModel,
                    frequentContacts = frequentContacts,
                    groups = groups,
                    communities = communities,
                    onBackClick = ::finish
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CHANNEL_ID = "channelId"

        fun createIntent(context: Context, channelId: String): Intent =
            Intent(context, ChannelStatusActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_ID, channelId)
            }
    }
}
