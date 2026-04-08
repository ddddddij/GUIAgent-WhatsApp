package com.example.whatsapp_sim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.AccountRepositoryImpl
import com.example.whatsapp_sim.ui.screen.profile.ProfileScreen
import com.example.whatsapp_sim.ui.screen.profile.ProfileViewModel
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class ProfileActivity : ComponentActivity() {

    private val viewModel by lazy {
        ProfileViewModel(AccountRepositoryImpl(AssetsHelper(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Whatsapp_simTheme {
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = ::finish
                )
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, ProfileActivity::class.java)
    }
}
