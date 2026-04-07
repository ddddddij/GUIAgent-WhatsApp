package com.example.whatsapp_sim

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.whatsapp_sim.data.local.AssetsHelper
import com.example.whatsapp_sim.data.repository.AccountRepositoryImpl
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.ChatRepositoryImpl
import com.example.whatsapp_sim.data.repository.CommunityRepository
import com.example.whatsapp_sim.data.repository.RuntimeContactStore
import com.example.whatsapp_sim.ui.components.BottomNavTab
import com.example.whatsapp_sim.ui.components.EmptyTabScreen
import com.example.whatsapp_sim.ui.components.NewContactDialog
import com.example.whatsapp_sim.ui.components.WhatsAppBottomNavigation
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastListViewModel
import com.example.whatsapp_sim.ui.screen.calls.CallsScreen
import com.example.whatsapp_sim.ui.screen.calls.CallsViewModel
import com.example.whatsapp_sim.ui.screen.chats.ChatsScreen
import com.example.whatsapp_sim.ui.screen.chats.ChatsViewModel
import com.example.whatsapp_sim.ui.screen.chats.NewChatViewModel
import com.example.whatsapp_sim.ui.screen.chats.NewGroupViewModel
import com.example.whatsapp_sim.ui.screen.communities.CommunitiesScreen
import com.example.whatsapp_sim.ui.screen.communities.CommunitiesViewModel
import com.example.whatsapp_sim.ui.screen.contactcreation.NewContactViewModel
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
        val contactStore = RuntimeContactStore.getInstance(assetsHelper)
        val chatRepository = ChatRepositoryImpl(assetsHelper)
        val chatsViewModel = ChatsViewModel(chatRepository)
        val newChatViewModel = NewChatViewModel(chatRepository)
        val newGroupViewModel = NewGroupViewModel(chatRepository)
        val newContactViewModel = NewContactViewModel(contactStore)
        val accountRepository = AccountRepositoryImpl(assetsHelper)
        val youViewModel = YouViewModel(accountRepository)
        val updatesViewModel = UpdatesViewModel()
        val callRepository = CallRepository.getInstance(assetsHelper)
        val callsViewModel = CallsViewModel(callRepository, contactStore)
        val communityRepository = CommunityRepository(assetsHelper)
        val communitiesViewModel = CommunitiesViewModel(communityRepository)
        val broadcastListViewModel = BroadcastListViewModel(contactStore, chatRepository)

        setContent {
            Whatsapp_simTheme {
                MainScreen(
                    chatsViewModel = chatsViewModel,
                    newChatViewModel = newChatViewModel,
                    newGroupViewModel = newGroupViewModel,
                    newContactViewModel = newContactViewModel,
                    youViewModel = youViewModel,
                    updatesViewModel = updatesViewModel,
                    callsViewModel = callsViewModel,
                    communitiesViewModel = communitiesViewModel,
                    broadcastListViewModel = broadcastListViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    chatsViewModel: ChatsViewModel,
    newChatViewModel: NewChatViewModel,
    newGroupViewModel: NewGroupViewModel,
    newContactViewModel: NewContactViewModel,
    youViewModel: YouViewModel,
    updatesViewModel: UpdatesViewModel,
    callsViewModel: CallsViewModel,
    communitiesViewModel: CommunitiesViewModel,
    broadcastListViewModel: BroadcastListViewModel
) {
    var selectedTab by remember { mutableStateOf(BottomNavTab.CHATS) }
    var showNewContactDialog by remember { mutableStateOf(false) }
    val totalUnreadCount by chatsViewModel.totalUnreadCount.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, chatsViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                chatsViewModel.refreshChats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                BottomNavTab.CHATS -> ChatsScreen(
                    viewModel = chatsViewModel,
                    newChatViewModel = newChatViewModel,
                    newGroupViewModel = newGroupViewModel,
                    communitiesViewModel = communitiesViewModel,
                    broadcastListViewModel = broadcastListViewModel,
                    onNewContactClick = { showNewContactDialog = true },
                    onChatClick = { conversationId ->
                        context.startActivity(
                            ChatDetailActivity.createIntent(
                                context = context,
                                conversationId = conversationId
                            )
                        )
                    }
                )
                BottomNavTab.UPDATES -> UpdatesScreen(updatesViewModel)
                BottomNavTab.CALLS -> CallsScreen(
                    viewModel = callsViewModel,
                    onNewContactClick = { showNewContactDialog = true }
                )
                BottomNavTab.COMMUNITIES -> CommunitiesScreen(communitiesViewModel)
                BottomNavTab.YOU -> YouScreen(youViewModel)
            }
        }
    }

    if (showNewContactDialog) {
        NewContactDialog(
            viewModel = newContactViewModel,
            onDismiss = { showNewContactDialog = false },
            onDone = {
                showNewContactDialog = false
                newChatViewModel.refreshContacts()
                newGroupViewModel.refreshContacts()
                callsViewModel.newCallViewModel.refreshContacts()
                Toast.makeText(context, "Contact created", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
