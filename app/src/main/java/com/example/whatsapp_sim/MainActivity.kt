package com.example.whatsapp_sim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.AccountRepositoryImpl
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.ui.components.BottomNavTab
import com.example.whatsapp_sim.ui.components.EmptyTabScreen
import com.example.whatsapp_sim.ui.components.WhatsAppBottomNavigation
import com.example.whatsapp_sim.ui.screen.calls.CallsScreen
import com.example.whatsapp_sim.ui.screen.calls.CallsViewModel
import com.example.whatsapp_sim.ui.screen.chats.ChatsScreen
import com.example.whatsapp_sim.ui.screen.chats.ChatsViewModel
import com.example.whatsapp_sim.ui.screen.communities.CommunitiesScreen
import com.example.whatsapp_sim.ui.screen.communities.CommunitiesViewModel
import com.example.whatsapp_sim.ui.screen.updates.UpdatesScreen
import com.example.whatsapp_sim.ui.screen.updates.UpdatesViewModel
import com.example.whatsapp_sim.ui.screen.you.YouScreen
import com.example.whatsapp_sim.ui.screen.you.YouViewModel
import com.example.whatsapp_sim.ui.theme.Whatsapp_simTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val assetsHelper = AssetsHelper(this)
        val chatRepository = ChatRepositoryImpl(assetsHelper)
        val chatsViewModel = ChatsViewModel(chatRepository)
        val accountRepository = AccountRepositoryImpl(assetsHelper)
        val youViewModel = YouViewModel(accountRepository)
        val updatesViewModel = UpdatesViewModel()
        val callRepository = CallRepository(assetsHelper)
        val callsViewModel = CallsViewModel(callRepository)
        val communityRepository = CommunityRepository(assetsHelper)
        val communitiesViewModel = CommunitiesViewModel(communityRepository)

        setContent {
            Whatsapp_simTheme {
                MainScreen(chatsViewModel, youViewModel, updatesViewModel, callsViewModel, communitiesViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(chatsViewModel: ChatsViewModel, youViewModel: YouViewModel, updatesViewModel: UpdatesViewModel, callsViewModel: CallsViewModel, communitiesViewModel: CommunitiesViewModel) {
    var selectedTab by remember { mutableStateOf(BottomNavTab.CHATS) }
    val totalUnreadCount by chatsViewModel.totalUnreadCount.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            WhatsAppBottomNavigation(
                selectedTab = selectedTab,
                unreadCount = totalUnreadCount,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                BottomNavTab.CHATS -> ChatsScreen(chatsViewModel)
                BottomNavTab.UPDATES -> UpdatesScreen(updatesViewModel)
                BottomNavTab.CALLS -> CallsScreen(callsViewModel)
                BottomNavTab.COMMUNITIES -> CommunitiesScreen(communitiesViewModel)
                BottomNavTab.YOU -> YouScreen(youViewModel)
            }
        }
    }
}
