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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SocietyNoticeEntity
import com.example.data.model.SocietyRuleEntity
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun NoticesRulesScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val notices by viewModel.allNotices.collectAsState()
    val rules by viewModel.allRules.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val societyConfig by viewModel.societyConfig.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Notice Board & Circulars, 1: Society Bylaws & Code
    var selectedTowerFilter by remember { mutableStateOf("ALL") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialogs
    var showPublishNoticeDialog by remember { mutableStateOf(false) }
    var noticeToEdit by remember { mutableStateOf<SocietyNoticeEntity?>(null) }
    var noticeToViewDetail by remember { mutableStateOf<SocietyNoticeEntity?>(null) }
    var showAddRuleDialog by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<SocietyRuleEntity?>(null) }
    var hasAgreedBylaws by remember { mutableStateOf(false) }

    val isCommittee = currentRole.isCommitteeMember()
    val isAuthorizedForRules = isCommittee || currentRole.isSuperAdmin()

    val filteredNotices = notices.filter { notice ->
        val matchesTower = selectedTowerFilter == "ALL" || notice.targetTower == "ALL" || notice.targetTower == "All Towers" || notice.targetTower.contains(selectedTowerFilter, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "ALL" || notice.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                notice.title.contains(searchQuery, ignoreCase = true) ||
                notice.content.contains(searchQuery, ignoreCase = true) ||
                notice.circularNo.contains(searchQuery, ignoreCase = true)
        matchesTower && matchesCategory && matchesSearch
    }

    val pinnedNotices = filteredNotices.filter { it.isPinned }
    val regularNotices = filteredNotices.filter { !it.isPinned }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (isCommittee) {
                ExtendedFloatingActionButton(
                    onClick = { showPublishNoticeDialog = true },
                    containerColor = NavyPrimary,
                    contentColor = GoldAccent,
                    icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                    text = { Text("Publish Notice", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("fab_publish_notice")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OffWhiteBackground)
        ) {
            // Screen Header
            Surface(
                color = PureWhiteSurface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Society Notice Board",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }
                            Text(
                                text = "Official circulars, notifications & statutory disclosures",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        if (isCommittee) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavyPrimary.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "Committee Mode",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhiteSurface,
                contentColor = NavyPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Digital Notice Board (${notices.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "MCS Bylaws & Rules",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // Notice Board Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhiteSurface)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search notices by circular #, keyword...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tower Filters
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            Text("Tower:", fontSize = 11.sp, color = TextMuted)
                        }
                        listOf("ALL", "Kaveh", "Baraz-1", "Baraz-2", "Zenath").forEach { tower ->
                            item {
                                FilterChip(
                                    selected = selectedTowerFilter == tower,
                                    onClick = { selectedTowerFilter = tower },
                                    label = { Text(if (tower == "ALL") "All Towers" else tower, fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                    // Category Filters
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            Text("Category:", fontSize = 11.sp, color = TextMuted)
                        }
                        listOf("ALL", "Water", "Event", "Security", "AGM/EGM", "Maintenance").forEach { cat ->
                            item {
                                FilterChip(
                                    selected = selectedCategoryFilter == cat,
                                    onClick = { selectedCategoryFilter = cat },
                                    label = { Text(cat, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pinned Notices Section
                    if (pinnedNotices.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.PushPin, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PINNED OFFICIAL CIRCULARS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                        }

                        items(pinnedNotices) { notice ->
                            NoticeItemCard(
                                notice = notice,
                                isCommittee = isCommittee,
                                onCardClick = { noticeToViewDetail = notice },
                                onAcknowledge = { viewModel.acknowledgeNotice(notice.id) },
                                onEdit = { noticeToEdit = notice },
                                onTogglePin = { viewModel.togglePinNotice(notice.id) },
                                onDelete = { viewModel.deleteNotice(notice.id) }
                            )
                        }

                        if (regularNotices.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RECENT SOCIETY NOTICES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                }
                            }
                        }
                    }

                    // Regular Notices
                    if (regularNotices.isEmpty() && pinnedNotices.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No notices match your filters", fontSize = 13.sp, color = TextMuted)
                                }
                            }
                        }
                    } else {
                        items(regularNotices) { notice ->
                            NoticeItemCard(
                                notice = notice,
                                isCommittee = isCommittee,
                                onCardClick = { noticeToViewDetail = notice },
                                onAcknowledge = { viewModel.acknowledgeNotice(notice.id) },
                                onEdit = { noticeToEdit = notice },
                                onTogglePin = { viewModel.togglePinNotice(notice.id) },
                                onDelete = { viewModel.deleteNotice(notice.id) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            } else {
                // Society Bylaws & Code of Conduct Tab
                SocietyBylawsTab(
                    rules = rules,
                    isAuthorized = isAuthorizedForRules,
                    onAddRuleClick = { showAddRuleDialog = true },
                    onEditRule = { ruleToEdit = it },
                    onDeleteRule = { id, title -> viewModel.deleteSocietyRule(id, title) },
                    hasAgreedBylaws = hasAgreedBylaws,
                    onAgree = { hasAgreedBylaws = true }
                )
            }
        }
    }

    // Dialogs
    if (showPublishNoticeDialog) {
        PublishNoticeDialog(
            viewModel = viewModel,
            onDismiss = { showPublishNoticeDialog = false }
        )
    }

    noticeToEdit?.let { notice ->
        AmendNoticeDialog(
            notice = notice,
            onDismiss = { noticeToEdit = null },
            onSave = { updated ->
                viewModel.amendNotice(updated)
                noticeToEdit = null
            }
        )
    }

    noticeToViewDetail?.let { notice ->
        OfficialNoticeDetailDialog(
            notice = notice,
            societyName = societyConfig?.societyName ?: "Arihant Alishan CHS Ltd.",
            regNumber = societyConfig?.regNumber ?: "NMMC/WAR/CHS/2026/894",
            onDismiss = { noticeToViewDetail = null },
            onAcknowledge = {
                viewModel.acknowledgeNotice(notice.id)
                noticeToViewDetail = null
            }
        )
    }

    if (showAddRuleDialog || ruleToEdit != null) {
        RuleEditDialog(
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
}

// ==========================================
// Notice Item Card
// ==========================================
@Composable
private fun NoticeItemCard(
    notice: SocietyNoticeEntity,
    isCommittee: Boolean,
    onCardClick: () -> Unit,
    onAcknowledge: () -> Unit,
    onEdit: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (notice.isPinned) PureWhiteSurface else PureWhiteSurface
        ),
        shape = RoundedCornerShape(14.dp),
        border = if (notice.isPinned) androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent) else CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("notice_card_${notice.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Circular No, Priority, Pin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notice.isPinned) {
                        Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NavyPrimary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = notice.circularNo.ifBlank { "CIR/2026/${notice.id}" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = notice.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                StatusBadge(status = notice.priority)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notice Title
            Text(
                text = notice.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Notice Body
            Text(
                text = notice.content,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Target: ${notice.targetTower} • By: ${notice.publishedBy}", fontSize = 10.sp, color = TextMuted)
                    Text(notice.publishDate, fontSize = 10.sp, color = TextMuted)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notice.isAcknowledged) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusSuccessBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Acknowledged (${notice.acknowledgedCount})", fontSize = 10.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onAcknowledge,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Acknowledge", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Committee Actions (Amend, Pin, Delete)
            if (isCommittee) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pin toggle
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (notice.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = "Toggle Pin",
                            tint = if (notice.isPinned) GoldAccent else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("btn_amend_notice_${notice.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Amend", fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// Society Bylaws Tab (Dynamic & Amendable)
// ==========================================
@Composable
private fun SocietyBylawsTab(
    rules: List<SocietyRuleEntity>,
    isAuthorized: Boolean,
    onAddRuleClick: () -> Unit,
    onEditRule: (SocietyRuleEntity) -> Unit,
    onDeleteRule: (Long, String) -> Unit,
    hasAgreedBylaws: Boolean,
    onAgree: () -> Unit
) {
    val groupedRules = rules.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Arihant Alishan Code of Conduct & Bylaws", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Under Maharashtra Co-operative Societies (MCS) Act 1960", color = GoldChampagne, fontSize = 11.sp)
                        }
                        if (isAuthorized) {
                            Button(
                                onClick = onAddRuleClick,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_rules_add_new")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Rule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        groupedRules.forEach { (category, categoryRules) ->
            item {
                Text(category.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
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
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rule.ruleDescription,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )

                        if (isAuthorized) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = BorderSubtle)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Updated: ${rule.lastUpdated} • ${if (rule.isMandatory) "Mandatory" else "Advisory"}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )

                                Row {
                                    TextButton(
                                        onClick = { onEditRule(rule) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = NavyPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Amend", fontSize = 11.sp, color = NavyPrimary)
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

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Resident Digital Acknowledgement", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "I confirm that all members of my household and visiting guests will comply with society rules.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onAgree,
                        colors = ButtonDefaults.buttonColors(containerColor = if (hasAgreedBylaws) StatusSuccess else NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (hasAgreedBylaws) "Acknowledged ✓" else "I Acknowledge & Agree", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// Dialogs: Publish, Amend & Official Circular View
// ==========================================
@Composable
fun PublishNoticeDialog(
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Water") }
    var tower by remember { mutableStateOf("ALL") }
    var priority by remember { mutableStateOf("Normal") }
    var circularNo by remember { mutableStateOf("CIR/2026/${(110..999).random()}") }
    var isPinned by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Publish Notice to Board", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Official Society Notice under MCS Act 1960", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = circularNo,
                    onValueChange = { circularNo = it },
                    label = { Text("Circular Reference Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title / Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Notice Content / Circular Body") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tower,
                        onValueChange = { tower = it },
                        label = { Text("Target Tower") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority Level:", fontSize = 11.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Normal", "High", "Critical").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 10.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pin to Top of Notice Board", fontSize = 12.sp, color = TextPrimary)
                    Switch(checked = isPinned, onCheckedChange = { isPinned = it })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                viewModel.publishNotice(
                                    title = title,
                                    content = content,
                                    category = category,
                                    tower = tower,
                                    priority = priority,
                                    circularNo = circularNo,
                                    isPinned = isPinned
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent)
                    ) {
                        Text("Publish")
                    }
                }
            }
        }
    }
}

@Composable
fun AmendNoticeDialog(
    notice: SocietyNoticeEntity,
    onDismiss: () -> Unit,
    onSave: (SocietyNoticeEntity) -> Unit
) {
    var title by remember { mutableStateOf(notice.title) }
    var content by remember { mutableStateOf(notice.content) }
    var category by remember { mutableStateOf(notice.category) }
    var tower by remember { mutableStateOf(notice.targetTower) }
    var priority by remember { mutableStateOf(notice.priority) }
    var circularNo by remember { mutableStateOf(notice.circularNo) }
    var isPinned by remember { mutableStateOf(notice.isPinned) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Amend Official Notice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Ref: ${notice.id}", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = circularNo,
                    onValueChange = { circularNo = it },
                    label = { Text("Circular No") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tower,
                        onValueChange = { tower = it },
                        label = { Text("Target Tower") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority:", fontSize = 11.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Normal", "High", "Critical").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 10.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pin to Notice Board", fontSize = 12.sp, color = TextPrimary)
                    Switch(checked = isPinned, onCheckedChange = { isPinned = it })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                notice.copy(
                                    title = title,
                                    content = content,
                                    category = category,
                                    targetTower = tower,
                                    priority = priority,
                                    circularNo = circularNo,
                                    isPinned = isPinned
                                )
                            )
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
fun OfficialNoticeDetailDialog(
    notice: SocietyNoticeEntity,
    societyName: String,
    regNumber: String,
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Official Society Letterhead Style
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(societyName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                    Text("Reg. No: $regNumber", fontSize = 10.sp, color = TextMuted)
                    Text("OFFICIAL SOCIETY CIRCULAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Circular #: ${notice.circularNo}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text("Date: ${notice.publishDate}", fontSize = 11.sp, color = TextMuted)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Target: ${notice.targetTower} Tower Residents", fontSize = 11.sp, color = TextSecondary)
                    Text("Category: ${notice.category}", fontSize = 11.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                Text("SUBJECT: ${notice.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notice.content,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Issued by:", fontSize = 10.sp, color = TextMuted)
                        Text(notice.publishedBy, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("For Managing Committee", fontSize = 9.sp, color = TextMuted)
                    }

                    if (!notice.isAcknowledged) {
                        Button(
                            onClick = onAcknowledge,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Acknowledge", fontSize = 11.sp)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusSuccessBg
                        ) {
                            Text("✓ Acknowledged", color = StatusSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

// ==========================================
// Dialog: Rule Edit / Add
// ==========================================
@Composable
fun RuleEditDialog(
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
                Text(
                    text = "Bylaw amendment under MCS Act 1960",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

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
                    label = { Text("Rule Description & Details") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = penaltyStr,
                    onValueChange = { penaltyStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Penalty Fine (₹)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isMandatory, onCheckedChange = { isMandatory = it })
                    Text("Mandatory Rule (Statutory Fine)", fontSize = 11.sp)
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
