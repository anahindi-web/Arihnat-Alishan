package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComplaintEntity
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val allComplaints by viewModel.allComplaints.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()

    val isFlatOwnerOnly = !currentRole.canViewAllSocietyComplaints()

    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var selectedTab by remember { mutableStateOf(0) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedComplaintDetail by remember { mutableStateOf<ComplaintEntity?>(null) }

    val tabs = if (isFlatOwnerOnly) {
        listOf("Active Complaints", "Resolved Archive")
    } else {
        listOf("My Complaints", "All Society Issues", "Resolved Archive")
    }

    val categories = listOf("ALL", "Water", "Lift", "Garbage & Waste", "Cleanliness", "Parking", "Security", "Electricity", "Plumbing")

    val filteredList = allComplaints.filter { complaint ->
        if (isFlatOwnerOnly) {
            // Flat owner can ONLY view his own flat complaints. Cannot view any other flat or society issues.
            val isMyComplaint = complaint.submittedBy == currentFlatId
            val matchesTab = if (selectedTab == 0) {
                complaint.status != "Resolved" && complaint.status != "Closed"
            } else {
                complaint.status == "Resolved" || complaint.status == "Closed"
            }
            val matchesCategory = selectedCategoryFilter == "ALL" || complaint.category == selectedCategoryFilter
            isMyComplaint && matchesTab && matchesCategory
        } else {
            val matchesTab = when (selectedTab) {
                0 -> complaint.submittedBy == currentFlatId
                1 -> true
                2 -> complaint.status == "Resolved" || complaint.status == "Closed"
                else -> true
            }
            val matchesCategory = selectedCategoryFilter == "ALL" || complaint.category == selectedCategoryFilter
            matchesTab && matchesCategory
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            CandyButton(
                text = "+ Report Issue",
                onClick = { showReportDialog = true },
                flavor = CandyFlavor.RUBY,
                icon = Icons.Default.Add,
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier.testTag("fab_report_issue")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhiteBackground)
        ) {
            // Candy Header
            Surface(
                color = PureWhiteSurface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    CandySectionHeading(
                        title = if (isFlatOwnerOnly) "My Flat Issues & Work Orders" else "Complaints & Service Tickets",
                        subtitle = if (isFlatOwnerOnly) "Flat $currentFlatId • Private to your household" else "Society-wide Maintenance Tickets",
                        icon = Icons.Default.Build,
                        flavor = LightCandyFlavor.PASTEL_CORAL,
                        badgeText = "${filteredList.size} Tickets"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    CandyPillTabRow(
                        tabs = tabs,
                        selectedIndex = selectedTab.coerceIn(0, tabs.size - 1),
                        onTabSelected = { selectedTab = it }
                    )
                }
            }

            // Category Chips Row in light candy style
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.5.sp) }
                    )
                }
            }

            // Complaints List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No complaints found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Everything is running smoothly.", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredList) { complaint ->
                        DetailedComplaintCard(
                            complaint = complaint,
                            isStaffOrManager = currentRole != UserRole.RESIDENT_OWNER && currentRole != UserRole.RESIDENT_TENANT,
                            onCardClick = { selectedComplaintDetail = complaint },
                            onReopen = { reason -> viewModel.reopenComplaint(complaint.id, reason) }
                        )
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        ReportComplaintDialog(
            viewModel = viewModel,
            onDismiss = { showReportDialog = false }
        )
    }

    if (selectedComplaintDetail != null) {
        ComplaintDetailModal(
            complaint = selectedComplaintDetail!!,
            viewModel = viewModel,
            onDismiss = { selectedComplaintDetail = null }
        )
    }
}

