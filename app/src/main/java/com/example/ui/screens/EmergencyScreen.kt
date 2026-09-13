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
import com.example.ui.ArihantViewModel
import com.example.ui.theme.*

@Composable
fun EmergencyScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    var callingContact by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSosDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Red Emergency Header
        Card(
            colors = CardDefaults.cardColors(containerColor = StatusCritical),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "EMERGENCY ACTION HOTLINE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "24x7 Society Quick Response & Public Emergency Services",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SOS Broadcast Trigger
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSosDialog = true }
                        .testTag("btn_sos_broadcast")
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(StatusCritical),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("TRIGGER SOCIETY SOS ALERT", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Alert Main Gate Security & All Marshals immediately", color = Color(0xFFFECACA), fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // Society Internal Emergency Contacts
            item {
                Text(
                    text = "Society Internal Response Units",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                EmergencyContactCard(
                    title = "Main Gate 1 Security Cabin",
                    subtitle = "Head Guard Ram Singh • 24x7 Gate Hotline",
                    number = "+91 22 2774 9101",
                    icon = Icons.Default.Security,
                    color = NavyPrimary,
                    onCall = { callingContact = Pair("Main Gate Security", "+91 22 2774 9101") }
                )
            }

            item {
                EmergencyContactCard(
                    title = "Society Emergency Plumber",
                    subtitle = "Major water pipeline burst / overhead tank leakage",
                    number = "+91 98200 44551",
                    icon = Icons.Default.WaterDrop,
                    color = StatusInfo,
                    onCall = { callingContact = Pair("Society Emergency Plumber", "+91 98200 44551") }
                )
            }

            item {
                EmergencyContactCard(
                    title = "Otis Lift 24x7 Rescue Hotline",
                    subtitle = "Trapped in elevator / Emergency sensor stop",
                    number = "1800 22 6847",
                    icon = Icons.Default.Elevator,
                    color = GoldChampagne,
                    onCall = { callingContact = Pair("Otis Lift Rescue", "1800 22 6847") }
                )
            }

            item {
                EmergencyContactCard(
                    title = "Emergency Electrician & DG Tech",
                    subtitle = "Electrical sparks / DG failure / Meter room alert",
                    number = "+91 98199 88321",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFFD97706),
                    onCall = { callingContact = Pair("Society Electrician", "+91 98199 88321") }
                )
            }

            // Public Services
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Government & Public Emergency Services",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                EmergencyContactCard(
                    title = "Fire Brigade (NMMC Kharghar)",
                    subtitle = "Emergency: 101 • Sector 12 Station",
                    number = "101 / +91 22 2774 2101",
                    icon = Icons.Default.LocalFireDepartment,
                    color = StatusCritical,
                    onCall = { callingContact = Pair("NMMC Fire Brigade", "101") }
                )
            }

            item {
                EmergencyContactCard(
                    title = "Ambulance & Kharghar Medicity",
                    subtitle = "Emergency: 102 • Sector 35 Trauma Care",
                    number = "102 / +91 22 2774 5500",
                    icon = Icons.Default.MedicalServices,
                    color = StatusCritical,
                    onCall = { callingContact = Pair("Ambulance", "102") }
                )
            }

            item {
                EmergencyContactCard(
                    title = "Kharghar Police Station",
                    subtitle = "Emergency: 112 • Sector 7 Station",
                    number = "112 / +91 22 2774 3366",
                    icon = Icons.Default.LocalPolice,
                    color = NavyPrimary,
                    onCall = { callingContact = Pair("Kharghar Police", "112") }
                )
            }
        }
    }

    if (callingContact != null) {
        AlertDialog(
            onDismissRequest = { callingContact = null },
            title = { Text("Dialing Emergency Hotline", fontWeight = FontWeight.Bold, color = StatusCritical) },
            text = {
                Column {
                    Text("Connecting call to:", fontSize = 12.sp, color = TextSecondary)
                    Text(callingContact!!.first, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text(callingContact!!.second, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StatusCritical)
                }
            },
            confirmButton = {
                Button(onClick = { callingContact = null }, colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)) {
                    Text("Call Now")
                }
            },
            dismissButton = { TextButton(onClick = { callingContact = null }) { Text("Cancel") } }
        )
    }

    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text("🚨 BROADCAST SOS ALERT?", fontWeight = FontWeight.Bold, color = StatusCritical) },
            text = {
                Text("This will immediately send emergency high-priority notifications to Main Gate Security Marshals, Facility Manager, and Society Chairman with your flat location: Kaveh 1204.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reportComplaint(
                            category = "Emergency",
                            subcategory = "SOS Panic Trigger",
                            tower = "Kaveh",
                            floor = 12,
                            location = "Flat 1204",
                            description = "SOS Panic trigger activated by resident Rajesh Sharma. Immediate security marshal response required.",
                            priority = "Critical"
                        )
                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical)
                ) {
                    Text("Confirm Broadcast SOS")
                }
            },
            dismissButton = { TextButton(onClick = { showSosDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun EmergencyContactCard(
    title: String,
    subtitle: String,
    number: String,
    icon: ImageVector,
    color: Color,
    onCall: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(subtitle, fontSize = 11.sp, color = TextSecondary)
                    Text(number, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
                }
            }

            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Call", fontSize = 11.sp)
            }
        }
    }
}
