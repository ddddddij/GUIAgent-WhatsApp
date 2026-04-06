package com.example.whatsapp_sim.ui.screen.channelstatus

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.domain.model.Status
import com.example.whatsapp_sim.ui.components.ReactionBar
import com.example.whatsapp_sim.ui.components.StatusPostCard
import com.example.whatsapp_sim.ui.screen.chatdetail.components.DateDivider

private val TopBarBg = Color(0xFFF5F0E8)
private val PageBg = Color(0xFFEDE8DC)
private val WhatsAppGreen = Color(0xFF25D366)

@Composable
fun ChannelStatusScreen(
    viewModel: ChannelStatusViewModel,
    frequentContacts: List<Contact>,
    groups: List<Conversation>,
    communities: List<Community>,
    onBackClick: () -> Unit
) {
    val channel by viewModel.channel.collectAsState()
    val statusPosts by viewModel.statusPosts.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val emojiSheetStatusId by viewModel.emojiSheetStatusId.collectAsState()
    val shareSheetStatusId by viewModel.shareSheetStatusId.collectAsState()

    // Group posts by dateLabel maintaining order
    val grouped = buildGroupedPosts(statusPosts)
    val firstPost = statusPosts.firstOrNull()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PageBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(TopBarBg)
                        .padding(start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Outlined.ArrowBackIosNew, "Back", tint = Color(0xFF3C3C43))
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC5B8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF6B5ECD)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = channel?.name ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            if (channel?.isVerified == true) {
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    null,
                                    modifier = Modifier.size(14.dp),
                                    tint = WhatsAppGreen
                                )
                            }
                        }
                        Text(
                            text = channel?.followersCount ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF8E8E8E)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleMute() }) {
                        Icon(
                            imageVector = if (isMuted) Icons.Outlined.NotificationsOff else Icons.Outlined.NotificationsNone,
                            contentDescription = "Mute",
                            tint = Color(0xFF3C3C43)
                        )
                    }
                    IconButton(onClick = { /* Coming soon */ }) {
                        Icon(Icons.Filled.MoreVert, "More", tint = Color(0xFF3C3C43))
                    }
                }
            }
        },
        bottomBar = {
            if (firstPost != null) {
                Column {
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color.White)
                            .padding(horizontal = 16.dp)
                    ) {
                        ReactionBar(
                            status = firstPost,
                            onEmojiClick = { viewModel.onEmojiClick(firstPost.id) },
                            onShareClick = { viewModel.onShareClick(firstPost.id) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            grouped.forEach { (dateLabel, posts) ->
                item(key = "date_$dateLabel") {
                    DateDivider(label = dateLabel)
                }
                items(posts, key = { it.id }) { status ->
                    StatusPostCard(
                        status = status,
                        onEmojiClick = { viewModel.onEmojiClick(status.id) },
                        onShareClick = { viewModel.onShareClick(status.id) }
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    // Emoji sheet
    if (emojiSheetStatusId != null) {
        StatusEmojiSheet(
            onEmojiSelected = { emoji ->
                emojiSheetStatusId?.let { viewModel.sendReaction(it, emoji) }
            },
            onDismiss = { viewModel.dismissEmojiSheet() }
        )
    }

    // Share sheet
    if (shareSheetStatusId != null) {
        StatusShareSheet(
            frequentContacts = frequentContacts,
            groups = groups,
            communities = communities,
            onShareToContact = { contact ->
                shareSheetStatusId?.let { statusId ->
                    viewModel.shareStatusToContact(contact, statusId)
                }
            },
            onShareToConversation = { conversationId, _ ->
                shareSheetStatusId?.let { statusId ->
                    viewModel.shareStatusTo(conversationId, statusId)
                }
            },
            onShareToCommunity = { community ->
                shareSheetStatusId?.let { statusId ->
                    viewModel.shareStatusToCommunity(community, statusId)
                }
            },
            onDismiss = { viewModel.dismissShareSheet() }
        )
    }
}

/** Returns list of (dateLabel, posts) preserving insertion order of dates. */
private fun buildGroupedPosts(posts: List<Status>): List<Pair<String, List<Status>>> {
    val result = mutableListOf<Pair<String, MutableList<Status>>>()
    for (post in posts) {
        val existing = result.lastOrNull()?.takeIf { it.first == post.dateLabel }
        if (existing != null) {
            existing.second.add(post)
        } else {
            result.add(post.dateLabel to mutableListOf(post))
        }
    }
    return result
}
