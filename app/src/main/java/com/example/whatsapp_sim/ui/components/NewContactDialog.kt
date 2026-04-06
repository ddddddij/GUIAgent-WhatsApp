package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.whatsapp_sim.ui.screen.contactcreation.ContactRegion
import com.example.whatsapp_sim.ui.screen.contactcreation.NewContactViewModel

private val DialogBg = Color(0xFFF2F2F7)
private val CardWhite = Color.White
private val DividerColor = Color(0xFFE5E5EA)
private val TextSecondary = Color(0xFF8E8E8E)
private val WhatsAppGreen = Color(0xFF25D366)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewContactDialog(
    viewModel: NewContactViewModel,
    onDismiss: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.reset()
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = DialogBg,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.82f)
                .padding(top = 6.dp)
        ) {
            Header(
                canSubmit = uiState.canSubmit,
                onCancel = {
                    viewModel.reset()
                    onDismiss()
                },
                onDone = {
                    if (viewModel.createContact() != null) {
                        onDone()
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            NameCard(
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                onFirstNameChanged = viewModel::onFirstNameChanged,
                onLastNameChanged = viewModel::onLastNameChanged
            )

            Spacer(modifier = Modifier.height(28.dp))

            PhoneCard(
                selectedRegion = uiState.selectedRegion,
                regions = viewModel.regions,
                onRegionSelected = viewModel::onRegionSelected,
                mobileNumber = uiState.mobileNumber,
                onMobileChanged = viewModel::onMobileChanged
            )

            Spacer(modifier = Modifier.height(28.dp))

            SyncCard(
                checked = uiState.syncToPhone,
                onCheckedChange = viewModel::onSyncToPhoneChanged
            )

            Spacer(modifier = Modifier.height(22.dp))

            AddViaQrCodeRow(
                onClick = {
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun Header(
    canSubmit: Boolean,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(text = "Cancel", color = Color.Black, fontSize = 15.sp)
        }

        Text(
            text = "New contact",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        TextButton(
            onClick = onDone,
            enabled = canSubmit
        ) {
            Text(
                text = "Done",
                color = if (canSubmit) WhatsAppGreen else TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NameCard(
    firstName: String,
    lastName: String,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        PlaceholderTextField(
            value = firstName,
            placeholder = "First name",
            onValueChange = onFirstNameChanged
        )

        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 16.dp))

        PlaceholderTextField(
            value = lastName,
            placeholder = "Last name",
            onValueChange = onLastNameChanged
        )
    }
}

@Composable
private fun PhoneCard(
    selectedRegion: ContactRegion,
    regions: List<ContactRegion>,
    onRegionSelected: (ContactRegion) -> Unit,
    mobileNumber: String,
    onMobileChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        CountryRow(
            selectedRegion = selectedRegion,
            regions = regions,
            onRegionSelected = onRegionSelected
        )

        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mobile",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(0.32f)
            )
            Row(
                modifier = Modifier.weight(0.68f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedRegion.dialCode,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BasicTextField(
                    value = mobileNumber,
                    onValueChange = onMobileChanged,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    cursorBrush = SolidColor(WhatsAppGreen),
                    decorationBox = { inner ->
                        if (mobileNumber.isEmpty()) {
                            Text("Phone", fontSize = 16.sp, color = TextSecondary)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SyncCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sync contact to phone",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun PlaceholderTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        cursorBrush = SolidColor(WhatsAppGreen),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(placeholder, fontSize = 16.sp, color = TextSecondary)
            }
            inner()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp)
    )
}

@Composable
private fun CountryRow(
    selectedRegion: ContactRegion,
    regions: List<ContactRegion>,
    onRegionSelected: (ContactRegion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Phone",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(0.32f)
            )
            Row(
                modifier = Modifier.weight(0.68f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedRegion.name,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Select country",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            regions.forEach { region ->
                DropdownMenuItem(
                    text = { Text("${region.name} (${region.dialCode})") },
                    onClick = {
                        expanded = false
                        onRegionSelected(region)
                    }
                )
            }
        }
    }
}

@Composable
private fun AddViaQrCodeRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.QrCode2,
            contentDescription = "Add via QR code",
            tint = WhatsAppGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Add via QR code",
            color = WhatsAppGreen,
            fontSize = 16.sp
        )
    }
}
