package com.example.whatsapp_sim.ui.screen.broadcast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.BroadcastList

private val WhatsAppGreen = Color(0xFF25D366)
private val PageBg = Color(0xFFF2F2F7)
private val TextSecondary = Color(0xFF8E8E8E)
private val DividerColor = Color(0xFFE5E5EA)

@Composable
fun BroadcastListScreen(
    viewModel: BroadcastListViewModel,
    onBackClick: () -> Unit,
    onBroadcastClick: (BroadcastList) -> Unit
) {
    val lists by viewModel.lists.collectAsState()
    val showNewBroadcastSheet by viewModel.showNewBroadcastSheet.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .statusBarsPadding()
    ) {
        // Top bar: back | centered title | Edit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color(0xFF3C3C43)
                )
            }
            Text(
                text = "Broadcast messages",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { /* Edit mode - coming soon */ }) {
                Text("Edit", fontSize = 16.sp, color = WhatsAppGreen)
            }
        }

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        if (lists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFFD0D0D0)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No broadcast lists yet",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap New List to create your first broadcast",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // New List button in empty state
                    NewListRow(onClick = { viewModel.onNewBroadcastClick() })
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                LazyColumn {
                    items(lists, key = { it.id }) { broadcastList ->
                        BroadcastListRow(
                            broadcastList = broadcastList,
                            onClick = { onBroadcastClick(broadcastList) }
                        )
                        if (lists.last().id != broadcastList.id) {
                            HorizontalDivider(
                                color = DividerColor,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 78.dp)
                            )
                        }
                    }
                    // "New List" row at the bottom of the list
                    item {
                        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 78.dp))
                        NewListRow(onClick = { viewModel.onNewBroadcastClick() })
                    }
                }
            }
        }
    }

    if (showNewBroadcastSheet) {
        NewBroadcastBottomSheet(
            viewModel = viewModel.newBroadcastViewModel,
            onDismiss = { viewModel.onNewBroadcastSheetDismiss() },
            onCreated = { broadcastList ->
                viewModel.onBroadcastCreated(broadcastList)
            }
        )
    }
}

@Composable
private fun BroadcastListRow(
    broadcastList: BroadcastList,
    onClick: () -> Unit
) {
    val totalCount = broadcastList.memberIds.size + 1 // +1 for "You"
    val recipientLabel = buildString {
        broadcastList.memberNames.forEachIndexed { i, name ->
            if (i > 0) append(", ")
            append(name)
        }
        if (broadcastList.memberNames.isNotEmpty()) append(", ")
        append("You")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$totalCount recipients",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = recipientLabel,
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NewListRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "New list",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
