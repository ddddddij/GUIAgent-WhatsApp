package com.example.whatsapp_sim.ui.screen.call

import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.CallControlBar
import kotlinx.coroutines.delay

private val GradientTop = Color(0xFF1A1A2E)
private val GradientBottom = Color(0xFF16213E)
private val TextWhite = Color.White
private val TextSecondary = Color(0xFFAAAAAA)
private val IconBg = Color(0xFF3A3A3A)
private val SelfPreviewBg = Color(0xFF2A2A3E)

@Composable
fun VideoCallScreen(
    viewModel: CallViewModel,
    onHangUp: () -> Unit
) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()
    val isVideoEnabled by viewModel.isVideoEnabled.collectAsState()
    val callDuration by viewModel.callDurationSeconds.collectAsState()
    val formattedDuration = remember(callDuration) {
        val m = callDuration / 60
        val s = callDuration % 60
        "%d:%02d".format(m, s)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            viewModel.incrementTimer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientBottom)
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = viewModel.contactName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = formattedDuration,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        // Self preview (bottom right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-16).dp, y = (-160).dp)
                .width(120.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SelfPreviewBg)
        )

        // Right side action buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add member
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { toast() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PersonAdd,
                    contentDescription = "Add member",
                    modifier = Modifier.size(22.dp),
                    tint = TextWhite
                )
            }

            // Flip camera
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { toast() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Cameraswitch,
                    contentDescription = "Flip camera",
                    modifier = Modifier.size(22.dp),
                    tint = TextWhite
                )
            }

            // Flash
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { toast() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FlashOn,
                    contentDescription = "Flash",
                    modifier = Modifier.size(22.dp),
                    tint = TextWhite
                )
            }
        }

        // Bottom control bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            CallControlBar(
                isMuted = isMuted,
                isSpeakerOn = isSpeakerOn,
                isVideoEnabled = isVideoEnabled,
                onMoreClick = { toast() },
                onVideoToggle = { viewModel.toggleVideo() },
                onSpeakerToggle = { viewModel.toggleSpeaker() },
                onMuteToggle = { viewModel.toggleMute() },
                onHangUp = {
                    viewModel.hangUp()
                    onHangUp()
                }
            )
        }
    }
}
