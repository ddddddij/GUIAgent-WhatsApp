package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IconGray = Color(0xFF3C3C43)
private val TextSecondary = Color(0xFF8E8E8E)
private val ChevronColor = Color(0xFFC7C7CC)
private val SwitchTrackChecked = Color(0xFF25D366)
private val SwitchTrackUnchecked = Color(0xFFE5E5EA)

/**
 * Generic settings item supporting:
 * - Single or double line text
 * - Optional right-side label (e.g. "Off", "None")
 * - Optional toggle (Switch)
 * - Optional chevron arrow
 */
@Composable
fun ContactInfoSettingItem(
    icon: ImageVector,
    label: String,
    subLabel: String? = null,
    rightLabel: String? = null,
    showChevron: Boolean = true,
    showToggle: Boolean = false,
    toggleChecked: Boolean = false,
    onToggleChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val rowHeight = if (subLabel != null) 68.dp else 56.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = IconGray
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Normal)
            if (subLabel != null) {
                Text(text = subLabel, fontSize = 12.sp, color = TextSecondary)
            }
        }

        if (rightLabel != null) {
            Text(text = rightLabel, fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
        }

        if (showToggle) {
            Switch(
                checked = toggleChecked,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SwitchTrackChecked,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SwitchTrackUnchecked,
                    uncheckedBorderColor = SwitchTrackUnchecked
                )
            )
        } else if (showChevron) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = ChevronColor
            )
        }
    }
}
