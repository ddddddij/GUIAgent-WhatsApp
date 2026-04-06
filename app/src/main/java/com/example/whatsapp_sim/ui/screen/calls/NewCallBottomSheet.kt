package com.example.whatsapp_sim.ui.screen.calls

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.ui.components.CallContactItem
import com.example.whatsapp_sim.ui.components.InviteContactItem
import kotlinx.coroutines.launch

private val SheetBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val SearchBg = Color(0xFFE5E5EA)
private val DividerColor = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)
private val WhatsAppGreen = Color(0xFF25D366)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCallBottomSheet(
    viewModel: NewCallViewModel,
    onDismiss: () -> Unit,
    onNewContactClick: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCount by viewModel.selectedCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val startCallContacts by viewModel.startCallContacts.collectAsState()
    val groupedContacts by viewModel.groupedContacts.collectAsState()
    val selectedIds by viewModel.selectedContactIds.collectAsState()
    val inviteContacts by viewModel.inviteContacts.collectAsState()
    val scrollTargetContactId by viewModel.scrollTargetContactId.collectAsState()

    val listState = rememberLazyListState()

    // Scroll to the group containing the first fuzzy-matched contact
    LaunchedEffect(scrollTargetContactId) {
        val targetId = scrollTargetContactId ?: return@LaunchedEffect
        val letter = groupedContacts.firstOrNull { group ->
            group.contacts.any { it.id == targetId }
        }?.letter ?: return@LaunchedEffect
        val groupIndex = groupedContacts.indexOfFirst { it.letter == letter }
        if (groupIndex < 0) return@LaunchedEffect
        // fixed items before groups: QuickActions(0) + StartCall(1 if present)
        val fixedCount = if (startCallContacts.isNotEmpty()) 2 else 1
        // each group: stickyHeader + card = 2 items
        val targetIndex = fixedCount + groupIndex * 2
        listState.animateScrollToItem(targetIndex)
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.onCancelClick()
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
            // Title bar
            NewCallTitleBar(
                selectedCount = selectedCount,
                totalCount = totalCount,
                onCancelClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        viewModel.onCancelClick()
                        onDismiss()
                    }
                }
            )

            // Search bar
            NewCallSearchBar(
                query = searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged
            )

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(end = 20.dp, bottom = 24.dp)
                ) {
                    // Quick Actions Card
                    item {
                        NewCallQuickActionsCard(
                            onNewCallLink = { viewModel.onNewCallLinkClick(); toast() },
                            onCallANumber = { viewModel.onCallANumberClick(); toast() },
                            onNewContact = {
                                viewModel.onNewContactClick()
                                onNewContactClick()
                            },
                            onScheduleCall = { viewModel.onScheduleCallClick(); toast() }
                        )
                    }

                    // Start a call section
                    if (startCallContacts.isNotEmpty()) {
                        item {
                            StartCallSection(
                                contacts = startCallContacts,
                                selectedIds = selectedIds,
                                onToggle = viewModel::onContactToggled
                            )
                        }
                    }

                    // Grouped contacts by letter
                    groupedContacts.forEach { group ->
                        stickyHeader(key = "header_${group.letter}") {
                            GroupLetterHeader(letter = group.letter)
                        }
                        item(key = "card_${group.letter}") {
                            AlphabetGroupCard(
                                group = group,
                                selectedIds = selectedIds,
                                onToggle = viewModel::onContactToggled
                            )
                        }
                    }

                    // Invite section
                    if (inviteContacts.isNotEmpty()) {
                        item {
                            InviteSection(
                                inviteContacts = inviteContacts,
                                onInviteClick = { contact ->
                                    viewModel.onInviteClick(contact)
                                    toast()
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }

                // Right-side alphabet index bar
                if (groupedContacts.isNotEmpty()) {
                    CallAlphabetIndexBar(
                        letters = groupedContacts.map { it.letter },
                        listState = listState,
                        hasStartCallSection = startCallContacts.isNotEmpty(),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }
            }
        }
    }
}

@Composable
private fun NewCallTitleBar(
    selectedCount: Int,
    totalCount: Int,
    onCancelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(SheetBg)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancelClick) {
            Text(text = "Cancel", fontSize = 14.sp, color = Color.Black)
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "New call", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "$selectedCount/$totalCount", fontSize = 13.sp, color = TextSecondary)
        }

        // Right spacer symmetric with Cancel button
        Spacer(modifier = Modifier.width(80.dp))
    }
}

@Composable
private fun NewCallSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
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
                contentDescription = "Search",
                modifier = Modifier.size(16.dp),
                tint = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("Search name or number", fontSize = 14.sp, color = TextSecondary)
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NewCallQuickActionsCard(
    onNewCallLink: () -> Unit,
    onCallANumber: () -> Unit,
    onNewContact: () -> Unit,
    onScheduleCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            QuickActionRow(icon = Icons.Outlined.Link, label = "New call link", onClick = onNewCallLink)
            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            QuickActionRow(icon = Icons.Outlined.Dialpad, label = "Call a number", onClick = onCallANumber)
            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            QuickActionRow(icon = Icons.Outlined.PersonAdd, label = "New contact", onClick = onNewContact)
            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            QuickActionRow(icon = Icons.Outlined.CalendarMonth, label = "Schedule call", onClick = onScheduleCall)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun QuickActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp), tint = WhatsAppGreen)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
private fun StartCallSection(
    contacts: List<com.example.whatsapp_sim.domain.model.Contact>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    Text(
        text = "Start a call",
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        contacts.forEachIndexed { index, contact ->
            CallContactItem(
                name = contact.displayName,
                statusText = null,
                isSelected = contact.id in selectedIds,
                onToggle = { onToggle(contact.id) }
            )
            if (index < contacts.lastIndex) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 86.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
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
private fun AlphabetGroupCard(
    group: ContactGroup,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        group.contacts.forEachIndexed { index, contact ->
            CallContactItem(
                name = contact.displayName,
                statusText = null,
                isSelected = contact.id in selectedIds,
                onToggle = { onToggle(contact.id) }
            )
            if (index < group.contacts.lastIndex) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 86.dp)
                )
            }
        }
    }
}

@Composable
private fun InviteSection(
    inviteContacts: List<InviteContact>,
    onInviteClick: (InviteContact) -> Unit
) {
    Text(
        text = "Invite to WhatsApp",
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        inviteContacts.forEachIndexed { index, contact ->
            InviteContactItem(
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                onInviteClick = { onInviteClick(contact) }
            )
            if (index < inviteContacts.lastIndex) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 86.dp)
                )
            }
        }
    }
}

@Composable
private fun CallAlphabetIndexBar(
    letters: List<String>,
    listState: LazyListState,
    hasStartCallSection: Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val fixedCount = if (hasStartCallSection) 2 else 1
    Column(
        modifier = modifier.padding(end = 4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        letters.forEachIndexed { i, letter ->
            Text(
                text = letter,
                fontSize = 11.sp,
                color = WhatsAppGreen,
                modifier = Modifier
                    .height(18.dp)
                    .clickable {
                        scope.launch {
                            listState.animateScrollToItem(fixedCount + i * 2)
                        }
                    }
            )
        }
    }
}
