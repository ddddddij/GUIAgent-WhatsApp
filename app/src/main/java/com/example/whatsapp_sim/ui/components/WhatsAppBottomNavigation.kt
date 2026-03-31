package com.example.whatsapp_sim.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.UpdateDisabled
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomNavTab {
    UPDATES, CALLS, COMMUNITIES, CHATS, YOU
}

data class NavItem(
    val tab: BottomNavTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun WhatsAppBottomNavigation(
    selectedTab: BottomNavTab,
    unreadCount: Int,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val whatsAppGreen = Color(0xFF000000)
    val iconGray = Color(0xFF8E8E8E)

    val items = listOf(
        NavItem(BottomNavTab.UPDATES, "Updates", Icons.Filled.UpdateDisabled, Icons.Filled.UpdateDisabled),
        NavItem(BottomNavTab.CALLS, "Calls", Icons.Filled.Phone, Icons.Outlined.Phone),
        NavItem(BottomNavTab.COMMUNITIES, "Communities", Icons.Filled.Groups, Icons.Outlined.Groups),
        NavItem(BottomNavTab.CHATS, "Chats", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
        NavItem(BottomNavTab.YOU, "You", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            val isSelected = selectedTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = whatsAppGreen,
                    selectedTextColor = whatsAppGreen,
                    unselectedIconColor = iconGray,
                    unselectedTextColor = iconGray,
                    indicatorColor = Color.Transparent
                ),
                icon = {
                    if (item.tab == BottomNavTab.CHATS && unreadCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = whatsAppGreen) {
                                Text(unreadCount.toString(), color = Color.White)
                            }
                        }) {
                            Icon(
                                if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        Icon(
                            if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}
