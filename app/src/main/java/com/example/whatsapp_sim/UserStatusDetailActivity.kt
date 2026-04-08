package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.ui.screen.userstatus.UserStatusDetailScreen
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class UserStatusDetailActivity : ComponentActivity() {

    private val statusId: String by lazy {
        intent.getStringExtra(EXTRA_STATUS_ID).orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (statusId.isBlank()) { finish(); return }

        enableEdgeToEdge()
        UserStatusStore.initialize(AssetsHelper(this))

        val isMine = statusId == "my_status_001"
        // Mark viewed when entering
        UserStatusStore.markViewed(statusId)

        setContent {
            Whatsapp_simTheme {
                // Observe shared store — mutableStateOf triggers recomposition
                val status = UserStatusStore.observeStatus(statusId)
                if (status != null) {
                    UserStatusDetailScreen(
                        status = status,
                        isMine = isMine,
                        onBackClick = ::finish,
                        onLikeClick = { UserStatusStore.toggleLike(statusId) },
                        onReactionSelected = { emoji -> UserStatusStore.sendReaction(statusId, emoji) }
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_STATUS_ID = "statusId"

        fun createIntent(context: Context, statusId: String): Intent =
            Intent(context, UserStatusDetailActivity::class.java).apply {
                putExtra(EXTRA_STATUS_ID, statusId)
            }
    }
}
