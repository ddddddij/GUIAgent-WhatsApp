package com.example.whatsapp_sim.ui.screen.chatdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.ContactAvatar

@Composable
fun ChatDetailTopBar(
    title: String,
    showOnlineStatus: Boolean,
    avatarUrl: String? = null,
    onBackClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null,
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

        ContactAvatar(
            avatarUrl = avatarUrl,
            size = 40.dp,
            modifier = Modifier.padding(start = 4.dp),
            onClick = onAvatarClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (showOnlineStatus) {
                Text(
                    text = "online",
                    color = Color(0xFF8E8E8E),
                    fontSize = 12.sp
                )
            }
        }

        IconButton(
            onClick = onVideoCallClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Videocam,
                contentDescription = "Video call",
                modifier = Modifier.size(28.dp),
                tint = Color(0xFF3C3C43)
            )
        }

        IconButton(
            onClick = onVoiceCallClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = "Voice call",
                modifier = Modifier.size(26.dp),
                tint = Color(0xFF3C3C43)
            )
        }
    }
}
