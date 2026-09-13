package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun WaterLiftMonitoringScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(16.dp),
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
                                    text = "Infrastructure Telemetry",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Live status across Kaveh, Baraz-1, Baraz-2 & Zenath",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp
                                )
                            }
                            Button(
                                onClick = { showReportDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Report Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Water Monitoring Section
            item {
                Text(
                    text = "💧 Water Supply & Overhead Tanks",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TowerWaterCard("Kaveh", 88, "2.8 bar", "Normal", Modifier.weight(1f))
                    TowerWaterCard("Baraz-1", 82, "2.7 bar", "Normal", Modifier.weight(1f))
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TowerWaterCard("Baraz-2", 64, "1.5 bar", "Maintenance (10 AM - 1 PM)", Modifier.weight(1f), isAlert = true)
                    TowerWaterCard("Zenath", 91, "2.9 bar", "Normal", Modifier.weight(1f))
                }
            }

            // Lift Monitoring Section
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🛗 High-Speed Elevators (Otis AMC)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                TowerLiftCard(
                    tower = "Kaveh Tower (24 Floors)",
                    lift1 = "Passenger Lift 1: Operational",
                    lift1Status = "Operational",
                    lift2 = "Service Lift 2: Sensor Alignment (AMC)",
                    lift2Status = "In Progress"
                )
            }

            item {
                TowerLiftCard(
                    tower = "Baraz-1 Tower (24 Floors)",
                    lift1 = "Passenger Lift 1: Operational",
                    lift1Status = "Operational",
                    lift2 = "Service Lift 2: Operational",
                    lift2Status = "Operational"
                )
            }

            item {
                TowerLiftCard(
                    tower = "Baraz-2 Tower (24 Floors)",
                    lift1 = "Passenger Lift 1: Operational",
                    lift1Status = "Operational",
                    lift2 = "Service Lift 2: Operational",
                    lift2Status = "Operational"
                )
            }

            item {
                TowerLiftCard(
                    tower = "Zenath Tower (24 Floors)",
                    lift1 = "Passenger Lift 1: Operational",
                    lift1Status = "Operational",
                    lift2 = "Service Lift 2: Operational",
                    lift2Status = "Operational"
                )
            }

            // DG Backup & Fire Hydrant System
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚡ Diesel Generator & Fire Hydrant Network",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Main DG Set 1 (750 kVA Cummins)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            StatusBadge(status = "DG Ready")
                        }
                        Text("Diesel Level: 84% • Auto-Mains Failure (AMF) synchronizer active", fontSize = 11.sp, color = TextSecondary)

                        Divider(color = BorderSubtle)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fire Hydrant Pump & Jockey Pump", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            StatusBadge(status = "Normal")
                        }
                        Text("Pressure: 7.2 bar • Tested 08-Sep-2026 • NOC Valid till 2027", fontSize = 11.sp, color = TextSecondary)
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
}

@Composable
fun TowerWaterCard(
    tower: String,
    tankPercent: Int,
    pressure: String,
    status: String,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isAlert) StatusWarningBg else PureWhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tower, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                Text(pressure, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = tankPercent / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isAlert) StatusWarning else StatusSuccess,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tank: $tankPercent%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(if (isAlert) "Alert" else "Normal", fontSize = 11.sp, color = if (isAlert) StatusWarning else StatusSuccess, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun TowerLiftCard(
    tower: String,
    lift1: String,
    lift1Status: String,
    lift2: String,
    lift2Status: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(tower, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(lift1, fontSize = 11.sp, color = TextPrimary)
                StatusBadge(status = lift1Status)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(lift2, fontSize = 11.sp, color = TextPrimary)
                StatusBadge(status = lift2Status)
            }
        }
    }
}
