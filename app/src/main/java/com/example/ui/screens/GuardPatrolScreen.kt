package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PatrolCheckpointEntity
import com.example.data.model.PatrolScanLogEntity
import com.example.ui.ArihantViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardPatrolScreen(
    viewModel: ArihantViewModel,
    onNavigateBack: () -> Unit
) {
    val theme = LocalAppThemePalette.current
    val context = LocalContext.current

    val checkpoints by viewModel.allPatrolCheckpoints.collectAsState()
    val scanLogs by viewModel.allPatrolScanLogs.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Checkpoints (P1-P5 + Night Guard), 1: Scan Logs
    var scanningCheckpoint by remember { mutableStateOf<PatrolCheckpointEntity?>(null) }

    val now = System.currentTimeMillis()
    val missedCount = checkpoints.count { cp ->
        val elapsed = now - cp.lastScanEpochMillis
        (cp.isMissedAlertActive || elapsed > 60 * 60 * 1000L) && !cp.missedAlertAcknowledged
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Guard Patrol QR Checkpoints",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hourly Checkpoints: P1 - P5 & Night Guard",
                            fontSize = 11.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(OffWhiteBackground)
        ) {
            // High Priority Alert Banner if any checkpoint is overdue
            if (missedCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFD32F2F)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "$missedCount PATROL SCAN(S) MISSED (>60 MINS)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Alert dispatched to Committee & Society Managers",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        if (currentRole.isCommitteeMember()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clickable {
                                    val missedCp = checkpoints.firstOrNull { it.isMissedAlertActive || (now - it.lastScanEpochMillis > 60 * 60 * 1000L) }
                                    if (missedCp != null) {
                                        viewModel.checkMissedPatrols()
                                    }
                                }
                            ) {
                                Text(
                                    text = "Review Alert",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tabs: Checkpoint Status vs History Logs
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.White,
                contentColor = theme.primary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Checkpoints (P1-P5 & NG)",
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                            if (missedCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD32F2F)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$missedCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = "Scan Activity Log (${scanLogs.size})",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (activeTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Protocol summary card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = theme.primary, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "1-Hour QR Patrol Compliance Mandate",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "Guards must physically reach and scan QR codes at P1, P2, P3, P4, P5, & Night beats every 60 minutes. Missed scans trigger automated alerts to Committee Members.",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Checkpoints list (P1, P2, P3, P4, P5, Night Guard)
                    items(checkpoints) { cp ->
                        PatrolCheckpointCard(
                            checkpoint = cp,
                            theme = theme,
                            onScanQrClicked = { scanningCheckpoint = cp },
                            onSimulateMissedScan = { viewModel.simulateMissedScan(cp.checkpointId) },
                            onCallGuard = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${cp.assignedGuardPhone}")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            } else {
                // Historical Scan Logs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (scanLogs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No patrol scans logged yet today", color = TextMuted, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(scanLogs) { log ->
                            PatrolScanLogRow(log = log, theme = theme)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    // Interactive QR Scanner Modal
    scanningCheckpoint?.let { cp ->
        GuardQrScannerModal(
            checkpoint = cp,
            theme = theme,
            onDismiss = { scanningCheckpoint = null },
            onScanConfirmed = {
                viewModel.scanPatrolCheckpoint(cp.checkpointId, "QR scanned via Guard device at physical post.")
                scanningCheckpoint = null
            }
        )
    }
}

@Composable
fun PatrolCheckpointCard(
    checkpoint: PatrolCheckpointEntity,
    theme: AppThemePalette,
    onScanQrClicked: () -> Unit,
    onSimulateMissedScan: () -> Unit,
    onCallGuard: () -> Unit
) {
    val now = System.currentTimeMillis()
    val elapsedMinutes = (now - checkpoint.lastScanEpochMillis) / (60 * 1000L)
    val remainingMinutes = (checkpoint.scanIntervalMinutes - elapsedMinutes).coerceAtLeast(0)
    val isMissed = checkpoint.isMissedAlertActive || elapsedMinutes > checkpoint.scanIntervalMinutes

    val cardBorderColor = if (isMissed) Color(0xFFD32F2F) else if (remainingMinutes <= 10) Color(0xFFF57C00) else Color.Transparent
    val statusBg = if (isMissed) Color(0xFFFFEBEE) else if (remainingMinutes <= 10) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
    val statusText = if (isMissed) Color(0xFFD32F2F) else if (remainingMinutes <= 10) Color(0xFFE65100) else Color(0xFF2E7D32)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isMissed) 2.dp else if (remainingMinutes <= 10) 1.5.dp else 0.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("checkpoint_card_${checkpoint.checkpointId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Code, Title, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NavyPrimary
                    ) {
                        Text(
                            text = checkpoint.code,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = GoldAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = checkpoint.checkpointName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = checkpoint.assignedShift,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = if (isMissed) "MISSED (>60m)" else if (remainingMinutes <= 10) "DUE SOON" else "ON TRACK",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = checkpoint.locationDescription,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar for 1-hour interval
            val progressFraction = (elapsedMinutes.toFloat() / checkpoint.scanIntervalMinutes.toFloat()).coerceIn(0f, 1f)
            val progressColor = if (isMissed) Color(0xFFD32F2F) else if (remainingMinutes <= 10) Color(0xFFF57C00) else theme.primary

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isMissed) "Overdue by ${elapsedMinutes - checkpoint.scanIntervalMinutes} mins" else "Next scan due in $remainingMinutes mins",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMissed) Color(0xFFD32F2F) else NavyPrimary
                    )
                    Text(
                        text = "${checkpoint.totalScansToday} scans today",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = progressColor,
                    trackColor = Color(0xFFEEEEEE)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guard assignment info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Guard: ${checkpoint.assignedGuard}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Last scan: ${checkpoint.lastScanFormatted}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = onCallGuard,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Guard",
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onScanQrClicked,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("scan_qr_button_${checkpoint.checkpointId}"),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan QR Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSimulateMissedScan,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_missed_scan_${checkpoint.checkpointId}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Simulate Miss", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GuardQrScannerModal(
    checkpoint: PatrolCheckpointEntity,
    theme: AppThemePalette,
    onDismiss: () -> Unit,
    onScanConfirmed: () -> Unit
) {
    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("qr_scanner_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scanning Checkpoint: ${checkpoint.code}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                Text(
                    text = checkpoint.checkpointName,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Viewfinder simulation
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1A1A))
                        .border(2.dp, theme.accent, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Viewfinder",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = checkpoint.qrPayload,
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Laser line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (laserOffset * 210).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFF00E676), Color.Transparent)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Geo-Verified Post • Kharghar Sector 35",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Scan button
                Button(
                    onClick = onScanConfirmed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_qr_scan_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm QR Scan & Reset 1h Timer", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun PatrolScanLogRow(
    log: PatrolScanLogEntity,
    theme: AppThemePalette
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (log.scanStatus == "On-Time") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Icon(
                    imageVector = if (log.scanStatus == "On-Time") Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (log.scanStatus == "On-Time") Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.checkpointId,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.checkpointName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = NavyPrimary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Scanned by ${log.guardName} (${log.guardRole})",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Text(
                    text = "${log.scanTimestamp} • Geo: ${log.latitude}, ${log.longitude}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
