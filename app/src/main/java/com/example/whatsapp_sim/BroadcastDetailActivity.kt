package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.BroadcastStore
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastDetailScreen
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastDetailViewModel
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastDetailViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class BroadcastDetailActivity : ComponentActivity() {

    private val broadcastId: String by lazy {
        intent.getStringExtra(EXTRA_BROADCAST_ID).orEmpty()
    }

    private val chatRepository by lazy {
        ChatRepositoryImpl(AssetsHelper(this))
    }

    private val viewModel: BroadcastDetailViewModel by viewModels {
        val broadcastList = BroadcastStore.lists.first { it.id == broadcastId }
        BroadcastDetailViewModelFactory(chatRepository, broadcastList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (broadcastId.isBlank()) { finish(); return }

        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                BroadcastDetailScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish
                )
            }
        }
    }

    companion object {
        private const val EXTRA_BROADCAST_ID = "broadcastId"

        fun createIntent(context: Context, broadcastId: String): Intent =
            Intent(context, BroadcastDetailActivity::class.java).apply {
                putExtra(EXTRA_BROADCAST_ID, broadcastId)
            }
    }
}
