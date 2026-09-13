package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StaffAttendanceEntity
import com.example.ui.ArihantViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAttendanceScreen(
    viewModel: ArihantViewModel,
    onNavigateBack: () -> Unit
) {
    val theme = LocalAppThemePalette.current
    val allAttendance by viewModel.allStaffAttendance.collectAsState()

    var activeScreenTab by remember { mutableStateOf(0) } // 0: Mark Attendance, 1: Attendance Log / Roster
    val screenTabs = listOf("Mark Attendance", "Daily Roster & Logs")

    // Attendance Form State
    var selectedStaffIndex by remember { mutableStateOf(0) }
    val defaultStaffList = listOf(
        Triple("SEC-01", "Surendra Singh", "Security Guard"),
        Triple("SEC-NG", "Om Prakash", "Night Shift Guard"),
        Triple("SEC-02", "Vikas Patil", "Security Guard"),
        Triple("HK-01", "Sunita Jadhav", "Housekeeping Supervisor"),
        Triple("MNT-01", "Prakash Nair", "Maintenance Supervisor"),
        Triple("GARD-01", "Tukaram Shinde", "Gardener / Landscape")
    )
    var selectedPunchType by remember { mutableStateOf("Punch In") } // "Punch In" or "Punch Out"

    // Photo Source Tab: 0 = Camera (Live Selfie), 1 = Gallery (Upload Photo)
    var photoSourceTab by remember { mutableStateOf(0) }
    var capturedSelfieBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedGalleryUri by remember { mutableStateOf<Uri?>(null) }
    var remarksText by remember { mutableStateOf("") }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // Real device launchers
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedSelfieBitmap = bitmap
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedGalleryUri = uri
        }
    }

    // Fixed society geofence coordinates for Kharghar Sector 35
    val currentLat = 19.043641
    val currentLng = 73.072218
    val locationAddress = "Main Security Cabin & Gate #1, Arihant Alishan, Sector 35, Kharghar"
    val isGeofenceValid = true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Staff Attendance",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Geo-location & Selfie Verification",
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
            // Screen Level Tabs
            TabRow(
                selectedTabIndex = activeScreenTab,
                containerColor = Color.White,
                contentColor = theme.primary
            ) {
                screenTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeScreenTab == index,
                        onClick = { activeScreenTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (activeScreenTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeScreenTab == index) theme.primary else TextSecondary
                            )
                        }
                    )
                }
            }

            if (activeScreenTab == 0) {
                // Form: Mark Attendance
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Geofence & GPS status card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2E7D32))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "GEOFENCE VERIFIED",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = "GPS Accuracy: ±3m",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = theme.accent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = locationAddress,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NavyPrimary
                                        )
                                        Text(
                                            text = "Coordinates: $currentLat N, $currentLng E (Sector 35)",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Staff Member Selection
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1. Select Staff Member",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                defaultStaffList.forEachIndexed { index, staff ->
                                    val isSelected = selectedStaffIndex == index
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) theme.accent.copy(alpha = 0.12f) else Color(0xFFF9FAFB))
                                            .border(
                                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                                color = if (isSelected) theme.accent else Color.LightGray.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedStaffIndex = index }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedStaffIndex = index },
                                            colors = RadioButtonDefaults.colors(selectedColor = theme.accent)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(staff.second, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                            Text("${staff.first} • ${staff.third}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Punch Type Selection (Punch In vs Punch Out)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "2. Punch Action",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    listOf("Punch In", "Punch Out").forEach { type ->
                                        val isSelected = selectedPunchType == type
                                        val isPunchIn = type == "Punch In"
                                        val btnColor = if (isPunchIn) Color(0xFF2E7D32) else Color(0xFFD32F2F)

                                        Button(
                                            onClick = { selectedPunchType = type },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("punch_type_${type.replace(" ", "_").lowercase()}"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) btnColor else Color(0xFFF0F2F5),
                                                contentColor = if (isSelected) Color.White else NavyPrimary
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isPunchIn) Icons.Default.Login else Icons.Default.Logout,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(type, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 3. Photo Capture with Tab option (Camera vs Gallery)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "3. Photo Verification (Mandatory Selfie)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Camera vs Gallery Tab options
                                TabRow(
                                    selectedTabIndex = photoSourceTab,
                                    containerColor = Color(0xFFF5F6F8),
                                    contentColor = theme.accent,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                ) {
                                    Tab(
                                        selected = photoSourceTab == 0,
                                        onClick = { photoSourceTab = 0 },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Camera (Selfie)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    )
                                    Tab(
                                        selected = photoSourceTab == 1,
                                        onClick = { photoSourceTab = 1 },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Gallery (Upload)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                if (photoSourceTab == 0) {
                                    // CAMERA TAB
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF9FAFB))
                                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (capturedSelfieBitmap != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(160.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .border(2.dp, theme.accent, RoundedCornerShape(12.dp))
                                            ) {
                                                Image(
                                                    bitmap = capturedSelfieBitmap!!.asImageBitmap(),
                                                    contentDescription = "Captured Selfie",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                                // Watermark tag
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .fillMaxWidth(),
                                                    color = Color.Black.copy(alpha = 0.65f)
                                                ) {
                                                    Text(
                                                        text = "GPS Verified • Arihant Alishan",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        takePictureLauncher.launch(null)
                                                    } catch (_: Exception) {}
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Retake Selfie", fontSize = 12.sp)
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(72.dp)
                                                    .clip(CircleShape)
                                                    .background(theme.accent.copy(alpha = 0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.AddAPhoto,
                                                    contentDescription = "Take Selfie",
                                                    tint = theme.accent,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = "Take live staff front-facing selfie at the security post.",
                                                fontSize = 12.sp,
                                                color = TextSecondary,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Button(
                                                onClick = {
                                                    try {
                                                        takePictureLauncher.launch(null)
                                                    } catch (_: Exception) {}
                                                },
                                                modifier = Modifier.testTag("launch_camera_selfie_button"),
                                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Open Camera & Take Selfie")
                                            }
                                        }
                                    }
                                } else {
                                    // GALLERY TAB
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF9FAFB))
                                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (selectedGalleryUri != null) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Photo Selected from Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                    Text(selectedGalleryUri.toString().takeLast(30), fontSize = 10.sp, color = TextMuted)
                                                }
                                                IconButton(onClick = { selectedGalleryUri = null }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(theme.primary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Collections,
                                                contentDescription = "Pick Photo",
                                                tint = theme.primary,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "Upload attendance verification photo or gate register slip from device gallery.",
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                try {
                                                    pickMediaLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                } catch (_: Exception) {}
                                            },
                                            modifier = Modifier.testTag("launch_gallery_upload_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Choose Photo from Gallery")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Optional Remarks
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "4. Remarks (Optional)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = remarksText,
                                    onValueChange = { remarksText = it },
                                    placeholder = { Text("e.g. Relieving night shift, gate duty, uniform clean") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("attendance_remarks_input"),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Submit Punch Button
                    item {
                        val currentStaff = defaultStaffList[selectedStaffIndex]
                        Button(
                            onClick = {
                                val photoSource = if (photoSourceTab == 0) "Camera" else "Gallery"
                                val photoUri = if (photoSourceTab == 0) "selfie_captured_${System.currentTimeMillis()}" else (selectedGalleryUri?.toString() ?: "gallery_selected")

                                viewModel.recordStaffPunch(
                                    staffId = currentStaff.first,
                                    staffName = currentStaff.second,
                                    staffRole = currentStaff.third,
                                    punchType = selectedPunchType,
                                    latitude = currentLat,
                                    longitude = currentLng,
                                    locationAddress = locationAddress,
                                    isGeoFenceVerified = isGeofenceValid,
                                    photoSource = photoSource,
                                    photoUri = photoUri,
                                    remarks = if (remarksText.isNotBlank()) remarksText else "Verified with $photoSource selfie."
                                )

                                showSuccessSnackbar = true
                                remarksText = ""
                                capturedSelfieBitmap = null
                                selectedGalleryUri = null
                                activeScreenTab = 1 // Switch to roster to see newly marked punch
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_attendance_punch"),
                            colors = ButtonDefaults.buttonColors(containerColor = if (selectedPunchType == "Punch In") Color(0xFF2E7D32) else Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedPunchType == "Punch In") Icons.Default.CheckCircle else Icons.Default.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit ${selectedPunchType} (${currentStaff.second})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                // Tab 1: Attendance Roster / Historical Logs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = theme.secondary.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Today's Verified Staff Punches",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "${allAttendance.size} Total check-ins with Geo-Fence Verification",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = theme.primary
                                ) {
                                    Text(
                                        text = "${allAttendance.size} Records",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (allAttendance.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No attendance records today yet", fontSize = 14.sp, color = TextMuted)
                                }
                            }
                        }
                    } else {
                        items(allAttendance) { item ->
                            StaffAttendanceRow(item = item, theme = theme)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaffAttendanceRow(
    item: StaffAttendanceEntity,
    theme: AppThemePalette
) {
    val isPunchIn = item.punchType == "Punch In"
    val badgeColor = if (isPunchIn) Color(0xFF2E7D32) else Color(0xFFD32F2F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_row_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                            .background(badgeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPunchIn) Icons.Default.Login else Icons.Default.Logout,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.staffName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "${item.staffId} • ${item.staffRole}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = item.punchType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo Source & Geo details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0F2F5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.photoSource == "Camera") Icons.Default.CameraAlt else Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = NavyPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Photo: ${item.photoSource}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(11.dp), tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Geofence OK", fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = item.timestamp,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.locationAddress} (${item.latitude}, ${item.longitude})",
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }

            if (item.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Remarks: ${item.remarks}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
