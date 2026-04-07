package com.example.whatsapp_sim.ui.screen.broadcast

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.runtime.remember
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
import com.example.whatsapp_sim.domain.model.BroadcastList
import com.example.whatsapp_sim.ui.components.ContactAvatar
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
fun NewBroadcastBottomSheet(
    viewModel: NewBroadcastViewModel,
    onDismiss: () -> Unit,
    onCreated: (BroadcastList) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedIds by viewModel.selectedContactIds.collectAsState()
    val groupedContacts by viewModel.groupedContacts.collectAsState()
    val isCreateEnabled by viewModel.isCreateEnabled.collectAsState()
    val scrollTargetContactId by viewModel.scrollTargetContactId.collectAsState()

    val dismiss = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            viewModel.onCancelClick()
            onDismiss()
        }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(SheetBg)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { dismiss() }) {
                    Text("Cancel", fontSize = 14.sp, color = Color.Black)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("New broadcast", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "${selectedIds.size} selected",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                TextButton(
                    onClick = {
                        if (isCreateEnabled) {
                            val created = viewModel.createBroadcastList()
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                viewModel.reset()
                                onCreated(created)
                            }
                        }
                    },
                    enabled = isCreateEnabled
                ) {
                    Text(
                        "Create",
                        fontSize = 14.sp,
                        color = if (isCreateEnabled) WhatsAppGreen else TextSecondary
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
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                        cursorBrush = SolidColor(Color.Black),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("Search name or number", fontSize = 14.sp, color = TextSecondary)
                            }
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Min 2 hint
            Text(
                text = "Select at least 2 recipients",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                val listState = rememberLazyListState()

                LaunchedEffect(scrollTargetContactId) {
                    val targetId = scrollTargetContactId ?: return@LaunchedEffect
                    val groupIndex = groupedContacts.indexOfFirst { g -> g.contacts.any { it.id == targetId } }
                    if (groupIndex < 0) return@LaunchedEffect
                    listState.animateScrollToItem(groupIndex * 2)
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    groupedContacts.forEach { group ->
                        item(key = "header_${group.letter}") {
                            Text(
                                text = group.letter,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SheetBg)
                                    .padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }
                        item(key = "card_${group.letter}") {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                group.contacts.forEachIndexed { index, contact ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(68.dp)
                                            .clickable { viewModel.onContactToggled(contact.id) }
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
                                    if (index < group.contacts.lastIndex) {
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

                // Alphabet index bar
                if (groupedContacts.isNotEmpty()) {
                    BroadcastAlphabetBar(
                        letters = groupedContacts.map { it.letter },
                        listState = listState,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun BroadcastAlphabetBar(
    letters: List<String>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
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
                    .clickable { scope.launch { listState.animateScrollToItem(i * 2) } }
            )
        }
    }
}
