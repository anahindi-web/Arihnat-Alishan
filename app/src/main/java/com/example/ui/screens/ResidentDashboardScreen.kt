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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplaintEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.QuickActionItem
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ResidentDashboardScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val myComplaints by viewModel.myComplaints.collectAsState()
    val myBookings by viewModel.myAmenityBookings.collectAsState()
    val notices by viewModel.allNotices.collectAsState()
    val myBills by viewModel.myMaintenanceBills.collectAsState()
    val myVehicles by viewModel.myVehicles.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val theme = LocalAppThemePalette.current

    var showReportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Super Admin Quick Access Banner
        if (currentRole.isSuperAdmin() || currentRole.isCommitteeMember()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("super_admin") }
                        .testTag("super_admin_banner"),
                    colors = CardDefaults.cardColors(containerColor = theme.secondary),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(theme.accent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = theme.accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (currentRole.isSuperAdmin()) "SUPER ADMIN CONSOLE" else "COMMITTEE MASTER CONTROL",
                                    color = theme.accent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Theme Switcher • Amend Members • Notices • Rules",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = theme.accent
                        )
                    }
                }
            }
        }

        // High-Visibility Emergency Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("emergency") }
                    .testTag("emergency_banner"),
                colors = CardDefaults.cardColors(containerColor = StatusCritical),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🚨 SOCIETY EMERGENCY HOTLINE",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Fire • Medical 102 • Security • Major Leakage",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        // Quick Actions Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.WaterDrop,
                            label = "Water",
                            iconTint = StatusInfo,
                            bgColor = StatusInfoBg,
                            onClick = { onNavigate("water_lifts") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.Elevator,
                            label = "Lifts",
                            iconTint = GoldChampagne,
                            bgColor = GoldContainer,
                            onClick = { onNavigate("water_lifts") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.LocalParking,
                            label = "Parking",
                            iconTint = NavyLight,
                            bgColor = SurfaceVariant,
                            onClick = { onNavigate("parking") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.PersonAdd,
                            label = "Visitors",
                            iconTint = StatusSuccess,
                            bgColor = StatusSuccessBg,
                            onClick = { onNavigate("visitors") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.AddAlert,
                            label = "Report Issue",
                            iconTint = StatusCritical,
                            bgColor = StatusCriticalBg,
                            onClick = { showReportDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.Pool,
                            label = "Amenities",
                            iconTint = Color(0xFF8B5CF6),
                            bgColor = Color(0xFFEDE9FE),
                            onClick = { onNavigate("amenities") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.ReceiptLong,
                            label = "Maintenance",
                            iconTint = Color(0xFF0D9488),
                            bgColor = Color(0xFFCCFBF1),
                            onClick = { onNavigate("maintenance") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.Badge,
                            label = "My Flat",
                            iconTint = NavyPrimary,
                            bgColor = SurfaceVariant,
                            onClick = { onNavigate("profile") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionItem(
                            icon = Icons.Default.HowToReg,
                            label = "Staff Punch",
                            iconTint = Color(0xFF2E7D32),
                            bgColor = Color(0xFFE8F5E9),
                            onClick = { onNavigate("attendance") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Guard Patrol",
                            iconTint = Color(0xFFD32F2F),
                            bgColor = Color(0xFFFFEBEE),
                            onClick = { onNavigate("patrol") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.Campaign,
                            label = "Notices",
                            iconTint = GoldChampagne,
                            bgColor = GoldContainer,
                            onClick = { onNavigate("notices") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionItem(
                            icon = Icons.Default.AccountBalance,
                            label = "Governance",
                            iconTint = NavyPrimary,
                            bgColor = SurfaceVariant,
                            onClick = { onNavigate("governance") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick Status Summary Cards
        item {
            Text(
                text = "Society Operations Status",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Water card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate("water_lifts") },
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = StatusInfo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Water Supply", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Normal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                        Text("Towers: 4/4 Active", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Power & DG card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Power Backup", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("DG Ready", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                        Text("Grid Power Active", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lifts card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate("water_lifts") },
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Elevator, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lifts", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("7/8 Operational", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Kaveh L-2 in AMC", fontSize = 11.sp, color = StatusWarning)
                    }
                }

                // Security gate card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Security", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Gate 1 & 2 Active", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                        Text("CCTV Online", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Report Issue Prominent Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_issue_prominent_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Facing an Issue in Society?",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lift, Water, Cleanliness, Parking or Common property",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { showReportDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = NavyPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("report_issue_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Report Issue", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // My Open Complaints Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Open Complaints",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate("complaints") }) {
                    Text("View All (${myComplaints.size})", color = NavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (myComplaints.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Everything looks good!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "You don't have any open complaints.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                myComplaints.take(2).forEach { complaint ->
                    ComplaintCardItem(
                        complaint = complaint,
                        onClick = { onNavigate("complaints") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Upcoming Amenity Booking
        item {
            val confirmedBooking = myBookings.firstOrNull { it.status == "Confirmed" || it.status == "Approved" }
            if (confirmedBooking != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = GoldChampagne)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Upcoming Amenity Booking",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            StatusBadge(status = confirmedBooking.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = confirmedBooking.amenityName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "${confirmedBooking.bookingDate} • ${confirmedBooking.timeSlot}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Society Updates / Notices
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Society Notices & Updates",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { onNavigate("notices") }) {
                    Text("View Board", color = NavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            notices.take(2).forEach { notice ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("notices") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (notice.priority == "High") StatusWarningBg else SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (notice.category == "Water") Icons.Default.WaterDrop else Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (notice.priority == "High") StatusWarning else NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = notice.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(status = notice.priority)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notice.content,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Target: ${notice.targetTower} • ${notice.publishDate}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showReportDialog) {
        ReportComplaintDialog(
            viewModel = viewModel,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
fun ComplaintCardItem(
    complaint: ComplaintEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${complaint.id}",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = complaint.category,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                StatusBadge(status = complaint.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = complaint.description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${complaint.tower} • ${complaint.location}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = "Priority: ${complaint.priority}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (complaint.priority == "Critical" || complaint.priority == "High") StatusCritical else TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportComplaintDialog(
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    val categories = listOf(
        "Water", "Lift", "Garbage & Waste", "Cleanliness", "Parking",
        "Security", "Noise", "Electricity", "Plumbing", "Garden & Amenities",
        "Pets & Stray Animals", "Pest Control", "Common Property", "Emergency"
    )
    val towers = listOf("Kaveh", "Baraz-1", "Baraz-2", "Zenath")

    var selectedCategory by remember { mutableStateOf("Water") }
    var selectedTower by remember { mutableStateOf("Kaveh") }
    var floor by remember { mutableStateOf("12") }
    var location by remember { mutableStateOf("12th Floor Lift Lobby") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("High") }
    var hasPhoto by remember { mutableStateOf(false) }

    // Smart priority suggestion
    LaunchedEffect(selectedCategory) {
        priority = when (selectedCategory) {
            "Emergency" -> "Critical"
            "Water", "Lift", "Security" -> "High"
            "Cleanliness", "Garbage & Waste", "Parking", "Electricity", "Plumbing" -> "Medium"
            else -> "Low"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Report Society Issue",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Selector
                item {
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    var catExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Tower & Floor
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tower", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            var towerExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = towerExpanded,
                                onExpandedChange = { towerExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedTower,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = towerExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = towerExpanded,
                                    onDismissRequest = { towerExpanded = false }
                                ) {
                                    towers.forEach { t ->
                                        DropdownMenuItem(
                                            text = { Text(t) },
                                            onClick = {
                                                selectedTower = t
                                                towerExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Floor", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            OutlinedTextField(
                                value = floor,
                                onValueChange = { floor = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Exact Location
                item {
                    Text("Exact Location", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("e.g. 12th Floor Lift Lobby") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Description
                item {
                    Text("Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe the problem clearly...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Duplicate check indicator
                item {
                    if (selectedCategory == "Lift" && selectedTower == "Kaveh") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusWarningBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Notice: 4 residents in Kaveh have already reported Lift 2 door issues today.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }

                // Photo upload simulation
                item {
                    Text("Attach Photograph", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { hasPhoto = !hasPhoto },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (hasPhoto) "Photo Attached ✓" else "Take / Upload Photo", fontSize = 12.sp)
                        }

                        if (hasPhoto) {
                            TextButton(onClick = { hasPhoto = false }) {
                                Text("Remove", color = StatusCritical, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Priority Selector
                item {
                    Text("Priority: $priority", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Low", "Medium", "High", "Critical").forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        viewModel.reportComplaint(
                            category = selectedCategory,
                            subcategory = "General $selectedCategory Issue",
                            tower = selectedTower,
                            floor = floor.toIntOrNull() ?: 1,
                            location = location,
                            description = description,
                            priority = priority,
                            photoPlaceholder = if (hasPhoto) "photo_sample_proof.jpg" else ""
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Submit Complaint", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
