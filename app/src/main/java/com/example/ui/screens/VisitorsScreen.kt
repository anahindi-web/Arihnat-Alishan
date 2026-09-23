package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.model.VisitorEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.CandyButton
import com.example.ui.components.CandyFlavor
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun VisitorsScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val allVisitors by viewModel.allVisitors.collectAsState()
    val myVisitors by viewModel.myVisitors.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCreatePassDialog by remember { mutableStateOf(false) }
    var viewingPassModal by remember { mutableStateOf<VisitorEntity?>(null) }

    val isGuardOrAdmin = currentRole.isCommitteeMember() || currentRole == UserRole.SECURITY_GUARD || currentRole == UserRole.SECURITY_INCHARGE

    val displayList = if (isGuardOrAdmin) allVisitors else myVisitors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePassDialog = true },
                containerColor = NavyPrimary,
                contentColor = GoldAccent,
                modifier = Modifier.testTag("fab_create_visitor_pass")
            ) {
                Icon(Icons.Default.QrCode, contentDescription = "Create Pass")
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
                    text = { Text("Active & Expected (${displayList.count { it.status != "Checked Out" }})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Past Gate Logs (${displayList.count { it.status == "Checked Out" }})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            val filteredList = if (selectedTab == 0) {
                displayList.filter { it.status != "Checked Out" }
            } else {
                displayList.filter { it.status == "Checked Out" }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pre-approval Banner for Residents
                if (!isGuardOrAdmin) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GoldContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(NavyPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(26.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Pre-Approve Visitors",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = OnGoldContainer
                                        )
                                        Text(
                                            text = "Guests show digital QR or 6-digit pass at Main Gate 1 for instant zero-contact entry.",
                                            fontSize = 11.5.sp,
                                            color = OnGoldContainer.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                                CandyButton(
                                    text = "+ Pass",
                                    onClick = { showCreatePassDialog = true },
                                    flavor = CandyFlavor.AMBER,
                                    shape = RoundedCornerShape(18.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

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
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No visitor records in this view.", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Tap '+ Pass' to generate a digital entry pass.", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredList) { visitor ->
                        VisitorCard(
                            visitor = visitor,
                            isGuardOrAdmin = isGuardOrAdmin,
                            onViewPass = { viewingPassModal = visitor },
                            onUpdateStatus = { newStatus -> viewModel.updateVisitorGateStatus(visitor.id, newStatus) }
                        )
                    }
                }
            }
        }
    }

    if (showCreatePassDialog) {
        CreateVisitorPassDialog(
            viewModel = viewModel,
            onDismiss = { showCreatePassDialog = false }
        )
    }

    if (viewingPassModal != null) {
        DigitalPassQRDialog(
            visitor = viewingPassModal!!,
            onDismiss = { viewingPassModal = null }
        )
    }
}

@Composable
fun VisitorCard(
    visitor: VisitorEntity,
    isGuardOrAdmin: Boolean,
    onViewPass: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
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
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when (visitor.type) {
                                    "Delivery" -> Color(0xFFFEF3C7)
                                    "Cab" -> Color(0xFFE0F2FE)
                                    "Domestic Help" -> Color(0xFFEDE9FE)
                                    else -> SurfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (visitor.type) {
                                "Delivery" -> Icons.Default.LocalShipping
                                "Cab" -> Icons.Default.DirectionsCar
                                "Domestic Help" -> Icons.Default.Badge
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (visitor.type) {
                                "Delivery" -> Color(0xFFB45309)
                                "Cab" -> Color(0xFF0284C7)
                                "Domestic Help" -> Color(0xFF7C3AED)
                                else -> NavyPrimary
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = visitor.visitorName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${visitor.type} • ${visitor.company.ifBlank { "Personal" }}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                StatusBadge(status = visitor.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Destination: ${visitor.tower} • Flat ${visitor.flat}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Expected: ${visitor.expectedArrival}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    if (visitor.inTime.isNotBlank()) {
                        Text(
                            text = "In: ${visitor.inTime}${if (visitor.outTime.isNotBlank()) " | Out: ${visitor.outTime}" else ""}",
                            fontSize = 11.sp,
                            color = StatusSuccess
                        )
                    }
                }

                // Digital Pass Code Badge
                Surface(
                    color = SurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clickable(onClick = onViewPass)
                        .padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = visitor.passCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Guard or approval action buttons
            if (visitor.status == "Approved" || visitor.status == "Expected") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CandyButton(
                        text = "Check In (Gate 1)",
                        onClick = { onUpdateStatus("Inside") },
                        flavor = CandyFlavor.EMERALD,
                        icon = Icons.Default.Login,
                        modifier = Modifier.weight(1f)
                    )

                    CandyButton(
                        text = "Deny Entry",
                        onClick = { onUpdateStatus("Rejected") },
                        flavor = CandyFlavor.RUBY,
                        modifier = Modifier.weight(0.8f)
                    )
                }
            } else if (visitor.status == "Inside") {
                Spacer(modifier = Modifier.height(10.dp))
                CandyButton(
                    text = "Check Out Visitor",
                    onClick = { onUpdateStatus("Checked Out") },
                    flavor = CandyFlavor.NAVY,
                    icon = Icons.Default.Logout,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitorPassDialog(
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    val types = listOf("Guest", "Delivery", "Cab", "Domestic Help", "Contractor")
    var selectedType by remember { mutableStateOf("Guest") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var expectedTime by remember { mutableStateOf("Today, 05:00 PM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Digital Visitor Pass", fontWeight = FontWeight.Bold, color = NavyPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Visitor Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    types.take(3).forEach { t ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = {
                                selectedType = t
                                if (t == "Delivery") company = "Swiggy"
                                if (t == "Cab") company = "Uber"
                            },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    types.drop(3).forEach { t ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = { selectedType = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Visitor Name / Driver Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company / Service (e.g. Swiggy/Uber)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expectedTime,
                    onValueChange = { expectedTime = it },
                    label = { Text("Expected Time of Arrival") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            CandyButton(
                text = "Generate QR Pass",
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createVisitorPass(name, phone, selectedType, company, expectedTime)
                        onDismiss()
                    }
                },
                flavor = CandyFlavor.SAPPHIRE
            )
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun DigitalPassQRDialog(
    visitor: VisitorEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Arihant Alishan Gate Pass", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 16.sp)
                Text("Main Gate 1 • Kharghar", fontSize = 11.sp, color = TextMuted)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Simulated QR Box
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White)
                        .border(2.dp, NavyPrimary, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Pass",
                            modifier = Modifier.size(110.dp),
                            tint = NavyPrimary
                        )
                        Text(
                            text = visitor.passCode,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp,
                            color = NavyPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = visitor.visitorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${visitor.type} • Destination: ${visitor.tower} • Flat ${visitor.flat}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                StatusBadge(status = visitor.status)

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Show this QR or 6-digit code to the Security Guard at Gate 1 or Gate 2 for instant vehicle / pedestrian clearance.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
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
