package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PatrolCheckpointEntity
import com.example.data.model.StaffAttendanceEntity
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Arihant Alishan", appName)
  }

  @Test
  fun `ArihantViewModel initializes cleanly without NullPointerException`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = ArihantViewModel(app)
    assertNotNull(vm)
    assertEquals("Rajesh Sharma", vm.currentUserName.value)
    assertEquals(UserRole.RESIDENT_OWNER, vm.currentRole.value)
  }

  @Test
  fun `verify UserRole committee member and manager permissions`() {
    assertTrue(UserRole.CHAIRMAN.isCommitteeMember())
    assertTrue(UserRole.SECRETARY.isCommitteeMember())
    assertTrue(UserRole.TREASURER.isCommitteeMember())
    assertTrue(UserRole.COMMITTEE_MEMBER.isCommitteeMember())
    assertTrue(UserRole.SOCIETY_MANAGER.isCommitteeMember())
    assertTrue(UserRole.SUPER_ADMIN.isCommitteeMember())

    assertFalse(UserRole.RESIDENT_OWNER.isCommitteeMember())
    assertFalse(UserRole.RESIDENT_TENANT.isCommitteeMember())
    assertFalse(UserRole.SECURITY_GUARD.isCommitteeMember())
    assertFalse(UserRole.MAINTENANCE_STAFF.isCommitteeMember())

    assertTrue(UserRole.SUPER_ADMIN.isSuperAdmin())
    assertFalse(UserRole.RESIDENT_OWNER.isSuperAdmin())
  }

  @Test
  fun `verify patrol checkpoint overdue calculation for 1-hour interval`() {
    val now = System.currentTimeMillis()
    val checkpointCompliant = PatrolCheckpointEntity(
      checkpointId = "P1",
      code = "P1",
      checkpointName = "Parking Level 1 (North Ramp & EV Hub)",
      locationDescription = "Basement Level 1",
      qrPayload = "ARIHANT-PATROL-P1-908123",
      assignedShift = "24x7 Rotational Patrol",
      assignedGuard = "Surendra Singh",
      assignedGuardPhone = "+91 98201 55662",
      lastScanEpochMillis = now - (25 * 60 * 1000L), // scanned 25 mins ago
      lastScanFormatted = "Today, 10:00 AM",
      lastScanByGuard = "Surendra Singh",
      scanIntervalMinutes = 60,
      isMissedAlertActive = false,
      totalScansToday = 10
    )

    val elapsedMinutes = (now - checkpointCompliant.lastScanEpochMillis) / (60 * 1000L)
    val isOverdue = elapsedMinutes > checkpointCompliant.scanIntervalMinutes
    assertFalse(isOverdue)
    assertEquals(25L, elapsedMinutes)

    val checkpointMissed = checkpointCompliant.copy(
      lastScanEpochMillis = now - (75 * 60 * 1000L), // 75 mins ago (>60 min interval)
      isMissedAlertActive = true
    )
    val missedElapsed = (now - checkpointMissed.lastScanEpochMillis) / (60 * 1000L)
    val isMissedOverdue = missedElapsed > checkpointMissed.scanIntervalMinutes
    assertTrue(isMissedOverdue)
    assertTrue(checkpointMissed.isMissedAlertActive)
  }

  @Test
  fun `verify staff attendance entity properties and dual photo modes`() {
    val attendanceCamera = StaffAttendanceEntity(
      staffId = "SEC-01",
      staffName = "Surendra Singh",
      staffRole = "Security Guard",
      punchType = "Punch In",
      timestamp = "Today, 08:00 AM",
      epochMillis = System.currentTimeMillis(),
      latitude = 19.0436,
      longitude = 73.0722,
      locationAddress = "Main Security Cabin, Arihant Alishan",
      isGeoFenceVerified = true,
      photoSource = "Camera",
      photoUri = "captured_selfie_uri",
      remarks = "On duty Gate 1"
    )

    assertEquals("Punch In", attendanceCamera.punchType)
    assertEquals("Camera", attendanceCamera.photoSource)
    assertTrue(attendanceCamera.isGeoFenceVerified)
    assertEquals(19.0436, attendanceCamera.latitude, 0.0001)

    val attendanceGallery = attendanceCamera.copy(
      punchType = "Punch Out",
      photoSource = "Gallery",
      photoUri = "gallery_photo_uri"
    )

    assertEquals("Punch Out", attendanceGallery.punchType)
    assertEquals("Gallery", attendanceGallery.photoSource)
  }
}
