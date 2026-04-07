package com.example.whatsapp_sim.ui.screen.updates

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.AddTextStatusActivity
import com.example.whatsapp_sim.ChannelStatusActivity
import com.example.whatsapp_sim.UserStatusDetailActivity
import com.example.whatsapp_sim.UserStatusStore
import com.example.whatsapp_sim.domain.model.Channel
import com.example.whatsapp_sim.domain.model.UserStatus
import com.example.whatsapp_sim.ui.components.ContactAvatar

private val WhatsAppGreen = Color(0xFF25D366)
private val IconBg = Color(0xFFF2F2F7)
private val IconGray = Color(0xFF3C3C43)
private val TextSecondary = Color(0xFF8E8E8E)
private val DividerColor = Color(0xFFF0F0F0)

@Composable
fun UpdatesScreen(viewModel: UpdatesViewModel) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
    val onChannelClick = { channelId: String ->
        context.startActivity(
            Intent(context, ChannelStatusActivity::class.java).putExtra("channelId", channelId)
        )
    }
    val isExpanded by viewModel.isChannelSectionExpanded.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val followingState by viewModel.followingState.collectAsState()
    val followingChannels = channels.filter { followingState[it.id] == true }

    // Read from shared store — observeAll() triggers recomposition on changes
    val allStatuses = UserStatusStore.observeAll()
    val myStatus = allStatuses.firstOrNull { it.id == "my_status_001" }
    val recentStatuses = allStatuses.filter { it.id != "my_status_001" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top action bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IconBg)
                    .clickable { viewModel.onMoreMenuClick(); toast() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.MoreHoriz, null, modifier = Modifier.size(20.dp), tint = IconGray)
            }
        }

        // Page title
        Text(
            text = "Updates",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
        )

        // Status section title
        Text(
            text = "Status",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        // Status cards row — rounded rectangle previews
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // My status card
            if (myStatus != null) {
                item {
                    MyStatusCard(
                        status = myStatus,
                        onClick = { context.startActivity(AddTextStatusActivity.createIntent(context)) }
                    )
                }
            }
            // Friends' status cards
            items(recentStatuses) { status ->
                FriendStatusCard(
                    status = status,
                    onClick = {
                        context.startActivity(
                            UserStatusDetailActivity.createIntent(context, status.id)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Channels section title
        Text(
            text = "Channels",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        // Followed channels
        if (followingChannels.isNotEmpty()) {
            followingChannels.forEachIndexed { index, channel ->
                FollowedChannelUpdateRow(channel = channel, onClick = { onChannelClick(channel.id) })
                if (index < followingChannels.lastIndex) {
                    HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 86.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Find channels collapsible header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { viewModel.toggleChannelSection() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Find channels to follow",
                fontSize = 14.sp,
                color = IconGray,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                channels.forEachIndexed { index, channel ->
                    ChannelItem(
                        channel = channel,
                        isFollowing = followingState[channel.id] ?: false,
                        onFollowClick = { viewModel.toggleFollow(channel.id) },
                        onChannelClick = { onChannelClick(channel.id) }
                    )
                    if (index < channels.lastIndex) {
                        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 86.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Rounded-rect status card for "My status" ───────────────────────────────

@Composable
private fun MyStatusCard(status: UserStatus, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(status.bgColor.toInt()))
                .clickable(onClick = onClick)
        ) {
            // Preview text
            Text(
                text = status.preview,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            // Add badge at bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(WhatsAppGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "My status",
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Rounded-rect status card for friends ────────────────────────────────────

@Composable
private fun FriendStatusCard(status: UserStatus, onClick: () -> Unit) {
    val ringColor = if (status.isViewed) TextSecondary else WhatsAppGreen

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.5.dp, ringColor, RoundedCornerShape(12.dp))
                .background(Color(status.bgColor.toInt()))
                .clickable(onClick = onClick)
        ) {
            // Preview text centered
            Text(
                text = status.preview,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
            // Small avatar at bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(26.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
            ) {
                ContactAvatar(avatarUrl = status.avatarUrl, size = 26.dp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = status.senderName.split(" ").first(),
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Channel item ─────────────────────────────────────────────────────────────

@Composable
private fun ChannelItem(
    channel: Channel,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onChannelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onChannelClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(avatarUrl = channel.avatarUrl, size = 56.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = channel.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                if (channel.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(14.dp), tint = WhatsAppGreen)
                }
            }
            Text(text = channel.followersCount, fontSize = 13.sp, color = TextSecondary)
        }
        FollowButton(isFollowing = isFollowing, onClick = onFollowClick)
    }
}

@Composable
private fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isFollowing) Color(0xFFF2F2F7) else Color(0xFFE8F5E9))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isFollowing) "Following" else "Follow",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isFollowing) TextSecondary else WhatsAppGreen
        )
    }
}

@Composable
private fun FollowedChannelUpdateRow(channel: Channel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(avatarUrl = channel.avatarUrl, size = 56.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = channel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (channel.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(14.dp), tint = WhatsAppGreen)
                }
            }
            Text(text = channel.latestUpdate ?: "", fontSize = 13.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (channel.updateTime != null) {
            Text(channel.updateTime, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
