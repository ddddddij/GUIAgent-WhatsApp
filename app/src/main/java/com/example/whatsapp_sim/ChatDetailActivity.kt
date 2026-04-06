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
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.ui.screen.chatdetail.ChatDetailScreen
import com.example.whatsapp_sim.ui.screen.chatdetail.ChatDetailViewModel
import com.example.whatsapp_sim.ui.screen.chatdetail.ChatDetailViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class ChatDetailActivity : ComponentActivity() {

    private val conversationId: String by lazy {
        intent.getStringExtra(EXTRA_CONVERSATION_ID).orEmpty()
    }

    private val repository by lazy {
        ChatRepositoryImpl(AssetsHelper(this))
    }

    private val viewModel: ChatDetailViewModel by viewModels {
        ChatDetailViewModelFactory(
            repository = repository,
            conversationId = conversationId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (conversationId.isBlank()) {
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                ChatDetailScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                    onVideoCallClick = {
                        viewModel.onVideoCallClick()
                        showComingSoon()
                    },
                    onVoiceCallClick = {
                        viewModel.onVoiceCallClick()
                        showComingSoon()
                    },
                    onAddAttachmentClick = {
                        viewModel.onAddAttachmentClick()
                        showComingSoon()
                    },
                    onCameraClick = {
                        viewModel.onCameraClick()
                        showComingSoon()
                    },
                    onMicClick = {
                        viewModel.onMicClick()
                        showComingSoon()
                    },
                    onLearnMoreClick = {
                        viewModel.onLearnMoreClick()
                        showComingSoon()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshMessages()
    }

    private fun showComingSoon() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_CONVERSATION_ID = "conversationId"

        fun createIntent(context: Context, conversationId: String): Intent {
            return Intent(context, ChatDetailActivity::class.java).apply {
                putExtra(EXTRA_CONVERSATION_ID, conversationId)
            }
        }
    }
}
