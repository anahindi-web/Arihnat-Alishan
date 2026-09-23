package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.ScreenAccessPolicy

data class MenuItemConfig(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoreMenuScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()

    // Screen groupings filtered by role access policy
    val householdItems = remember(currentRole) {
        listOf(
            MenuItemConfig("profile", Icons.Default.Badge, "Household Master & Occupancy", "Self/Rented status, family members, school info, pets & domestic help", NavyPrimary),
            MenuItemConfig("parking", Icons.Default.LocalParking, "Multi-Level Parking Management", "Levels P1-P5 visual map, EV charging bays, parking dispute reporting", GoldChampagne),
            MenuItemConfig("maintenance", Icons.Default.ReceiptLong, "Maintenance & Official Receipts", "Sinking & repair fund breakdown, instant UPI payment & official PDF receipts", Color(0xFF0D9488)),
            MenuItemConfig("service_requests", Icons.Default.Handyman, "Service NOCs & Renovation Permits", "Move-In / Move-Out clearances, interior renovation contractor passes", Color(0xFF8B5CF6))
        ).filter { ScreenAccessPolicy.isScreenAccessible(it.route, currentRole) }
    }

    val infraItems = remember(currentRole) {
        listOf(
            MenuItemConfig("super_admin", Icons.Default.Shield, "Super Admin Console", "Dynamic app theme switcher, member directory management, digital notices & bylaws", Color(0xFF7E22CE)),
            MenuItemConfig("water_lifts", Icons.Default.WaterDrop, "Water & High-Speed Lifts Status", "Overhead tank telemetry, pressure, Otis elevator AMC tracker", StatusInfo),
            MenuItemConfig("governance", Icons.Default.AccountBalance, "Governance & Approval Center", "Role dashboards (Chairman, Secretary, Treasurer), AGM minutes & Audit trail", NavyPrimary),
            MenuItemConfig("master_data", Icons.Default.Dns, "Society Master Data Management", "Master config, flat registry, parking bays, amenities, statutory AMCs & committee directory (Amendable)", NavyPrimary),
            MenuItemConfig("notices", Icons.Default.Campaign, "Society Notice Board & Bylaws", "Official circulars with circular #, pinned announcements, AGM agenda & MCS bylaws", GoldChampagne),
            MenuItemConfig("emergency", Icons.Default.Emergency, "🚨 Emergency Hotline Action Center", "Fire 101, Ambulance 102, Police 112, Main Gate security & emergency plumber", StatusCritical)
        ).filter { ScreenAccessPolicy.isScreenAccessible(it.route, currentRole) }
    }

    val staffItems = remember(currentRole) {
        listOf(
            MenuItemConfig("attendance", Icons.Default.HowToReg, "Staff Attendance with Geo & Selfie", "Punch In/Out with real-time GPS geofence verification and Camera/Gallery selfie upload", Color(0xFF2E7D32)),
            MenuItemConfig("patrol", Icons.Default.QrCodeScanner, "Guard Patrol QR Checkpoints (P1-P5)", "1-Hour mandatory scans at P1, P2, P3, P4, P5 & Night Guard. Automated alert popup to Committee & Managers on missed scans", Color(0xFFD32F2F))
        ).filter { ScreenAccessPolicy.isScreenAccessible(it.route, currentRole) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUserName.split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                                .ifEmpty { "AR" },
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentUserName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = if (currentFlatId.startsWith("K-")) "Flat: $currentFlatId • Kaveh Tower"
                            else "Unit: $currentFlatId",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(status = currentRole.displayName)
                    }
                }
            }
        }

        // Debug-Build-Only Role Selector for Testing
        if (BuildConfig.DEBUG) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debug_role_selector_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                tint = StatusCritical,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Developer Testing: Switch Active Role",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Switch roles below to test role access policies and screen permissions:",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UserRole.values().forEach { role ->
                                val isSelected = currentRole == role
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setRole(role) },
                                    label = {
                                        Text(
                                            role.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Resident & Property Services
        if (householdItems.isNotEmpty()) {
            item {
                Text("Household & Property Services", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        householdItems.forEachIndexed { index, menu ->
                            MenuRowItem(
                                icon = menu.icon,
                                title = menu.title,
                                subtitle = menu.subtitle,
                                color = menu.color,
                                onClick = { onNavigate(menu.route) }
                            )
                            if (index < householdItems.size - 1) {
                                Divider(color = BorderSubtle)
                            }
                        }
                    }
                }
            }
        }

        // Section: Society Operations & Infrastructure
        if (infraItems.isNotEmpty()) {
            item {
                Text("Infrastructure & Society Administration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        infraItems.forEachIndexed { index, menu ->
                            MenuRowItem(
                                icon = menu.icon,
                                title = menu.title,
                                subtitle = menu.subtitle,
                                color = menu.color,
                                onClick = { onNavigate(menu.route) }
                            )
                            if (index < infraItems.size - 1) {
                                Divider(color = BorderSubtle)
                            }
                        }
                    }
                }
            }
        }

        // Section: Security Patrol & Staff Operations
        if (staffItems.isNotEmpty()) {
            item {
                Text("Security Patrol & Staff Operations", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        staffItems.forEachIndexed { index, menu ->
                            MenuRowItem(
                                icon = menu.icon,
                                title = menu.title,
                                subtitle = menu.subtitle,
                                color = menu.color,
                                onClick = { onNavigate(menu.route) }
                            )
                            if (index < staffItems.size - 1) {
                                Divider(color = BorderSubtle)
                            }
                        }
                    }
                }
            }
        }

        // Society Registration Footnote
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Arihant Alishan Co-operative Housing Society Ltd.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sector 35, Kharghar, Navi Mumbai 410210\nRegistered under Maharashtra Co-operative Societies Act 1960\nReg No: NMMC/WAR/CHS/2026/894",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MenuRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.5.sp, color = TextSecondary, maxLines = 2, lineHeight = 16.sp)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(22.dp)
        )
    }
}
