package com.example.whatsapp_sim.ui.screen.chats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.screen.chats.NewGroupViewModel
import kotlinx.coroutines.launch

private val SheetBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val SearchBg = Color(0xFFE5E5EA)
private val DividerColor = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)
private val WhatsAppGreen = Color(0xFF25D366)
private val AvatarPurple = Color(0xFFC5B8F0)
private val AvatarDark = Color(0xFF6B5ECD)
private val UncheckedGray = Color(0xFFC7C7CC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGroupBottomSheet(
    viewModel: NewGroupViewModel,
    onDismiss: () -> Unit,
    onGroupCreated: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedIds by viewModel.selectedContactIds.collectAsState()
    val selectedContacts by viewModel.selectedContacts.collectAsState()
    val groupedContacts by viewModel.groupedContacts.collectAsState()
    val groupName by viewModel.groupName.collectAsState()
    val disappearingMessages by viewModel.disappearingMessages.collectAsState()
    val scrollTargetContactId by viewModel.scrollTargetContactId.collectAsState()

    // step: "select" or "create"
    val step = if (selectedIds.isEmpty() || groupName.isEmpty() && selectedIds.isNotEmpty()) "select" else "select"
    // We use a simple in-composable state flag
    val showCreateStep = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    val dismiss = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            viewModel.reset()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.reset()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = SheetBg,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            if (!showCreateStep.value) {
                // ── Step 1: Select contacts ──
                GroupTitleBar(
                    title = "New group",
                    leftContent = {
                        TextButton(onClick = { dismiss() }) {
                            Text("Cancel", fontSize = 14.sp, color = Color.Black)
                        }
                    },
                    rightContent = {
                        TextButton(
                            onClick = { if (selectedIds.isNotEmpty()) showCreateStep.value = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Text(
                                "Next",
                                fontSize = 14.sp,
                                color = if (selectedIds.isNotEmpty()) WhatsAppGreen else TextSecondary
                            )
                        }
                    }
                )

                GroupSearchBar(query = searchQuery, onQueryChanged = viewModel::onSearchQueryChanged)

                // Selected chips row
                if (selectedContacts.isNotEmpty()) {
                    SelectedContactsChips(
                        contacts = selectedContacts,
                        onRemove = { viewModel.onContactToggled(it.id) }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(scrollTargetContactId) {
                        val targetId = scrollTargetContactId ?: return@LaunchedEffect
                        val groupIndex = groupedContacts.indexOfFirst { group ->
                            group.contacts.any { it.id == targetId }
                        }
                        if (groupIndex < 0) return@LaunchedEffect
                        // Each group: header item + card item = 2 items per group
                        listState.animateScrollToItem(groupIndex * 2)
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        groupedContacts.forEach { group ->
                            item(key = "header_${group.letter}") {
                                GroupLetterHeader(letter = group.letter)
                            }
                            item(key = "card_${group.letter}") {
                                GroupContactCard(
                                    contacts = group.contacts,
                                    selectedIds = selectedIds,
                                    onToggle = viewModel::onContactToggled
                                )
                            }
                        }
                        if (groupedContacts.isEmpty()) {
                            item {
                                Text(
                                    "No contacts found",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // ── Step 2: Create group ──
                GroupTitleBar(
                    title = "New group",
                    leftContent = {
                        TextButton(onClick = { showCreateStep.value = false }) {
                            Text("Back", fontSize = 14.sp, color = Color.Black)
                        }
                    },
                    rightContent = {
                        TextButton(
                            onClick = {
                                val convId = viewModel.createGroup()
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    viewModel.reset()
                                    onGroupCreated(convId)
                                }
                            },
                            enabled = groupName.isNotBlank()
                        ) {
                            Text(
                                "Create",
                                fontSize = 14.sp,
                                color = if (groupName.isNotBlank()) WhatsAppGreen else TextSecondary
                            )
                        }
                    }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Group icon + name input
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Camera / avatar circle
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFD0D0D0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = "Add photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                BasicTextField(
                                    value = groupName,
                                    onValueChange = viewModel::onGroupNameChanged,
                                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                                    cursorBrush = SolidColor(WhatsAppGreen),
                                    singleLine = true,
                                    decorationBox = { inner ->
                                        if (groupName.isEmpty()) {
                                            Text(
                                                "Group name (required)",
                                                color = TextSecondary,
                                                fontSize = 16.sp
                                            )
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                HorizontalDivider(
                                    color = WhatsAppGreen,
                                    thickness = 1.5.dp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Disappearing messages
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                    .height(56.dp)
                                    .clickable { /* toggle */ }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Disappearing messages",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(disappearingMessages, fontSize = 14.sp, color = TextSecondary)
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Members section label
                    item {
                        Text(
                            "${selectedContacts.size} MEMBERS",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                        )
                    }

                    // Members list
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            selectedContacts.forEachIndexed { index, contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ContactAvatar(avatarUrl = contact.avatarUrl, size = 44.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = contact.displayName,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (index < selectedContacts.lastIndex) {
                                    HorizontalDivider(
                                        color = DividerColor,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 72.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupTitleBar(
    title: String,
    leftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(SheetBg)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftContent()
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        rightContent()
    }
}

@Composable
private fun GroupSearchBar(query: String, onQueryChanged: (String) -> Unit) {
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
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                cursorBrush = SolidColor(Color.Black),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Search name or number", fontSize = 14.sp, color = TextSecondary)
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SelectedContactsChips(
    contacts: List<Contact>,
    onRemove: (Contact) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        contacts.forEach { contact ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Box {
                    ContactAvatar(avatarUrl = contact.avatarUrl, size = 44.dp)
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF636366), CircleShape)
                            .clickable { onRemove(contact) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
                Text(
                    text = contact.displayName.split(" ").first(),
                    fontSize = 11.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun GroupLetterHeader(letter: String) {
    Text(
        text = letter,
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .background(SheetBg)
            .padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun GroupContactCard(
    contacts: List<Contact>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        contacts.forEachIndexed { index, contact ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clickable { onToggle(contact.id) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContactAvatar(avatarUrl = contact.avatarUrl, size = 44.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = contact.displayName,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = if (contact.id in selectedIds) Icons.Filled.CheckCircle
                                  else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (contact.id in selectedIds) WhatsAppGreen else UncheckedGray
                )
            }
            if (index < contacts.lastIndex) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 72.dp)
                )
            }
        }
    }
}
