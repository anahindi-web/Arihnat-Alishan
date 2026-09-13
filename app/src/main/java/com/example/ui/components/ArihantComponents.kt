package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun ArihantHeader(
    currentRole: UserRole,
    userName: String,
    flatId: String,
    onRoleSelected: (UserRole) -> Unit,
    onEmergencyClicked: () -> Unit,
    onNoticesClicked: () -> Unit,
    onSuperAdminClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppThemePalette.current
    var showRoleMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(theme.primary, theme.secondary)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Society registration bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(theme.accent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ARIHANT ALISHAN • KHARGHAR",
                    color = theme.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "MCS Act 1960 Reg",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Greeting and Role switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Good Morning,",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentRole.isSuperAdmin()) Icons.Default.Shield else Icons.Default.Apartment,
                        contentDescription = null,
                        tint = theme.accent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentRole.isSuperAdmin()) "Root Super Administrator • Full Access"
                        else if (flatId.startsWith("K-")) "Kaveh Tower • Flat ${flatId.removePrefix("K-")}"
                        else if (flatId.startsWith("B1-")) "Baraz-1 • Flat ${flatId.removePrefix("B1-")}"
                        else if (flatId.startsWith("B2-")) "Baraz-2 • Flat ${flatId.removePrefix("B2-")}"
                        else if (flatId.startsWith("Z-")) "Zenath • Flat ${flatId.removePrefix("Z-")}"
                        else flatId,
                        color = theme.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Emergency & Role Switcher Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentRole.isSuperAdmin()) {
                    IconButton(
                        onClick = { onSuperAdminClicked?.invoke() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(theme.accent.copy(alpha = 0.25f))
                            .border(1.dp, theme.accent, CircleShape)
                            .testTag("super_admin_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Super Admin Console",
                            tint = theme.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = onNoticesClicked,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .testTag("notices_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notices",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Role Dropdown Trigger
                Box {
                    Button(
                        onClick = { showRoleMenu = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.accent.copy(alpha = 0.2f),
                            contentColor = theme.accent
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .border(1.dp, theme.accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .testTag("role_switcher_button")
                    ) {
                        Text(
                            text = when (currentRole) {
                                UserRole.SUPER_ADMIN -> "Super Admin 👑"
                                UserRole.RESIDENT_OWNER -> "Owner"
                                UserRole.RESIDENT_TENANT -> "Tenant"
                                UserRole.SOCIETY_MANAGER -> "Manager"
                                UserRole.CHAIRMAN -> "Chairman"
                                UserRole.SECRETARY -> "Secretary"
                                UserRole.TREASURER -> "Treasurer"
                                UserRole.COMMITTEE_MEMBER -> "Committee"
                                UserRole.SECURITY_GUARD -> "Guard"
                                UserRole.MAINTENANCE_STAFF -> "Staff"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false },
                        modifier = Modifier.background(theme.secondary)
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    if (role == UserRole.SUPER_ADMIN) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = theme.accent, modifier = Modifier.size(18.dp))
                                    }
                                },
                                text = {
                                    Text(
                                        text = role.displayName,
                                        color = if (role == currentRole) theme.accent else Color.White,
                                        fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    showRoleMenu = false
                                    onRoleSelected(role)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArihantBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("home", "Home", Icons.Default.Home),
        Triple("complaints", "Complaints", Icons.Default.Build),
        Triple("visitors", "Visitors", Icons.Default.QrCode),
        Triple("amenities", "Amenities", Icons.Default.Pool),
        Triple("more", "More", Icons.Default.Dashboard)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = PureWhiteSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { (route, label, icon) ->
            val selected = currentScreen == route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) NavyPrimary else TextMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (selected) NavyPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GoldContainer
                ),
                modifier = Modifier.testTag("nav_$route")
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "super admin", "root", "super_admin" ->
            Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
        "available", "verified", "active", "resolved", "closed", "paid", "operational", "approved", "confirmed" ->
            Pair(StatusSuccessBg, StatusSuccess)
        "in progress", "assigned", "acknowledged", "under review", "visitor", "expiring soon" ->
            Pair(StatusInfoBg, StatusInfo)
        "pending", "pending verification", "waiting approval", "correction required", "new" ->
            Pair(StatusWarningBg, StatusWarning)
        "critical", "high", "blocked", "emergency", "breakdown", "overdue", "rejected", "expired" ->
            Pair(StatusCriticalBg, StatusCritical)
        else ->
            Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("action_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor)
                .border(1.dp, iconTint.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SocietyAlertBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
