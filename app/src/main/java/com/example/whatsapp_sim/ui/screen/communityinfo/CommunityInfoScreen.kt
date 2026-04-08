package com.example.whatsapp_sim.ui.screen.communityinfo

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ContactInfoActivity
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.components.ContactInfoSettingItem
import com.example.whatsapp_sim.ui.screen.groupinfo.AddMemberSheet

private val PageBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val DividerColor = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)
private val WhatsAppGreen = Color(0xFF25D366)
private val IconGray = Color(0xFF3C3C43)
private val AvatarBg = Color(0xFFE8ECE8)
private val AvatarIconColor = Color(0xFF4E6856)
private val MemberCircleBg = Color(0xFFE5E5EA)
private val MemberCircleIcon = Color(0xFF5C5C5C)

@Composable
fun CommunityInfoScreen(
    viewModel: CommunityInfoViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
    val community by viewModel.community.collectAsState()
    val members by viewModel.members.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val showAddMemberSheet by viewModel.showAddMemberSheet.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val existingMemberIds by viewModel.existingMemberIds.collectAsState()

    val name = community?.name ?: "Community"
    val memberCount = members.size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PageBg,
        topBar = {
            CommunityInfoTopBar(
                title = "Community info",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBg),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
        ) {
            item {
                CommunityHeroSection(
                    name = name,
                    memberCount = memberCount,
                    avatarUrl = community?.iconUrl
                )
            }

            item {
                CommunityQuickActionsCard(
                    onAddClick = viewModel::onAddClick,
                    onSearchClick = toast
                )
            }

            item {
                CommunityDescriptionCard(description = community?.description.orEmpty())
            }

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
                        subLabel = "Mute this community on this device.",
                        showChevron = false,
                        showToggle = true,
                        toggleChecked = isMuted,
                        onToggleChange = { viewModel.toggleMute() },
                        onClick = { viewModel.toggleMute() }
                    )
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
                    ContactInfoSettingItem(
                        icon = Icons.Outlined.Link,
                        label = "Invite via link",
                        rightLabel = "Share"
                    ) {
                        toast()
                    }
                }
            }

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
                    IconButton(onClick = toast, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search members", tint = IconGray, modifier = Modifier.size(24.dp))
                    }
                }
            }

            item {
                CommunityMembersCard(
                    members = members,
                    onAddMembersClick = viewModel::onAddClick,
                    onInviteClick = toast,
                    onMemberClick = { member ->
                        member.contactId?.let { contactId ->
                            context.startActivity(ContactInfoActivity.createIntent(context, contactId))
                        }
                    }
                )
            }

            item {
                community?.let { item ->
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 32.dp)
                    ) {
                        Text("Created by ${item.createdBy ?: "you"}.", fontSize = 13.sp, color = TextSecondary)
                        Text("Created on ${item.createdAtDisplay ?: ""}.", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            }
        }
    }

    if (showAddMemberSheet) {
        AddMemberSheet(
            allContacts = allContacts,
            existingMemberIds = existingMemberIds,
            resolveMemberId = viewModel::resolveMemberId,
            onDismiss = viewModel::dismissAddMemberSheet,
            onAddMember = { contact ->
                viewModel.onAddMember(contact)
                Toast.makeText(context, "Added ${contact.displayName}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun CommunityInfoTopBar(
    title: String,
    onBackClick: () -> Unit
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
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun CommunityHeroSection(
    name: String,
    memberCount: Int,
    avatarUrl: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (avatarUrl != null) {
            ContactAvatar(avatarUrl = avatarUrl, size = 96.dp)
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AvatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(48.dp), tint = AvatarIconColor)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Community · $memberCount members", fontSize = 15.sp, color = WhatsAppGreen)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CommunityQuickActionsCard(
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
            CommunityQuickAction(Icons.Outlined.PersonAdd, "Add", onAddClick, Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
            CommunityQuickAction(Icons.Outlined.Search, "Search", onSearchClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CommunityQuickAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() }.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp), tint = WhatsAppGreen)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 13.sp, color = Color.Black)
    }
}

@Composable
private fun CommunityDescriptionCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Composable
private fun CommunityMembersCard(
    members: List<CommunityInfoMemberUiModel>,
    onAddMembersClick: () -> Unit,
    onInviteClick: () -> Unit,
    onMemberClick: (CommunityInfoMemberUiModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
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
            Text("Invite via link", fontSize = 16.sp, color = Color.Black)
        }

        members.forEach { member ->
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 86.dp))
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
                ContactAvatar(avatarUrl = member.avatarUrl, size = 52.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (member.isSelf) "You" else member.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        member.roleLabel?.let { role ->
                            Text(role, fontSize = 13.sp, color = TextSecondary)
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
