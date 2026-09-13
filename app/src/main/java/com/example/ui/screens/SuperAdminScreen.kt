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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreen(
    viewModel: ArihantViewModel,
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppThemePalette.current
    val currentRole by viewModel.currentRole.collectAsState()
    val currentTheme by viewModel.currentAppTheme.collectAsState()
    val config by viewModel.societyConfig.collectAsState()
    val flats by viewModel.allFlats.collectAsState()
    val committeeMembers by viewModel.committeeDirectory.collectAsState()
    val tenants by viewModel.allTenants.collectAsState()
    val familyMembers by viewModel.allFamilyMembersAcrossSociety.collectAsState()
    val rules by viewModel.allRules.collectAsState()
    val notices by viewModel.allNotices.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Themes, 1: Members, 2: Digital Notices, 3: Society Rules, 4: System Audit

    // Dialog States
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<Any?>(null) } // CommitteeMasterEntity, FlatEntity, or TenantEntity
    var showAddRuleDialog by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<SocietyRuleEntity?>(null) }
    var showPublishNoticeDialog by remember { mutableStateOf(false) }
    var noticeToEdit by remember { mutableStateOf<SocietyNoticeEntity?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Super Admin Control Center",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFF3E8FF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "ROOT ACCESS",
                                    color = Color(0xFF7E22CE),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            "Theme • Members • Rules • Notice Board • MCS Bylaws",
                            fontSize = 11.sp,
                            color = theme.accent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showBroadcastDialog = true },
                        modifier = Modifier.testTag("super_admin_broadcast_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Broadcast",
                            tint = theme.accent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(theme.background)
        ) {
            // Executive Stat Banner
            SuperAdminBanner(
                activeTheme = currentTheme,
                totalUnits = config?.totalUnits ?: 960,
                totalCommittee = committeeMembers.size,
                totalRules = rules.size,
                totalNotices = notices.size
            )

            // Category Tab Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhiteSurface,
                contentColor = theme.primary,
                edgePadding = 12.dp,
                divider = { Divider(color = BorderSubtle) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("App Themes", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Manage Members (${committeeMembers.size + flats.size + tenants.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Digital Notices (${notices.size})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Society Rules (${rules.size})", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Audit & MCS Logs", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Tab Contents
            when (selectedTab) {
                0 -> SuperAdminThemesTab(
                    currentTheme = currentTheme,
                    onSelectTheme = { viewModel.setAppTheme(it) }
                )
                1 -> SuperAdminMembersTab(
                    committeeMembers = committeeMembers,
                    flats = flats,
                    tenants = tenants,
                    familyMembers = familyMembers,
                    onAddMemberClick = { showAddMemberDialog = true },
                    onEditMember = { memberToEdit = it },
                    onDeleteCommittee = { id -> viewModel.deleteCommitteeMember(id) },
                    onDeleteFlat = { id -> viewModel.deleteFlat(id) }
                )
                2 -> SuperAdminNoticesTab(
                    notices = notices,
                    onPublishNoticeClick = { showPublishNoticeDialog = true },
                    onEditNotice = { noticeToEdit = it },
                    onTogglePin = { viewModel.togglePinNotice(it) },
                    onDeleteNotice = { viewModel.deleteNotice(it) }
                )
                3 -> SuperAdminRulesTab(
                    rules = rules,
                    onAddRuleClick = { showAddRuleDialog = true },
                    onEditRule = { ruleToEdit = it },
                    onDeleteRule = { id, title -> viewModel.deleteSocietyRule(id, title) }
                )
                4 -> SuperAdminAuditTab(
                    logs = auditLogs
                )
            }
        }
    }

    // Dialog: Add New Member
    if (showAddMemberDialog) {
        SuperAdminAddMemberDialog(
            flats = flats,
            onDismiss = { showAddMemberDialog = false },
            onAddCommittee = { member ->
                viewModel.addCommitteeMember(member)
                showAddMemberDialog = false
            },
            onAddFlatOwner = { flat ->
                viewModel.addFlat(flat)
                showAddMemberDialog = false
            },
            onAddTenant = { tenant ->
                viewModel.addTenant(tenant)
                showAddMemberDialog = false
            }
        )
    }

    // Dialog: Edit Member
    if (memberToEdit != null) {
        SuperAdminEditMemberDialog(
            member = memberToEdit!!,
            onDismiss = { memberToEdit = null },
            onSaveCommittee = { updated ->
                viewModel.amendCommitteeMember(updated)
                memberToEdit = null
            },
            onSaveFlat = { updated ->
                viewModel.amendFlat(updated)
                memberToEdit = null
            },
            onSaveTenant = { updated ->
                viewModel.amendTenant(updated)
                memberToEdit = null
            }
        )
    }

    // Dialog: Add / Edit Rule
    if (showAddRuleDialog || ruleToEdit != null) {
        SuperAdminRuleDialog(
            existingRule = ruleToEdit,
            onDismiss = {
                showAddRuleDialog = false
                ruleToEdit = null
            },
            onSave = { category, title, description, penalty, isMandatory ->
                if (ruleToEdit != null) {
                    viewModel.amendSocietyRule(
                        ruleToEdit!!.copy(
                            category = category,
                            ruleTitle = title,
                            ruleDescription = description,
                            penaltyAmount = penalty,
                            isMandatory = isMandatory
                        )
                    )
                } else {
                    viewModel.addSocietyRule(category, title, description, penalty, isMandatory)
                }
                showAddRuleDialog = false
                ruleToEdit = null
            }
        )
    }

    // Dialog: Publish or Edit Notice
    if (showPublishNoticeDialog || noticeToEdit != null) {
        SuperAdminNoticeDialog(
            existingNotice = noticeToEdit,
            onDismiss = {
                showPublishNoticeDialog = false
                noticeToEdit = null
            },
            onSave = { title, content, priority, targetTower, category, isPinned, circularNo ->
                if (noticeToEdit != null) {
                    viewModel.amendNotice(
                        noticeToEdit!!.copy(
                            title = title,
                            content = content,
                            priority = priority,
                            targetTower = targetTower,
                            category = category,
                            isPinned = isPinned,
                            circularNo = circularNo
                        )
                    )
                } else {
                    viewModel.publishNotice(
                        title = title,
                        content = content,
                        category = category,
                        tower = targetTower,
                        priority = priority,
                        circularNo = circularNo,
                        isPinned = isPinned
                    )
                }
                showPublishNoticeDialog = false
                noticeToEdit = null
            }
        )
    }

    // Dialog: Emergency Broadcast
    if (showBroadcastDialog) {
        SuperAdminBroadcastDialog(
            onDismiss = { showBroadcastDialog = false },
            onSendBroadcast = { message, priority ->
                viewModel.publishNotice(
                    title = "🚨 EMERGENCY BROADCAST: $priority",
                    content = message,
                    category = "Safety",
                    tower = "All Towers",
                    priority = "Critical",
                    circularNo = "EMERGENCY/${(100..999).random()}",
                    isPinned = true
                )
                showBroadcastDialog = false
            }
        )
    }
}

// ==========================================
// Banner Component
// ==========================================
@Composable
private fun SuperAdminBanner(
    activeTheme: AppTheme,
    totalUnits: Int,
    totalCommittee: Int,
    totalRules: Int,
    totalNotices: Int
) {
    val theme = LocalAppThemePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = theme.primary),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                            .background(theme.accent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = theme.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Super Administrator Mode",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Full read/write rights across all society sub-modules",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = theme.accent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.accent.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(theme.accent)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = activeTheme.displayName,
                            color = theme.accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatChip("Flats", "$totalUnits", Modifier.weight(1f))
                AdminStatChip("Committee", "$totalCommittee", Modifier.weight(1f))
                AdminStatChip("Active Rules", "$totalRules", Modifier.weight(1f))
                AdminStatChip("Notices", "$totalNotices", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AdminStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp)
        }
    }
}

