package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.ui.screen.groupinfo.GroupInfoScreen
import com.example.whatsapp_sim.ui.screen.groupinfo.GroupInfoViewModelFactory
import com.example.whatsapp_sim.ui.screen.groupinfo.GroupInfoViewModel
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class GroupInfoActivity : ComponentActivity() {

    private val conversationId: String by lazy {
        intent.getStringExtra(EXTRA_CONVERSATION_ID).orEmpty()
    }

    private val repository by lazy {
        ChatRepositoryImpl(AssetsHelper(this))
    }

    private val viewModel: GroupInfoViewModel by viewModels {
        GroupInfoViewModelFactory(conversationId, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (conversationId.isBlank()) { finish(); return }

        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                GroupInfoScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                    onExitGroup = ::finish,
                    onNavigateToCall = { convId, memberIds, memberAvatars, memberNames ->
                        startActivity(
                            CallActivity.createIntent(
                                context = this,
                                contactNames = memberNames,
                                avatarUrls = memberAvatars,
                                contactIds = memberIds,
                                conversationId = convId
                            )
                        )
                    },
                    onNavigateToVideoCall = { convId, memberIds, memberAvatars, memberNames ->
                        startActivity(
                            VideoCallActivity.createIntent(
                                context = this,
                                contactNames = memberNames,
                                avatarUrls = memberAvatars,
                                contactIds = memberIds,
                                conversationId = convId
                            )
                        )
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CONVERSATION_ID = "conversationId"

        fun createIntent(context: Context, conversationId: String): Intent =
            Intent(context, GroupInfoActivity::class.java).apply {
                putExtra(EXTRA_CONVERSATION_ID, conversationId)
            }
    }
}
