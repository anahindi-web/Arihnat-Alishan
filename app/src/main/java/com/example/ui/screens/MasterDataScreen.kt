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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterDataScreen(
    viewModel: ArihantViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val societyConfig by viewModel.societyConfig.collectAsState()
    val allFlats by viewModel.allFlats.collectAsState()
    val allParkingSlots by viewModel.allParkingSlots.collectAsState()
    val allAmenities by viewModel.allAmenities.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val committeeMembers by viewModel.committeeDirectory.collectAsState()

    val isCommittee = currentRole.isCommitteeMember()

    var selectedTab by remember { mutableStateOf(0) }
    // Tabs: 0: Society Config & Tariffs, 1: Flats & Units, 2: Parking Bays, 3: Amenities, 4: Vendors & AMCs, 5: Committee Directory

    // Dialog states
    var showEditConfigDialog by remember { mutableStateOf(false) }
    var flatToEdit by remember { mutableStateOf<FlatEntity?>(null) }
    var showAddFlatDialog by remember { mutableStateOf(false) }
    var parkingSlotToEdit by remember { mutableStateOf<ParkingSlotEntity?>(null) }
    var amenityToEdit by remember { mutableStateOf<AmenityEntity?>(null) }
    var vendorToEdit by remember { mutableStateOf<VendorMasterEntity?>(null) }
    var showAddVendorDialog by remember { mutableStateOf(false) }
    var committeeMemberToEdit by remember { mutableStateOf<CommitteeMasterEntity?>(null) }
    var showAddCommitteeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Society Master Data",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = if (isCommittee) "Committee Member Amend Mode (MCS Act 1960)" else "Official Registry (Read-Only Resident View)",
                            fontSize = 11.sp,
                            color = if (isCommittee) StatusSuccess else TextMuted,
                            fontWeight = if (isCommittee) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NavyPrimary)
                    }
                },
                actions = {
                    if (isCommittee) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldAccent.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amend Access", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhiteSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhiteBackground)
        ) {
            // Authorization Info Banner
            if (!isCommittee) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Master data changes are restricted to Managing Committee office bearers. Use the role switcher in the header to access amend tools.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authenticated as ${currentRole.displayName}. You can amend flat allocations, parking slots, society tariffs, amenity rules, and vendor contracts.",
                            fontSize = 11.sp,
                            color = NavyPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Scrollable Master Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhiteSurface,
                contentColor = NavyPrimary,
                edgePadding = 12.dp
            ) {
                val tabs = listOf(
                    "Config & Tariffs",
                    "Flats & Units",
                    "Parking Bays",
                    "Amenities Master",
                    "Vendors & AMCs",
                    "Committee Directory"
                )
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> SocietyConfigTab(
                        config = societyConfig,
                        isCommittee = isCommittee,
                        onEdit = { showEditConfigDialog = true }
                    )
                    1 -> FlatsMasterTab(
                        flats = allFlats,
                        isCommittee = isCommittee,
                        onEditFlat = { flatToEdit = it },
                        onAddFlat = { showAddFlatDialog = true },
                        onDeleteFlat = { viewModel.deleteFlat(it) }
                    )
                    2 -> ParkingMasterTab(
                        slots = allParkingSlots,
                        isCommittee = isCommittee,
                        onEditSlot = { parkingSlotToEdit = it }
                    )
                    3 -> AmenitiesMasterTab(
                        amenities = allAmenities,
                        isCommittee = isCommittee,
                        onEditAmenity = { amenityToEdit = it }
                    )
                    4 -> VendorsMasterTab(
                        vendors = allVendors,
                        isCommittee = isCommittee,
                        onEditVendor = { vendorToEdit = it },
                        onAddVendor = { showAddVendorDialog = true },
                        onDeleteVendor = { viewModel.deleteVendor(it) }
                    )
                    5 -> CommitteeDirectoryTab(
                        members = committeeMembers,
                        isCommittee = isCommittee,
                        onEditMember = { committeeMemberToEdit = it },
                        onAddMember = { showAddCommitteeDialog = true },
                        onDeleteMember = { viewModel.deleteCommitteeMember(it) }
                    )
                }
            }
        }
    }

    // Dialogs for amending master data
    if (showEditConfigDialog && societyConfig != null) {
        AmendSocietyConfigDialog(
            currentConfig = societyConfig!!,
            onDismiss = { showEditConfigDialog = false },
            onSave = { updated ->
                viewModel.amendSocietyConfig(updated)
                showEditConfigDialog = false
            }
        )
    }

    flatToEdit?.let { flat ->
        AmendFlatDialog(
            flat = flat,
            onDismiss = { flatToEdit = null },
            onSave = { updated ->
                viewModel.amendFlat(updated)
                flatToEdit = null
            }
        )
    }

    if (showAddFlatDialog) {
        AddFlatDialog(
            onDismiss = { showAddFlatDialog = false },
            onAdd = { newFlat ->
                viewModel.addFlat(newFlat)
                showAddFlatDialog = false
            }
        )
    }

    parkingSlotToEdit?.let { slot ->
        AmendParkingSlotDialog(
            slot = slot,
            onDismiss = { parkingSlotToEdit = null },
            onSave = { updated ->
                viewModel.amendParkingSlot(updated)
                parkingSlotToEdit = null
            }
        )
    }

    amenityToEdit?.let { amenity ->
        AmendAmenityDialog(
            amenity = amenity,
            onDismiss = { amenityToEdit = null },
            onSave = { updated ->
                viewModel.amendAmenity(updated)
                amenityToEdit = null
            }
        )
    }

    vendorToEdit?.let { vendor ->
        AmendVendorDialog(
            vendor = vendor,
            onDismiss = { vendorToEdit = null },
            onSave = { updated ->
                viewModel.amendVendor(updated)
                vendorToEdit = null
            }
        )
    }

    if (showAddVendorDialog) {
        AddVendorDialog(
            onDismiss = { showAddVendorDialog = false },
            onAdd = { newVendor ->
                viewModel.addVendor(newVendor)
                showAddVendorDialog = false
            }
        )
    }

    committeeMemberToEdit?.let { member ->
        AmendCommitteeMemberDialog(
            member = member,
            onDismiss = { committeeMemberToEdit = null },
            onSave = { updated ->
                viewModel.amendCommitteeMember(updated)
                committeeMemberToEdit = null
            }
        )
    }

    if (showAddCommitteeDialog) {
        AddCommitteeMemberDialog(
            onDismiss = { showAddCommitteeDialog = false },
            onAdd = { newMember ->
                viewModel.addCommitteeMember(newMember)
                showAddCommitteeDialog = false
            }
        )
    }
}

