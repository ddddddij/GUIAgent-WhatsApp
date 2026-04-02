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
import com.example.whatsapp_sim.ui.screen.contactinfo.ContactInfoScreen
import com.example.whatsapp_sim.ui.screen.contactinfo.ContactInfoViewModel
import com.example.whatsapp_sim.ui.screen.contactinfo.ContactInfoViewModelFactory
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class ContactInfoActivity : ComponentActivity() {

    private val contactId: String by lazy {
        intent.getStringExtra(EXTRA_CONTACT_ID).orEmpty()
    }

    private val repository by lazy {
        ChatRepositoryImpl(AssetsHelper(this))
    }

    private val viewModel: ContactInfoViewModel by viewModels {
        ContactInfoViewModelFactory(contactId, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (contactId.isBlank()) {
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            Whatsapp_simTheme {
                ContactInfoScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                    onNavigateToChat = { conversationId ->
                        startActivity(
                            ChatDetailActivity.createIntent(this, conversationId)
                        )
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_CONTACT_ID = "contactId"

        fun createIntent(context: Context, contactId: String): Intent =
            Intent(context, ContactInfoActivity::class.java).apply {
                putExtra(EXTRA_CONTACT_ID, contactId)
            }
    }
}
