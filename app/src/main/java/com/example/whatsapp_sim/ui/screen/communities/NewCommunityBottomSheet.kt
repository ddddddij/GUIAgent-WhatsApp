package com.example.whatsapp_sim.ui.screen.communities

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_sim.domain.model.Community
import com.example.whatsapp_sim.domain.model.CommunityMember
import com.example.whatsapp_sim.domain.model.CommunityRole
import com.example.whatsapp_sim.domain.model.MembershipStatus
import kotlinx.coroutines.launch

private val SheetBg = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val WhatsAppGreen = Color(0xFF25D366)
private val AvatarBg = Color(0xFFE0E0E0)
private val AvatarIconColor = Color(0xFF757575)
private val PlaceholderColor = Color(0xFFC7C7CC)
private val DisabledBg = Color(0xFFE0E0E0)
private val DisabledText = Color(0xFFA0A0A0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommunityBottomSheet(
    viewModel: NewCommunityViewModel,
    onDismiss: () -> Unit,
    onCreateCommunity: (Community) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val toast = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }

    val communityName by viewModel.communityName.collectAsState()
    val communityDescription by viewModel.communityDescription.collectAsState()
    val isCreateEnabled by viewModel.isCreateEnabled.collectAsState()

    val dismissSheet = {
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
            NewCommunityTitleBar(onCancelClick = { dismissSheet() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SeeExamplesBanner(
                    onClick = { viewModel.onSeeExamplesClick(); toast() }
                )

                CommunityAvatarSection(
                    onAddPhotoClick = { viewModel.onAddPhotoClick(); toast() },
                    onRefreshClick = { viewModel.onRefreshPhotoClick(); toast() }
                )

                CommunityNameInput(
                    value = communityName,
                    onValueChange = viewModel::onNameChanged
                )

                CommunityDescriptionInput(
                    value = communityDescription,
                    onValueChange = viewModel::onDescriptionChanged
                )

                Spacer(modifier = Modifier.weight(1f))

                CreateCommunityButton(
                    enabled = isCreateEnabled,
                    onClick = {
                        val newCommunity = Community(
                            id = "community_${System.currentTimeMillis()}",
                            name = communityName.trim(),
                            description = communityDescription.trim(),
                            iconUrl = null,
                            creatorId = "user_001",
                            createdAt = System.currentTimeMillis(),
                            members = listOf(
                                CommunityMember(
                                    userId = "user_001",
                                    displayName = "Alex Johnson",
                                    role = CommunityRole.ADMIN,
                                    membershipStatus = MembershipStatus.JOINED,
                                    joinedAt = System.currentTimeMillis()
                                )
                            ),
                            inviteLink = "https://chat.whatsapp.com/invite/${System.currentTimeMillis()}"
                        )
                        onCreateCommunity(newCommunity)
                        dismissSheet()
                    }
                )
            }
        }
    }
}

@Composable
private fun NewCommunityTitleBar(onCancelClick: () -> Unit) {
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

        Text(
            text = "New community",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Symmetric spacer matching Cancel button width (~80dp)
        Spacer(modifier = Modifier.width(80.dp))
    }
}

@Composable
private fun SeeExamplesBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = WhatsAppGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = WhatsAppGreen)) {
                        append("See examples")
                    }
                    withStyle(SpanStyle(color = Color.Black)) {
                        append(" of different communities")
                    }
                },
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CommunityAvatarSection(
    onAddPhotoClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AvatarBg)
                    .clickable { onAddPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = AvatarIconColor
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = 6.dp, y = 6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreen)
                    .clickable { onRefreshClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Change photo",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add photo",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WhatsAppGreen,
            modifier = Modifier
                .clickable { onAddPhotoClick() }
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun CommunityNameInput(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 12.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            cursorBrush = SolidColor(Color.Black),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text("Community name", fontSize = 16.sp, color = PlaceholderColor)
                }
                innerTextField()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CommunityDescriptionInput(value: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .height(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            cursorBrush = SolidColor(Color.Black),
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CreateCommunityButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 24.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(if (enabled) WhatsAppGreen else DisabledBg)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Create community",
            fontSize = 16.sp,
            fontWeight = if (enabled) FontWeight.Bold else FontWeight.Normal,
            color = if (enabled) Color.White else DisabledText
        )
    }
}
