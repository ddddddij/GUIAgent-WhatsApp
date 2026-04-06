package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Status

private val WhatsAppGreen = Color(0xFF25D366)
private val ReactionBg = Color(0xFFE8F5E9)

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> String.format("%.1fK", count / 1000.0)
        else -> "$count"
    }
}

@Composable
fun ReactionBar(
    status: Status,
    onEmojiClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasUserReaction = status.userReaction != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: emoji list + total count，用户已选时显示绿色背景
        Box(
            modifier = Modifier
                .background(
                    color = if (hasUserReaction) ReactionBg else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onEmojiClick)
                .padding(horizontal = if (hasUserReaction) 8.dp else 0.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Show user reaction first if exists, then up to 3 others
                val displayReactions = if (hasUserReaction) {
                    val userEmoji = status.userReaction!!
                    val others = status.reactions
                        .filter { it.emoji != userEmoji }
                        .take(3)
                    listOf(userEmoji) + others.map { it.emoji }
                } else {
                    status.reactions.take(4).map { it.emoji }
                }
                displayReactions.take(4).forEach { emoji ->
                    Text(text = emoji, fontSize = 18.sp)
                    Spacer(Modifier.width(2.dp))
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatCount(status.reactionCount),
                    fontSize = 13.sp,
                    color = if (hasUserReaction) WhatsAppGreen else Color(0xFF555555)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Right: share button
        Row(
            modifier = Modifier.clickable(onClick = onShareClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Reply,
                contentDescription = "Share",
                tint = Color(0xFF555555),
                modifier = Modifier
                    .graphicsLayer(scaleX = -1f)
                    .width(20.dp)
                    .height(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = formatCount(status.shareCount),
                fontSize = 13.sp,
                color = Color(0xFF555555)
            )
        }
    }
}
