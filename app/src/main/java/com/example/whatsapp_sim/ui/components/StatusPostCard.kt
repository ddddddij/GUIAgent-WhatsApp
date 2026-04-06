package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Status
import com.example.whatsapp_sim.domain.model.StatusContentType

@Composable
fun StatusPostCard(
    status: Status,
    onEmojiClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Media area
            when (status.contentType) {
                StatusContentType.VIDEO_WITH_TEXT -> {
                    VideoPlaceholder(duration = status.videoDuration ?: "")
                    Spacer(Modifier.height(8.dp))
                }
                StatusContentType.IMAGE_ONLY, StatusContentType.IMAGE_WITH_TEXT -> {
                    ImagePlaceholder()
                    Spacer(Modifier.height(8.dp))
                }
                StatusContentType.MULTI_IMAGE -> {
                    MultiImageGrid(timestamp = status.timestamp)
                    Spacer(Modifier.height(8.dp))
                }
                StatusContentType.TEXT_ONLY -> { /* no media */ }
            }

            // Text content + timestamp
            if (status.textContent != null || status.contentType == StatusContentType.TEXT_ONLY) {
                TextContentArea(
                    text = status.textContent ?: "",
                    timestamp = status.timestamp,
                    isEdited = status.isEdited
                )
            } else {
                // Timestamp for image-only
                Text(
                    text = status.timestamp,
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E8E),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            ReactionBar(
                status = status,
                onEmojiClick = onEmojiClick,
                onShareClick = onShareClick
            )
        }
    }
}

@Composable
private fun VideoPlaceholder(duration: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Videocam,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = duration, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFCCCCCC)),
        contentAlignment = Alignment.Center
    ) {
        Text("📷", fontSize = 40.sp)
    }
}

@Composable
private fun MultiImageGrid(timestamp: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFCCCCCC)),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text("📷", fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
                    Text(
                        text = timestamp,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0x88000000))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFCCCCCC)),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text("📷", fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
                    Text(
                        text = timestamp,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0x88000000))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TextContentArea(text: String, timestamp: String, isEdited: Boolean) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            if (isEdited) {
                Text(
                    text = "Edited ",
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E8E),
                    fontStyle = FontStyle.Italic
                )
            }
            Text(text = timestamp, fontSize = 13.sp, color = Color(0xFF8E8E8E))
        }
    }
}
