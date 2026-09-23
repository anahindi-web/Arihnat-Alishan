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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.ArihantViewModel
import com.example.ui.components.*
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
    val currentFlatId by viewModel.currentFlatId.collectAsState()
    val myFlat by viewModel.myFlat.collectAsState()
    val myFamilyMembers by viewModel.myFamilyMembers.collectAsState()
    val selectedBackgroundWall by viewModel.selectedBackgroundWall.collectAsState()
    val theme = LocalAppThemePalette.current

    val isFlatOwner = currentRole == UserRole.RESIDENT_OWNER || currentRole == UserRole.FAMILY_MEMBER || currentRole == UserRole.RESIDENT_TENANT

    var showReportDialog by remember { mutableStateOf(false) }
    var editingFamilyMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }

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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(theme.accent.copy(alpha = 0.2f))
                                    .border(1.dp, theme.accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = theme.accent,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (currentRole.isSuperAdmin()) "SUPER ADMIN CONSOLE" else "COMMITTEE MASTER CONTROL",
                                    color = theme.accent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Theme Switcher • Amend Members • Notices • Rules",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = theme.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Quick Action Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CandySectionHeading(
                        title = "Quick Actions",
                        subtitle = if (isFlatOwner) "Services for Flat $currentFlatId" else "Society Operations & Resident Services",
                        icon = Icons.Default.Widgets,
                        flavor = LightCandyFlavor.PASTEL_SKY
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isFlatOwner) {
                        // Flat Owner Exclusive Quick Actions (No security, no housekeeping, light candy style)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LightCandyQuickActionItem(
                                icon = Icons.Default.HomeWork,
                                label = "My Flat",
                                flavor = LightCandyFlavor.PASTEL_SKY,
                                onClick = { onNavigate("profile") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Build,
                                label = "Complaints",
                                flavor = LightCandyFlavor.PASTEL_CORAL,
                                onClick = { onNavigate("complaints") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Forum,
                                label = "Society Chat",
                                flavor = LightCandyFlavor.PASTEL_MINT,
                                onClick = { onNavigate("chat") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Campaign,
                                label = "Notices",
                                flavor = LightCandyFlavor.PASTEL_LAVENDER,
                                onClick = { onNavigate("notices") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LightCandyQuickActionItem(
                                icon = Icons.Default.AddAlert,
                                label = "Report Issue",
                                flavor = LightCandyFlavor.PASTEL_PEACH,
                                onClick = { showReportDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.ReceiptLong,
                                label = "Maintenance",
                                flavor = LightCandyFlavor.PASTEL_INDIGO,
                                onClick = { onNavigate("maintenance") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Pool,
                                label = "Amenities",
                                flavor = LightCandyFlavor.PASTEL_LEMON,
                                onClick = { onNavigate("amenities") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.DirectionsCar,
                                label = "Vehicles",
                                flavor = LightCandyFlavor.PASTEL_ROSE,
                                onClick = { onNavigate("parking") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // Committee / Staff full actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LightCandyQuickActionItem(
                                icon = Icons.Default.WaterDrop,
                                label = "Water",
                                flavor = LightCandyFlavor.PASTEL_SKY,
                                onClick = { onNavigate("water_lifts") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Elevator,
                                label = "Lifts",
                                flavor = LightCandyFlavor.PASTEL_PEACH,
                                onClick = { onNavigate("water_lifts") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.LocalParking,
                                label = "Parking",
                                flavor = LightCandyFlavor.PASTEL_INDIGO,
                                onClick = { onNavigate("parking") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.PersonAdd,
                                label = "Visitors",
                                flavor = LightCandyFlavor.PASTEL_MINT,
                                onClick = { onNavigate("visitors") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LightCandyQuickActionItem(
                                icon = Icons.Default.AddAlert,
                                label = "Report Issue",
                                flavor = LightCandyFlavor.PASTEL_CORAL,
                                onClick = { showReportDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Pool,
                                label = "Amenities",
                                flavor = LightCandyFlavor.PASTEL_LEMON,
                                onClick = { onNavigate("amenities") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.ReceiptLong,
                                label = "Maintenance",
                                flavor = LightCandyFlavor.PASTEL_MINT,
                                onClick = { onNavigate("maintenance") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Badge,
                                label = "My Flat",
                                flavor = LightCandyFlavor.PASTEL_SKY,
                                onClick = { onNavigate("profile") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LightCandyQuickActionItem(
                                icon = Icons.Default.HowToReg,
                                label = "Staff Punch",
                                flavor = LightCandyFlavor.PASTEL_MINT,
                                onClick = { onNavigate("attendance") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.QrCodeScanner,
                                label = "Guard Patrol",
                                flavor = LightCandyFlavor.PASTEL_ROSE,
                                onClick = { onNavigate("patrol") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.Campaign,
                                label = "Notices",
                                flavor = LightCandyFlavor.PASTEL_LAVENDER,
                                onClick = { onNavigate("notices") },
                                modifier = Modifier.weight(1f)
                            )
                            LightCandyQuickActionItem(
                                icon = Icons.Default.AccountBalance,
                                label = "Governance",
                                flavor = LightCandyFlavor.PASTEL_INDIGO,
                                onClick = { onNavigate("governance") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Arihant Alishan Architectural Background Wall Showcase & Switcher
        item {
            ArihantWallpaperGalleryCard(
                currentWallResId = selectedBackgroundWall,
                onSelectWall = { resId ->
                    viewModel.setBackgroundWall(resId)
                }
            )
        }

        // If flat owner, show My Flat Household Details and Family Members with direct Edit
        if (isFlatOwner) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CandySectionHeading(
                            title = "My Flat Household (Flat $currentFlatId)",
                            subtitle = "Tower ${myFlat?.tower ?: "Kaveh"} • ${myFlat?.flatType ?: "3 BHK"} • ${myFlat?.occupancyStatus ?: "Self Occupied"}",
                            icon = Icons.Default.HomeWork,
                            flavor = LightCandyFlavor.PASTEL_SKY,
                            badgeText = "Verified Record"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Balanced phone-optimized 3-column household metadata tile
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F9FF))
                                .border(BorderStroke(1.dp, Color(0xFFBAE6FD)), RoundedCornerShape(12.dp))
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.15f)) {
                                Text("Owner Name", fontSize = 10.5.sp, color = TextMuted, maxLines = 1)
                                Text(
                                    text = myFlat?.ownerName ?: "Rajesh Sharma",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(modifier = Modifier.width(1.dp).height(26.dp).background(Color(0xFFBAE6FD)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1.0f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Floor / Tower", fontSize = 10.5.sp, color = TextMuted, maxLines = 1)
                                Text(
                                    text = "${myFlat?.floor ?: 12}th • ${myFlat?.tower ?: "Kaveh"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.width(1.dp).height(26.dp).background(Color(0xFFBAE6FD)))
                            Column(
                                modifier = Modifier.weight(1.0f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("Occupancy", fontSize = 10.5.sp, color = TextMuted, maxLines = 1)
                                Text(
                                    text = myFlat?.occupancyStatus ?: "Self Occupied",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Family Members (${myFamilyMembers.size})",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            LightCandyButton(
                                text = "+ Manage",
                                onClick = { onNavigate("profile") },
                                flavor = LightCandyFlavor.PASTEL_SKY,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            myFamilyMembers.forEach { member ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (member.isChild) Color(0xFFEDE9FE) else Color(0xFFE0F2FE)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (member.isChild) Icons.Default.ChildCare else Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = if (member.isChild) Color(0xFF8B5CF6) else Color(0xFF0284C7),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = member.fullName,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${member.relationship} • Age ${member.calculatedAge}${if (member.isChild && member.grade.isNotBlank()) " • ${member.grade}" else ""}",
                                                    fontSize = 11.sp,
                                                    color = TextSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        LightCandyButton(
                                            text = "Edit",
                                            icon = Icons.Default.Edit,
                                            onClick = { editingFamilyMember = member },
                                            flavor = LightCandyFlavor.PASTEL_SKY,
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp),
                                            modifier = Modifier.testTag("btn_edit_dash_${member.id}")
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Society Community Chat Card (Flat Owner can chat with other members)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.2.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CandySectionHeading(
                            title = "Society Community Chat",
                            subtitle = "Chat with neighbors & join resident groups",
                            icon = Icons.Default.Forum,
                            flavor = LightCandyFlavor.PASTEL_MINT,
                            badgeText = "Active"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("#general", "#buy-sell", "#cultural", "#sports").forEach { channel ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = channel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CandyButton(
                            text = "Open Community Chat",
                            onClick = { onNavigate("chat") },
                            flavor = CandyFlavor.EMERALD,
                            icon = Icons.Default.Forum,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Quick Status Summary Cards - Society Operations Status Buttons (Only for Committee & Staff, hidden for Flat Owner)
        if (!isFlatOwner) {
            item {
                CandySectionHeading(
                    title = "Society Operations Status",
                    subtitle = "Real-time automated facility telemetry",
                    icon = Icons.Default.Speed,
                    flavor = LightCandyFlavor.PASTEL_SKY
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Water Candy Status Button
                    CandyOperationStatusButton(
                        title = "Water Supply",
                        statusText = "Normal (100%)",
                        subtitleText = "Towers: 4/4 Active",
                        icon = Icons.Default.WaterDrop,
                        flavor = CandyFlavor.SAPPHIRE,
                        onClick = { onNavigate("water_lifts") },
                        modifier = Modifier.weight(1f)
                    )

                    // Power DG Candy Status Button
                    CandyOperationStatusButton(
                        title = "Power Backup",
                        statusText = "DG Ready",
                        subtitleText = "Grid Power Active",
                        icon = Icons.Default.Bolt,
                        flavor = CandyFlavor.AMBER,
                        onClick = { onNavigate("water_lifts") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Lifts Candy Status Button
                    CandyOperationStatusButton(
                        title = "High-Speed Lifts",
                        statusText = "7/8 Operational",
                        subtitleText = "Kaveh L-2 in AMC",
                        icon = Icons.Default.Elevator,
                        flavor = CandyFlavor.PURPLE,
                        onClick = { onNavigate("water_lifts") },
                        modifier = Modifier.weight(1f)
                    )

                    // Gate Security Candy Status Button
                    CandyOperationStatusButton(
                        title = "Gate Security",
                        statusText = "Gates 1 & 2 Active",
                        subtitleText = "CCTV & Patrols ON",
                        icon = Icons.Default.Security,
                        flavor = CandyFlavor.EMERALD,
                        onClick = { onNavigate("visitors") },
                        modifier = Modifier.weight(1f)
                    )
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

                    CandyButton(
                        text = "+ Report Issue",
                        onClick = { showReportDialog = true },
                        flavor = CandyFlavor.GOLD,
                        icon = Icons.Default.Add,
                        modifier = Modifier.testTag("report_issue_button")
                    )
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
                CandySectionHeading(
                    title = if (isFlatOwner) "My Flat Complaints" else "My Open Complaints",
                    subtitle = if (isFlatOwner) "Tickets submitted for Flat $currentFlatId" else "Your open active tickets",
                    icon = Icons.Default.Build,
                    flavor = LightCandyFlavor.PASTEL_CORAL,
                    badgeText = "${myComplaints.size} Active",
                    modifier = Modifier.weight(1f)
                )
                LightCandyButton(
                    text = "View All",
                    onClick = { onNavigate("complaints") },
                    flavor = LightCandyFlavor.PASTEL_CORAL,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )
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
                CandySectionHeading(
                    title = "Society Notifications & Notices",
                    subtitle = "Official broadcasts from Society Management",
                    icon = Icons.Default.Notifications,
                    flavor = LightCandyFlavor.PASTEL_LAVENDER,
                    badgeText = "${notices.size} Updates",
                    modifier = Modifier.weight(1f)
                )
                LightCandyButton(
                    text = "View Board",
                    onClick = { onNavigate("notices") },
                    flavor = LightCandyFlavor.PASTEL_LAVENDER,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )
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
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (notice.priority == "High") StatusWarningBg else SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (notice.category == "Water") Icons.Default.WaterDrop else Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (notice.priority == "High") StatusWarning else NavyPrimary,
                                modifier = Modifier.size(26.dp)
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

        // Compact Society Emergency Hotline (Small & discreet placement)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("emergency") }
                    .testTag("emergency_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(StatusCritical),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Society Emergency Hotline (24×7)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9F1239)
                            )
                            Text(
                                text = "Security Gate 1 • Medical 102 • Fire 101",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    CandyButton(
                        text = "Call SOS",
                        onClick = { onNavigate("emergency") },
                        flavor = CandyFlavor.RUBY,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }

    if (showReportDialog) {
        ReportComplaintDialog(
            viewModel = viewModel,
            onDismiss = { showReportDialog = false }
        )
    }

    editingFamilyMember?.let { memberToEdit ->
        EditFamilyMemberDialog(
            member = memberToEdit,
            onDismiss = { editingFamilyMember = null },
            onSave = { updated ->
                viewModel.updateFamilyMember(updated)
                editingFamilyMember = null
            }
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
    val myFlat by viewModel.myFlat.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()

    val initialTower = myFlat?.tower ?: "Kaveh"
    val initialFloor = myFlat?.floor?.toString() ?: "3"
    val initialLocation = "Flat $currentFlatId"

    val categories = listOf(
        "Water", "Lift", "Garbage & Waste", "Cleanliness", "Parking",
        "Security", "Noise", "Electricity", "Plumbing", "Garden & Amenities",
        "Pets & Stray Animals", "Pest Control", "Common Property", "Emergency"
    )
    val towers = listOf("Kaveh", "Baraz-1", "Baraz-2", "Zenath")

    var selectedCategory by remember { mutableStateOf("Water") }
    var selectedTower by remember { mutableStateOf(initialTower) }
    var floor by remember { mutableStateOf(initialFloor) }
    var location by remember { mutableStateOf(initialLocation) }
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
            CandyButton(
                text = "Submit Complaint",
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
                flavor = CandyFlavor.SAPPHIRE
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
