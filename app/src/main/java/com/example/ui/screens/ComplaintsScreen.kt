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
import com.example.ui.components.StatusBadge
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

    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var selectedTab by remember { mutableStateOf(0) } // 0: My Complaints, 1: Society-Wide, 2: Resolved
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedComplaintDetail by remember { mutableStateOf<ComplaintEntity?>(null) }

    val categories = listOf("ALL", "Water", "Lift", "Garbage & Waste", "Cleanliness", "Parking", "Security", "Electricity", "Plumbing")

    val filteredList = allComplaints.filter { complaint ->
        val matchesTab = when (selectedTab) {
            0 -> complaint.submittedBy == currentFlatId || currentRole != UserRole.RESIDENT_OWNER && currentRole != UserRole.RESIDENT_TENANT
            1 -> true
            2 -> complaint.status == "Resolved" || complaint.status == "Closed"
            else -> true
        }
        val matchesCategory = selectedCategoryFilter == "ALL" || complaint.category == selectedCategoryFilter
        matchesTab && matchesCategory
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showReportDialog = true },
                containerColor = NavyPrimary,
                contentColor = GoldAccent,
                modifier = Modifier.testTag("fab_report_issue")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Report Issue")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhiteBackground)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhiteSurface,
                contentColor = NavyPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("My Complaints", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("All Society Issues", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Resolved Archive", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            // Category Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 12.sp) }
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
                    Text(
                        text = "#${complaint.id}",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = complaint.category,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (complaint.priority == "Critical" || complaint.priority == "High") {
                        StatusBadge(status = complaint.priority)
                    }
                    StatusBadge(status = complaint.status)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = complaint.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(14.dp))
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

    val isManager = currentRole != UserRole.RESIDENT_OWNER && currentRole != UserRole.RESIDENT_TENANT

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
                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    viewModel.addComplaintComment(complaint.id, newComment)
                                    newComment = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = NavyPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                Text("Done")
            }
        }
    )
}
