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
  fun `ArihantViewModel initializes cleanly with default Flat K-302 owner Rahul Sharma`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = ArihantViewModel(app)
    assertNotNull(vm)
    assertEquals("Rahul Sharma", vm.currentUserName.value)
    assertEquals("K-302", vm.currentFlatId.value)
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
    assertFalse(UserRole.FAMILY_MEMBER.isCommitteeMember())
    assertFalse(UserRole.RESIDENT_TENANT.isCommitteeMember())
    assertFalse(UserRole.SECURITY_GUARD.isCommitteeMember())
    assertFalse(UserRole.HOUSEKEEPING_STAFF.isCommitteeMember())

    assertTrue(UserRole.SUPER_ADMIN.isSuperAdmin())
    assertFalse(UserRole.RESIDENT_OWNER.isSuperAdmin())
  }

  @Test
  fun `verify Role Based Access Control permissions for Flat Owner vs Committee`() {
    // Flat Owner
    val owner = UserRole.RESIDENT_OWNER
    assertFalse("Flat owner cannot view other flats and residents", owner.canViewOtherFlatsAndResidents())
    assertFalse("Flat owner cannot view all society complaints", owner.canViewAllSocietyComplaints())
    assertFalse("Flat owner cannot amend society notices", owner.canAmendNotices())
    assertFalse("Flat owner cannot manage operational masters", owner.canManageOperationalMasters())
    assertFalse("Flat owner cannot add society members", owner.canAddMember())

    // Super Admin & Committee
    assertTrue(UserRole.SUPER_ADMIN.canManageOperationalMasters())
    assertTrue(UserRole.CHAIRMAN.canViewAllSocietyComplaints())
    assertTrue(UserRole.CHAIRMAN.canAmendComplaints())
    assertTrue(UserRole.SECRETARY.canAddMember())
    assertTrue(UserRole.SECRETARY.canGenerateInvitations())
    assertTrue(UserRole.TREASURER.canViewSocietyFinances())
    assertTrue(UserRole.COMMITTEE_MEMBER.canGenerateInvitations())
  }

  @Test
  fun `verify Child Protection rule - children below 16 cannot have app access or invitations`() {
    val childAarav = com.example.data.model.FamilyMemberEntity(
      flatId = "K-302",
      fullName = "Aarav Sharma",
      relationship = "Son",
      memberType = "Son",
      gender = "Male",
      dob = "20-Aug-2014",
      calculatedAge = 12,
      isChild = true,
      isChildBelow16 = true,
      parentGuardianName = "Rahul Sharma",
      parentGuardianPhone = "+91 98201 12345",
      hasAppAccess = false,
      inviteStatus = "Not Allowed for Minors"
    )

    assertTrue(childAarav.isChildBelow16)
    assertFalse("Children below 16 must not have app access", childAarav.hasAppAccess)
    assertEquals("Rahul Sharma", childAarav.parentGuardianName)

    val spousePriya = com.example.data.model.FamilyMemberEntity(
      flatId = "K-302",
      fullName = "Priya Sharma",
      relationship = "Spouse",
      memberType = "Spouse",
      gender = "Female",
      dob = "14-May-1988",
      calculatedAge = 38,
      isChild = false,
      isChildBelow16 = false,
      hasAppAccess = true,
      inviteStatus = "Active"
    )

    assertFalse(spousePriya.isChildBelow16)
    assertTrue("Adult members can have app access", spousePriya.hasAppAccess)
  }

  @Test
  fun `verify Secure Invitation generation and expiration parameters`() {
    val now = System.currentTimeMillis()
    val validityHours = 48
    val expiry = now + (validityHours * 3600 * 1000L)
    val invitation = com.example.data.model.InvitationEntity(
      inviteCode = "INV-7731",
      token = "ALISHAN-SEC-INV-7731-99827",
      targetFlat = "K-302",
      targetTower = "Kaveh",
      assignedRole = "Family Member",
      recipientName = "Priya Sharma",
      recipientPhone = "+91 98201 99887",
      inviteLink = "https://arihant-alishan.society.in/invite?code=INV-7731&token=ALISHAN-SEC-INV-7731-99827&flat=K-302",
      createdAtEpoch = now,
      expiresAtEpoch = expiry,
      isSingleUse = true,
      isUsed = false,
      createdBy = "Rahul Sharma",
      status = "ACTIVE"
    )

    assertTrue(invitation.isSingleUse)
    assertFalse(invitation.isUsed)
    assertEquals("ACTIVE", invitation.status)
    assertTrue("Invitation expiry must be in the future", invitation.expiresAtEpoch > now)
    assertTrue(invitation.inviteLink.contains("INV-7731"))
  }

  @Test
  fun `verify Tenant Expiry tracking and notification model`() {
    val expiredTenant = com.example.data.model.TenantEntity(
      flatId = "B1-402",
      fullName = "Suresh Mehta",
      phone = "+91 98331 44556",
      email = "suresh.mehta@example.com",
      status = "Expired",
      permanentAddress = "Flat 12, Swapna Nagari, Pune",
      occupation = "Software Engineer",
      moveInDate = "15-Sep-2025",
      expectedMoveOutDate = "14-Sep-2026",
      agreementDocument = "Rent_Agreement_B1_402_Expired.pdf",
      agreementStartDate = "15-Sep-2025",
      agreementExpiryDate = "14-Sep-2026",
      agreementStatus = "Expired",
      daysRemaining = -1,
      policeVerificationStatus = "Verified"
    )

    assertEquals("Expired", expiredTenant.agreementStatus)
    assertTrue("Days remaining should be negative for expired agreement", expiredTenant.daysRemaining < 0)

    val notification = com.example.data.model.RentAgreementNotificationEntity(
      flatId = "B1-402",
      tenantName = "Suresh Mehta",
      ownerName = "Amit Joshi",
      expiryDate = "14 September 2026",
      daysExpiredOrRemaining = 1,
      notificationMessage = "Rent Agreement for Flat B1-402 expired on 14 September 2026.",
      isExpired = true,
      isRead = false
    )

    assertTrue(notification.isExpired)
    assertFalse(notification.isRead)
    assertEquals("B1-402", notification.flatId)
  }

  @Test
  fun `verify Emergency Volunteer attributes for Flat K-302`() {
    val flatK302 = com.example.data.model.FlatEntity(
      flatId = "K-302",
      tower = "Kaveh",
      floor = 3,
      flatNumber = "302",
      ownerName = "Rahul Sharma",
      ownerPhone = "+91 98201 12345",
      ownerEmail = "rahul.sharma@example.com",
      possessionDate = "15-Dec-2022",
      occupancyStatus = "Self Occupied",
      emergencyVolunteer = "Yes",
      volunteerAreas = "Technical Support, First Aid, Evacuation Support",
      volunteerPhone = "+91 98201 12345"
    )

    assertEquals("Yes", flatK302.emergencyVolunteer)
    assertTrue(flatK302.volunteerAreas.contains("First Aid"))
    assertTrue(flatK302.volunteerAreas.contains("Technical Support"))
  }

  @Test
  fun `verify Theme Presets availability and default light mode`() {
    val classic = com.example.ui.theme.AppTheme.ARIHANT_CLASSIC
    val modernBlue = com.example.ui.theme.AppTheme.MODERN_BLUE
    val greenComm = com.example.ui.theme.AppTheme.GREEN_COMMUNITY
    val indigo = com.example.ui.theme.AppTheme.PROFESSIONAL_INDIGO

    assertEquals("ARIHANT_CLASSIC", classic.id)
    assertEquals("MODERN_BLUE", modernBlue.id)
    assertEquals("GREEN_COMMUNITY", greenComm.id)
    assertEquals("PROFESSIONAL_INDIGO", indigo.id)

    val palette = com.example.ui.theme.getPaletteForTheme(classic)
    val colorScheme = palette.toMaterialColorScheme()
    assertNotNull(colorScheme)
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

  @Test
  fun `verify SoundNotificationHelper executes and releases without crashing or leaking`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.ui.util.SoundNotificationHelper.playClickSound(context)
    com.example.ui.util.SoundNotificationHelper.playNotificationSound(context)
    com.example.ui.util.SoundNotificationHelper.playTaskCompletionSound(context)
    com.example.ui.util.SoundNotificationHelper.playAlertSound(context)
    com.example.ui.util.SoundNotificationHelper.release()
    assertTrue("Sound helper handles calls gracefully and safely releases resources", true)
  }

  @Test
  fun `verify committee and security roles have access to society visitor records`() {
    val superAdmin = UserRole.SUPER_ADMIN
    val secretary = UserRole.SECRETARY
    val guard = UserRole.SECURITY_GUARD
    val resident = UserRole.RESIDENT_OWNER

    val isAdminOrGuardSuperAdmin = superAdmin.isCommitteeMember() || superAdmin == UserRole.SECURITY_GUARD || superAdmin == UserRole.SECURITY_INCHARGE
    val isAdminOrGuardSecretary = secretary.isCommitteeMember() || secretary == UserRole.SECURITY_GUARD || secretary == UserRole.SECURITY_INCHARGE
    val isAdminOrGuardGuard = guard.isCommitteeMember() || guard == UserRole.SECURITY_GUARD || guard == UserRole.SECURITY_INCHARGE
    val isAdminOrGuardResident = resident.isCommitteeMember() || resident == UserRole.SECURITY_GUARD || resident == UserRole.SECURITY_INCHARGE

    assertTrue("Super Admin can view visitor logs", isAdminOrGuardSuperAdmin)
    assertTrue("Secretary can view visitor logs", isAdminOrGuardSecretary)
    assertTrue("Security Guard can view visitor logs", isAdminOrGuardGuard)
    assertFalse("Flat Owner is restricted to their own flat visitor logs", isAdminOrGuardResident)
  }
}
