package com.example.whatsapp_sim.ui.screen.groupinfo

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ContactInfoActivity
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.components.ContactInfoSettingItem

private val PageBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val DividerColor = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)
private val WhatsAppGreen = Color(0xFF25D366)
private val IconGray = Color(0xFF3C3C43)
private val GreenAction = Color(0xFF25D366)
private val RedAction = Color(0xFFE53935)
private val GroupAvatarBg = Color(0xFFF5E6D0)
private val GroupAvatarIcon = Color(0xFF8B6914)
private val MemberCircleBg = Color(0xFFE5E5EA)
private val MemberCircleIcon = Color(0xFF5C5C5C)

@Composable
fun GroupInfoScreen(
    viewModel: GroupInfoViewModel,
    onBackClick: () -> Unit,
    onExitGroup: () -> Unit,
    onNavigateToCall: (conversationId: String, memberIds: Array<String>, memberAvatars: Array<String>, memberNames: Array<String>) -> Unit,
    onNavigateToVideoCall: (conversationId: String, memberIds: Array<String>, memberAvatars: Array<String>, memberNames: Array<String>) -> Unit
) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    val conversation by viewModel.conversation.collectAsState()
    val members by viewModel.members.collectAsState()
    val groupDetail by viewModel.groupDetail.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val showAddMemberSheet by viewModel.showAddMemberSheet.collectAsState()
    val showExitDialog by viewModel.showExitDialog.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val existingMemberIds by viewModel.existingMemberIds.collectAsState()

    val listState = rememberLazyListState()
    val showGroupName by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 200
        }
    }

    val groupName = conversation?.groupName ?: "Group"
    val memberCount = members.size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PageBg,
        topBar = {
            GroupInfoTopBar(
                title = if (showGroupName) groupName else "Group info",
                onBackClick = onBackClick,
                onQrCodeClick = { toast() },
                onEditClick = { toast() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(PageBg),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
        ) {
            // Hero section
            item {
                HeroSection(
                    groupName = groupName,
                    memberCount = memberCount,
                    avatarUrl = groupDetail?.avatarUrl
                )
            }

            // Quick actions
            item {
                QuickActionsCard(
                    onAudioClick = {
                        val conv = conversation ?: return@QuickActionsCard
                        val callMembers = members.filterNot { it.isSelf }
                        val names = callMembers.map { it.displayName }.toTypedArray()
                        val avatars = callMembers.map { it.avatarUrl ?: "" }.toTypedArray()
                        val ids = callMembers.map { it.userId }.toTypedArray()
                        onNavigateToCall(conv.id, ids, avatars, names)
                    },
                    onVideoClick = {
                        val conv = conversation ?: return@QuickActionsCard
                        val callMembers = members.filterNot { it.isSelf }
                        val names = callMembers.map { it.displayName }.toTypedArray()
                        val avatars = callMembers.map { it.avatarUrl ?: "" }.toTypedArray()
                        val ids = callMembers.map { it.userId }.toTypedArray()
                        onNavigateToVideoCall(conv.id, ids, avatars, names)
                    },
                    onAddClick = { viewModel.onAddClick() },
                    onSearchClick = { toast() }
                )
            }

            // Group description
            item {
                GroupDescriptionCard(
                    description = conversation?.description,
                    onClick = { toast() }
                )
            }

            // Settings group 1: Media & Starred
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ContactInfoSettingItem(Icons.Outlined.Image, "Media, links and docs", rightLabel = "None") { viewModel.onSettingItemClick(GroupInfoItem.MEDIA_LINKS_DOCS); toast() }
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(Icons.Outlined.StarBorder, "Starred", rightLabel = "None") { viewModel.onSettingItemClick(GroupInfoItem.STARRED); toast() }
                }
            }

            // Settings group 2: Notifications
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ContactInfoSettingItem(
                        icon = Icons.Outlined.NotificationsNone,
                        label = "Notifications",
                        subLabel = "Mute this group on this device.",
                        showChevron = false,
                        showToggle = true,
                        toggleChecked = isMuted,
                        onToggleChange = { viewModel.toggleMute() },
                        onClick = { viewModel.toggleMute() }
                    )
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(Icons.Outlined.Palette, "Chat theme") { viewModel.onSettingItemClick(GroupInfoItem.CHAT_THEME); toast() }
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(Icons.Outlined.SaveAlt, "Save to Photos", rightLabel = "Default") { viewModel.onSettingItemClick(GroupInfoItem.SAVE_TO_PHOTOS); toast() }
                }
            }

            // Settings group 3: Privacy & Security
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ContactInfoSettingItem(Icons.Outlined.Timer, "Disappearing messages", rightLabel = "Off") { viewModel.onSettingItemClick(GroupInfoItem.DISAPPEARING_MESSAGES); toast() }
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(Icons.Outlined.Settings, "Group permissions") { viewModel.onSettingItemClick(GroupInfoItem.GROUP_PERMISSIONS); toast() }
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(
                        icon = Icons.Outlined.Lock,
                        label = "Lock chat",
                        subLabel = "Lock and hide this chat on this device.",
                        showChevron = false,
                        showToggle = true,
                        toggleChecked = isLocked,
                        onToggleChange = { viewModel.toggleLock() },
                        onClick = { viewModel.toggleLock() }
                    )
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(Icons.Outlined.Shield, "Advanced chat privacy", rightLabel = "Off") { viewModel.onSettingItemClick(GroupInfoItem.ADVANCED_PRIVACY); toast() }
                }
            }

            // Settings group 4: Encryption
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ContactInfoSettingItem(
                        icon = Icons.Outlined.Lock,
                        label = "Encryption",
                        subLabel = "Messages and calls are end-to-end encrypted. Tap to learn more."
                    ) { viewModel.onSettingItemClick(GroupInfoItem.ENCRYPTION); toast() }
                }
            }

            // Add to community card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ContactInfoSettingItem(
                        icon = Icons.Outlined.Groups,
                        label = "Add group to a community",
                        subLabel = "Bring members together in topic-based groups."
                    ) { viewModel.onSettingItemClick(GroupInfoItem.ADD_TO_COMMUNITY); toast() }
                }
            }

            // Members section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$memberCount members",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { toast() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search members", tint = IconGray, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Members card
            item {
                MembersCard(
                    members = members,
                    onAddMembersClick = { viewModel.onAddClick() },
                    onInviteClick = { toast() },
                    onMemberClick = { member ->
                        member.contactId?.let { contactId ->
                            context.startActivity(ContactInfoActivity.createIntent(context, contactId))
                        }
                    }
                )
            }

            // Actions card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ActionRow("Add to Favorites", GreenAction) { viewModel.onSettingItemClick(GroupInfoItem.ADD_TO_FAVORITES); toast() }
                    HorizontalDivider(color = DividerColor)
                    ActionRow("Add to list", GreenAction) { viewModel.onSettingItemClick(GroupInfoItem.ADD_TO_LIST); toast() }
                    HorizontalDivider(color = DividerColor)
                    ActionRow("Export chat", GreenAction) { viewModel.onSettingItemClick(GroupInfoItem.EXPORT_CHAT); toast() }
                    HorizontalDivider(color = DividerColor)
                    ActionRow("Clear chat", RedAction) { viewModel.onSettingItemClick(GroupInfoItem.CLEAR_CHAT); toast() }
                }
            }

            // Danger card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    ActionRow("Exit group", RedAction) { viewModel.onExitGroupClick() }
                    HorizontalDivider(color = DividerColor)
                    ActionRow("Report group", RedAction) { viewModel.onSettingItemClick(GroupInfoItem.REPORT_GROUP); toast() }
                }
            }

            // Created info
            item {
                val conv = conversation
                if (conv != null) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 32.dp)
                    ) {
                        Text("Created by ${conv.createdBy}.", fontSize = 13.sp, color = TextSecondary)
                        Text("Created on ${conv.createdAtDisplay}.", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            }
        }
    }

    // Add member sheet
    if (showAddMemberSheet) {
        AddMemberSheet(
            allContacts = allContacts,
            existingMemberIds = existingMemberIds,
            resolveMemberId = viewModel::resolveMemberId,
            onDismiss = { viewModel.dismissAddMemberSheet() },
            onAddMember = { contact ->
                viewModel.onAddMember(contact)
                Toast.makeText(context, "Added ${contact.displayName}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Exit group dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissExitDialog() },
            title = { Text("Exit group?") },
            text = { Text("You will no longer receive messages from this group.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.confirmExitGroup()
                    onExitGroup()
                }) {
                    Text("Exit", color = RedAction)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissExitDialog() }) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Composable
private fun GroupInfoTopBar(
    title: String,
    onBackClick: () -> Unit,
    onQrCodeClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(PageBg)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(22.dp), tint = IconGray)
        }
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onQrCodeClick) {
            Icon(Icons.Outlined.QrCode, contentDescription = "QR Code", modifier = Modifier.size(24.dp), tint = IconGray)
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(24.dp), tint = IconGray)
        }
    }
}

