package com.example.whatsapp_sim.ui.screen.communities

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Community

private val WhatsAppGreen = Color(0xFF25D366)
private val IconBg = Color(0xFFF2F2F7)
private val IconGray = Color(0xFF3C3C43)
private val TextSecondary = Color(0xFF8E8E8E)
private val DividerColor = Color(0xFFF0F0F0)
private val AvatarGray = Color(0xFFB0B0B0)

// Hardcoded timestamps per community for variety
private val communityTimestamps = mapOf(
    "community_001" to Pair("9:30", "Yesterday"),
    "community_002" to Pair("14:07", "10:52"),
    "community_003" to Pair("Yesterday", "16:33")
)

@Composable
fun CommunitiesScreen(viewModel: CommunitiesViewModel) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
    val communities by viewModel.communities.collectAsState()
    val showNewCommunitySheet by viewModel.showNewCommunitySheet.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        CommunitiesTopBar(
            onMoreMenuClick = { viewModel.onMoreMenuClick(); toast() },
            onNewCommunityClick = { viewModel.onNewCommunityClick() }
        )

        Text(
            text = "Communities",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
        )

        communities.forEach { community ->
            val timestamps = communityTimestamps[community.id] ?: Pair("9:00", "Yesterday")
            CommunitySection(
                community = community,
                announcementsTime = timestamps.first,
                generalTime = timestamps.second,
                onSeeAllClick = { viewModel.onSeeAllClick(community.id); toast() },
                onAnnouncementsClick = { viewModel.onAnnouncementsClick(community.id); toast() },
                onGeneralClick = { viewModel.onGeneralClick(community.id); toast() }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showNewCommunitySheet) {
        NewCommunityBottomSheet(
            viewModel = viewModel.newCommunityViewModel,
            onDismiss = { viewModel.onNewCommunitySheetDismiss() },
            onCreateCommunity = { community ->
                viewModel.addCommunity(community)
            }
        )
    }
}

@Composable
fun CommunitiesTopBar(onMoreMenuClick: () -> Unit, onNewCommunityClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(IconBg)
                .clickable { onMoreMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.MoreHoriz,
                contentDescription = "More",
                modifier = Modifier.size(20.dp),
                tint = IconGray
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen)
                .clickable { onNewCommunityClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "New Community",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun CommunitySection(
    community: Community,
    announcementsTime: String,
    generalTime: String,
    onSeeAllClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onGeneralClick: () -> Unit
) {
    Column {
        // Community title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = community.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            // "See all" gray rounded button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBg)
                    .clickable { onSeeAllClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "See all",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Announcements row
        AnnouncementsRow(timestamp = announcementsTime, onClick = onAnnouncementsClick)

        // Divider between Announcements and General
        HorizontalDivider(
            color = DividerColor,
            thickness = 1.dp,
            modifier = Modifier.padding(start = 80.dp, end = 16.dp)
        )

        // General row
        GeneralRow(timestamp = generalTime, onClick = onGeneralClick)

        // Section bottom divider
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
    }
}

@Composable
fun AnnouncementsRow(timestamp: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Green circle avatar with megaphone icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Campaign,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Announcements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Ask members to start chatting", fontSize = 13.sp, color = TextSecondary)
        }
        Text(text = timestamp, fontSize = 13.sp, color = TextSecondary)
    }
}

@Composable
fun GeneralRow(timestamp: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gray circle avatar with groups icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AvatarGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Groups,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "General", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Ask members to start chatting", fontSize = 13.sp, color = TextSecondary)
        }
        Text(text = timestamp, fontSize = 13.sp, color = TextSecondary)
    }
}
