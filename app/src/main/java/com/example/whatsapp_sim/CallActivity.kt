package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.domain.model.CallType
import com.example.whatsapp_sim.ui.screen.call.CallScreen
import com.example.whatsapp_sim.ui.screen.call.CallViewModel
import com.example.whatsapp_sim.ui.screen.call.CallViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class CallActivity : ComponentActivity() {

    private val contactNames by lazy {
        intent.getStringArrayExtra(EXTRA_CONTACT_NAMES)?.toList()
            ?: listOfNotNull(intent.getStringExtra(EXTRA_CONTACT_NAME))
    }
    private val avatarUrls by lazy {
        intent.getStringArrayExtra(EXTRA_AVATAR_URLS)?.toList()
            ?: listOf(intent.getStringExtra(EXTRA_AVATAR_URL))
    }
    private val contactIds by lazy {
        intent.getStringArrayExtra(EXTRA_CONTACT_IDS)?.toList()
            ?: listOfNotNull(intent.getStringExtra(EXTRA_CONTACT_ID))
    }
    private val conversationId by lazy { intent.getStringExtra(EXTRA_CONVERSATION_ID).orEmpty() }

    private val assetsHelper by lazy { AssetsHelper(this) }

    private val viewModel: CallViewModel by viewModels {
        CallViewModelFactory(
            contactNames = contactNames,
            avatarUrls = avatarUrls,
            contactIds = contactIds,
            conversationId = conversationId,
            callType = CallType.VOICE,
            callRepository = CallRepository.getInstance(assetsHelper),
            chatRepository = ChatRepositoryImpl(assetsHelper)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                CallScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onSwitchToVideo = {
                        startActivity(
                            VideoCallActivity.createIntent(
                                context = this,
                                contactNames = contactNames.toTypedArray(),
                                avatarUrls = avatarUrls.map { it ?: "" }.toTypedArray(),
                                contactIds = contactIds.toTypedArray(),
                                conversationId = conversationId
                            )
                        )
                        finish()
                    },
                    onHangUp = { finish() }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CONTACT_NAME = "contactName"
        private const val EXTRA_AVATAR_URL = "avatarUrl"
        private const val EXTRA_CONTACT_ID = "contactId"
        private const val EXTRA_CONTACT_NAMES = "contactNames"
        private const val EXTRA_AVATAR_URLS = "avatarUrls"
        private const val EXTRA_CONTACT_IDS = "contactIds"
        private const val EXTRA_CONVERSATION_ID = "conversationId"

        /** Single contact convenience */
        fun createIntent(
            context: Context,
            contactName: String,
            avatarUrl: String?,
            contactId: String,
            conversationId: String
        ): Intent = Intent(context, CallActivity::class.java).apply {
            putExtra(EXTRA_CONTACT_NAMES, arrayOf(contactName))
            putExtra(EXTRA_AVATAR_URLS, arrayOf(avatarUrl ?: ""))
            putExtra(EXTRA_CONTACT_IDS, arrayOf(contactId))
            putExtra(EXTRA_CONVERSATION_ID, conversationId)
        }

        /** Multi-contact */
        fun createIntent(
            context: Context,
            contactNames: Array<String>,
            avatarUrls: Array<String>,
            contactIds: Array<String>,
            conversationId: String
        ): Intent = Intent(context, CallActivity::class.java).apply {
            putExtra(EXTRA_CONTACT_NAMES, contactNames)
            putExtra(EXTRA_AVATAR_URLS, avatarUrls)
            putExtra(EXTRA_CONTACT_IDS, contactIds)
            putExtra(EXTRA_CONVERSATION_ID, conversationId)
        }
    }
}
