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
import com.example.data.model.*
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MasterProfileScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val myFlat by viewModel.myFlat.collectAsState()
    val familyMembers by viewModel.myFamilyMembers.collectAsState()
    val tenants by viewModel.myTenants.collectAsState()
    val vehicles by viewModel.myVehicles.collectAsState()
    val pets by viewModel.myPets.collectAsState()
    val domesticHelp by viewModel.myDomesticHelp.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Family", "Tenants", "Vehicles", "Pets & Staff")

    var showAddFamilyDialog by remember { mutableStateOf(false) }
    var showAddTenantDialog by remember { mutableStateOf(false) }
    var showAddVehicleDialog by remember { mutableStateOf(false) }
    var showAddPetDialog by remember { mutableStateOf(false) }
    var showAddHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = PureWhiteSurface,
            contentColor = NavyPrimary,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Household Overview & Occupancy Mode
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                            shape = RoundedCornerShape(16.dp),
                            border = CardDefaults.outlinedCardBorder(),
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
                                            text = "Flat Household Master Record",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                        Text(
                                            text = "Kaveh Tower • 12th Floor • Flat 1204",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    StatusBadge(status = "Verified")
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                                Text(
                                    text = "Owner: Rajesh Sharma",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text("Contact: +91 98201 12345 | rajesh.sharma@example.com", fontSize = 12.sp, color = TextSecondary)
                                Text("Possession Date: 15-Dec-2022", fontSize = 12.sp, color = TextMuted)

                                Spacer(modifier = Modifier.height(14.dp))

                                // Occupancy Status toggle
                                Text(
                                    text = "Occupancy Status",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val currentOccupancy = myFlat?.occupancyStatus ?: "Self Occupied"
                                    listOf("Self Occupied", "Rented", "Vacant").forEach { status ->
                                        val isSelected = currentOccupancy == status
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                if (status == "Rented") {
                                                    // Mandate Tenant information
                                                    showAddTenantDialog = true
                                                } else {
                                                    viewModel.submitTenantRegistration(
                                                        flatId = "K-1204",
                                                        name = "",
                                                        phone = "",
                                                        email = "",
                                                        permanentAddress = "",
                                                        occupation = "",
                                                        employer = "",
                                                        moveInDate = "",
                                                        moveOutDate = "",
                                                        agreementDocName = "",
                                                        startDate = "",
                                                        expiryDate = ""
                                                    )
                                                }
                                            },
                                            label = { Text(status, fontSize = 12.sp) }
                                        )
                                    }
                                }

                                if (myFlat?.occupancyStatus == "Rented") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = GoldContainer),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = OnGoldContainer, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Mandatory Policy: Rented flat requires active Leave-and-License agreement & tenant verification.",
                                                fontSize = 11.sp,
                                                color = OnGoldContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Key Summary Cards
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = 1 }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Family Members", fontSize = 11.sp, color = TextMuted)
                                    Text("${familyMembers.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text("4 Registered", fontSize = 11.sp, color = StatusSuccess)
                                }
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = 3 }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Vehicles & Parking", fontSize = 11.sp, color = TextMuted)
                                    Text("${vehicles.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text("Slot: P1-034", fontSize = 11.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = 4 }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Registered Pets", fontSize = 11.sp, color = TextMuted)
                                    Text("${pets.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text("Buddy (Dog) • Valid", fontSize = 11.sp, color = StatusSuccess)
                                }
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = 4 }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Domestic Help", fontSize = 11.sp, color = TextMuted)
                                    Text("${domesticHelp.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    Text("Maid & Cook active", fontSize = 11.sp, color = StatusInfo)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Family Members
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Family Members (${familyMembers.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddFamilyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Member", fontSize = 12.sp)
                            }
                        }
                    }

                    items(familyMembers) { member ->
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
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (member.isChild) Color(0xFFEDE9FE) else SurfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (member.isChild) Icons.Default.ChildCare else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (member.isChild) Color(0xFF8B5CF6) else NavyPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = member.fullName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${member.relationship} • ${member.gender} • Age: ${member.calculatedAge} yrs",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    if (member.isEmergencyContact) {
                                        StatusBadge(status = "Emergency Contact")
                                    }
                                }

                                if (member.isChild && member.schoolName.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${member.schoolName} (${member.grade})",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                if (member.phone.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Phone: ${member.phone}", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Tenant Management
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tenant & Lease Management",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddTenantDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Register Tenant", fontSize = 12.sp)
                            }
                        }
                    }

                    if (tenants.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.HomeWork, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No active tenant registered for K-1204.", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("Occupancy is currently set to Self-Occupied.", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    } else {
                        items(tenants) { tenant ->
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
                                        Column {
                                            Text(
                                                text = tenant.fullName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary
                                            )
                                            Text("Tenant • ${tenant.occupation}", fontSize = 12.sp, color = TextSecondary)
                                        }
                                        StatusBadge(status = tenant.verificationStatus)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Contact: ${tenant.phone} • ${tenant.email}", fontSize = 12.sp, color = TextPrimary)
                                    Text("Employer: ${tenant.employer}", fontSize = 12.sp, color = TextSecondary)
                                    Text("Permanent Address: ${tenant.permanentAddress}", fontSize = 11.sp, color = TextMuted)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Description, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Leave & License Document:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            }
                                            Text(tenant.agreementDocument, fontSize = 11.sp, color = NavyLight, maxLines = 1)
                                            Text(
                                                "Validity: ${tenant.agreementStartDate} to ${tenant.agreementExpiryDate}",
                                                fontSize = 11.sp,
                                                color = StatusSuccess,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    if (tenant.verificationComments.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Manager Note: ${tenant.verificationComments}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Vehicles & Parking
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Registered Vehicles (${vehicles.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddVehicleDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Vehicle", fontSize = 12.sp)
                            }
                        }
                    }

                    items(vehicles) { v ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (v.isEv) StatusSuccessBg else SurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (v.vehicleType.contains("Four")) Icons.Default.DirectionsCar else Icons.Default.TwoWheeler,
                                            contentDescription = null,
                                            tint = if (v.isEv) StatusSuccess else NavyPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(v.makeModel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(v.registrationNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                                        Text("${v.vehicleType} • ${v.fuelType} • ${v.color}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(status = "Active")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Slot: ${v.allottedSlot}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Pets & Domestic Help
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pets Registration (${pets.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddPetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Pet", fontSize = 12.sp)
                            }
                        }
                    }

                    items(pets) { pet ->
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
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(GoldContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Pets, contentDescription = null, tint = OnGoldContainer, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("${pet.petName} (${pet.breed})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text("${pet.petType} • ${pet.gender} • Age: ${pet.age}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    StatusBadge(status = pet.vaccinationStatus)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Society Reg: ${pet.registrationNumber}", fontSize = 11.sp, color = TextMuted)
                                Text("Vaccination Expiry: ${pet.vaccinationExpiryDate}", fontSize = 12.sp, color = StatusSuccess, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Domestic Help (${domesticHelp.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddHelpDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Register Staff", fontSize = 12.sp)
                            }
                        }
                    }

                    items(domesticHelp) { help ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = NavyPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(help.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${help.category} • Pass: ${help.passId}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Phone: ${help.phone}", fontSize = 11.sp, color = TextMuted)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(status = if (help.currentlyInside) "Inside Society" else "Outside")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Valid till ${help.validity}", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddFamilyDialog) {
        AddFamilyMemberDialog(
            onDismiss = { showAddFamilyDialog = false },
            onAdd = { name, rel, gen, dob, age, phone, email, isEmerg, isChild, school, grade ->
                viewModel.addFamilyMember(name, rel, gen, dob, age, phone, email, isEmerg, isChild, school, grade)
                showAddFamilyDialog = false
            }
        )
    }

    if (showAddTenantDialog) {
        AddTenantDialog(
            onDismiss = { showAddTenantDialog = false },
            onRegister = { name, phone, email, addr, occ, emp, moveIn, moveOut, doc, sDate, eDate ->
                viewModel.submitTenantRegistration(
                    flatId = "K-1204",
                    name = name,
                    phone = phone,
                    email = email,
                    permanentAddress = addr,
                    occupation = occ,
                    employer = emp,
                    moveInDate = moveIn,
                    moveOutDate = moveOut,
                    agreementDocName = doc,
                    startDate = sDate,
                    expiryDate = eDate
                )
                showAddTenantDialog = false
            }
        )
    }

    if (showAddVehicleDialog) {
        AddVehicleDialog(
            onDismiss = { showAddVehicleDialog = false },
            onAdd = { type, make, reg, color, fuel, isEv, slot ->
                viewModel.addVehicle(type, make, reg, color, fuel, isEv, slot)
                showAddVehicleDialog = false
            }
        )
    }

    if (showAddPetDialog) {
        AddPetDialog(
            onDismiss = { showAddPetDialog = false },
            onAdd = { name, type, breed, gen, age, expiry, doc ->
                viewModel.addPet(name, type, breed, gen, age, expiry, doc)
                showAddPetDialog = false
            }
        )
    }

    if (showAddHelpDialog) {
        AddDomesticHelpDialog(
            onDismiss = { showAddHelpDialog = false },
            onAdd = { name, cat, phone ->
                viewModel.addDomesticHelp(name, cat, phone)
                showAddHelpDialog = false
            }
        )
    }
}

// Dialog Implementations
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Int, String, String, Boolean, Boolean, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Spouse") }
    var gender by remember { mutableStateOf("Female") }
    var dob by remember { mutableStateOf("15-Aug-1990") }
    var age by remember { mutableStateOf("36") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }
    var isChild by remember { mutableStateOf(false) }
    var school by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Family Member", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age (Auto)") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number (Optional)") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                        Text("Emergency Contact", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Checkbox(checked = isChild, onCheckedChange = { isChild = it })
                        Text("Child (Under 18)", fontSize = 12.sp)
                    }
                }
                if (isChild) {
                    item {
                        OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School Name") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Class / Grade") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, relationship, gender, dob, age.toIntOrNull() ?: 30, phone, email, isEmergency, isChild, school, grade)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Member")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddTenantDialog(
    onDismiss: () -> Unit,
    onRegister: (String, String, String, String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("Rohan Mehta") }
    var phone by remember { mutableStateOf("+91 98190 66772") }
    var email by remember { mutableStateOf("rohan.mehta@example.com") }
    var address by remember { mutableStateOf("Flat 302, Green Glen, Vashi, Navi Mumbai") }
    var occupation by remember { mutableStateOf("Senior Software Architect") }
    var employer by remember { mutableStateOf("Tata Consultancy Services") }
    var moveIn by remember { mutableStateOf("01-Apr-2025") }
    var moveOut by remember { mutableStateOf("31-Mar-2027") }
    var docName by remember { mutableStateOf("Leave_and_License_Agreement_2025_2027.pdf") }
    var startDate by remember { mutableStateOf("01-Apr-2025") }
    var expiryDate by remember { mutableStateOf("31-Mar-2027") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Register Tenant & Lease Agreement", fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Mandatory for Rented Flats under Society Bylaws", fontSize = 11.sp, color = StatusWarning)
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tenant Full Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    OutlinedTextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Occupation") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = employer, onValueChange = { employer = it }, label = { Text("Company / Employer") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Permanent Address") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Divider(color = BorderSubtle)
                    Text("Leave & License Agreement", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
                item {
                    OutlinedTextField(value = docName, onValueChange = { docName = it }, label = { Text("Agreement File Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiry Date") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onRegister(name, phone, email, address, occupation, employer, moveIn, moveOut, docName, startDate, expiryDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Submit for Verification")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, Boolean, String) -> Unit
) {
    var type by remember { mutableStateOf("Four Wheeler") }
    var make by remember { mutableStateOf("") }
    var reg by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var fuel by remember { mutableStateOf("Petrol") }
    var isEv by remember { mutableStateOf(false) }
    var slot by remember { mutableStateOf("P1-034") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Vehicle", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "Four Wheeler", onClick = { type = "Four Wheeler" }, label = { Text("4-Wheeler") })
                    FilterChip(selected = type == "Two Wheeler", onClick = { type = "Two Wheeler" }, label = { Text("2-Wheeler") })
                }
                OutlinedTextField(value = make, onValueChange = { make = it }, label = { Text("Make & Model (e.g. Creta SX)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reg, onValueChange = { reg = it }, label = { Text("Registration No (e.g. MH-46-AZ-1204)") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fuel, onValueChange = { fuel = it }, label = { Text("Fuel") }, modifier = Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isEv, onCheckedChange = { isEv = it })
                    Text("Electric Vehicle (EV)", fontSize = 12.sp)
                }
                OutlinedTextField(value = slot, onValueChange = { slot = it }, label = { Text("Allotted Parking Slot") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (make.isNotBlank() && reg.isNotBlank()) {
                        onAdd(type, make, reg, color, fuel, isEv, slot)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Register Vehicle")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddPetDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Dog") }
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf("2 Years") }
    var expiry by remember { mutableStateOf("15-Nov-2026") }
    var cert by remember { mutableStateOf("Pet_Vaccination_Certificate.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Society Pet", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Pet Name") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Pet Type") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Breed") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = expiry, onValueChange = { expiry = it }, label = { Text("Vaccination Expiry Date") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cert, onValueChange = { cert = it }, label = { Text("Certificate File Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, type, breed, gender, age, expiry, cert)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Register Pet")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddDomesticHelpDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("House Maid") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Domestic Worker", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Worker Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Work Category (Maid/Cook/Driver)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, category, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Generate Gate Pass")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
