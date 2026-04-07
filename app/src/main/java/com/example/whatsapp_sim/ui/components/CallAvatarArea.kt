package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallAvatarArea(
    avatarUrls: List<String?>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when {
            avatarUrls.size <= 1 -> {
                ContactAvatar(
                    avatarUrl = avatarUrls.firstOrNull(),
                    size = 140.dp
                )
            }
            else -> {
                val gridItems = avatarUrls.take(4)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (rowStart in gridItems.indices step 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ContactAvatar(
                                avatarUrl = gridItems[rowStart],
                                size = 88.dp
                            )
                            if (rowStart + 1 < gridItems.size) {
                                ContactAvatar(
                                    avatarUrl = gridItems[rowStart + 1],
                                    size = 88.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
