package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ChannelStatusActivity
import com.example.whatsapp_sim.domain.model.Message
import com.example.whatsapp_sim.domain.model.MessageStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ForwardedStatusCard(
    message: Message,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFDCF8C6))
    ) {
        Column {
            // ① Forwarded label
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Reply,
                    contentDescription = null,
                    tint = Color(0xFF8E8E8E),
                    modifier = Modifier
                        .graphicsLayer(scaleX = -1f)
                        .width(14.dp)
                        .height(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Forwarded",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E8E),
                    fontStyle = FontStyle.Italic
                )
            }

            // ② Source channel name
            Text(
                text = message.forwardedFrom ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF25D366),
                modifier = Modifier.padding(start = 8.dp, top = 2.dp, end = 8.dp)
            )

            // ③ Image placeholder (if has image)
            if (message.forwardedImageResName != null) {
                AssetImage(
                    imagePath = parseAssetImagePaths(message.forwardedImageResName).firstOrNull(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            // ④ Text content
            if (!message.textContent.isNullOrBlank()) {
                Text(
                    text = message.textContent,
                    fontSize = 15.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
                )
            }

            // ⑤ Timestamp + status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatTime(message.sentAt),
                    fontSize = 11.sp,
                    color = Color(0xFF8E8E8E)
                )
                Text(
                    text = " ${message.messageStatus.toSymbol()}",
                    fontSize = 11.sp,
                    color = if (message.messageStatus == MessageStatus.READ) Color(0xFF4FC3F7) else Color(0xFF8E8E8E)
                )
            }

            // ⑥ View channel button
            HorizontalDivider(color = Color(0xFFC8F7A0), thickness = 1.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable {
                        val channelId = message.forwardedChannelId
                        if (!channelId.isNullOrBlank()) {
                            context.startActivity(
                                ChannelStatusActivity.createIntent(context, channelId)
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View channel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF25D366)
                )
            }
        }
    }
}

private fun MessageStatus.toSymbol() = when (this) {
    MessageStatus.READ, MessageStatus.DELIVERED -> "✓✓"
    MessageStatus.SENT, MessageStatus.SENDING -> "✓"
    MessageStatus.FAILED -> "!"
}

private fun formatTime(millis: Long): String =
    SimpleDateFormat("h:mm a", Locale.US).format(Date(millis))
