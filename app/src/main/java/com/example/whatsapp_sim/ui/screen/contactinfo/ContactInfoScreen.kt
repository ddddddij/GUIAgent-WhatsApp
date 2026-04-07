package com.example.whatsapp_sim.ui.screen.contactinfo

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Videocam
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.ContactInfoSettingItem
import com.example.whatsapp_sim.ui.components.ContactAvatar

private val PageBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val DividerColor = Color(0xFFE5E5EA)
private val AvatarPurple = Color(0xFFC5B8F0)
private val WhatsAppGreen = Color(0xFF25D366)
private val TextSecondary = Color(0xFF8E8E8E)
private val IconGray = Color(0xFF3C3C43)
private val GreenAction = Color(0xFF25D366)
private val RedAction = Color(0xFFE53935)
private val CreateGroupBg = Color(0xFFE5E5EA)
private val CreateGroupIcon = Color(0xFF5C5C5C)

@Composable
fun ContactInfoScreen(
    viewModel: ContactInfoViewModel,
    onBackClick: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onAudioCallClick: () -> Unit = {},
    onVideoCallClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    val contact by viewModel.contact.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val listState = rememberLazyListState()

    val showContactName by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 200
        }
    }

    val contactName = contact?.displayName ?: "Contact"
    val phone = contact?.phone ?: ""

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PageBg,
        topBar = {
            ContactInfoTopBar(
                title = if (showContactName) contactName else "Contact info",
                onBackClick = onBackClick,
                onEditClick = { toast() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBg),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
        ) {
            // Hero section
            item {
                ContactHeroSection(
                    name = contactName,
                    phone = phone,
                    avatarUrl = contact?.avatarUrl
                )
            }

            // Quick actions
            item {
                QuickActionsCard(
                    onMessageClick = {
                        val convId = viewModel.onMessageClick()
                        if (convId.isNotEmpty()) onNavigateToChat(convId)
                    },
                    onAudioClick = { onAudioCallClick() },
                    onVideoClick = { onVideoCallClick() }
                )
            }

            // Settings group 1: Media & Starred
            item {
                SettingsCardGroup1(onItemClick = { viewModel.onSettingItemClick(it); toast() })
            }

            // Settings group 2: Notifications, Theme, Save
            item {
                SettingsCardGroup2(onItemClick = { viewModel.onSettingItemClick(it); toast() })
            }

            // Settings group 3: Privacy & Security
            item {
                SettingsCardGroup3(
                    isLocked = isLocked,
                    onToggleLock = { viewModel.toggleLock() },
                    onItemClick = { viewModel.onSettingItemClick(it); toast() }
                )
            }

            // Settings group 4: Contact details
            item {
                SettingsCardGroup4(onItemClick = { viewModel.onSettingItemClick(it); toast() })
            }

            // No groups in common
            item {
                NoGroupsSection(
                    contactName = contactName,
                    onCreateGroupClick = { viewModel.onSettingItemClick(ContactInfoItem.CREATE_GROUP); toast() }
                )
            }

            // Actions card
            item {
                ActionsCard(onItemClick = { viewModel.onSettingItemClick(it); toast() })
            }

            // Danger card
            item {
                DangerActionsCard(
                    contactName = contactName,
                    onBlockClick = { viewModel.onBlockClick(); toast() },
                    onReportClick = { viewModel.onReportClick(); toast() }
                )
            }
        }
    }
}