@Composable
private fun HeroSection(groupName: String, memberCount: Int, avatarUrl: String?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (avatarUrl != null) {
            ContactAvatar(avatarUrl = avatarUrl, size = 96.dp)
        } else {
            Box(
                modifier = Modifier.size(96.dp).clip(CircleShape).background(GroupAvatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Group, contentDescription = null, modifier = Modifier.size(52.dp), tint = GroupAvatarIcon)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = groupName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Group · $memberCount members",
            fontSize = 15.sp,
            color = WhatsAppGreen
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun QuickActionsCard(
    onAudioClick: () -> Unit,
    onVideoClick: () -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionColumn(Icons.Outlined.Call, "Audio", onAudioClick, Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
            QuickActionColumn(Icons.Outlined.Videocam, "Video", onVideoClick, Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
            QuickActionColumn(Icons.Outlined.PersonAdd, "Add", onAddClick, Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
            QuickActionColumn(Icons.Outlined.Search, "Search", onSearchClick, Modifier.weight(1f))
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun QuickActionColumn(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clickable { onClick() }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(28.dp), tint = WhatsAppGreen)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 13.sp, color = Color.Black)
    }
}

@Composable
private fun GroupDescriptionCard(description: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        if (description != null) {
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        } else {
            Text(
                text = "Add group description",
                fontSize = 16.sp,
                color = WhatsAppGreen,
                modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp)
            )
        }
    }
}

@Composable
private fun MembersCard(
    members: List<GroupInfoMemberUiModel>,
    onAddMembersClick: () -> Unit,
    onInviteClick: () -> Unit,
    onMemberClick: (GroupInfoMemberUiModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // Add members row
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).clickable { onAddMembersClick() }.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(MemberCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp), tint = MemberCircleIcon)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Add members", fontSize = 16.sp, color = Color.Black)
        }
        HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 86.dp))

        // Invite via link row
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).clickable { onInviteClick() }.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(MemberCircleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(20.dp), tint = MemberCircleIcon)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Invite via link or QR code", fontSize = 16.sp, color = Color.Black)
        }
        HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 86.dp))

        members.forEachIndexed { index, member ->
            if (index > 0) {
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 86.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .then(
                        if (member.contactId != null && !member.isSelf) {
                            Modifier.clickable { onMemberClick(member) }
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (member.isSelf && member.avatarUrl == null) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(GroupAvatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(52.dp), tint = GroupAvatarIcon)
                    }
                } else {
                    ContactAvatar(avatarUrl = member.avatarUrl, size = 52.dp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (member.isSelf) "You" else member.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        if (member.isAdmin) {
                            Text("Admin", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    Text(
                        text = member.about ?: "",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                if (member.contactId != null && !member.isSelf) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFC7C7CC))
                }
            }
        }
    }
}

@Composable
private fun ActionRow(text: String, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(52.dp).clickable { onClick() }.padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, fontSize = 16.sp, color = textColor)
    }
}
