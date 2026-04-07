package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.CallResult
import com.example.whatsapp_sim.domain.model.CallType
import com.example.whatsapp_sim.domain.model.MessageType

private val BubbleGreen = Color(0xFFDCF8C6)
private val IconCircleBg = Color(0xFF3A3A3A)
private val TextSecondary = Color(0xFF8E8E8E)
private val MissedRed = Color(0xFFE53935)

@Composable
fun CallRecordCard(
    messageType: MessageType,
    callResult: CallResult?,
    callDurationDisplay: String?,
    isSelf: Boolean,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    val isVoice = messageType == MessageType.VOICE_CALL
    val typeLabel = if (isVoice) "Voice call" else "Video call"

    val resultIcon = when {
        callResult == CallResult.MISSED -> Icons.AutoMirrored.Filled.CallMissed
        callResult == CallResult.NO_ANSWER -> Icons.AutoMirrored.Filled.CallMade
        isSelf -> Icons.AutoMirrored.Filled.CallMade
        else -> Icons.AutoMirrored.Filled.CallReceived
    }

    val resultIconTint = when (callResult) {
        CallResult.MISSED, CallResult.NO_ANSWER -> MissedRed
        else -> Color(0xFF4CAF50)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BubbleGreen)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left circle icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(IconCircleBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isVoice) resultIcon else Icons.Outlined.Videocam,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Middle: type + duration
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = typeLabel,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = resultIcon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = resultIconTint
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = callDurationDisplay ?: "0 sec",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Right: timestamp
        Text(
            text = timestamp,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}
