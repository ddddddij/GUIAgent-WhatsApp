package com.example.whatsapp_sim.ui.screen.calls

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
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Videocam
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.data.repository.CallRepository
import com.example.whatsapp_sim.data.repository.CallWithContact
import com.example.whatsapp_sim.domain.model.CallStatus
import com.example.whatsapp_sim.domain.model.CallType

private val WhatsAppGreen = Color(0xFF25D366)
private val IconBg = Color(0xFFF2F2F7)
private val IconGray = Color(0xFF3C3C43)
private val TextSecondary = Color(0xFF8E8E8E)
private val MissedRed = Color(0xFFE53935)
private val DividerColor = Color(0xFFF0F0F0)
private val AvatarPurple = Color(0xFFC5B8F0)

@Composable
fun CallsScreen(viewModel: CallsViewModel) {
    val context = LocalContext.current
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }
    val recentCalls by viewModel.recentCalls.collectAsState()
    val showNewCallSheet by viewModel.showNewCallSheet.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        CallsTopBar(
            onMoreMenuClick = { viewModel.onMoreMenuClick(); toast() },
            onNewCallClick = { viewModel.onNewCallClick() }
        )

        // Page title
        Text(
            text = "Calls",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 12.dp)
        )

        CallsSearchBar(onClick = { viewModel.onSearchClick(); toast() })

        Spacer(modifier = Modifier.height(20.dp))

        // Favorites section title
        Text(
            text = "Favorites",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        AddFavoriteRow(onClick = { viewModel.onAddFavoriteClick(); toast() })

        Spacer(modifier = Modifier.height(20.dp))

        // Recent section title
        Text(
            text = "Recent",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        // Call log list
        Column {
            recentCalls.forEachIndexed { index, callWithContact ->
                val navigateToContact = {
                    val contactId = callWithContact.contactId
                    if (contactId != null) {
                        context.startActivity(
                            com.example.whatsapp_sim.ContactInfoActivity.createIntent(context, contactId)
                        )
                    } else {
                        toast()
                    }
                }
                CallLogItem(
                    callWithContact = callWithContact,
                    onItemClick = { navigateToContact() },
                    onDetailClick = { navigateToContact() }
                )
                if (index < recentCalls.lastIndex) {
                    HorizontalDivider(
                        color = DividerColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 86.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showNewCallSheet) {
        NewCallBottomSheet(
            viewModel = viewModel.newCallViewModel,
            onDismiss = { viewModel.onNewCallSheetDismiss() }
        )
    }
}

@Composable
fun CallsTopBar(onMoreMenuClick: () -> Unit, onNewCallClick: () -> Unit) {
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
                .clickable { onNewCallClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "New Call",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun CallsSearchBar(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(IconBg)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Search",
                modifier = Modifier.size(16.dp),
                tint = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Search", fontSize = 14.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AddFavoriteRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(IconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add",
                modifier = Modifier.size(24.dp),
                tint = IconGray
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = "Add favorite", fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun CallLogItem(
    callWithContact: CallWithContact,
    onItemClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val call = callWithContact.call
    val isMissed = call.callStatus == CallStatus.MISSED
    val nameColor = if (isMissed) MissedRed else Color.Black
    val timestamp = CallRepository.formatTimestamp(call.startedAt)

    // Determine call type display info using callStatus and callType
    val (callIcon, callIconTint, callTypeText) = when {
        isMissed -> Triple(Icons.AutoMirrored.Filled.CallMissed, MissedRed, "Missed")
        call.callStatus == CallStatus.INCOMING && call.callType == CallType.VIDEO ->
            Triple(Icons.Outlined.Videocam, TextSecondary, "Video")
        call.callStatus == CallStatus.INCOMING ->
            Triple(Icons.AutoMirrored.Filled.CallReceived, TextSecondary, "Incoming")
        call.callStatus == CallStatus.OUTGOING && call.callType == CallType.VIDEO ->
            Triple(Icons.Outlined.Videocam, TextSecondary, "Video")
        else -> Triple(Icons.AutoMirrored.Filled.CallMade, TextSecondary, "Outgoing")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(52.dp),
            tint = AvatarPurple
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Middle info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = callWithContact.contactName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = nameColor
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = callIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = callIconTint
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = callTypeText, fontSize = 14.sp, color = TextSecondary)
            }
        }

        // Right: timestamp + detail icon
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = timestamp, fontSize = 12.sp, color = TextSecondary)
            IconButton(
                onClick = { onDetailClick() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Detail",
                    modifier = Modifier.size(24.dp),
                    tint = TextSecondary
                )
            }
        }
    }
}