@Composable
fun DetailedComplaintCard(
    complaint: ComplaintEntity,
    isStaffOrManager: Boolean,
    onCardClick: () -> Unit,
    onReopen: (String) -> Unit
) {
    var showReopenPrompt by remember { mutableStateOf(false) }
    var reopenReason by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (catBg, catTint, catIcon) = when (complaint.category) {
                        "Water" -> Triple(StatusInfoBg, StatusInfo, Icons.Default.WaterDrop)
                        "Electrical" -> Triple(GoldContainer, GoldAccent, Icons.Default.Bolt)
                        "Lift" -> Triple(Color(0xFFFEF3C7), GoldChampagne, Icons.Default.Elevator)
                        "Security" -> Triple(StatusSuccessBg, StatusSuccess, Icons.Default.Security)
                        "Housekeeping" -> Triple(Color(0xFFEDE9FE), Color(0xFF7C3AED), Icons.Default.CleaningServices)
                        else -> Triple(SurfaceVariant, NavyPrimary, Icons.Default.Build)
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(catBg)
                            .border(1.dp, catTint.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = catIcon,
                            contentDescription = null,
                            tint = catTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "#${complaint.id}",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 14.5.sp
                        )
                        Text(
                            text = complaint.category,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (complaint.priority == "Critical" || complaint.priority == "High") {
                        StatusBadge(status = complaint.priority)
                    }
                    StatusBadge(status = complaint.status)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = complaint.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${complaint.tower} • Floor ${complaint.floor} • ${complaint.location}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            if (complaint.duplicateReportCount > 1) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠️ Reported by ${complaint.duplicateReportCount} residents in ${complaint.tower}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusWarning
                )
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BorderSubtle)

            // Visual Status Timeline (Submitted -> Assigned -> In Progress -> Resolved)
            ComplaintMiniTimeline(status = complaint.status)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned: ${complaint.assignedTo.ifBlank { "Awaiting Assignment" }}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )

                if (complaint.status == "Resolved" || complaint.status == "Closed") {
                    TextButton(onClick = { showReopenPrompt = true }) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reopen Issue", fontSize = 11.sp, color = StatusCritical)
                    }
                } else {
                    Text(
                        text = "Submitted: ${complaint.createdAt}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }

    if (showReopenPrompt) {
        AlertDialog(
            onDismissRequest = { showReopenPrompt = false },
            title = { Text("Reopen Complaint #${complaint.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please explain why this issue was not satisfactorily resolved:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reopenReason,
                        onValueChange = { reopenReason = it },
                        label = { Text("Reason for Reopening") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reopenReason.isNotBlank()) {
                            onReopen(reopenReason)
                            showReopenPrompt = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical)
                ) {
                    Text("Confirm Reopen")
                }
            },
            dismissButton = { TextButton(onClick = { showReopenPrompt = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun ComplaintMiniTimeline(status: String) {
    val steps = listOf("Submitted", "Assigned", "In Progress", "Resolved")
    val currentIndex = when (status) {
        "Submitted" -> 0
        "Acknowledged", "Assigned" -> 1
        "In Progress" -> 2
        "Resolved", "Closed" -> 3
        else -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val isDone = index <= currentIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isDone) StatusSuccess else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = if (isDone) NavyPrimary else TextMuted,
                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ComplaintDetailModal(
    complaint: ComplaintEntity,
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val comments by viewModel.getComplaintComments(complaint.id).collectAsState(initial = emptyList())
    var newComment by remember { mutableStateOf("") }
    var selectedNewStatus by remember { mutableStateOf(complaint.status) }
    var assignedVendor by remember { mutableStateOf(complaint.assignedTo) }

    val isManager = currentRole.canAmendComplaints() ||
            currentRole == UserRole.HOUSEKEEPING_SUPERVISOR ||
            currentRole == UserRole.SECURITY_INCHARGE

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#${complaint.id} • ${complaint.category}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                    StatusBadge(status = complaint.status)
                }
                Text("${complaint.tower} • ${complaint.location}", fontSize = 12.sp, color = TextSecondary)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(complaint.description, fontSize = 13.sp, color = TextPrimary)
                }

                item {
                    Divider(color = BorderSubtle)
                    Text("Resolution Timeline & SLA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Created: ${complaint.createdAt}", fontSize = 11.sp, color = TextMuted)
                    if (complaint.expectedResolution.isNotBlank()) {
                        Text("Expected by: ${complaint.expectedResolution}", fontSize = 11.sp, color = StatusInfo)
                    }
                    if (complaint.resolvedAt.isNotBlank()) {
                        Text("Resolved at: ${complaint.resolvedAt}", fontSize = 11.sp, color = StatusSuccess)
                    }
                }

                // Manager Action Controls
                if (isManager) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Management Actions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Change Status:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Assigned", "In Progress", "Resolved").forEach { st ->
                                        FilterChip(
                                            selected = selectedNewStatus == st,
                                            onClick = {
                                                selectedNewStatus = st
                                                viewModel.updateComplaintStatus(complaint.id, st, assignedVendor, "Status changed to $st")
                                            },
                                            label = { Text(st, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = assignedVendor,
                                    onValueChange = { assignedVendor = it },
                                    label = { Text("Assigned Vendor / Staff", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Comments log
                item {
                    Divider(color = BorderSubtle)
                    Text("Activity History & Comments", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                items(comments) { comment ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${comment.authorName} (${comment.authorRole})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Text(comment.timestamp, fontSize = 9.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(comment.commentText, fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }

                // Add comment field
                item {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("Write update or note...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CandyIconButton(
                            icon = Icons.Default.Send,
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    viewModel.addComplaintComment(complaint.id, newComment)
                                    newComment = ""
                                }
                            },
                            flavor = CandyFlavor.SAPPHIRE,
                            modifier = Modifier.size(40.dp),
                            iconSize = 18.dp
                        )
                    }
                }
            }
        },
        confirmButton = {
            CandyButton(
                text = "Done",
                onClick = onDismiss,
                flavor = CandyFlavor.NAVY
            )
        }
    )
}
