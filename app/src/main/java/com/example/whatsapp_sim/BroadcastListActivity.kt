package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastListScreen
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastListViewModel
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class BroadcastListActivity : ComponentActivity() {

    private val assetsHelper by lazy { AssetsHelper(this) }
    private val contactStore by lazy { RuntimeContactStore.getInstance(assetsHelper) }
    private val chatRepository by lazy { ChatRepositoryImpl(assetsHelper) }
    private val viewModel by lazy {
        BroadcastListViewModel(contactStore, chatRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                BroadcastListScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                    onBroadcastClick = { broadcastList ->
                        startActivity(
                            BroadcastDetailActivity.createIntent(this, broadcastList.id)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, BroadcastListActivity::class.java)
    }
}
