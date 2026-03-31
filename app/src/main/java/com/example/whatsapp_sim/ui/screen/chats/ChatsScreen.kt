package com.example.whatsapp_sim.ui.screen.chats

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.whatsapp_sim.ui.screen.chats.components.ChatListItem
import com.example.whatsapp_sim.ui.screen.chats.components.ChatsTopBar
import com.example.whatsapp_sim.ui.screen.chats.components.FilterTabRow

@Composable
fun ChatsScreen(viewModel: ChatsViewModel) {
    val context = LocalContext.current
    val filteredChats by viewModel.filteredChats.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ChatsTopBar(
            onMoreMenuClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            },
            onCameraClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
            },
            onNewChatClick = {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
