package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PatrolCheckpointEntity
import com.example.ui.theme.*

@Composable
fun MissedPatrolAlertPopup(
    checkpoint: PatrolCheckpointEntity,
    onDismiss: () -> Unit,
    onAcknowledge: (resolutionNotes: String) -> Unit,
    onNavigateToPatrol: () -> Unit
) {
    val context = LocalContext.current
    var showResolutionDialog by remember { mutableStateOf(false) }
    var resolutionNotes by remember { mutableStateOf("Verified with security supervisor. Checkpoint scanned and perimeter cleared.") }

    // Pulsing animation for high-urgency alert
    val infiniteTransition = rememberInfiniteTransition(label = "alert_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val elapsedMinutes = ((System.currentTimeMillis() - checkpoint.lastScanEpochMillis) / (60 * 1000L)).coerceAtLeast(61)
    val overdueMinutes = (elapsedMinutes - checkpoint.scanIntervalMinutes).coerceAtLeast(1)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(2.5.dp, Color(0xFFD32F2F), RoundedCornerShape(20.dp))
                .testTag("missed_patrol_alert_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Header with Pulsing Badge
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .border(2.dp, Color(0xFFD32F2F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Missed Patrol Alert",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High-priority Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD32F2F)
                ) {
                    Text(
                        text = "CRITICAL SECURITY BREACH: 1-HOUR SCAN MISSED",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🚨 Missed QR Patrol Alert",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Attention Committee Members & Society Managers",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Alert details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFFCDD2), Color(0xFFFFEBEE))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Text(
                                text = checkpoint.checkpointName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = checkpoint.locationDescription,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFFFCDD2))

                        // Guard details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ASSIGNED GUARD", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                                Text(checkpoint.assignedGuard, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                Text("Shift: ${checkpoint.assignedShift}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("OVERDUE BY", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD32F2F))
                                Text("$overdueMinutes mins", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                                Text("Intvl: Every 60m", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Last Scan: ${checkpoint.lastScanFormatted} (${elapsedMinutes}m ago)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                // Call Guard Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${checkpoint.assignedGuardPhone}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("call_guard_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call Guard", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Guard (${checkpoint.assignedGuard})", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Acknowledge / Resolve button
                OutlinedButton(
                    onClick = { showResolutionDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("acknowledge_patrol_alert_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NavyPrimary, NavyPrimary))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Acknowledge", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Acknowledge & Record Incident", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onNavigateToPatrol,
                        modifier = Modifier.testTag("view_patrol_monitor_button")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open Patrol Console", fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_patrol_alert_button")
                    ) {
                        Text("Dismiss for 10m", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }
    }

    // Resolution Modal
    if (showResolutionDialog) {
        AlertDialog(
            onDismissRequest = { showResolutionDialog = false },
            title = {
                Text(
                    text = "Acknowledge Incident: ${checkpoint.code}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter resolution notes or reason for the missed hourly scan. This will be archived in the security compliance audit log.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = { Text("Resolution / Audit Remarks") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("resolution_notes_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAcknowledge(resolutionNotes)
                        showResolutionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier.testTag("confirm_resolution_button")
                ) {
                    Text("Confirm & Clear Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolutionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
