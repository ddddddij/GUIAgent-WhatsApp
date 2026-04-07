package com.example.whatsapp_sim.ui.screen.channelstatus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.domain.model.Conversation
import com.example.whatsapp_sim.ui.components.ContactAvatar

private val AvatarPurple = Color(0xFFC5B8F0)
private val AvatarDark = Color(0xFF6B5ECD)
private val SearchBg = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusShareSheet(
    frequentContacts: List<Contact>,
    groups: List<Conversation>,
    communities: List<Community>,
    onShareToContact: (contact: Contact) -> Unit,
    onShareToConversation: (conversationId: String, name: String) -> Unit,
    onShareToCommunity: (community: Community) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = remember(searchQuery, frequentContacts) {
        if (searchQuery.isBlank()) frequentContacts
        else frequentContacts.filter { it.displayName.contains(searchQuery, ignoreCase = true) }
    }
    val filteredGroups = remember(searchQuery, groups) {
        if (searchQuery.isBlank()) groups
        else groups.filter { (it.groupName ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredCommunities = remember(searchQuery, communities) {
        if (searchQuery.isBlank()) communities
        else communities.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    "Share to",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F7))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF3C3C43)
                    )
                }
            }

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(44.dp)
                    .background(SearchBg, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                        cursorBrush = SolidColor(Color.Black),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("Search contacts or groups", fontSize = 14.sp, color = TextSecondary)
                            }
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                // Frequently contacted
                if (filteredContacts.isNotEmpty()) {
                    item {
                        Text(
                            "Frequently contacted",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            Column {
                                filteredContacts.forEachIndexed { index, contact ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(68.dp)
                                            .clickable {
                                                onShareToContact(contact)
                                                Toast.makeText(context, "Shared to ${contact.displayName}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ContactAvatar(avatarUrl = contact.avatarUrl, size = 44.dp)
                                        Spacer(Modifier.width(12.dp))
                                        Text(contact.displayName, fontSize = 16.sp, color = Color.Black)
                                    }
                                    if (index < filteredContacts.lastIndex) {
                                        Box(modifier = Modifier.padding(start = 72.dp)) {
                                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Groups
                if (filteredGroups.isNotEmpty()) {
                    item {
                        Text(
                            "Groups",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            Column {
                                filteredGroups.forEachIndexed { index, conv ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(68.dp)
                                            .clickable {
                                                onShareToConversation(conv.id, conv.groupName ?: "Group")
                                                Toast.makeText(context, "Shared to ${conv.groupName}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(AvatarPurple, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Groups, null, modifier = Modifier.size(28.dp), tint = AvatarDark)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(conv.groupName ?: "Group", fontSize = 16.sp, color = Color.Black)
                                    }
                                    if (index < filteredGroups.lastIndex) {
                                        Box(modifier = Modifier.padding(start = 72.dp)) {
                                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Communities
                if (filteredCommunities.isNotEmpty()) {
                    item {
                        Text(
                            "Communities",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(filteredCommunities) { community ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .clickable {
                                    onShareToCommunity(community)
                                    Toast.makeText(context, "Shared to ${community.name}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8E8E8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Groups, null, modifier = Modifier.size(28.dp), tint = Color(0xFF6B5ECD))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(community.name, fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
