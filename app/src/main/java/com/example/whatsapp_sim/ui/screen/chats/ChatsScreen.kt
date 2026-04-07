package com.example.whatsapp_sim.ui.screen.chats

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.whatsapp_sim.ui.screen.broadcast.BroadcastListViewModel
import com.example.whatsapp_sim.ui.screen.broadcast.NewBroadcastBottomSheet
import com.example.whatsapp_sim.ui.screen.chats.components.ChatListItem
import com.example.whatsapp_sim.ui.screen.chats.components.ChatsTopBar
import com.example.whatsapp_sim.ui.screen.chats.components.FilterTabRow
import com.example.whatsapp_sim.ui.screen.chats.components.NewChatBottomSheet
import com.example.whatsapp_sim.ui.screen.chats.components.NewGroupBottomSheet
import com.example.whatsapp_sim.ui.screen.communities.CommunitiesViewModel
import com.example.whatsapp_sim.ui.screen.communities.NewCommunityBottomSheet

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel,
    newChatViewModel: NewChatViewModel,
    newGroupViewModel: NewGroupViewModel,
    communitiesViewModel: CommunitiesViewModel,
    broadcastListViewModel: BroadcastListViewModel,
    onNewContactClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val context = LocalContext.current
    val filteredChats by viewModel.filteredChats.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val showNewCommunitySheet by communitiesViewModel.showNewCommunitySheet.collectAsState()
    val showNewBroadcastSheet by broadcastListViewModel.showNewBroadcastSheet.collectAsState()
    var showNewChatSheet by remember { mutableStateOf(false) }
    var showNewGroupSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatsTopBar(
            onMoreMenuClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            },
            onCameraClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            },
            onNewChatClick = {
                viewModel.onNewChatClick()
                showNewChatSheet = true
            }
        )

        FilterTabRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { viewModel.selectFilter(it) }
        )

        LazyColumn {
            items(filteredChats) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = {
                        viewModel.onChatItemClick(chat.id)
                        onChatClick(chat.id)
                    },
                    onAvatarClick = if (!chat.isGroup) {
                        {
                            val contact = viewModel.findContactByName(chat.name)
                            if (contact != null) {
                                context.startActivity(
                                    com.example.whatsapp_sim.ContactInfoActivity.createIntent(context, contact.id)
                                )
                            }
                        }
                    } else null
                )
            }
        }
    }

    if (showNewChatSheet) {
        NewChatBottomSheet(
            viewModel = newChatViewModel,
            onDismiss = { showNewChatSheet = false },
            onNewGroupClick = {
                showNewChatSheet = false
                showNewGroupSheet = true
            },
            onNewContactClick = {
                onNewContactClick()
            },
            onNewCommunityClick = {
                showNewChatSheet = false
                communitiesViewModel.onNewCommunityClick()
            },
            onNewBroadcastClick = {
                showNewChatSheet = false
                broadcastListViewModel.onNewBroadcastClick()
            },
            onOtherQuickActionClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            },
            onOpenConversation = { conversationId ->
                showNewChatSheet = false
                viewModel.refreshChats()
                onChatClick(conversationId)
            }
        )
    }

    if (showNewGroupSheet) {
        NewGroupBottomSheet(
            viewModel = newGroupViewModel,
            onDismiss = { showNewGroupSheet = false },
            onGroupCreated = { conversationId ->
                showNewGroupSheet = false
                viewModel.refreshChats()
                onChatClick(conversationId)
            }
        )
    }

    if (showNewCommunitySheet) {
        NewCommunityBottomSheet(
            viewModel = communitiesViewModel.newCommunityViewModel,
            onDismiss = { communitiesViewModel.onNewCommunitySheetDismiss() },
            onCreateCommunity = { community ->
                communitiesViewModel.addCommunity(community)
            }
        )
    }

    if (showNewBroadcastSheet) {
        NewBroadcastBottomSheet(
            viewModel = broadcastListViewModel.newBroadcastViewModel,
            onDismiss = { broadcastListViewModel.onNewBroadcastSheetDismiss() },
            onCreated = { broadcastList ->
                broadcastListViewModel.onBroadcastCreated(broadcastList)
                viewModel.refreshChats()
            }
        )
    }
}
