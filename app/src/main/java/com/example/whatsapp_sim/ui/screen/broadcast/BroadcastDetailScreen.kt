package com.example.whatsapp_sim.ui.screen.broadcast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.R
import com.example.whatsapp_sim.ui.screen.chatdetail.components.ChatInputBar
import com.example.whatsapp_sim.ui.screen.chatdetail.components.ChatMessageBubble
import com.example.whatsapp_sim.ui.screen.chatdetail.components.DateDivider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val CURRENT_USER_ID = "user_001"

@Composable
fun BroadcastDetailScreen(
    viewModel: BroadcastDetailViewModel,
    onBackClick: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val showSendButton by viewModel.showSendButton.collectAsState()
    val listState = rememberLazyListState()
    val totalItems = remember(messages) { messages.size }

    LaunchedEffect(totalItems) {
        if (totalItems > 0) listState.scrollToItem(totalItems - 1)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                showSendButton = showSendButton,
                onInputChanged = viewModel::onInputChanged,
                onAddClick = {},
                onCameraClick = {},
                onMicClick = {},
                onSendClick = viewModel::sendMessage
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color(0xFFEDE8DC))
        ) {
            val totalCount = viewModel.broadcastList.memberIds.size + 1
            BroadcastDetailTopBar(
                totalCount = totalCount,
                onBackClick = onBackClick,
                modifier = Modifier.statusBarsPadding()
            )

            // Broadcast info banner
            BroadcastInfoBanner(memberCount = viewModel.broadcastList.memberIds.size + 1)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chat_detail_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (messages.isEmpty()) {
                    Text(
                        text = "No messages yet. Send a message to broadcast to all recipients.",
                        color = Color(0xFF8E8E8E),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        item(key = "first_date") {
                            DateDivider(label = formatDateDivider(messages.first().sentAt))
                        }

                        itemsIndexed(
                            items = messages,
                            key = { _, msg -> msg.id }
                        ) { index, message ->
                            if (index > 0 && !isSameDay(messages[index - 1].sentAt, message.sentAt)) {
                                DateDivider(label = formatDateDivider(message.sentAt))
                            }
                            ChatMessageBubble(
                                message = message,
                                isSelf = message.senderId == CURRENT_USER_ID,
                                topSpacing = if (index == 0) 8.dp else 4.dp,
                                isGroupChat = false,
                                showAvatar = false,
                                showSenderName = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BroadcastDetailTopBar(
    totalCount: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White)
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color(0xFF3C3C43)
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(40.dp)
                .background(Color(0xFF25D366), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(text = "$totalCount recipients", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Tap for broadcast info", color = Color(0xFF8E8E8E), fontSize = 12.sp)
        }
    }
}

@Composable
private fun BroadcastInfoBanner(memberCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3CD))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Messages sent here are broadcast to all $memberCount recipients individually.",
            fontSize = 12.sp,
            color = Color(0xFF856404),
            lineHeight = 16.sp
        )
    }
}

private fun formatDateDivider(timestamp: Long): String {
    val msgCal = Calendar.getInstance().apply { time = Date(timestamp) }
    val todayCal = Calendar.getInstance()
    val yesCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    return when {
        isSameDay(msgCal.timeInMillis, todayCal.timeInMillis) -> "Today"
        isSameDay(msgCal.timeInMillis, yesCal.timeInMillis) -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(timestamp))
    }
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { time = Date(t1) }
    val c2 = Calendar.getInstance().apply { time = Date(t2) }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
