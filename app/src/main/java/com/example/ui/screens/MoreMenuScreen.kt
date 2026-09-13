package com.example.ui.screens

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
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MoreMenuScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()

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
                            text = currentUserName.take(2).uppercase(),
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentUserName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Flat: $currentFlatId • Kaveh Tower", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(status = currentRole.displayName)
                    }
                }
            }
        }

        // Section: Resident & Property Services
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
                    MenuRowItem(
                        icon = Icons.Default.Badge,
                        title = "Household Master & Occupancy",
                        subtitle = "Self/Rented status, family members, school info, pets & domestic help",
                        color = NavyPrimary,
                        onClick = { onNavigate("profile") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.LocalParking,
                        title = "Multi-Level Parking Management",
                        subtitle = "Levels P1-P5 visual map, EV charging bays, parking dispute reporting",
                        color = GoldChampagne,
                        onClick = { onNavigate("parking") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.ReceiptLong,
                        title = "Maintenance & Official Receipts",
                        subtitle = "Sinking & repair fund breakdown, instant UPI payment & official PDF receipts",
                        color = Color(0xFF0D9488),
                        onClick = { onNavigate("maintenance") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.Handyman,
                        title = "Service NOCs & Renovation Permits",
                        subtitle = "Move-In / Move-Out clearances, interior renovation contractor passes",
                        color = Color(0xFF8B5CF6),
                        onClick = { onNavigate("service_requests") }
                    )
                }
            }
        }

        // Section: Society Operations & Infrastructure
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
                    if (currentRole.isSuperAdmin() || currentRole.isCommitteeMember()) {
                        MenuRowItem(
                            icon = Icons.Default.Shield,
                            title = "Super Admin Console",
                            subtitle = "Dynamic app theme switcher, member directory management, digital notices & bylaws",
                            color = Color(0xFF7E22CE),
                            onClick = { onNavigate("super_admin") }
                        )
                        Divider(color = BorderSubtle)
                    }
                    MenuRowItem(
                        icon = Icons.Default.WaterDrop,
                        title = "Water & High-Speed Lifts Status",
                        subtitle = "Overhead tank telemetry, pressure, Otis elevator AMC tracker",
                        color = StatusInfo,
                        onClick = { onNavigate("water_lifts") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Governance & Approval Center",
                        subtitle = "Role dashboards (Chairman, Secretary, Treasurer), AGM minutes & Audit trail",
                        color = NavyPrimary,
                        onClick = { onNavigate("governance") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.Dns,
                        title = "Society Master Data Management",
                        subtitle = "Master config, flat registry, parking bays, amenities, statutory AMCs & committee directory (Amendable)",
                        color = NavyPrimary,
                        onClick = { onNavigate("master_data") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.Campaign,
                        title = "Society Notice Board & Bylaws",
                        subtitle = "Official circulars with circular #, pinned announcements, AGM agenda & MCS bylaws",
                        color = GoldChampagne,
                        onClick = { onNavigate("notices") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.Emergency,
                        title = "🚨 Emergency Hotline Action Center",
                        subtitle = "Fire 101, Ambulance 102, Police 112, Main Gate security & emergency plumber",
                        color = StatusCritical,
                        onClick = { onNavigate("emergency") }
                    )
                }
            }
        }

        // Section: Security Patrol & Staff Operations
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
                    MenuRowItem(
                        icon = Icons.Default.HowToReg,
                        title = "Staff Attendance with Geo & Selfie",
                        subtitle = "Punch In/Out with real-time GPS geofence verification and Camera/Gallery selfie upload",
                        color = Color(0xFF2E7D32),
                        onClick = { onNavigate("attendance") }
                    )
                    Divider(color = BorderSubtle)
                    MenuRowItem(
                        icon = Icons.Default.QrCodeScanner,
                        title = "Guard Patrol QR Checkpoints (P1-P5)",
                        subtitle = "1-Hour mandatory scans at P1, P2, P3, P4, P5 & Night Guard. Automated alert popup to Committee & Managers on missed scans",
                        color = Color(0xFFD32F2F),
                        onClick = { onNavigate("patrol") }
                    )
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
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}
