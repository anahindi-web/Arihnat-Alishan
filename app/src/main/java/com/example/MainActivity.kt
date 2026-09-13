package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.components.ArihantBottomBar
import com.example.ui.components.ArihantHeader
import com.example.ui.components.MissedPatrolAlertPopup
import com.example.ui.components.SocietyAlertBanner
import com.example.ui.screens.*
import com.example.ui.theme.ArihantAlishanTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OffWhiteBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ArihantViewModel = viewModel()
            val currentAppTheme by viewModel.currentAppTheme.collectAsState()
            ArihantAlishanTheme(appTheme = currentAppTheme) {
                ArihantApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArihantApp(
    viewModel: ArihantViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeAlert by viewModel.activeAlert.collectAsState()

    // Handle back button: return to home screen if on a sub-screen
    BackHandler(enabled = currentScreen != "home") {
        viewModel.navigateTo("home")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                if (currentScreen in listOf("home", "complaints", "visitors", "amenities", "more")) {
                    ArihantHeader(
                        currentRole = currentRole,
                        userName = currentUserName,
                        flatId = currentFlatId,
                        onRoleSelected = { role ->
                            viewModel.setRole(role)
                            if (role == UserRole.SUPER_ADMIN) {
                                viewModel.navigateTo("super_admin")
                            }
                        },
                        onEmergencyClicked = { viewModel.navigateTo("emergency") },
                        onNoticesClicked = { viewModel.navigateTo("notices") },
                        onSuperAdminClicked = { viewModel.navigateTo("super_admin") }
                    )
                } else if (currentScreen in listOf("master_data", "super_admin", "attendance", "patrol")) {
                    // Dedicated top app bar provided by the screen
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (currentScreen) {
                                    "profile" -> "Household Master Record"
                                    "parking" -> "Multi-Level Parking (P1-P5)"
                                    "water_lifts" -> "Water & Lift Telemetry"
                                    "emergency" -> "Emergency Action Center"
                                    "maintenance" -> "Maintenance & Receipts"
                                    "service_requests" -> "Permits & NOC Requests"
                                    "governance" -> "Governance & Approvals"
                                    "notices" -> "Society Notice Board"
                                    else -> "Arihant Alishan"
                                },
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.navigateTo("home") }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
                    )
                }

                activeAlert?.let { msg ->
                    SocietyAlertBanner(
                        message = msg,
                        onDismiss = { viewModel.clearAlert() }
                    )
                }
            }
        },
        bottomBar = {
            ArihantBottomBar(
                currentScreen = if (currentScreen in listOf("home", "complaints", "visitors", "amenities", "more")) currentScreen else "more",
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhiteBackground)
        ) {
            when (currentScreen) {
                "home" -> ResidentDashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "complaints" -> ComplaintsScreen(viewModel = viewModel)
                "visitors" -> VisitorsScreen(viewModel = viewModel)
                "amenities" -> AmenitiesScreen(viewModel = viewModel)
                "more" -> MoreMenuScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "profile" -> MasterProfileScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "parking" -> ParkingScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "water_lifts" -> WaterLiftMonitoringScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "emergency" -> EmergencyScreen(viewModel = viewModel)
                "maintenance" -> MaintenanceScreen(viewModel = viewModel)
                "service_requests" -> ServiceRequestsScreen(viewModel = viewModel)
                "governance" -> GovernanceScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
                "notices" -> NoticesRulesScreen(viewModel = viewModel)
                "master_data" -> MasterDataScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo("home") }
                )
                "super_admin" -> SuperAdminScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onNavigateBack = { viewModel.navigateTo("home") }
                )
                "attendance" -> StaffAttendanceScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo("home") }
                )
                "patrol" -> GuardPatrolScreen(
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo("home") }
                )
                else -> ResidentDashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    }

    // High Priority Popup Alert for Committee Members & Society Managers when 1-Hour Patrol is Missed
    val activeMissedAlertPopup by viewModel.activeMissedAlertPopup.collectAsState()
    activeMissedAlertPopup?.let { checkpoint ->
        MissedPatrolAlertPopup(
            checkpoint = checkpoint,
            onDismiss = { viewModel.dismissMissedAlertPopup(checkpoint.checkpointId) },
            onAcknowledge = { resolutionNotes ->
                viewModel.acknowledgeMissedAlert(checkpoint.checkpointId, resolutionNotes)
            },
            onNavigateToPatrol = {
                viewModel.dismissMissedAlertPopup(null)
                viewModel.navigateTo("patrol")
            }
        )
    }
}
