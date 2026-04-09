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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.ArrowBackIosNew
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.CallAvatarArea
import com.example.whatsapp_sim.ui.components.CallControlBar
import kotlinx.coroutines.delay

private val ScreenBg = Color(0xFF111111)
private val TextWhite = Color.White
private val TextSecondary = Color(0xFFAAAAAA)
private val IconBg = Color(0xFF3A3A3A)

@Composable
fun CallScreen(
    viewModel: CallViewModel,
    onBackClick: () -> Unit,
    onSwitchToVideo: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back/minimize button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp),
                    tint = TextWhite
                )
            }

            // Center: name + timer
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

            // Add member button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { toast() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PersonAdd,
                    contentDescription = "Add member",
                    modifier = Modifier.size(18.dp),
                    tint = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.3f))

        // Center: Avatar area
        CallAvatarArea(
            avatarUrls = viewModel.avatarUrls,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Bottom: Control bar
        CallControlBar(
            isMuted = isMuted,
            isSpeakerOn = isSpeakerOn,
            isVideoEnabled = isVideoEnabled,
            onMoreClick = { toast() },
            onVideoToggle = {
                viewModel.hangUp()
                onSwitchToVideo()
            },
            onSpeakerToggle = { viewModel.toggleSpeaker() },
            onMuteToggle = { viewModel.toggleMute() },
            onHangUp = {
                viewModel.hangUp()
                onHangUp()
            }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}
