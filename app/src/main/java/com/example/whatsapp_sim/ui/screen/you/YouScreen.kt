package com.example.whatsapp_sim.ui.screen.you

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.R
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.components.SettingsItem

private val HeroBg = Color(0xFFF5F0E8)
private val PageBg = Color(0xFFF2F2F7)
private val IconGray = Color(0xFF3C3C43)
private val DividerColor = Color(0xFFE0E0E0)
private val WhatsAppGreen = Color(0xFF25D366)
private val AvatarBg = Color(0xFFC5B8F0)
private val AvatarIcon = Color(0xFF6B5ECD)

@Composable
fun YouScreen(viewModel: YouViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero section: background image covers top bar + avatar only
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.you_tab_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )

            // Content on top of image
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onSearchClick(); toast() }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.onQrCodeClick(); toast() }) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = "QR Code", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status bubble above avatar
                StatusBubble(onClick = { viewModel.onStatusBubbleClick(); toast() })

                Spacer(modifier = Modifier.height(4.dp))

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.onAvatarClick(); toast() },
                    contentAlignment = Alignment.Center
                ) {
                    ContactAvatar(avatarUrl = currentUser?.avatarUrl, size = 120.dp)
                }
            }
        } // end Hero Box

        // User name below hero area (on white/gray background)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PageBg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "JiayiDai",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Settings group title
        Text(
            text = "Settings",
            fontSize = 14.sp,
            color = Color(0xFF8E8E8E),
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )

        // First settings card
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                SettingsItem(Icons.Outlined.GroupAdd, "Invite a friend") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.AutoMirrored.Outlined.ViewList, "Lists") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.Campaign, "Broadcast messages") {
                    context.startActivity(
                        com.example.whatsapp_sim.BroadcastListActivity.createIntent(context)
                    )
                }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.StarBorder, "Starred") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.Laptop, "Linked devices") { toast() }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second settings card (no title)
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                SettingsItem(Icons.Outlined.Key, "Account") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.Lock, "Privacy") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.AutoMirrored.Outlined.Chat, "Chats") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.NotificationsNone, "Notifications") { toast() }
                HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp))
                SettingsItem(Icons.Outlined.ImportExport, "Storage and data") { toast() }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatusBubble(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Bubble
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Free to chat", fontSize = 14.sp, color = Color.Black)
            }
        }
        // Triangle pointer (rotated square)
        Box(
            modifier = Modifier
                .offset(y = (-4).dp)
                .size(8.dp)
                .rotate(45f)
                .background(Color.White)
        )
    }
}
