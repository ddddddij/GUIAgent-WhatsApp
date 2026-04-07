package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private val BarBackground = Color(0xFF2A2A2A)
private val HangUpRed = Color(0xFFE53935)
private val ActiveBg = Color.White
private val ActiveIcon = Color(0xFF2A2A2A)
private val InactiveIcon = Color.White

@Composable
fun CallControlBar(
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isVideoEnabled: Boolean,
    onMoreClick: () -> Unit,
    onVideoToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    onHangUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(88.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(BarBackground),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // More
            ControlButton(
                icon = Icons.Filled.MoreHoriz,
                isActive = false,
                onClick = onMoreClick
            )

            // Video toggle
            ControlButton(
                icon = if (isVideoEnabled) Icons.Outlined.Videocam else Icons.Outlined.VideocamOff,
                isActive = isVideoEnabled,
                onClick = onVideoToggle
            )

            // Speaker
            ControlButton(
                icon = if (isSpeakerOn) Icons.Filled.VolumeUp else Icons.Outlined.VolumeUp,
                isActive = isSpeakerOn,
                onClick = onSpeakerToggle
            )

            // Mute
            ControlButton(
                icon = if (isMuted) Icons.Filled.MicOff else Icons.Outlined.Mic,
                isActive = isMuted,
                onClick = onMuteToggle
            )

            // Hang up
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(HangUpRed)
                    .clickable { onHangUp() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CallEnd,
                    contentDescription = "Hang up",
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (isActive) ActiveBg else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            tint = if (isActive) ActiveIcon else InactiveIcon
        )
    }
}
