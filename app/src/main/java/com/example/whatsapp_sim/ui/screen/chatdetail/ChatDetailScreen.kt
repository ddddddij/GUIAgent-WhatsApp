package com.example.whatsapp_sim.ui.screen.chatdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.whatsapp_sim.R
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.ui.screen.chatdetail.components.ChatDetailTopBar
import com.example.whatsapp_sim.ui.screen.chatdetail.components.ChatInputBar
import com.example.whatsapp_sim.ui.screen.chatdetail.components.ChatMessageBubble
import com.example.whatsapp_sim.ui.screen.chatdetail.components.DateDivider
import com.example.whatsapp_sim.ui.screen.chatdetail.components.EncryptionBanner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val CURRENT_USER_ID = "user_001"

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    onBackClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onAddAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMicClick: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null
) {
    val conversation by viewModel.conversation.collectAsState()
    val contact by viewModel.contact.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val showSendButton by viewModel.showSendButton.collectAsState()
    val isGroupChat = conversation?.isGroupChat == true
    val contactAvatarMap = remember { viewModel.contactAvatarMap }
    val listState = rememberLazyListState()
    val totalListItems = remember(messages) { calculateListItemCount(messages) }

    LaunchedEffect(totalListItems) {
        if (totalListItems > 0) {
            listState.scrollToItem(totalListItems - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                showSendButton = showSendButton,
                onInputChanged = viewModel::onInputChanged,
                onAddClick = onAddAttachmentClick,
                onCameraClick = onCameraClick,
                onMicClick = onMicClick,
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
            ChatDetailTopBar(
                title = conversation?.groupName
                    ?: contact?.displayName
                    ?: conversation?.participantNames?.firstOrNull { it != "JiayiDai" }
                    ?: conversation?.participantNames?.firstOrNull()
                    ?: "Chat",
                showOnlineStatus = conversation?.isGroupChat == false && contact?.isOnline == true,
                avatarUrl = viewModel.chatAvatarUrl,
                onBackClick = onBackClick,
                onVideoCallClick = onVideoCallClick,
                onVoiceCallClick = onVoiceCallClick,
                onAvatarClick = onAvatarClick,
                onTitleClick = onTitleClick,
                modifier = Modifier.statusBarsPadding()
            )

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

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    if (messages.isNotEmpty()) {
                        item(key = "first_date") {
                            DateDivider(label = formatDateDivider(messages.first().sentAt))
                        }
                    }

                    item(key = "encryption_banner") {
                        EncryptionBanner(onLearnMoreClick = onLearnMoreClick)
                    }

                    itemsIndexed(
                        items = messages,
                        key = { _, message -> message.id }
                    ) { index, message ->
                        if (index > 0 && !isSameDay(messages[index - 1].sentAt, message.sentAt)) {
                            DateDivider(label = formatDateDivider(message.sentAt))
                        }

                        val isSelf = message.senderId == CURRENT_USER_ID
                        val isLastInGroup = index == messages.lastIndex ||
                            messages[index + 1].senderId != message.senderId
                        val isFirstInGroup = index == 0 ||
                            messages[index - 1].senderId != message.senderId

                        ChatMessageBubble(
                            message = message,
                            isSelf = isSelf,
                            topSpacing = when {
                                index == 0 -> 8.dp
                                messages[index - 1].senderId == message.senderId -> 2.dp
                                else -> 8.dp
                            },
                            isGroupChat = isGroupChat,
                            showAvatar = isGroupChat && !isSelf && isLastInGroup,
                            showSenderName = isGroupChat && !isSelf && isFirstInGroup,
                            senderAvatarUrl = contactAvatarMap[message.senderName]
                        )
                    }
                }
            }
        }
    }
}

private fun calculateListItemCount(messages: List<Message>): Int {
    if (messages.isEmpty()) {
        return 1
    }

    val dateDividerCount = 1 + messages.zipWithNext().count { (previous, current) ->
        !isSameDay(previous.sentAt, current.sentAt)
    }
    return messages.size + dateDividerCount + 1
}

private fun formatDateDivider(timestamp: Long): String {
    val messageCalendar = Calendar.getInstance().apply { time = Date(timestamp) }
    val todayCalendar = Calendar.getInstance()
    val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(messageCalendar.timeInMillis, todayCalendar.timeInMillis) -> "Today"
        isSameDay(messageCalendar.timeInMillis, yesterdayCalendar.timeInMillis) -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(timestamp))
    }
}

private fun isSameDay(firstTimestamp: Long, secondTimestamp: Long): Boolean {
    val firstCalendar = Calendar.getInstance().apply { time = Date(firstTimestamp) }
    val secondCalendar = Calendar.getInstance().apply { time = Date(secondTimestamp) }
    return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
        firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR)
}
