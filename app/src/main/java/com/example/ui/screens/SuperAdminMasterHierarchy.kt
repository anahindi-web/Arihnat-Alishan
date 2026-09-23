package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MasterUnitEntity
import com.example.data.model.SystemUserEntity
import com.example.ui.components.CandyButton
import com.example.ui.components.CandyFlavor
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun SuperAdminMasterHierarchyTab(
    masterUnits: List<MasterUnitEntity>,
    systemUsers: List<SystemUserEntity>,
    onAddMasterClick: () -> Unit,
    onEditMaster: (MasterUnitEntity) -> Unit,
    onToggleMasterActive: (masterId: String, isActive: Boolean) -> Unit,
    onDeleteMaster: (masterId: String) -> Unit,
    onAddUserClick: () -> Unit,
    onReassignUser: (SystemUserEntity) -> Unit,
    onToggleUserStatus: (userId: String, status: String) -> Unit,
    onDeleteUser: (userId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var subSection by remember { mutableStateOf(0) } // 0: Masters, 1: Users & Hierarchy
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedMasterFilter by remember { mutableStateOf("ALL") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Governance & Policy Notice
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF7C3AED),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Master Hierarchy & Strict Isolation Rule",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5B21B6)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "• Rule: Every user MUST be linked to exactly one Master.\n" +
                        "• Isolation: Individual Masters cannot view or modify other Masters.\n" +
                        "• Authority: Only Super Admin can create, edit, activate, deactivate, or reassign Masters.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF4C1D95),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Sub Section Selector (Pill tabs)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tab0Active = subSection == 0
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (tab0Active) PureWhiteSurface else Color.Transparent,
                        shadowElevation = if (tab0Active) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { subSection = 0 }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = if (tab0Active) NavyPrimary else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Masters (${masterUnits.size})",
                                fontSize = 12.5.sp,
                                fontWeight = if (tab0Active) FontWeight.Bold else FontWeight.Medium,
                                color = if (tab0Active) NavyPrimary else TextSecondary
                            )
                        }
                    }

                    val tab1Active = subSection == 1
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (tab1Active) PureWhiteSurface else Color.Transparent,
                        shadowElevation = if (tab1Active) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { subSection = 1 }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null,
                                tint = if (tab1Active) NavyPrimary else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "All Users & Links (${systemUsers.size})",
                                fontSize = 12.5.sp,
                                fontWeight = if (tab1Active) FontWeight.Bold else FontWeight.Medium,
                                color = if (tab1Active) NavyPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        if (subSection == 0) {
            // MASTERS LIST
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("System Masters", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Configured administrative and residential units", fontSize = 11.sp, color = TextMuted)
                    }

                    CandyButton(
                        text = "+ Create Master",
                        onClick = onAddMasterClick,
                        flavor = CandyFlavor.NAVY,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            items(masterUnits) { master ->
                val linkedCount = systemUsers.count { it.linkedMasterId == master.masterId }
                val isActive = master.status.equals("Active", ignoreCase = true)

                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isActive) CardBorder else Color(0xFFFCA5A5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isActive) Color(0xFFEFF6FF) else Color(0xFFFEF2F2)
                                    ) {
                                        Text(
                                            text = master.masterId,
                                            color = if (isActive) Color(0xFF1D4ED8) else Color(0xFFDC2626),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = master.masterType,
                                            color = Color(0xFFB45309),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = master.masterName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }

                            StatusBadge(status = master.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Details grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Head / Lead Contact", fontSize = 10.5.sp, color = TextMuted)
                                Text(master.headOfMaster, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                if (master.contactPhone.isNotBlank()) {
                                    Text(master.contactPhone, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Unit & Capacity", fontSize = 10.5.sp, color = TextMuted)
                                Text("Unit: ${master.assignedUnit.ifEmpty { "N/A" }}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("$linkedCount Users linked (Max: ${master.maxUsersAllowed})", fontSize = 11.sp, color = if (linkedCount > 0) Color(0xFF047857) else TextMuted)
                            }
                        }

                        if (master.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Note: ${master.notes}", fontSize = 11.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onEditMaster(master) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { onToggleMasterActive(master.masterId, !isActive) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (isActive) Color(0xFFDC2626) else Color(0xFF059669)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isActive) "Deactivate" else "Activate", fontSize = 11.sp)
                                }
                            }

                            IconButton(
                                onClick = { onDeleteMaster(master.masterId) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Master", tint = StatusCritical, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // USERS & HIERARCHY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("System Users & Linkages", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Every user is strictly linked to a Master", fontSize = 11.sp, color = TextMuted)
                    }

                    CandyButton(
                        text = "+ Add User",
                        onClick = onAddUserClick,
                        flavor = CandyFlavor.EMERALD,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Search & Filter
            item {
                OutlinedTextField(
                    value = userSearchQuery,
                    onValueChange = { userSearchQuery = it },
                    placeholder = { Text("Search by user name, phone, or flat...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        if (userSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { userSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Master Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedMasterFilter == "ALL",
                            onClick = { selectedMasterFilter = "ALL" },
                            label = { Text("All Masters (${systemUsers.size})", fontSize = 11.sp) }
                        )
                    }
                    items(masterUnits) { m ->
                        val count = systemUsers.count { it.linkedMasterId == m.masterId }
                        FilterChip(
                            selected = selectedMasterFilter == m.masterId,
                            onClick = { selectedMasterFilter = m.masterId },
                            label = { Text("${m.masterName} ($count)", fontSize = 11.sp) }
                        )
                    }
                }
            }

            val filteredUsers = systemUsers.filter { user ->
                val matchesMaster = selectedMasterFilter == "ALL" || user.linkedMasterId == selectedMasterFilter
                val matchesQuery = userSearchQuery.isBlank() ||
                        user.fullName.contains(userSearchQuery, ignoreCase = true) ||
                        user.phone.contains(userSearchQuery) ||
                        user.flatId.contains(userSearchQuery, ignoreCase = true) ||
                        user.roleName.contains(userSearchQuery, ignoreCase = true)
                matchesMaster && matchesQuery
            }

            if (filteredUsers.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No users match filter", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Try selecting 'All Masters' or clearing your search.", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                }
            } else {
                items(filteredUsers) { user ->
                    val isUserActive = user.status.equals("Active", ignoreCase = true)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isUserActive) CardBorder else Color(0xFFFCA5A5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = NavyPrimary.copy(alpha = 0.1f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = user.fullName.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = user.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = NavyPrimary
                                        )
                                        Text(
                                            text = "${user.roleName} • Flat ${user.flatId}",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                StatusBadge(status = user.status)
                            }

                            if (user.phone.isNotBlank() || user.email.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (user.phone.isNotBlank()) {
                                        Text("📞 ${user.phone}", fontSize = 11.sp, color = TextMuted)
                                    }
                                    if (user.email.isNotBlank()) {
                                        Text("✉️ ${user.email}", fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Linked Master Highlight Box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Linked Master Unit (Required)", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = "${user.linkedMasterName} [${user.linkedMasterId}]",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Super Admin Reassign Action Button
                                    Button(
                                        onClick = { onReassignUser(user) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Reassign", fontSize = 10.5.sp, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Perms: ${user.permissionsSummary}",
                                    fontSize = 10.5.sp,
                                    color = TextMuted,
                                    modifier = Modifier.weight(1f).padding(end = 6.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    TextButton(
                                        onClick = { onToggleUserStatus(user.userId, if (isUserActive) "Suspended" else "Active") },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isUserActive) "Suspend" else "Activate",
                                            fontSize = 11.sp,
                                            color = if (isUserActive) StatusWarning else StatusSuccess
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteUser(user.userId) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
                                    }
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
// Dialogs for Super Admin Master Management
// ==========================================

@Composable
fun SuperAdminAddEditMasterDialog(
    master: MasterUnitEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, head: String, phone: String, email: String, unit: String, maxUsers: Int, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(master?.masterName ?: "") }
    var type by remember { mutableStateOf(master?.masterType ?: "Residential Unit") }
    var head by remember { mutableStateOf(master?.headOfMaster ?: "") }
    var phone by remember { mutableStateOf(master?.contactPhone ?: "") }
    var email by remember { mutableStateOf(master?.contactEmail ?: "") }
    var unit by remember { mutableStateOf(master?.assignedUnit ?: "") }
    var maxUsersText by remember { mutableStateOf((master?.maxUsersAllowed ?: 10).toString()) }
    var notes by remember { mutableStateOf(master?.notes ?: "") }
    val scrollState = rememberScrollState()

    val types = listOf("Residential Unit", "Society Committee", "Security Wing", "Facility Maintenance", "Commercial / Vendor")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (master == null) "Create New Master Unit" else "Edit Master Unit (${master.masterId})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimary
                )
                Text(
                    text = "A Master unit defines the administrative boundary for linked residents and staff.",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Master Unit Name *") },
                    placeholder = { Text("e.g. Flat K-302 Master Unit") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Master Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(types) { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 10.5.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = head,
                    onValueChange = { head = it },
                    label = { Text("Head of Master (Primary Contact) *") },
                    placeholder = { Text("e.g. Rahul Sharma / Security Officer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Assigned Flat/Unit") },
                        placeholder = { Text("e.g. K-302") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Contact Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxUsersText,
                    onValueChange = { maxUsersText = it },
                    label = { Text("Max Users Allowed Under Master") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Administrative Notes / Scope") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && head.isNotBlank()) {
                                val max = maxUsersText.toIntOrNull() ?: 10
                                onSave(name, type, head, phone, email, unit, max, notes)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(if (master == null) "Create Master" else "Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun SuperAdminAddSystemUserDialog(
    masterUnits: List<MasterUnitEntity>,
    onDismiss: () -> Unit,
    onAddUser: (name: String, phone: String, email: String, role: String, masterId: String, flat: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Resident (Tenant)") }
    var flat by remember { mutableStateOf("") }
    var selectedMasterId by remember { mutableStateOf(masterUnits.firstOrNull()?.masterId ?: "") }
    val scrollState = rememberScrollState()

    val availableRoles = listOf("Resident Owner", "Resident (Tenant)", "Family Member", "Committee Member", "Security Guard", "Society Staff")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Add System User", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                Text("Rule: User MUST be linked to exactly one Master.", fontSize = 11.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = flat,
                        onValueChange = { flat = it },
                        label = { Text("Flat / Unit") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("System Role", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(availableRoles) { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r, fontSize = 10.5.sp) }
                        )
                    }
                }

                Text("Assign to Master Unit (MANDATORY)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                masterUnits.forEach { m ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedMasterId == m.masterId) Color(0xFFF0FDF4) else Color.Transparent,
                        border = BorderStroke(1.dp, if (selectedMasterId == m.masterId) Color(0xFF22C55E) else CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMasterId = m.masterId }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMasterId == m.masterId,
                                onClick = { selectedMasterId = m.masterId },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(m.masterName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("${m.masterId} • ${m.masterType}", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank() && selectedMasterId.isNotBlank()) {
                                onAddUser(name, phone, email, role, selectedMasterId, flat)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Create User")
                    }
                }
            }
        }
    }
}

@Composable
fun SuperAdminReassignMasterDialog(
    user: SystemUserEntity,
    masterUnits: List<MasterUnitEntity>,
    onDismiss: () -> Unit,
    onConfirm: (newMasterId: String) -> Unit
) {
    var selectedMasterId by remember { mutableStateOf(user.linkedMasterId) }
    val activeMasters = masterUnits.filter { it.status.equals("Active", ignoreCase = true) }
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reassign User Master", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Target User: ${user.fullName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                        Text("Role: ${user.roleName} • Flat: ${user.flatId}", fontSize = 11.5.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Currently Linked To: ${user.linkedMasterName} (${user.linkedMasterId})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusWarning
                        )
                    }
                }

                Text(
                    "Select New Master Unit for this user:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                activeMasters.forEach { m ->
                    val isCurrent = m.masterId == user.linkedMasterId
                    val isSelected = selectedMasterId == m.masterId

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFF0FDF4) else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF16A34A) else CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMasterId = m.masterId }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMasterId = m.masterId },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(m.masterName, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "Current",
                                                color = Color(0xFFB45309),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text("${m.masterId} • Head: ${m.headOfMaster} (${m.masterType})", fontSize = 10.5.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Note: Reassignment takes effect immediately. The user will inherit the scope, verification policies, and notifications of the newly assigned Master.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF9D174D),
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedMasterId.isNotBlank()) {
                                onConfirm(selectedMasterId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                    ) {
                        Text("Confirm Reassignment", color = Color.White)
                    }
                }
            }
        }
    }
}
