package com.example.whatsapp_sim.ui.screen.groupinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.ui.components.ContactAvatar

private val WhatsAppGreen = Color(0xFF25D366)
private val TextSecondary = Color(0xFF8E8E8E)
private val DividerColor = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberSheet(
    allContacts: List<Contact>,
    existingMemberIds: List<String>,
    resolveMemberId: (Contact) -> String = { it.id },
    onDismiss: () -> Unit,
    onAddMember: (Contact) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery, allContacts) {
        if (searchQuery.isBlank()) allContacts
        else allContacts.filter { it.displayName.contains(searchQuery, ignoreCase = true) }
    }

    val grouped = remember(filtered) {
        filtered
            .groupBy { it.displayName.first().uppercaseChar().toString() }
            .entries
            .sortedBy { it.key }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(80.dp))
                Text(
                    text = "Add members",
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier.width(80.dp).padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).background(Color(0xFFF2F2F7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF8E8E8E)
                            )
                        }
                    }
                }
            }

            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2F2F7), RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF8E8E8E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) Text("Search name or number", color = Color(0xFF8E8E8E), fontSize = 14.sp)
                        inner()
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Contact list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                grouped.forEach { (letter, contacts) ->
                    item(key = "header_$letter") {
                        Text(
                            text = letter,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(contacts, key = { it.id }) { contact ->
                        val isAlreadyMember = existingMemberIds.contains(resolveMemberId(contact))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .alpha(if (isAlreadyMember) 0.5f else 1f)
                                .then(
                                    if (!isAlreadyMember) Modifier.clickable { onAddMember(contact) }
                                    else Modifier
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContactAvatar(avatarUrl = contact.avatarUrl, size = 52.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = contact.displayName,
                                color = Color.Black,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAlreadyMember) {
                                Text(
                                    text = "Already added",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        HorizontalDivider(
                            color = DividerColor,
                            modifier = Modifier.padding(start = 86.dp)
                        )
                    }
                }
                if (grouped.isEmpty()) {
                    item {
                        Text(
                            text = "No contacts found",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}
