package com.example.whatsapp_sim.ui.screen.chats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Chat
import com.example.whatsapp_sim.domain.model.MessageStatus

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit
) {
    val whatsAppGreen = Color(0xFF25D366)
    val textSecondary = Color(0xFF8E8E8E)
    val dividerGray = Color(0xFFF0F0F0)
    val avatarBg = Color(0xFFC5B8F0)
    val avatarIcon = Color(0xFF6B5ECD)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar - 56dp circle with purple background (same as UpdatesTab)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = avatarIcon
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle section
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    chat.lastMessageStatus?.let { status ->
                        Icon(
                            imageVector = when (status) {
                                MessageStatus.SENT, MessageStatus.SENDING -> Icons.Filled.Done
                                MessageStatus.DELIVERED, MessageStatus.READ -> Icons.Filled.DoneAll
                                MessageStatus.FAILED -> Icons.Filled.Done
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (status == MessageStatus.READ) Color(0xFF53BDEB) else textSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    chat.lastMessageSender?.let { sender ->
                        Text(text = "$sender: ", color = textSecondary, fontSize = 14.sp)
                    }

                    if (chat.isTyping) {
                        Text(text = "typing...", color = whatsAppGreen, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    } else {
                        Text(
                            text = chat.lastMessage,
                            color = textSecondary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right section
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chat.timestamp,
                    color = if (chat.unreadCount > 0) whatsAppGreen else textSecondary,
                    fontSize = 13.sp
                )
                when {
                    chat.unreadCount > 0 -> {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(whatsAppGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = chat.unreadCount.toString(), color = Color.White, fontSize = 12.sp)
                        }
                    }
                    chat.isPinned -> {
                        Icon(Icons.Filled.PushPin, contentDescription = "Pinned", modifier = Modifier.size(16.dp), tint = textSecondary)
                    }
                    chat.isMuted -> {
                        Icon(Icons.Outlined.NotificationsOff, contentDescription = "Muted", modifier = Modifier.size(16.dp), tint = textSecondary)
                    }
                }
            }
        }

        // Divider aligned with text (86dp left padding, same as UpdatesTab)
        HorizontalDivider(color = dividerGray, thickness = 1.dp, modifier = Modifier.padding(start = 86.dp))
    }
}