@Composable
private fun ContactInfoTopBar(
    title: String,
    onBackClick: () -> Unit,
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
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = IconGray
            )
        }

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = onEditClick) {
            Text(text = "Edit", fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Composable
private fun ContactHeroSection(name: String, phone: String, avatarUrl: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContactAvatar(avatarUrl = avatarUrl, size = 96.dp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = phone,
            fontSize = 15.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun QuickActionsCard(
    onMessageClick: () -> Unit,
    onAudioClick: () -> Unit,
    onVideoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionColumn(
                icon = Icons.Outlined.Chat,
                label = "Message",
                onClick = onMessageClick,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(DividerColor)
            )

            QuickActionColumn(
                icon = Icons.Outlined.Call,
                label = "Audio",
                onClick = onAudioClick,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(DividerColor)
            )

            QuickActionColumn(
                icon = Icons.Outlined.Videocam,
                label = "Video",
                onClick = onVideoClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = WhatsAppGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 13.sp, color = Color.Black)
    }
}

@Composable
private fun SettingsCardGroup1(onItemClick: (ContactInfoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ContactInfoSettingItem(
            icon = Icons.Outlined.Image,
            label = "Media, links and docs",
            rightLabel = "None",
            onClick = { onItemClick(ContactInfoItem.MEDIA_LINKS_DOCS) }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.StarBorder,
            label = "Starred",
            rightLabel = "None",
            onClick = { onItemClick(ContactInfoItem.STARRED) }
        )
    }
}

@Composable
private fun SettingsCardGroup2(onItemClick: (ContactInfoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ContactInfoSettingItem(
            icon = Icons.Outlined.NotificationsNone,
            label = "Notifications",
            onClick = { onItemClick(ContactInfoItem.NOTIFICATIONS) }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.Palette,
            label = "Chat theme",
            onClick = { onItemClick(ContactInfoItem.CHAT_THEME) }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.SaveAlt,
            label = "Save to Photos",
            rightLabel = "Default",
            onClick = { onItemClick(ContactInfoItem.SAVE_TO_PHOTOS) }
        )
    }
}

@Composable
private fun SettingsCardGroup3(
    isLocked: Boolean,
    onToggleLock: () -> Unit,
    onItemClick: (ContactInfoItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ContactInfoSettingItem(
            icon = Icons.Outlined.Timer,
            label = "Disappearing messages",
            rightLabel = "Off",
            onClick = { onItemClick(ContactInfoItem.DISAPPEARING_MESSAGES) }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.Lock,
            label = "Lock chat",
            subLabel = "Lock and hide this chat on this device.",
            showChevron = false,
            showToggle = true,
            toggleChecked = isLocked,
            onToggleChange = { onToggleLock() },
            onClick = { onToggleLock() }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.Shield,
            label = "Advanced chat privacy",
            rightLabel = "Off",
            onClick = { onItemClick(ContactInfoItem.ADVANCED_PRIVACY) }
        )
        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        ContactInfoSettingItem(
            icon = Icons.Outlined.Lock,
            label = "Encryption",
            subLabel = "Messages and calls are end-to-end encrypted. Tap to verify.",
            onClick = { onItemClick(ContactInfoItem.ENCRYPTION) }
        )
    }
}

@Composable
private fun SettingsCardGroup4(onItemClick: (ContactInfoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ContactInfoSettingItem(
            icon = Icons.Outlined.AccountCircle,
            label = "Contact details",
            onClick = { onItemClick(ContactInfoItem.CONTACT_DETAILS) }
        )
    }
}

@Composable
private fun NoGroupsSection(contactName: String, onCreateGroupClick: () -> Unit) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = "No groups in common",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable { onCreateGroupClick() }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CreateGroupBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = CreateGroupIcon
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Create group with $contactName",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ActionsCard(onItemClick: (ContactInfoItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ActionRow("Share contact", GreenAction) { onItemClick(ContactInfoItem.SHARE_CONTACT) }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        ActionRow("Add to Favorites", GreenAction) { onItemClick(ContactInfoItem.ADD_TO_FAVORITES) }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        ActionRow("Add to list", GreenAction) { onItemClick(ContactInfoItem.ADD_TO_LIST) }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        ActionRow("Export chat", GreenAction) { onItemClick(ContactInfoItem.EXPORT_CHAT) }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        ActionRow("Clear chat", RedAction) { onItemClick(ContactInfoItem.CLEAR_CHAT) }
    }
}

@Composable
private fun DangerActionsCard(
    contactName: String,
    onBlockClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        ActionRow("Block $contactName", RedAction) { onBlockClick() }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
        ActionRow("Report $contactName", RedAction) { onReportClick() }
    }
}

@Composable
private fun ActionRow(text: String, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, fontSize = 16.sp, color = textColor)
    }
}
