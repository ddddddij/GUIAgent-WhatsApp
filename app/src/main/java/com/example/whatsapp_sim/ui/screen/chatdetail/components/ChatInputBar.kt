package com.example.whatsapp_sim.ui.screen.chatdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatInputBar(
    inputText: String,
    showSendButton: Boolean,
    onInputChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMicClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter),
            color = Color(0xFFE0E0E0)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add attachment",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF3C3C43)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(Color(0xFFF2F2F7), RoundedCornerShape(24.dp))
                    .padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Message",
                                color = Color(0xFF8E8E8E),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Box(
                    modifier = Modifier.padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEmotions,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF8E8E8E)
                    )
                }
            }

            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Camera",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF3C3C43)
                )
            }

            IconButton(
                onClick = if (showSendButton) onSendClick else onMicClick,
                modifier = Modifier.padding(end = 0.dp)
            ) {
                Icon(
                    imageVector = if (showSendButton) Icons.AutoMirrored.Filled.Send else Icons.Outlined.Mic,
                    contentDescription = if (showSendButton) "Send" else "Mic",
                    modifier = Modifier.size(28.dp),
                    tint = if (showSendButton) Color(0xFF25D366) else Color(0xFF3C3C43)
                )
            }
        }
    }
}
