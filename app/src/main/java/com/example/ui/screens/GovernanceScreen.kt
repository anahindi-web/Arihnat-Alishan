package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.example.data.model.*
import com.example.ui.ArihantViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun GovernanceScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val allFlats by viewModel.allFlats.collectAsState()
    val allTenants by viewModel.allTenants.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()
    val allBills by viewModel.allMaintenanceBills.collectAsState()
    val allTasks by viewModel.allCommitteeTasks.collectAsState()
    val allMeetings by viewModel.allMeetings.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()
    val allRequests by viewModel.allServiceRequests.collectAsState()
    val taskCompletionEvent by viewModel.taskCompletionEvent.collectAsState()
    val isSoundNotificationEnabled by viewModel.isSoundNotificationEnabled.collectAsState()

    var selectedSection by remember { mutableStateOf(0) }
    // 0: Overview (Role customized), 1: Approval Center, 2: Tasks & Meetings, 3: Audit Trail

    var auditQuery by remember { mutableStateOf("") }
    var auditModule by remember { mutableStateOf("ALL") }
    var showDecisionModal by remember { mutableStateOf<TenantEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
        ) {
        // Section Header
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (currentRole) {
                                UserRole.CHAIRMAN -> "Chairman Executive Portal"
                                UserRole.SECRETARY -> "Hon. Secretary Operations"
                                UserRole.TREASURER -> "Treasury & Finance Management"
                                UserRole.COMMITTEE_MEMBER -> "Managing Committee Dashboard"
                                else -> "Society Governance & Administration"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Arihant Alishan CHS Ltd. • Kharghar (Reg 2026)",
                            fontSize = 11.sp,
                            color = GoldChampagne
                        )
                    }

                    StatusBadge(status = currentRole.displayName)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onNavigate("master_data") },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_gov_master_data")
                    ) {
                        Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Master Data & Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigate("notices") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_gov_notice_board")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Notice Board", fontSize = 11.sp)
                    }
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = PureWhiteSurface,
            contentColor = NavyPrimary
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("KPI Overview", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = {
                    val pendingCount = allTenants.count { it.verificationStatus == "Pending" } + allRequests.count { it.status == "Pending" }
                    Text("Approvals ($pendingCount)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Tasks & AGMs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedSection == 3,
                onClick = { selectedSection = 3 },
                text = { Text("Audit Trail", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        when (selectedSection) {
            0 -> {
                // Role customized KPI Overview
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (currentRole) {
                        UserRole.TREASURER -> {
                            item {
                                TreasurerFinancialOverview(allBills)
                            }
                        }
                        UserRole.SECRETARY -> {
                            item {
                                SecretaryOperationalOverview(allComplaints, allFlats, allTenants)
                            }
                        }
                        else -> {
                            // Chairman / General Executive View
                            item {
                                ChairmanExecutiveOverview(allFlats, allComplaints, allTenants)
                            }
                        }
                    }

                    // Tower comparison health
                    item {
                        Text("Tower-by-Tower Society Distribution", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TowerDistributionCard("Kaveh", 80, 72, 12, Modifier.weight(1f))
                            TowerDistributionCard("Baraz-1", 80, 70, 10, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TowerDistributionCard("Baraz-2", 80, 71, 14, Modifier.weight(1f))
                            TowerDistributionCard("Zenath", 80, 73, 12, Modifier.weight(1f))
                        }
                    }
                }
            }

            1 -> {
                // Unified Approval Center
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Pending Verifications & Approvals",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Review uploaded documents, tenant agreements, and contractor requests.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    val pendingTenants = allTenants.filter { it.verificationStatus == "Pending" }
                    if (pendingTenants.isNotEmpty()) {
                        item {
                            Text("Tenant & Lease Verifications", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        items(pendingTenants) { tenant ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(tenant.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text("Flat: ${tenant.flatId} • ${tenant.occupation}", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        StatusBadge(status = tenant.verificationStatus)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Permanent Address: ${tenant.permanentAddress}", fontSize = 11.sp, color = TextMuted)
                                    Text("Employer: ${tenant.employer} • Phone: ${tenant.phone}", fontSize = 11.sp, color = TextSecondary)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Leave & License Document:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(tenant.agreementDocument, fontSize = 11.sp, color = NavyLight)
                                            Text("Lease: ${tenant.agreementStartDate} to ${tenant.agreementExpiryDate}", fontSize = 11.sp, color = StatusSuccess)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.verifyTenant(tenant.id, "Verified", "All documents verified and approved.")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Approve Tenant", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { showDecisionModal = tenant },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Review Options", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Pending Service Requests
                    val pendingNocs = allRequests.filter { it.status == "Pending" }
                    if (pendingNocs.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Pending Service NOCs & Renovation Permits", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        items(pendingNocs) { req ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${req.type} (#${req.id})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                                        StatusBadge(status = req.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Flat: ${req.tower} • ${req.flat} | Details: ${req.details}", fontSize = 12.sp, color = TextPrimary)
                                    if (req.contractorName.isNotBlank()) {
                                        Text("Contractor: ${req.contractorName} (${req.workerCount} workers)", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.updateServiceRequestStatus(req.id, "Approved") },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Grant NOC", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.updateServiceRequestStatus(req.id, "Rejected") },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(0.8f)
                                        ) {
                                            Text("Reject", fontSize = 11.sp, color = StatusCritical)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pendingTenants.isEmpty() && pendingNocs.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("All verifications clear!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Zero pending approvals in queue.", fontSize = 12.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tasks & AGM Meetings
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Committee Tasks Tracker", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${allTasks.count { it.status == "Completed" }}/${allTasks.size} tasks finished", fontSize = 11.sp, color = TextMuted)
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSoundNotificationEnabled) Color(0xFFE0F2FE) else SurfaceVariant,
                                border = BorderStroke(1.dp, if (isSoundNotificationEnabled) Color(0xFF38BDF8) else CardBorder),
                                modifier = Modifier.clickable { viewModel.toggleSoundNotification() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSoundNotificationEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                        contentDescription = "Sound feedback toggle",
                                        tint = if (isSoundNotificationEnabled) NavyPrimary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSoundNotificationEnabled) "Sound ON" else "Sound OFF",
                                        fontSize = 10.5.sp,
                                        color = if (isSoundNotificationEnabled) NavyPrimary else TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    items(allTasks) { task ->
                        TaskCardItem(
                            task = task,
                            currentRole = currentRole,
                            onComplete = { viewModel.updateTaskStatus(task.id, "Completed") }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("AGM & Managing Committee Meetings", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    items(allMeetings) { meeting ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(meeting.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                                    StatusBadge(status = meeting.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Date: ${meeting.meetingDate} • Location: ${meeting.location}", fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Agenda: ${meeting.agenda}", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Audit Trail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search & module filter
                    OutlinedTextField(
                        value = auditQuery,
                        onValueChange = { auditQuery = it },
                        placeholder = { Text("Search audit records, actions, users...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ALL", "Tenant", "Resident", "Parking", "Complaint", "Finance").forEach { mod ->
                            item {
                                FilterChip(
                                    selected = auditModule == mod,
                                    onClick = { auditModule = mod },
                                    label = { Text(mod, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredLogs = allAuditLogs.filter { log ->
                        val matchesMod = auditModule == "ALL" || log.module == auditModule
                        val matchesQuery = auditQuery.isBlank() ||
                                log.details.contains(auditQuery, ignoreCase = true) ||
                                log.action.contains(auditQuery, ignoreCase = true) ||
                                log.userName.contains(auditQuery, ignoreCase = true)
                        matchesMod && matchesQuery
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredLogs) { log ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(10.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = SurfaceVariant, shape = RoundedCornerShape(4.dp)) {
                                                Text(log.module, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = TextSecondary)
                                            }
                                        }
                                        Text(log.timestamp, fontSize = 10.sp, color = TextMuted)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(log.details, fontSize = 11.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("By: ${log.userName} (${log.userRole})", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        taskCompletionEvent?.let { event ->
            TaskCompletionCelebrationOverlay(
                event = event,
                soundEnabled = isSoundNotificationEnabled,
                onToggleSound = { viewModel.toggleSoundNotification() },
                onDismiss = { viewModel.clearTaskCompletionEvent() }
            )
        }
    }

    if (showDecisionModal != null) {
        val tenant = showDecisionModal!!
        var comment by remember { mutableStateOf("") }
        var decision by remember { mutableStateOf("Correction Required") }

        AlertDialog(
            onDismissRequest = { showDecisionModal = null },
            title = { Text("Tenant Verification Decision", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tenant: ${tenant.fullName} (Flat ${tenant.flatId})", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = decision == "Verified",
                            onClick = { decision = "Verified" },
                            label = { Text("Approve") }
                        )
                        FilterChip(
                            selected = decision == "Correction Required",
                            onClick = { decision = "Correction Required" },
                            label = { Text("Need Correction") }
                        )
                        FilterChip(
                            selected = decision == "Rejected",
                            onClick = { decision = "Rejected" },
                            label = { Text("Reject") }
                        )
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Reason / Note to Owner & Tenant") },
                        placeholder = { Text("e.g. Please upload police verification certificate and clearer lease PDF.") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyTenant(tenant.id, decision, comment)
                        showDecisionModal = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Submit Decision")
                }
            },
            dismissButton = { TextButton(onClick = { showDecisionModal = null }) { Text("Cancel") } }
        )
    }
}

@Composable
fun ChairmanExecutiveOverview(
    flats: List<FlatEntity>,
    complaints: List<ComplaintEntity>,
    tenants: List<TenantEntity>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Society Health & Key Metrics", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiMetricBox("Total Flats", "320", "4 Towers", StatusInfo, Modifier.weight(1f))
                KpiMetricBox("Occupancy", "89.4%", "286 Occupied", StatusSuccess, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiMetricBox("Open Tickets", "${complaints.count { it.status != "Resolved" && it.status != "Closed" }}", "1 In Progress", StatusWarning, Modifier.weight(1f))
                KpiMetricBox("SLA Adherence", "94.2%", "Within 24h", StatusSuccess, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TreasurerFinancialOverview(
    bills: List<MaintenanceBillEntity>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Treasury & Maintenance Collection", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Text("FY 2026-27 Maintenance Ledger", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiMetricBox("Total Billed", "₹18,72,000", "Sep 2026", NavyPrimary, Modifier.weight(1f))
                KpiMetricBox("Total Collected", "₹16,87,500", "90.1% Collection", StatusSuccess, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiMetricBox("Pending Dues", "₹1,84,500", "34 Flats Defaulters", StatusCritical, Modifier.weight(1f))
                KpiMetricBox("Sinking Reserve", "₹42,50,000", "Fixed Deposit", GoldChampagne, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SecretaryOperationalOverview(
    complaints: List<ComplaintEntity>,
    flats: List<FlatEntity>,
    tenants: List<TenantEntity>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Secretarial Operations & Compliance", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiMetricBox("Active Tenants", "${tenants.size}", "1 Pending Verif", StatusInfo, Modifier.weight(1f))
                KpiMetricBox("Complaints", "${complaints.size}", "94% Resolved", StatusSuccess, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun KpiMetricBox(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun TowerDistributionCard(
    tower: String,
    total: Int,
    occupied: Int,
    rented: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(tower, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total: $total Flats", fontSize = 10.sp, color = TextSecondary)
            Text("Occupied: $occupied", fontSize = 10.sp, color = StatusSuccess)
            Text("Rented: $rented", fontSize = 10.sp, color = GoldChampagne)
        }
    }
}