// ==========================================
// Tab 0: Themes Tab
// ==========================================
@Composable
private fun SuperAdminThemesTab(
    currentTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit
) {
    val theme = LocalAppThemePalette.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = theme.accent, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Application Theme Customizer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "As Super Admin, you can select and deploy a luxury visual theme. The theme instantly updates headers, navigation, cards, and accent buttons across all resident and committee screens.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        items(AppTheme.values()) { t ->
            val isSelected = t == currentTheme
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PureWhiteSurface else PureWhiteSurface
                ),
                shape = RoundedCornerShape(14.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, t.previewAccent) else CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTheme(t) }
                    .testTag("theme_card_${t.id.lowercase()}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Color Swatch Disc
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(t.previewPrimary, t.previewAccent)
                                    )
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = t.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = StatusSuccessBg,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "ACTIVE THEME",
                                            color = StatusSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = t.subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSelectTheme(t) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) t.previewAccent else Color(0xFFF1F5F9),
                            contentColor = if (isSelected) Color.White else NavyPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (isSelected) "Applied ✓" else "Apply",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 1: Manage Members Tab
// ==========================================
@Composable
private fun SuperAdminMembersTab(
    committeeMembers: List<CommitteeMasterEntity>,
    flats: List<FlatEntity>,
    tenants: List<TenantEntity>,
    familyMembers: List<FamilyMemberEntity>,
    onAddMemberClick: () -> Unit,
    onEditMember: (Any) -> Unit,
    onDeleteCommittee: (String) -> Unit,
    onDeleteFlat: (String) -> Unit
) {
    val theme = LocalAppThemePalette.current
    var memberFilter by remember { mutableStateOf("ALL") } // ALL, COMMITTEE, OWNERS, TENANTS
    var memberSearch by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Society Member Directory", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Add new members, edit designations, and reassign roles", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = onAddMemberClick,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_new_member")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search and Filter Row
        item {
            OutlinedTextField(
                value = memberSearch,
                onValueChange = { memberSearch = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_member_search"),
                placeholder = { Text("Search by name, flat (e.g. K-1204), phone, role...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = {
                    if (memberSearch.isNotBlank()) {
                        IconButton(onClick = { memberSearch = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.primary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = PureWhiteSurface,
                    unfocusedContainerColor = PureWhiteSurface
                )
            )
        }

        // Filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val filters = listOf(
                    Pair("ALL", "All Members (${committeeMembers.size + flats.size + tenants.size})"),
                    Pair("COMMITTEE", "Committee (${committeeMembers.size})"),
                    Pair("OWNERS", "Flat Owners (${flats.size})"),
                    Pair("TENANTS", "Tenants (${tenants.size})")
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = memberFilter == key,
                        onClick = { memberFilter = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (memberFilter == key) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = theme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Section: Committee Members
        if (memberFilter == "ALL" || memberFilter == "COMMITTEE") {
            val filteredCommittee = committeeMembers.filter {
                memberSearch.isBlank() ||
                        it.fullName.contains(memberSearch, ignoreCase = true) ||
                        it.roleTitle.contains(memberSearch, ignoreCase = true) ||
                        it.flatId.contains(memberSearch, ignoreCase = true) ||
                        it.phone.contains(memberSearch, ignoreCase = true)
            }

            if (filteredCommittee.isNotEmpty()) {
                item {
                    Text("MANAGEMENT COMMITTEE EXECUTIVES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                }

                items(filteredCommittee) { cm ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(cm.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            cm.roleTitle,
                                            color = Color(0xFF78350F),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Flat ${cm.flatId} • Phone: ${cm.phone}", fontSize = 11.sp, color = TextSecondary)
                                Text("Term: ${cm.term} • Signatory: ${if (cm.isBankSignatory) "Yes (Bank Auth)" else "No"}", fontSize = 10.sp, color = TextMuted)
                            }

                            Row {
                                IconButton(
                                    onClick = { onEditMember(cm) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = theme.primary, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteCommittee(cm.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Flat Owners
        if (memberFilter == "ALL" || memberFilter == "OWNERS") {
            val filteredFlats = flats.filter {
                memberSearch.isBlank() ||
                        it.ownerName.contains(memberSearch, ignoreCase = true) ||
                        it.flatId.contains(memberSearch, ignoreCase = true) ||
                        it.ownerPhone.contains(memberSearch, ignoreCase = true) ||
                        it.tower.contains(memberSearch, ignoreCase = true)
            }

            if (filteredFlats.isNotEmpty()) {
                item {
                    Text("RESIDENT FLAT OWNERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }

                items(filteredFlats) { flat ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(flat.ownerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFDBEAFE),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            flat.flatId,
                                            color = Color(0xFF1E40AF),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(flat.occupancyStatus)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${flat.tower} Tower • ${flat.flatType} • Phone: ${flat.ownerPhone}", fontSize = 11.sp, color = TextSecondary)
                                Text("Email: ${flat.ownerEmail.ifBlank { "Unregistered" }}", fontSize = 10.sp, color = TextMuted)
                            }

                            Row {
                                IconButton(
                                    onClick = { onEditMember(flat) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = theme.primary, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteFlat(flat.flatId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Tenants
        if (memberFilter == "ALL" || memberFilter == "TENANTS") {
            val filteredTenants = tenants.filter {
                memberSearch.isBlank() ||
                        it.fullName.contains(memberSearch, ignoreCase = true) ||
                        it.flatId.contains(memberSearch, ignoreCase = true) ||
                        it.phone.contains(memberSearch, ignoreCase = true)
            }

            if (filteredTenants.isNotEmpty()) {
                item {
                    Text("TENANT RESIDENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }

                items(filteredTenants) { tenant ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tenant.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Flat ${tenant.flatId}",
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(tenant.verificationStatus)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Phone: ${tenant.phone} • Agreement Exp: ${tenant.agreementExpiryDate}", fontSize = 11.sp, color = TextSecondary)
                                Text("Profession: ${tenant.occupation}", fontSize = 10.sp, color = TextMuted)
                            }

                            IconButton(
                                onClick = { onEditMember(tenant) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = theme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 2: Digital Notices Tab
// ==========================================
@Composable
private fun SuperAdminNoticesTab(
    notices: List<SocietyNoticeEntity>,
    onPublishNoticeClick: () -> Unit,
    onEditNotice: (SocietyNoticeEntity) -> Unit,
    onTogglePin: (String) -> Unit,
    onDeleteNotice: (String) -> Unit
) {
    val theme = LocalAppThemePalette.current
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
                    Text("Digital Notice Board Manager", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Publish official circulars, pin emergency advisories, or withdraw notices", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = onPublishNoticeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(notices) { notice ->
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = if (notice.isPinned) androidx.compose.foundation.BorderStroke(1.5.dp, theme.accent) else CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (notice.isPinned) {
                                Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = theme.accent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                Text(notice.circularNo.ifBlank { "CIR-${notice.id}" }, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = theme.accentContainer, shape = RoundedCornerShape(4.dp)) {
                                Text(notice.category, color = theme.onAccentContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        StatusBadge(notice.priority)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(notice.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notice.content, fontSize = 12.sp, color = TextSecondary, maxLines = 3)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Published: ${notice.publishDate} • Target: ${notice.targetTower}", fontSize = 10.sp, color = TextMuted)

                        Row {
                            TextButton(
                                onClick = { onTogglePin(notice.id) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(if (notice.isPinned) Icons.Default.PushPin else Icons.Default.VerticalAlignTop, contentDescription = null, modifier = Modifier.size(14.dp), tint = theme.accent)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (notice.isPinned) "Unpin" else "Pin", fontSize = 11.sp, color = theme.accent)
                            }
                            TextButton(
                                onClick = { onEditNotice(notice) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = theme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 11.sp, color = theme.primary)
                            }
                            IconButton(
                                onClick = { onDeleteNotice(notice.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 3: Society Rules & Bylaws Tab
// ==========================================
@Composable
private fun SuperAdminRulesTab(
    rules: List<SocietyRuleEntity>,
    onAddRuleClick: () -> Unit,
    onEditRule: (SocietyRuleEntity) -> Unit,
    onDeleteRule: (Long, String) -> Unit
) {
    val theme = LocalAppThemePalette.current
    val groupedRules = rules.groupBy { it.category }

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
                    Text("Society Rules & Code of Conduct", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Under MCS Act 1960 • Set violation penalties and guidelines", fontSize = 11.sp, color = TextSecondary)
                }

                Button(
                    onClick = onAddRuleClick,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_new_rule")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Rule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        groupedRules.forEach { (category, categoryRules) ->
            item {
                Text(category.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.primary)
            }

            items(categoryRules) { rule ->
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
                            Text(
                                text = rule.ruleTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (rule.penaltyAmount > 0) {
                                Surface(
                                    color = StatusCriticalBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "Fine: ₹${rule.penaltyAmount}",
                                        color = StatusCritical,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = StatusSuccessBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "Advisory",
                                        color = StatusSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rule.ruleDescription, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Updated: ${rule.lastUpdated} • ${if (rule.isMandatory) "Mandatory" else "Optional"}", fontSize = 10.sp, color = TextMuted)

                            Row {
                                TextButton(
                                    onClick = { onEditRule(rule) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = theme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp, color = theme.primary)
                                }
                                IconButton(
                                    onClick = { onDeleteRule(rule.id, rule.ruleTitle) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
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
// Tab 4: Audit Tab
// ==========================================
@Composable
private fun SuperAdminAuditTab(
    logs: List<AuditLogEntity>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("MCS Act 1960 Statutory Audit Trail", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                    Text("Every change by Super Admin and Committee is cryptographically sequenced and logged.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        items(logs) { log ->
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
                        Surface(
                            color = NavyPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                log.action,
                                color = NavyPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(log.timestamp, fontSize = 10.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(log.details, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Performed by: ${log.userName} (${log.userRole}) • Module: ${log.module}", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}

// ==========================================
// Dialog: Add Member
// ==========================================
@Composable
private fun SuperAdminAddMemberDialog(
    flats: List<FlatEntity>,
    onDismiss: () -> Unit,
    onAddCommittee: (CommitteeMasterEntity) -> Unit,
    onAddFlatOwner: (FlatEntity) -> Unit,
    onAddTenant: (TenantEntity) -> Unit
) {
    var memberType by remember { mutableStateOf("COMMITTEE") } // COMMITTEE, OWNER, TENANT
    var fullName by remember { mutableStateOf("") }
    var flatId by remember { mutableStateOf(flats.firstOrNull()?.flatId ?: "K-1204") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var roleOrDesignation by remember { mutableStateOf("Committee Member") }
    var termOrOccupation by remember { mutableStateOf("2024 - 2029") }
    var isSignatory by remember { mutableStateOf(false) }

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
            ) {
                Text("Add New Society Member", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                // Member Type Radio Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("COMMITTEE" to "Committee", "OWNER" to "Owner", "TENANT" to "Tenant").forEach { (type, label) ->
                        FilterChip(
                            selected = memberType == type,
                            onClick = { memberType = type },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = flatId,
                        onValueChange = { flatId = it },
                        label = { Text("Flat No") },
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
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (memberType == "COMMITTEE") {
                    OutlinedTextField(
                        value = roleOrDesignation,
                        onValueChange = { roleOrDesignation = it },
                        label = { Text("Committee Role / Portfolio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isSignatory, onCheckedChange = { isSignatory = it })
                        Text("Authorized Bank & Cheque Signatory", fontSize = 11.sp)
                    }
                } else if (memberType == "TENANT") {
                    OutlinedTextField(
                        value = termOrOccupation,
                        onValueChange = { termOrOccupation = it },
                        label = { Text("Profession / Occupation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isNotBlank()) {
                                when (memberType) {
                                    "COMMITTEE" -> {
                                        onAddCommittee(
                                            CommitteeMasterEntity(
                                                id = "CM-${(10..99).random()}",
                                                roleTitle = roleOrDesignation,
                                                fullName = fullName,
                                                flatId = flatId,
                                                phone = phone,
                                                email = email,
                                                term = "2024 - 2029",
                                                isBankSignatory = isSignatory,
                                                portfolioDescription = "Super Admin appointed committee executive."
                                            )
                                        )
                                    }
                                    "OWNER" -> {
                                        onAddFlatOwner(
                                            FlatEntity(
                                                flatId = flatId,
                                                tower = if (flatId.startsWith("B1")) "Baraz-1" else if (flatId.startsWith("B2")) "Baraz-2" else if (flatId.startsWith("Z")) "Zenath" else "Kaveh",
                                                floor = 10,
                                                flatNumber = flatId.substringAfter("-"),
                                                ownerName = fullName,
                                                ownerPhone = phone,
                                                ownerEmail = email,
                                                possessionDate = "01 Jan 2024",
                                                occupancyStatus = "Self Occupied",
                                                isVerified = true,
                                                flatType = "2 BHK Royal",
                                                areaSqFt = 1150
                                            )
                                        )
                                    }
                                    "TENANT" -> {
                                        onAddTenant(
                                            TenantEntity(
                                                flatId = flatId,
                                                fullName = fullName,
                                                phone = phone,
                                                altPhone = "",
                                                email = email,
                                                permanentAddress = "Navi Mumbai, Maharashtra",
                                                occupation = termOrOccupation,
                                                employer = "Private Sector",
                                                moveInDate = "01 Jan 2026",
                                                expectedMoveOutDate = "31 Dec 2026",
                                                status = "Active",
                                                agreementDocument = "Registered_Rent_Agreement.pdf",
                                                agreementStartDate = "01 Jan 2026",
                                                agreementExpiryDate = "31 Dec 2026",
                                                agreementStatus = "Active",
                                                verificationStatus = "Verified",
                                                verificationComments = "Super Admin approved"
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Add Member")
                    }
                }
            }
        }
    }
}

// ==========================================
// Dialog: Edit Member
// ==========================================
@Composable
private fun SuperAdminEditMemberDialog(
    member: Any,
    onDismiss: () -> Unit,
    onSaveCommittee: (CommitteeMasterEntity) -> Unit,
    onSaveFlat: (FlatEntity) -> Unit,
    onSaveTenant: (TenantEntity) -> Unit
) {
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
            ) {
                Text("Change Existing Member", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                when (member) {
                    is CommitteeMasterEntity -> {
                        var name by remember { mutableStateOf(member.fullName) }
                        var role by remember { mutableStateOf(member.roleTitle) }
                        var flat by remember { mutableStateOf(member.flatId) }
                        var phone by remember { mutableStateOf(member.phone) }
                        var email by remember { mutableStateOf(member.email) }
                        var signatory by remember { mutableStateOf(member.isBankSignatory) }

                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = flat, onValueChange = { flat = it }, label = { Text("Flat No") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = signatory, onCheckedChange = { signatory = it })
                            Text("Bank Cheque Signatory Rights", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onDismiss) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                onSaveCommittee(member.copy(fullName = name, roleTitle = role, flatId = flat, phone = phone, email = email, isBankSignatory = signatory))
                            }) { Text("Save Changes") }
                        }
                    }
                    is FlatEntity -> {
                        var ownerName by remember { mutableStateOf(member.ownerName) }
                        var phone by remember { mutableStateOf(member.ownerPhone) }
                        var email by remember { mutableStateOf(member.ownerEmail) }
                        var status by remember { mutableStateOf(member.occupancyStatus) }

                        OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Owner Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Occupancy Status") }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onDismiss) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                onSaveFlat(member.copy(ownerName = ownerName, ownerPhone = phone, ownerEmail = email, occupancyStatus = status))
                            }) { Text("Save Changes") }
                        }
                    }
                    is TenantEntity -> {
                        var tenantName by remember { mutableStateOf(member.fullName) }
                        var phone by remember { mutableStateOf(member.phone) }
                        var email by remember { mutableStateOf(member.email) }
                        var occupation by remember { mutableStateOf(member.occupation) }

                        OutlinedTextField(value = tenantName, onValueChange = { tenantName = it }, label = { Text("Tenant Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Occupation") }, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onDismiss) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                onSaveTenant(member.copy(fullName = tenantName, phone = phone, email = email, occupation = occupation))
                            }) { Text("Save Changes") }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Dialog: Add / Edit Rule
// ==========================================
@Composable
private fun SuperAdminRuleDialog(
    existingRule: SocietyRuleEntity?,
    onDismiss: () -> Unit,
    onSave: (category: String, title: String, description: String, penalty: Int, isMandatory: Boolean) -> Unit
) {
    val categories = listOf(
        "Parking & Vehicles",
        "Pets & Animals",
        "Quiet Hours & Noise",
        "Waste & Sanitation",
        "Clubhouse & Sports",
        "Renovation & Construction",
        "Security & Visitors",
        "General Code of Conduct"
    )

    var category by remember { mutableStateOf(existingRule?.category ?: categories.first()) }
    var title by remember { mutableStateOf(existingRule?.ruleTitle ?: "") }
    var description by remember { mutableStateOf(existingRule?.ruleDescription ?: "") }
    var penaltyStr by remember { mutableStateOf((existingRule?.penaltyAmount ?: 500).toString()) }
    var isMandatory by remember { mutableStateOf(existingRule?.isMandatory ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Text(
                    text = if (existingRule != null) "Amend Society Rule" else "Add New Society Rule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Category selection chips
                Text("Category", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Rule Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Rule Description & Bylaw Text") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = penaltyStr,
                    onValueChange = { penaltyStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Violation Fine (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isMandatory, onCheckedChange = { isMandatory = it })
                    Text("Mandatory compliance (Legal fine under MCS Act)", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank()) {
                                onSave(category, title, description, penaltyStr.toIntOrNull() ?: 0, isMandatory)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Save Rule")
                    }
                }
            }
        }
    }
}

// ==========================================
// Dialog: Publish Notice
// ==========================================
@Composable
private fun SuperAdminNoticeDialog(
    existingNotice: SocietyNoticeEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, priority: String, targetTower: String, category: String, isPinned: Boolean, circularNo: String) -> Unit
) {
    var title by remember { mutableStateOf(existingNotice?.title ?: "") }
    var content by remember { mutableStateOf(existingNotice?.content ?: "") }
    var priority by remember { mutableStateOf(existingNotice?.priority ?: "Normal") }
    var targetTower by remember { mutableStateOf(existingNotice?.targetTower ?: "All Towers") }
    var category by remember { mutableStateOf(existingNotice?.category ?: "General") }
    var isPinned by remember { mutableStateOf(existingNotice?.isPinned ?: false) }
    var circularNo by remember { mutableStateOf(existingNotice?.circularNo ?: "CIR/2026/${(100..999).random()}") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Text(
                    text = if (existingNotice != null) "Amend Digital Notice" else "Publish Official Notice",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = circularNo,
                    onValueChange = { circularNo = it },
                    label = { Text("Circular Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetTower,
                        onValueChange = { targetTower = it },
                        label = { Text("Target Tower") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Text("Pin to top of Digital Notice Board", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, content, priority, targetTower, category, isPinned, circularNo)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Publish")
                    }
                }
            }
        }
    }
}

// ==========================================
// Dialog: Emergency Broadcast
// ==========================================
@Composable
private fun SuperAdminBroadcastDialog(
    onDismiss: () -> Unit,
    onSendBroadcast: (message: String, priority: String) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("CRITICAL ALERT") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = StatusCritical, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Society Emergency Broadcast", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StatusCritical)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This broadcast will trigger an instant emergency circular across all 4 towers and alert all 960 flats.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Alert Heading") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Emergency Broadcast Message") },
                    minLines = 3,
                    placeholder = { Text("e.g. Urgent lift maintenance underway in Tower Kaveh. Please use Service Lift #2.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (message.isNotBlank()) {
                                onSendBroadcast(message, priority)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCritical)
                    ) {
                        Text("Send Broadcast Now", color = Color.White)
                    }
                }
            }
        }
    }
}
