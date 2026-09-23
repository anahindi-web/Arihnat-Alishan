package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.ParkingSlotEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ParkingScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSlots by viewModel.allParkingSlots.collectAsState()
    val selectedLevel by viewModel.selectedParkingLevel.collectAsState()
    val myVehicles by viewModel.myVehicles.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // ALL, EV_ONLY, AVAILABLE, MY_SLOTS
    var selectedSlotDetail by remember { mutableStateOf<ParkingSlotEntity?>(null) }
    var showDisputeDialog by remember { mutableStateOf(false) }

    val levels = listOf("P1", "P2", "P3", "P4", "P5")

    val levelSlots = allSlots.filter { it.level == selectedLevel }
    val filteredSlots = levelSlots.filter { slot ->
        when (filterType) {
            "EV_ONLY" -> slot.hasEvCharger
            "AVAILABLE" -> slot.status == "Available"
            "MY_SLOTS" -> slot.allottedFlat == currentFlatId
            else -> true
        }
    }

    val totalCount = levelSlots.size
    val availableCount = levelSlots.count { it.status == "Available" }
    val allocatedCount = levelSlots.count { it.status == "Allocated" }
    val visitorCount = levelSlots.count { it.status == "Visitor" }
    val blockedCount = levelSlots.count { it.status == "Blocked" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Multi-level Parking Selector Tabs
        ScrollableTabRow(
            selectedTabIndex = levels.indexOf(selectedLevel).coerceAtLeast(0),
            containerColor = PureWhiteSurface,
            contentColor = NavyPrimary,
            edgePadding = 16.dp
        ) {
            levels.forEach { level ->
                Tab(
                    selected = selectedLevel == level,
                    onClick = { viewModel.setParkingLevel(level) },
                    text = {
                        Text(
                            text = "Level $level",
                            fontWeight = if (selectedLevel == level) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Summary Bar & Legend
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Podium Level $selectedLevel Overview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyPrimary
                    )
                    Text(
                        text = "$availableCount / $totalCount Bays Free",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Color Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem(color = StatusSuccess, label = "Available ($availableCount)")
                    LegendItem(color = NavyPrimary, label = "Allocated ($allocatedCount)")
                    LegendItem(color = GoldChampagne, label = "Visitor ($visitorCount)")
                    LegendItem(color = StatusCritical, label = "Blocked ($blockedCount)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterType == "ALL",
                        onClick = { filterType = "ALL" },
                        label = { Text("All Bays", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = filterType == "EV_ONLY",
                        onClick = { filterType = "EV_ONLY" },
                        label = { Text("⚡ EV Only", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = filterType == "AVAILABLE",
                        onClick = { filterType = "AVAILABLE" },
                        label = { Text("🟢 Vacant", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = filterType == "MY_SLOTS",
                        onClick = { filterType = "MY_SLOTS" },
                        label = { Text("My Slot", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Interactive Parking Bay Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSlots) { slot ->
                ParkingSlotBayItem(
                    slot = slot,
                    isMySlot = slot.allottedFlat == currentFlatId,
                    onClick = { selectedSlotDetail = slot }
                )
            }
        }
    }

    if (selectedSlotDetail != null) {
        ParkingSlotDetailDialog(
            slot = selectedSlotDetail!!,
            onDismiss = { selectedSlotDetail = null },
            onReportDispute = {
                showDisputeDialog = true
            }
        )
    }

    if (showDisputeDialog && selectedSlotDetail != null) {
        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            title = { Text("Report Parking Dispute", fontWeight = FontWeight.Bold, color = NavyPrimary) },
            text = {
                Text(
                    text = "A ticket will be lodged with Society Security & Management for unauthorized vehicle parked in slot ${selectedSlotDetail!!.slotNumber}."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reportComplaint(
                            category = "Parking",
                            subcategory = "Unauthorized Parking",
                            tower = "Podium Level ${selectedSlotDetail!!.level}",
                            floor = selectedSlotDetail!!.level.removePrefix("P").toIntOrNull() ?: 1,
                            location = "Slot ${selectedSlotDetail!!.slotNumber}",
                            description = "Unauthorized vehicle parked in allotted slot ${selectedSlotDetail!!.slotNumber}.",
                            priority = "High"
                        )
                        showDisputeDialog = false
                        selectedSlotDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical)
                ) {
                    Text("Lodge Dispute")
                }
            },
            dismissButton = { TextButton(onClick = { showDisputeDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
fun ParkingSlotBayItem(
    slot: ParkingSlotEntity,
    isMySlot: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when (slot.status) {
        "Available" -> StatusSuccess
        "Allocated" -> if (isMySlot) GoldAccent else NavyPrimary
        "Visitor" -> GoldChampagne
        "Blocked" -> StatusCritical
        else -> BorderSubtle
    }

    val bgColor = when (slot.status) {
        "Available" -> StatusSuccessBg
        "Allocated" -> if (isMySlot) GoldContainer else PureWhiteSurface
        "Visitor" -> Color(0xFFFEF3C7)
        "Blocked" -> StatusCriticalBg
        else -> PureWhiteSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slot.slotNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMySlot) OnGoldContainer else NavyPrimary
                )
                if (slot.hasEvCharger) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "EV Charger",
                        tint = StatusSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Icon(
                imageVector = if (slot.slotType == "Two Wheeler") Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = if (slot.status == "Available") StatusSuccess else if (isMySlot) OnGoldContainer else NavyLight,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isMySlot) "MY FLAT" else if (slot.status == "Available") "FREE" else slot.allottedFlat.ifBlank { slot.status },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMySlot) OnGoldContainer else if (slot.status == "Available") StatusSuccess else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ParkingSlotDetailDialog(
    slot: ParkingSlotEntity,
    onDismiss: () -> Unit,
    onReportDispute: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Parking Bay: ${slot.slotNumber}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                StatusBadge(status = slot.status)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Podium Level: Level ${slot.level}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("Type: ${slot.slotType}", fontSize = 13.sp)
                Text("EV Fast Charger: ${if (slot.hasEvCharger) "Installed (22kW Type-2 AC)" else "No"}", fontSize = 13.sp)

                Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

                Text("Allotted Flat: ${slot.allottedFlat.ifBlank { "Unassigned / Society Common" }}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                if (slot.vehiclePlate.isNotBlank()) {
                    Text("Authorized Vehicle: ${slot.vehiclePlate}", fontSize = 13.sp, color = NavyLight, fontWeight = FontWeight.Bold)
                }

                if (slot.status == "Allocated") {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onReportDispute,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Report Wrong Parking in this Slot", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                Text("Close")
            }
        }
    )
}
