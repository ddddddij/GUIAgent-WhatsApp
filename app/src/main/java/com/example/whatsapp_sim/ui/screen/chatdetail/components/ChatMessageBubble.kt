package com.example.whatsapp_sim.ui.screen.chatdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.AssetImage
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import com.example.whatsapp_sim.domain.model.MessageType
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.components.CallRecordCard
import com.example.whatsapp_sim.ui.components.ForwardedStatusCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Preset colors for group chat sender names
private val senderColors = listOf(
    Color(0xFF00BCD4),
    Color(0xFF9C27B0),
    Color(0xFF009688),
    Color(0xFFE91E63),
    Color(0xFF3F51B5),
    Color(0xFFFF5722),
    Color(0xFF8BC34A),
    Color(0xFFFF9800),
)

@Composable
fun ChatMessageBubble(
    message: Message,
    isSelf: Boolean,
    topSpacing: Dp,
    isGroupChat: Boolean = false,
    showAvatar: Boolean = false,
    showSenderName: Boolean = false,
    senderAvatarUrl: String? = null
) {
    val messageText = remember(message) { message.toDisplayText() }
    val emojiOnly = remember(messageText) { isEmojiOnlyText(messageText) }
    val maxBubbleWidth = LocalConfiguration.current.screenWidthDp.dp * 0.7f

    // Forwarded status card has its own layout
    if (message.messageType == MessageType.FORWARDED_STATUS) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isSelf) 56.dp else 12.dp,
                    end = if (isSelf) 12.dp else 56.dp,
                    top = topSpacing
                ),
            horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
        ) {
            ForwardedStatusCard(message = message)
        }
        return
    }

    // Call record card has its own layout
    if (message.messageType == MessageType.VOICE_CALL || message.messageType == MessageType.VIDEO_CALL) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isSelf) 56.dp else 12.dp,
                    end = if (isSelf) 12.dp else 56.dp,
                    top = topSpacing
                ),
            horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
        ) {
            CallRecordCard(
                messageType = message.messageType,
                callResult = message.callResult,
                callDurationDisplay = message.callDurationDisplay,
                isSelf = isSelf,
                timestamp = formatMessageTime(message.sentAt)
            )
        }
        return
    }

    val avatarSize = 38.dp
    val avatarGap = 6.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isSelf) 56.dp else 12.dp,
                end = if (isSelf) 12.dp else 56.dp,
                top = topSpacing
            ),
        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar area for group chat (other people's messages)
        if (isGroupChat && !isSelf) {
            if (showAvatar) {
                ContactAvatar(avatarUrl = senderAvatarUrl, size = avatarSize)
            } else {
                Spacer(modifier = Modifier.size(avatarSize))
            }
            Spacer(modifier = Modifier.width(avatarGap))
        }

        val contentModifier = if (emojiOnly) {
            Modifier
        } else {
            Modifier
                .widthIn(max = maxBubbleWidth)
                .background(
                    color = if (isSelf) Color(0xFFDCF8C6) else Color.White,
                    shape = if (isSelf) {
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 2.dp)
                    } else {
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 2.dp, bottomEnd = 12.dp)
                    }
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        }

        if (message.messageType == MessageType.IMAGE) {
            ImageMessageContent(
                message = message,
                isSelf = isSelf,
                isGroupChat = isGroupChat,
                showSenderName = showSenderName,
                maxBubbleWidth = maxBubbleWidth
            )
            return@Row
        }

        Column(modifier = contentModifier) {
            // Sender name in group chat
            if (isGroupChat && !isSelf && showSenderName && !emojiOnly) {
                Text(
                    text = message.senderName,
                    color = rememberSenderColor(message.senderId),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = messageText,
                    modifier = if (emojiOnly) Modifier else Modifier.weight(1f, fill = false),
                    color = Color.Black,
                    fontSize = if (emojiOnly) 36.sp else 16.sp,
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier = Modifier.padding(start = 6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatMessageTime(message.sentAt),
                        color = Color(0xFF8E8E8E),
                        fontSize = 11.sp
                    )

                    if (isSelf) {
                        Text(
                            text = " ${message.messageStatus.toStatusSymbol()}",
                            color = if (message.messageStatus == MessageStatus.READ) Color(0xFF4FC3F7) else Color(0xFF8E8E8E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageMessageContent(
    message: Message,
    isSelf: Boolean,
    isGroupChat: Boolean,
    showSenderName: Boolean,
    maxBubbleWidth: Dp
) {
    Column(
        modifier = Modifier
            .widthIn(max = maxBubbleWidth)
            .background(
                color = if (isSelf) Color(0xFFDCF8C6) else Color.White,
                shape = if (isSelf) {
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 2.dp)
                } else {
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 2.dp, bottomEnd = 12.dp)
                }
            )
            .padding(4.dp)
    ) {
        if (isGroupChat && !isSelf && showSenderName) {
            Text(
                text = message.senderName,
                color = rememberSenderColor(message.senderId),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 6.dp)
            )
        }

        AssetImage(
            imagePath = message.mediaUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        if (!message.textContent.isNullOrBlank()) {
            Text(
                text = message.textContent.orEmpty(),
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = formatMessageTime(message.sentAt),
                color = Color(0xFF8E8E8E),
                fontSize = 11.sp
            )

            if (isSelf) {
                Text(
                    text = " ${message.messageStatus.toStatusSymbol()}",
                    color = if (message.messageStatus == MessageStatus.READ) Color(0xFF4FC3F7) else Color(0xFF8E8E8E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun rememberSenderColor(senderId: String): Color {
    return remember(senderId) {
        senderColors[senderId.hashCode().and(0x7FFFFFFF) % senderColors.size]
    }
}

@Composable
fun DateDivider(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                color = Color(0xFF555555),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EncryptionBanner(
    onLearnMoreClick: () -> Unit
) {
    val bannerText = buildAnnotatedString {
        append("\uD83D\uDD12 ")
        append("Messages and calls are end-to-end encrypted. Only people in this chat can read, listen to, or share them. ")
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append("Learn more")
        pop()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onLearnMoreClick)
            .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bannerText,
            color = Color(0xFF555555),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Message.toDisplayText(): String {
    return when (messageType) {
        MessageType.TEXT -> textContent.orEmpty()
        MessageType.IMAGE -> textContent ?: "\uD83D\uDCF7 Photo"
        MessageType.AUDIO -> textContent ?: "\uD83C\uDFA4 Audio"
        MessageType.DOCUMENT -> textContent ?: "\uD83D\uDCC4 Document"
        MessageType.LOCATION -> textContent ?: "\uD83D\uDCCD Location"
        MessageType.GIF -> textContent ?: "GIF"
        MessageType.FORWARDED_STATUS -> textContent ?: "[Channel post]"
        MessageType.COMMUNITY_ANNOUNCEMENT -> textContent ?: "[Community announcement]"
        MessageType.VOICE_CALL -> "Voice call"
        MessageType.VIDEO_CALL -> "Video call"
    }
}

private fun MessageStatus.toStatusSymbol(): String {
    return when (this) {
        MessageStatus.READ,
        MessageStatus.DELIVERED -> "✓✓"
        MessageStatus.SENT,
        MessageStatus.SENDING -> "✓"
        MessageStatus.FAILED -> "!"
    }
}

private fun formatMessageTime(timestamp: Long): String {
    return SimpleDateFormat("h:mm a", Locale.US).format(Date(timestamp))
}

private fun isEmojiOnlyText(text: String): Boolean {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) {
        return false
    }

    var hasEmojiLikeCodePoint = false
    var index = 0
    while (index < trimmed.length) {
        val codePoint = trimmed.codePointAt(index)
        index += Character.charCount(codePoint)

        if (Character.isWhitespace(codePoint)) {
            continue
        }

        if (!isAllowedEmojiCodePoint(codePoint)) {
            return false
        }
        hasEmojiLikeCodePoint = true
    }

    return hasEmojiLikeCodePoint
}

private fun isAllowedEmojiCodePoint(codePoint: Int): Boolean {
    return when {
        codePoint == 0x200D || codePoint == 0xFE0F -> true
        codePoint in 0x1F000..0x1FAFF -> true
        codePoint in 0x2600..0x27BF -> true
        codePoint in 0x2300..0x23FF -> true
        else -> false
    }
}
