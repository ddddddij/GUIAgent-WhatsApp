package com.example.whatsapp_sim.ui.screen.chats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Account
import com.example.whatsapp_sim.domain.model.Contact
import com.example.whatsapp_sim.ui.components.ContactAvatar
import com.example.whatsapp_sim.ui.screen.chats.NewChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatBottomSheet(
    viewModel: NewChatViewModel,
    onDismiss: () -> Unit,
    onNewGroupClick: () -> Unit,
    onNewContactClick: () -> Unit,
    onNewCommunityClick: () -> Unit,
    onNewBroadcastClick: () -> Unit,
    onOtherQuickActionClick: () -> Unit,
    onOpenConversation: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val frequentContacts by viewModel.frequentContacts.collectAsState()
    val groupedContacts by viewModel.groupedContacts.collectAsState()
    val selfAccount by viewModel.selfAccount.collectAsState()
    val scrollTargetContactId by viewModel.scrollTargetContactId.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val contactIndexMap = remember(groupedContacts) { buildContactScrollIndexMap(groupedContacts) }

    // Scroll to first matching contact when search query changes
    LaunchedEffect(scrollTargetContactId) {
        val targetIndex = scrollTargetContactId?.let { contactIndexMap[it] } ?: return@LaunchedEffect
        listState.animateScrollToItem(targetIndex)
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
            SheetHeader(onCloseClick = onDismiss)

            SearchBar(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged
            )

            Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    QuickActionsCard(
                        onNewGroupClick = {
                            viewModel.onNewGroupClick()
                            onNewGroupClick()
                        },
                        onNewContactClick = {
                            viewModel.onNewContactClick()
                            onNewContactClick()
                        },
                        onNewCommunityClick = {
                            viewModel.onNewCommunityClick()
                            onNewCommunityClick()
                        },
                        onNewBroadcastClick = {
                            viewModel.onNewBroadcastClick()
                            onNewBroadcastClick()
                        }
                    )
                }

                item {
                    SectionLabel(label = "Frequently contacted")
                }

                item {
                    FrequentContactsCard(
                        contacts = frequentContacts,
                        onContactClick = { contact ->
                            onOpenConversation(viewModel.onContactSelected(contact))
                        }
                    )
                }

                item {
                    SectionLabel(label = "Contacts on WhatsApp")
                }

                item {
                    SelfAccountCard(
                        account = selfAccount,
                        onClick = {
                            onOpenConversation(viewModel.onCurrentUserSelected())
                        }
                    )
                }

                groupedContacts.forEach { group ->
                    item(key = "header_${group.letter}") {
                        GroupHeader(letter = group.letter)
                    }

                    items(
                        items = group.contacts,
                        key = { contact -> "${group.letter}_${contact.id}" }
                    ) { contact ->
                        ContactRow(
                            name = contact.displayName,
                            subtitle = null,
                            avatarUrl = contact.avatarUrl,
                            onClick = {
                                onOpenConversation(viewModel.onContactSelected(contact))
                            }
                        )
                    }
                }

                if (groupedContacts.isEmpty()) {
                    item {
                        Text(
                            text = "No contacts found",
                            color = Color(0xFF8E8E8E),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (groupedContacts.isNotEmpty()) {
                LetterIndexBar(
                    letters = groupedContacts.map { it.letter },
                    onLetterClick = { letter ->
                        val group = groupedContacts.firstOrNull { it.letter == letter } ?: return@LetterIndexBar
                        val firstContactId = group.contacts.firstOrNull()?.id ?: return@LetterIndexBar
                        val targetIndex = contactIndexMap[firstContactId] ?: return@LetterIndexBar
                        coroutineScope.launch { listState.animateScrollToItem(targetIndex) }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            }
        }
    }
}

@Composable
private fun SheetHeader(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left spacer symmetric with close button
        Spacer(modifier = Modifier.width(80.dp))

        Text(
            text = "New chat",
            modifier = Modifier.weight(1f),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFF2F2F7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onCloseClick, modifier = Modifier.size(32.dp)) {
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
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
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
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Search name or number",
                        color = Color(0xFF8E8E8E),
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun QuickActionsCard(
    onNewGroupClick: () -> Unit,
    onNewContactClick: () -> Unit,
    onNewCommunityClick: () -> Unit,
    onNewBroadcastClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        QuickActionRow(label = "New group", icon = Icons.Outlined.Group, onClick = onNewGroupClick)
        DividerWithInset(56.dp)
        QuickActionRow(label = "New contact", icon = Icons.Outlined.PersonAdd, onClick = onNewContactClick)
        DividerWithInset(56.dp)
        QuickActionRow(label = "New community", icon = Icons.Outlined.Groups, onClick = onNewCommunityClick)
        DividerWithInset(56.dp)
        QuickActionRow(label = "New broadcast", icon = Icons.Outlined.Campaign, onClick = onNewBroadcastClick)
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun QuickActionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF25D366)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = Color.Black, fontSize = 16.sp)
    }
}

@Composable
private fun FrequentContactsCard(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (contacts.isEmpty()) {
            Text(
                text = "No matching frequent contacts",
                color = Color(0xFF8E8E8E),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
            )
        } else {
            contacts.forEachIndexed { index, contact ->
                ContactRow(
                    name = contact.displayName,
                    subtitle = null,
                    avatarUrl = contact.avatarUrl,
                    onClick = { onContactClick(contact) },
                    useCardPadding = false,
                    showDivider = index < contacts.lastIndex
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SelfAccountCard(
    account: Account?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(avatarUrl = null, size = 52.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account?.phone?.let { "$it (You)" } ?: "You",
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Message yourself",
                    color = Color(0xFF8E8E8E),
                    fontSize = 13.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label,
        color = Color(0xFF8E8E8E),
        fontSize = 13.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun GroupHeader(letter: String) {
    Text(
        text = letter,
        color = Color(0xFF8E8E8E),
        fontSize = 13.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun ContactRow(
    name: String,
    subtitle: String?,
    onClick: () -> Unit,
    avatarUrl: String? = null,
    useCardPadding: Boolean = true,
    showDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(avatarUrl = avatarUrl, size = 52.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = Color(0xFF8E8E8E),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (showDivider) {
            DividerWithInset(86.dp, horizontalPadding = if (useCardPadding) 16.dp else 0.dp)
        }
    }
}

@Composable
private fun DividerWithInset(startInset: Dp, horizontalPadding: Dp = 0.dp) {
    HorizontalDivider(
        color = Color(0xFFF0F0F0),
        thickness = 1.dp,
        modifier = Modifier.padding(start = startInset + horizontalPadding)
    )
}

@Composable
private fun LetterIndexBar(
    letters: List<String>,
    onLetterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(end = 4.dp)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        letters.forEach { letter ->
            Text(
                text = letter,
                color = Color(0xFF25D366),
                fontSize = 11.sp,
                modifier = Modifier
                    .height(18.dp)
                    .clickable { onLetterClick(letter) }
            )
        }
    }
}

// Fixed items before groups: QuickActionsCard(0), SectionLabel-Frequent(1),
// FrequentContactsCard(2), SectionLabel-Contacts(3), SelfAccountCard(4)
// Then for each group: GroupHeader(+1), contact items (+1 each)
private fun buildContactScrollIndexMap(groups: List<NewChatViewModel.ContactGroup>): Map<String, Int> {
    var currentIndex = 5 // after the 5 fixed items
    val map = linkedMapOf<String, Int>()
    groups.forEach { group ->
        currentIndex += 1 // GroupHeader item
        group.contacts.forEach { contact ->
            map[contact.id] = currentIndex
            currentIndex += 1
        }
    }
    return map
}
