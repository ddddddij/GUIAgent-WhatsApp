package com.example.whatsapp_sim.ui.screen.userstatus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class BgOption(val color: Color, val label: String)

private val BG_OPTIONS = listOf(
    BgOption(Color(0xFF128C7E), "Green"),
    BgOption(Color(0xFF6B5ECD), "Purple"),
    BgOption(Color(0xFFD44000), "Red"),
    BgOption(Color(0xFFFFFFFF), "White"),
    BgOption(Color(0xFFE91E8C), "Pink"),
    BgOption(Color(0xFF1565C0), "Blue"),
    BgOption(Color(0xFFF9A825), "Yellow"),
)

@Composable
fun AddTextStatusScreen(
    onClose: () -> Unit,
    onSend: (text: String, bgColor: Long) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedBgIndex by remember { mutableIntStateOf(0) }
    val bg = BG_OPTIONS[selectedBgIndex]
    val isLight = selectedBgIndex == 3 || selectedBgIndex == 6  // White or Yellow
    val textColor = if (isLight) Color.Black else Color.White
    val hintColor = if (isLight) Color(0xFF888888) else Color.White.copy(alpha = 0.55f)

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg.color)
            .imePadding()
    ) {
        // ── Top bar ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // Close button
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = textColor)
            }

            // Vertical color picker on right
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BG_OPTIONS.forEachIndexed { index, option ->
                    val isSelected = index == selectedBgIndex
                    val dotSize = if (isSelected) 28.dp else 22.dp
                    val dotColor = if (option.color == Color.White) Color(0xFFDDDDDD) else option.color
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor)
                            .clickable { selectedBgIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            // White ring indicator
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }
        }

        // ── Center text input ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                ),
                cursorBrush = SolidColor(textColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.Center) {
                        if (text.isEmpty()) {
                            Text(
                                "Type a status",
                                style = TextStyle(
                                    color = hintColor,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        inner()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }

        // ── Send button ──────────────────────────────────────────────
        if (text.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 24.dp, bottom = 24.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366))
                    .clickable {
                        // toArgb() returns standard ARGB int; store as Long with 0xFF alpha mask
                        val argb = bg.color.toArgb()
                        onSend(text.trim(), argb.toLong())
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
