package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.ui.screen.userstatus.AddTextStatusScreen
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTextStatusActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UserStatusStore.initialize(AssetsHelper(this))

        setContent {
            Whatsapp_simTheme {
                AddTextStatusScreen(
                    onClose = ::finish,
                    onSend = { text, bgColorLong ->
                        val timeLabel = SimpleDateFormat("h:mm a", Locale.US).format(Date())
                        UserStatusStore.updateMyStatus(
                            text = text,
                            bgColor = bgColorLong,
                            timeLabel = timeLabel
                        )
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, AddTextStatusActivity::class.java)
    }
}
