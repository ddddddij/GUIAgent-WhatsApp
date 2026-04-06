package com.example.whatsapp_sim.ui.screen.updates

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.GridView
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.whatsapp_sim.domain.model.Channel

private val AvatarBg = Color(0xFFC5B8F0)
private val AvatarIcon = Color(0xFF6B5ECD)
private val WhatsAppGreen = Color(0xFF25D366)
private val IconBg = Color(0xFFF2F2F7)
private val IconGray = Color(0xFF3C3C43)
private val TextSecondary = Color(0xFF8E8E8E)
private val DividerColor = Color(0xFFF0F0F0)

@Composable
fun UpdatesScreen(viewModel: UpdatesViewModel) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
    val isExpanded by viewModel.isChannelSectionExpanded.collectAsState()
    val channels by viewModel.channels.collectAsState()
    val followingState by viewModel.followingState.collectAsState()
    val followingChannels = channels.filter { followingState[it.id] == true }

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
                Icon(
                    Icons.Filled.MoreHoriz,
                    contentDescription = "More",
                    modifier = Modifier.size(20.dp),
                    tint = IconGray
                )
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
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        // Status bubbles: my status + contacts, horizontal scroll
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // My own status bubble (with + badge)
            item {
                StatusBubble(
                    name = "My status",
                    timeLabel = viewModel.myStatus.timeLabel,
                    isViewed = false,
                    showAddBadge = true,
                    onClick = { viewModel.onAddStatusClick(); toast() }
                )
            }
            // Friends' statuses
            items(viewModel.recentStatuses) { status ->
                StatusBubble(
                    name = status.senderName,
                    timeLabel = status.timeLabel,
                    isViewed = status.isViewed,
                    showAddBadge = false,
                    onClick = toast
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

        // Followed channels with latest updates
        if (followingChannels.isNotEmpty()) {
            followingChannels.forEachIndexed { index, channel ->
                FollowedChannelUpdateRow(channel = channel, onClick = toast)
                if (index < followingChannels.lastIndex) {
                    HorizontalDivider(
                        color = DividerColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 86.dp)
                    )
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

        // Channel list (animated)
        AnimatedVisibility(visible = isExpanded) {
            Column {
                channels.forEachIndexed { index, channel ->
                    ChannelItem(
                        channel = channel,
                        isFollowing = followingState[channel.id] ?: false,
                        onFollowClick = { viewModel.toggleFollow(channel.id) }
                    )
                    if (index < channels.lastIndex) {
                        HorizontalDivider(
                            color = DividerColor,
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 86.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Explore more button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(IconBg)
                .clickable { viewModel.onExploreMoreClick(); toast() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.GridView, contentDescription = null, modifier = Modifier.size(20.dp), tint = IconGray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explore more", fontSize = 16.sp, color = IconGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ChannelItem(
    channel: Channel,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Channel logo
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(AvatarBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = AvatarIcon
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = channel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (channel.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = WhatsAppGreen
                    )
                }
            }
            Text(text = channel.followersCount, fontSize = 13.sp, color = TextSecondary)
        }

        // Follow button
        FollowButton(isFollowing = isFollowing, onClick = onFollowClick)
    }
}

@Composable
private fun StatusBubble(
    name: String,
    timeLabel: String,
    isViewed: Boolean,
    showAddBadge: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.size(64.dp)) {
            val ringColor = if (showAddBadge || !isViewed) WhatsAppGreen else TextSecondary
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ringColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AvatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = AvatarIcon
                        )
                    }
                }
            }
            if (showAddBadge) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 12.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = timeLabel,
            fontSize = 11.sp,
            color = TextSecondary,
            maxLines = 1,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
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
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(AvatarBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = AvatarIcon
            )
        }
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
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = WhatsAppGreen
                    )
                }
            }
            Text(
                text = channel.latestUpdate ?: "",
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (channel.updateTime != null) {
            Text(channel.updateTime, fontSize = 12.sp, color = TextSecondary)
        }
    }
}