// ==========================================
// 1. Society Config & Tariffs Tab
// ==========================================
@Composable
private fun SocietyConfigTab(
    config: SocietyConfigEntity?,
    isCommittee: Boolean,
    onEdit: () -> Unit
) {
    if (config == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(config.societyName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("Registration No: ${config.regNumber}", fontSize = 11.sp, color = TextMuted)
                        }
                        if (isCommittee) {
                            Button(
                                onClick = onEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_amend_config")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amend Tariffs", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(10.dp))

                    DetailItemRow("Statutory Compliance", config.actCompliance)
                    DetailItemRow("Society Address", config.address)
                    DetailItemRow("Scale of Complex", "${config.totalTowers} High-Rise Towers (G+53) • ${config.totalUnits} Residential Flats")
                    DetailItemRow("Registration Date", config.registrationDate)
                    DetailItemRow("Office Timings", config.officeHours)
                    DetailItemRow("Emergency Helpline", config.emergencyPhone)
                }
            }
        }

        item {
            Text("Financial Tariffs & Rates (Approved by AGM)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailItemRow("Base Maintenance Rate", "₹${config.baseMaintenanceRateSqFt} / sq.ft per month")
                    DetailItemRow("Sinking Fund Rate", "${config.sinkingFundRate}% of construction cost p.a.")
                    DetailItemRow("Repair & Replacement Fund", "${config.repairFundRate}% of construction cost p.a.")
                    DetailItemRow("Late Payment Penalty", "${config.latePaymentInterestPercent}% p.a. simple interest (MCS Bye-law 71)")
                    DetailItemRow("Move-In Deposit & Charges", "₹${config.moveInCharges}")
                    DetailItemRow("Move-Out NOC Clearance Fee", "₹${config.moveOutClearanceCharges}")
                }
            }
        }

        item {
            Text("Society Designated Bank Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailItemRow("Bank Name", config.bankName)
                    DetailItemRow("Account Number", config.bankAccountNumber)
                    DetailItemRow("IFSC Code", config.bankIfsc)
                    DetailItemRow("Account Type", "Current Account (Dual Authorized Signatories)")
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Audit Trail: Last Amended By", fontSize = 10.sp, color = TextMuted)
                        Text("${config.lastAmendedBy} on ${config.lastAmendedDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. Flats & Units Master Tab
// ==========================================
@Composable
private fun FlatsMasterTab(
    flats: List<FlatEntity>,
    isCommittee: Boolean,
    onEditFlat: (FlatEntity) -> Unit,
    onAddFlat: () -> Unit,
    onDeleteFlat: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTower by remember { mutableStateOf("ALL") }

    val filtered = flats.filter {
        val matchesTower = selectedTower == "ALL" || it.tower.contains(selectedTower, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                it.flatId.contains(searchQuery, ignoreCase = true) ||
                it.ownerName.contains(searchQuery, ignoreCase = true)
        matchesTower && matchesQuery
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Controls Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhiteSurface)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search flat or owner...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )

                if (isCommittee) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAddFlat,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(50.dp).testTag("btn_add_flat")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Flat", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "Kaveh", "Baraz-1", "Baraz-2", "Zenath").forEach { tower ->
                    item {
                        FilterChip(
                            selected = selectedTower == tower,
                            onClick = { selectedTower = tower },
                            label = { Text(if (tower == "ALL") "All Towers (${flats.size})" else tower, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { flat ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(12.dp),
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NavyPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = flat.flatId,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(flat.ownerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${flat.tower} Tower • Floor ${flat.floor} • ${flat.flatType}", fontSize = 11.sp, color = TextMuted)
                                }
                            }

                            StatusBadge(status = flat.occupancyStatus)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Phone: ${flat.ownerPhone}", fontSize = 11.sp, color = TextSecondary)
                            Text("Area: ${flat.areaSqFt} sq.ft", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NavyPrimary)
                        }

                        if (isCommittee) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { onEditFlat(flat) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp).testTag("btn_amend_flat_${flat.flatId}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Amend", fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { onDeleteFlat(flat.flatId) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. Parking Bays Master Tab
// ==========================================
@Composable
private fun ParkingMasterTab(
    slots: List<ParkingSlotEntity>,
    isCommittee: Boolean,
    onEditSlot: (ParkingSlotEntity) -> Unit
) {
    var selectedLevel by remember { mutableStateOf("ALL") }

    val filtered = slots.filter {
        selectedLevel == "ALL" || it.level == selectedLevel
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhiteSurface)
                .padding(12.dp)
        ) {
            Text("Multi-Level Parking Slots Registry (P1 to P5)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "P1", "P2", "P3", "P4", "P5").forEach { level ->
                    item {
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { selectedLevel = level },
                            label = { Text(if (level == "ALL") "All Levels (${slots.size})" else "Level $level", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { slot ->
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (slot.hasEvCharger) Color(0xFFE0F2FE) else SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (slot.hasEvCharger) Icons.Default.ElectricCar else Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = if (slot.hasEvCharger) Color(0xFF0284C7) else NavyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(slot.slotNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (slot.hasEvCharger) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF0284C7).copy(alpha = 0.15f)
                                        ) {
                                            Text("EV Charger", color = Color(0xFF0284C7), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text("Allotted: Flat ${slot.allottedFlat} • ${slot.vehiclePlate.ifBlank { "Unassigned Plate" }}", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(status = slot.status)
                            if (isCommittee) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onEditSlot(slot) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant, contentColor = NavyPrimary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Amend", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. Amenities Master Tab
// ==========================================
@Composable
private fun AmenitiesMasterTab(
    amenities: List<AmenityEntity>,
    isCommittee: Boolean,
    onEditAmenity: (AmenityEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Clubhouse & Lifestyle Amenities Master Rules", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
        }

        items(amenities) { amenity ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(amenity.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("Operating Hours: ${amenity.timings}", fontSize = 11.sp, color = TextMuted)
                        }
                        StatusBadge(status = amenity.status)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(amenity.description, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Capacity: ${amenity.capacity} persons", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = if (amenity.bookingFee == 0) "Fee: Complimentary" else "Fee: ₹${amenity.bookingFee} (Deposit: ₹${amenity.securityDeposit})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (amenity.bookingFee == 0) StatusSuccess else NavyPrimary
                        )
                    }

                    if (isCommittee) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { onEditAmenity(amenity) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_amend_amenity_${amenity.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amend Tariffs & Hours", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. Vendors & AMCs Master Tab
// ==========================================
@Composable
private fun VendorsMasterTab(
    vendors: List<VendorMasterEntity>,
    isCommittee: Boolean,
    onEditVendor: (VendorMasterEntity) -> Unit,
    onAddVendor: () -> Unit,
    onDeleteVendor: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Statutory AMC & Service Agency Master", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Active contracts under MCS Act 1960 oversight", fontSize = 11.sp, color = TextMuted)
                }
                if (isCommittee) {
                    Button(
                        onClick = onAddVendor,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_add_vendor")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Vendor", fontSize = 12.sp)
                    }
                }
            }
        }

        items(vendors) { vendor ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(vendor.agencyName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Text("Category: ${vendor.category} • AMC: ${vendor.amcRefNumber}", fontSize = 11.sp, color = TextMuted)
                        }
                        StatusBadge(status = vendor.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(vendor.notes, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(6.dp))

                    DetailItemRow("Contract Validity", "${vendor.contractStartDate} to ${vendor.contractExpiryDate}")
                    DetailItemRow("Monthly Billing", "₹${vendor.monthlyCharges} / month")
                    DetailItemRow("Contact Person", "${vendor.contactPerson} (${vendor.contactPhone})")
                    DetailItemRow("Emergency Helpline", vendor.emergencyHelpline)

                    if (isCommittee) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { onEditVendor(vendor) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_amend_vendor_${vendor.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amend Contract", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { onDeleteVendor(vendor.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. Committee Directory Master Tab
// ==========================================
@Composable
private fun CommitteeDirectoryTab(
    members: List<CommitteeMasterEntity>,
    isCommittee: Boolean,
    onEditMember: (CommitteeMasterEntity) -> Unit,
    onAddMember: () -> Unit,
    onDeleteMember: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Managing Committee Directory Master", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Elected Office Bearers • Term 2024 - 2029", fontSize = 11.sp, color = TextMuted)
                }
                if (isCommittee) {
                    Button(
                        onClick = onAddMember,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_add_committee_member")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Member", fontSize = 12.sp)
                    }
                }
            }
        }

        items(members) { member ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.fullName.take(2).uppercase(),
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(member.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${member.roleTitle} • Flat ${member.flatId}", fontSize = 11.sp, color = GoldChampagne, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        if (member.isBankSignatory) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusSuccessBg
                            ) {
                                Text("Bank Signatory", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(member.portfolioDescription, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phone: ${member.phone}", fontSize = 11.sp, color = TextMuted)
                        Text("Email: ${member.email}", fontSize = 11.sp, color = TextMuted)
                    }

                    if (isCommittee) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { onEditMember(member) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_amend_committee_${member.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amend", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { onDeleteMember(member.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Helper Row Composable
// ==========================================
@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = TextMuted)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// ==========================================
// Dialogs for Amending Master Data
// ==========================================
@Composable
fun AmendSocietyConfigDialog(
    currentConfig: SocietyConfigEntity,
    onDismiss: () -> Unit,
    onSave: (SocietyConfigEntity) -> Unit
) {
    var rate by remember { mutableStateOf(currentConfig.baseMaintenanceRateSqFt.toString()) }
    var sinking by remember { mutableStateOf(currentConfig.sinkingFundRate.toString()) }
    var repair by remember { mutableStateOf(currentConfig.repairFundRate.toString()) }
    var lateFee by remember { mutableStateOf(currentConfig.latePaymentInterestPercent.toString()) }
    var moveIn by remember { mutableStateOf(currentConfig.moveInCharges.toString()) }
    var moveOut by remember { mutableStateOf(currentConfig.moveOutClearanceCharges.toString()) }
    var hours by remember { mutableStateOf(currentConfig.officeHours) }
    var phone by remember { mutableStateOf(currentConfig.emergencyPhone) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Society Tariffs & Rules", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Changes update the official master ledger under MCS Act 1960", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Base Maintenance (₹ / sq.ft)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sinking,
                        onValueChange = { sinking = it },
                        label = { Text("Sinking Fund %") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lateFee,
                        onValueChange = { lateFee = it },
                        label = { Text("Late Fee Interest %") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = moveIn,
                        onValueChange = { moveIn = it },
                        label = { Text("Move-In Fee (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = moveOut,
                        onValueChange = { moveOut = it },
                        label = { Text("Move-Out Fee (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Society Office Timings") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Emergency Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = currentConfig.copy(
                                baseMaintenanceRateSqFt = rate.toDoubleOrNull() ?: currentConfig.baseMaintenanceRateSqFt,
                                sinkingFundRate = sinking.toDoubleOrNull() ?: currentConfig.sinkingFundRate,
                                latePaymentInterestPercent = lateFee.toDoubleOrNull() ?: currentConfig.latePaymentInterestPercent,
                                moveInCharges = moveIn.toIntOrNull() ?: currentConfig.moveInCharges,
                                moveOutClearanceCharges = moveOut.toIntOrNull() ?: currentConfig.moveOutClearanceCharges,
                                officeHours = hours,
                                emergencyPhone = phone
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Save Amendments")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendFlatDialog(
    flat: FlatEntity,
    onDismiss: () -> Unit,
    onSave: (FlatEntity) -> Unit
) {
    var ownerName by remember { mutableStateOf(flat.ownerName) }
    var ownerPhone by remember { mutableStateOf(flat.ownerPhone) }
    var ownerEmail by remember { mutableStateOf(flat.ownerEmail) }
    var status by remember { mutableStateOf(flat.occupancyStatus) }
    var flatType by remember { mutableStateOf(flat.flatType) }
    var area by remember { mutableStateOf(flat.areaSqFt.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Flat ${flat.flatId} Record", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Registered Owner Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ownerPhone,
                    onValueChange = { ownerPhone = it },
                    label = { Text("Owner Mobile Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ownerEmail,
                    onValueChange = { ownerEmail = it },
                    label = { Text("Owner Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = flatType,
                        onValueChange = { flatType = it },
                        label = { Text("Flat Type (e.g. 2 BHK)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area (Sq.Ft)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Occupancy Status:", fontSize = 11.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Self Occupied", "Rented", "Vacant").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = flat.copy(
                                ownerName = ownerName,
                                ownerPhone = ownerPhone,
                                ownerEmail = ownerEmail,
                                flatType = flatType,
                                areaSqFt = area.toIntOrNull() ?: flat.areaSqFt,
                                occupancyStatus = status
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AddFlatDialog(
    onDismiss: () -> Unit,
    onAdd: (FlatEntity) -> Unit
) {
    var flatId by remember { mutableStateOf("") }
    var tower by remember { mutableStateOf("Kaveh") }
    var floor by remember { mutableStateOf("1") }
    var number by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("+91 ") }
    var occupancy by remember { mutableStateOf("Self Occupied") }
    var flatType by remember { mutableStateOf("2 BHK Royal") }
    var area by remember { mutableStateOf("1150") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add New Flat to Master Registry", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = flatId,
                        onValueChange = { flatId = it },
                        label = { Text("Flat ID (e.g. K-1402)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tower,
                        onValueChange = { tower = it },
                        label = { Text("Tower") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ownerPhone,
                    onValueChange = { ownerPhone = it },
                    label = { Text("Owner Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = flatType,
                        onValueChange = { flatType = it },
                        label = { Text("Flat Type") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area (Sq.Ft)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (flatId.isNotBlank() && ownerName.isNotBlank()) {
                                onAdd(
                                    FlatEntity(
                                        flatId = flatId,
                                        tower = tower,
                                        floor = floor.toIntOrNull() ?: 1,
                                        flatNumber = number.ifBlank { flatId },
                                        ownerName = ownerName,
                                        ownerPhone = ownerPhone,
                                        ownerEmail = "owner.${flatId.lowercase()}@gmail.com",
                                        possessionDate = "01 Jan 2026",
                                        occupancyStatus = occupancy,
                                        flatType = flatType,
                                        areaSqFt = area.toIntOrNull() ?: 1150
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Add Flat")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendParkingSlotDialog(
    slot: ParkingSlotEntity,
    onDismiss: () -> Unit,
    onSave: (ParkingSlotEntity) -> Unit
) {
    var flat by remember { mutableStateOf(slot.allottedFlat) }
    var plate by remember { mutableStateOf(slot.vehiclePlate) }
    var hasEv by remember { mutableStateOf(slot.hasEvCharger) }
    var status by remember { mutableStateOf(slot.status) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Parking Bay ${slot.slotNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Level: ${slot.level} • Slot Type: ${slot.slotType}", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = flat,
                    onValueChange = { flat = it },
                    label = { Text("Allotted Flat (e.g. K-1204)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it },
                    label = { Text("Vehicle Registration Plate (e.g. MH-46-AZ-1204)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Designate as EV Charger Bay", fontSize = 12.sp, color = TextPrimary)
                    Switch(checked = hasEv, onCheckedChange = { hasEv = it })
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Bay Status:", fontSize = 11.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Occupied", "Available", "Reserved").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(slot.copy(allottedFlat = flat, vehiclePlate = plate, hasEvCharger = hasEv, status = status))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Update Slot")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendAmenityDialog(
    amenity: AmenityEntity,
    onDismiss: () -> Unit,
    onSave: (AmenityEntity) -> Unit
) {
    var fee by remember { mutableStateOf(amenity.bookingFee.toString()) }
    var deposit by remember { mutableStateOf(amenity.securityDeposit.toString()) }
    var timings by remember { mutableStateOf(amenity.timings) }
    var capacity by remember { mutableStateOf(amenity.capacity.toString()) }
    var status by remember { mutableStateOf(amenity.status) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend ${amenity.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = timings,
                    onValueChange = { timings = it },
                    label = { Text("Operating Timings") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fee,
                        onValueChange = { fee = it },
                        label = { Text("Fee (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deposit,
                        onValueChange = { deposit = it },
                        label = { Text("Deposit (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Max Capacity (persons)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Operational Status:", fontSize = 11.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Available", "Maintenance", "Closed").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                amenity.copy(
                                    timings = timings,
                                    bookingFee = fee.toIntOrNull() ?: amenity.bookingFee,
                                    securityDeposit = deposit.toIntOrNull() ?: amenity.securityDeposit,
                                    capacity = capacity.toIntOrNull() ?: amenity.capacity,
                                    status = status
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Save Amenity")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendVendorDialog(
    vendor: VendorMasterEntity,
    onDismiss: () -> Unit,
    onSave: (VendorMasterEntity) -> Unit
) {
    var agencyName by remember { mutableStateOf(vendor.agencyName) }
    var contactPerson by remember { mutableStateOf(vendor.contactPerson) }
    var phone by remember { mutableStateOf(vendor.contactPhone) }
    var monthly by remember { mutableStateOf(vendor.monthlyCharges.toString()) }
    var expiry by remember { mutableStateOf(vendor.contractExpiryDate) }
    var status by remember { mutableStateOf(vendor.status) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Vendor: ${vendor.category}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = agencyName,
                    onValueChange = { agencyName = it },
                    label = { Text("Agency Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = { contactPerson = it },
                        label = { Text("Contact Person") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = monthly,
                        onValueChange = { monthly = it },
                        label = { Text("Monthly Fee (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = { Text("Contract Expiry") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                vendor.copy(
                                    agencyName = agencyName,
                                    contactPerson = contactPerson,
                                    contactPhone = phone,
                                    monthlyCharges = monthly.toIntOrNull() ?: vendor.monthlyCharges,
                                    contractExpiryDate = expiry,
                                    status = status
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Save Vendor")
                    }
                }
            }
        }
    }
}

@Composable
fun AddVendorDialog(
    onDismiss: () -> Unit,
    onAdd: (VendorMasterEntity) -> Unit
) {
    var agencyName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Elevator AMC") }
    var contactPerson by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }
    var monthly by remember { mutableStateOf("50000") }
    var expiry by remember { mutableStateOf("31 Dec 2026") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Register New Vendor", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = agencyName,
                    onValueChange = { agencyName = it },
                    label = { Text("Agency Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Service Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("Contact Person") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Emergency Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (agencyName.isNotBlank()) {
                                onAdd(
                                    VendorMasterEntity(
                                        id = "VND-${(110..999).random()}",
                                        category = category,
                                        agencyName = agencyName,
                                        contactPerson = contactPerson,
                                        contactPhone = phone,
                                        contractStartDate = "Today",
                                        contractExpiryDate = expiry,
                                        monthlyCharges = monthly.toIntOrNull() ?: 50000,
                                        emergencyHelpline = phone,
                                        status = "Active",
                                        amcRefNumber = "AMC-NEW-${(100..999).random()}",
                                        notes = "Newly registered society vendor."
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Add Vendor")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendCommitteeMemberDialog(
    member: CommitteeMasterEntity,
    onDismiss: () -> Unit,
    onSave: (CommitteeMasterEntity) -> Unit
) {
    var name by remember { mutableStateOf(member.fullName) }
    var roleTitle by remember { mutableStateOf(member.roleTitle) }
    var flat by remember { mutableStateOf(member.flatId) }
    var phone by remember { mutableStateOf(member.phone) }
    var email by remember { mutableStateOf(member.email) }
    var isSignatory by remember { mutableStateOf(member.isBankSignatory) }
    var portfolio by remember { mutableStateOf(member.portfolioDescription) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Committee Member", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Office Role (e.g. Chairman, Member)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = flat,
                        onValueChange = { flat = it },
                        label = { Text("Flat ID") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = portfolio,
                    onValueChange = { portfolio = it },
                    label = { Text("Portfolio Responsibilities") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bank Account Signatory", fontSize = 12.sp, color = TextPrimary)
                    Switch(checked = isSignatory, onCheckedChange = { isSignatory = it })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                member.copy(
                                    fullName = name,
                                    roleTitle = roleTitle,
                                    flatId = flat,
                                    phone = phone,
                                    portfolioDescription = portfolio,
                                    isBankSignatory = isSignatory
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Save Member")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCommitteeMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (CommitteeMasterEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var roleTitle by remember { mutableStateOf("Committee Member") }
    var flat by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }
    var isSignatory by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Appoint Committee Member", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Portfolio Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = flat,
                    onValueChange = { flat = it },
                    label = { Text("Flat ID (e.g. B1-1203)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(
                                    CommitteeMasterEntity(
                                        id = "CM-${(10..99).random()}",
                                        roleTitle = roleTitle,
                                        fullName = name,
                                        flatId = flat,
                                        phone = phone,
                                        email = "${name.lowercase().replace(" ", ".")}@gmail.com",
                                        term = "2024 - 2029",
                                        isBankSignatory = isSignatory,
                                        portfolioDescription = "Managing Committee member portfolio under MCS Act 1960."
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Appoint")
                    }
                }
            }
        }
    }
}
