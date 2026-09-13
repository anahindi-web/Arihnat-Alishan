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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ServiceRequestsScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.allServiceRequests.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val isManager = currentRole != UserRole.RESIDENT_OWNER && currentRole != UserRole.RESIDENT_TENANT

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = NavyPrimary,
                contentColor = GoldAccent,
                modifier = Modifier.testTag("fab_new_service_request")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New NOC / Permit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhiteBackground)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("NOC & Society Service Requests", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Move-In / Move-Out clearance, Flat Renovation Permits, and Document NOCs.", fontSize = 12.sp, color = TextSecondary)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (requests.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No pending service requests.", fontSize = 13.sp, color = TextMuted)
                            }
                        }
                    }
                } else {
                    items(requests) { req ->
                        ServiceRequestCard(
                            req = req,
                            isManager = isManager,
                            onStatusChange = { newStatus -> viewModel.updateServiceRequestStatus(req.id, newStatus) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateServiceRequestDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun ServiceRequestCard(
    req: ServiceRequestEntity,
    isManager: Boolean,
    onStatusChange: (String) -> Unit
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (req.type) {
                                "Renovation Permit" -> Icons.Default.Handyman
                                "Move-In NOC" -> Icons.Default.MoveToInbox
                                "Move-Out Clearance" -> Icons.Default.Outbox
                                else -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = OnGoldContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(req.type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Ticket: #${req.id} • ${req.createdAt}", fontSize = 11.sp, color = TextMuted)
                    }
                }

                StatusBadge(status = req.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Flat: ${req.tower} • Flat ${req.flat}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(req.details, fontSize = 12.sp, color = TextSecondary)

            if (req.contractorName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Engineering, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Contractor: ${req.contractorName} • Approved Workers: ${req.workerCount}",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            if (isManager && req.status == "Pending") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange("Approved") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve NOC", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onStatusChange("Rejected") },
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

@Composable
fun CreateServiceRequestDialog(
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    val types = listOf("Move-In NOC", "Move-Out Clearance", "Renovation Permit", "Document Issuance NOC")
    var selectedType by remember { mutableStateOf("Renovation Permit") }
    var details by remember { mutableStateOf("") }
    var contractor by remember { mutableStateOf("") }
    var workers by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply for Society Permit / NOC", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Service Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                types.forEach { t ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = t },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedType == t, onClick = { selectedType = t })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Work Details / Dates") },
                    placeholder = { Text("e.g. Modular kitchen carpentering work 10am to 6pm") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedType == "Renovation Permit") {
                    OutlinedTextField(
                        value = contractor,
                        onValueChange = { contractor = it },
                        label = { Text("Contractor / Agency Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = workers,
                        onValueChange = { workers = it },
                        label = { Text("Number of Workers (Daily Pass)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (details.isNotBlank()) {
                        viewModel.createServiceRequest(
                            type = selectedType,
                            details = details,
                            contractor = contractor,
                            workers = workers.toIntOrNull() ?: 1
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
