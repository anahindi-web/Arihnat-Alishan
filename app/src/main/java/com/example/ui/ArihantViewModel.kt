package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ArihantRepository
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ArihantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArihantRepository = ArihantRepository(
        AppDatabase.getDatabase(application).appDao()
    )

    // Current User Session State
    private val _currentRole = MutableStateFlow(UserRole.RESIDENT_OWNER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentFlatId = MutableStateFlow("K-302")
    val currentFlatId: StateFlow<String> = _currentFlatId.asStateFlow()

    private val _currentUserName = MutableStateFlow("Rahul Sharma")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // Application Theme State
    private val _currentAppTheme = MutableStateFlow(AppTheme.ARIHANT_CLASSIC)
    val currentAppTheme: StateFlow<AppTheme> = _currentAppTheme.asStateFlow()

    // Background Wall State
    private val _selectedBackgroundWall = MutableStateFlow(com.example.R.drawable.img_alishan_sunset)
    val selectedBackgroundWall: StateFlow<Int> = _selectedBackgroundWall.asStateFlow()

    fun setBackgroundWall(drawableResId: Int) {
        _selectedBackgroundWall.value = drawableResId
    }

    // Missed Patrol Alert Popup State for Committee Members and Society Managers
    private val _activeMissedAlertPopup = MutableStateFlow<PatrolCheckpointEntity?>(null)
    val activeMissedAlertPopup: StateFlow<PatrolCheckpointEntity?> = _activeMissedAlertPopup.asStateFlow()
    private val _dismissedAlertCheckpointIds = MutableStateFlow<Set<String>>(emptySet())

    // Navigation State
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Filter States
    private val _selectedTowerFilter = MutableStateFlow("ALL")
    val selectedTowerFilter: StateFlow<String> = _selectedTowerFilter.asStateFlow()

    private val _selectedParkingLevel = MutableStateFlow("P1")
    val selectedParkingLevel: StateFlow<String> = _selectedParkingLevel.asStateFlow()

    private val _auditSearchQuery = MutableStateFlow("")
    val auditSearchQuery: StateFlow<String> = _auditSearchQuery.asStateFlow()

    private val _auditModuleFilter = MutableStateFlow("ALL")
    val auditModuleFilter: StateFlow<String> = _auditModuleFilter.asStateFlow()

    // Notification Banner / Alert message
    private val _activeAlert = MutableStateFlow<String?>(null)
    val activeAlert: StateFlow<String?> = _activeAlert.asStateFlow()

    // Data streams from repository (dynamically reactive to currentFlatId)
    val allFlats = repository.allFlats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myFlat = _currentFlatId.flatMapLatest { repository.getFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myFamilyMembers = _currentFlatId.flatMapLatest { repository.getFamilyMembers(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myTenants = _currentFlatId.flatMapLatest { repository.getTenantsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTenants = repository.allTenants.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myVehicles = _currentFlatId.flatMapLatest { repository.getVehiclesForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allVehicles = repository.allVehicles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myPets = _currentFlatId.flatMapLatest { repository.getPetsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myDomesticHelp = _currentFlatId.flatMapLatest { repository.getDomesticHelpForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allComplaints = repository.allComplaints.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myComplaints = _currentFlatId.flatMapLatest { repository.getComplaintsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allVisitors = repository.allVisitors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myVisitors = _currentFlatId.flatMapLatest { repository.getVisitorsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAmenities = repository.allAmenities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAmenityBookings = repository.allAmenityBookings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myAmenityBookings = _currentFlatId.flatMapLatest { repository.getBookingsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMaintenanceBills = repository.allMaintenanceBills.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myMaintenanceBills = _currentFlatId.flatMapLatest { repository.getBillsForFlat(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotices = repository.allNotices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allParkingSlots = repository.allParkingSlots.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCommitteeTasks = repository.allCommitteeTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMeetings = repository.allMeetings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAuditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDocuments = repository.allDocuments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allServiceRequests = repository.allServiceRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val societyConfig = repository.societyConfig.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allVendors = repository.allVendors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val committeeDirectory = repository.committeeDirectory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRules = repository.allSocietyRules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFamilyMembersAcrossSociety = repository.allFamilyMembers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStaffAttendance = repository.allStaffAttendance.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPatrolCheckpoints = repository.allPatrolCheckpoints.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPatrolScanLogs = repository.allPatrolScanLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allInvitations = repository.allInvitations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allProfileCorrections = repository.allProfileCorrections.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allRentAgreementNotifications = repository.allRentAgreementNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val emergencyVolunteers = repository.emergencyVolunteers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMasterUnits = repository.allMasterUnits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allSystemUsers = repository.allSystemUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Task Completion Event & Notification Sound Settings
    data class TaskCompletionEvent(
        val taskId: String,
        val taskTitle: String,
        val category: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _taskCompletionEvent = MutableStateFlow<TaskCompletionEvent?>(null)
    val taskCompletionEvent: StateFlow<TaskCompletionEvent?> = _taskCompletionEvent.asStateFlow()

    fun dismissTaskCompletionEvent() {
        _taskCompletionEvent.value = null
    }

    fun clearTaskCompletionEvent() {
        _taskCompletionEvent.value = null
    }

    private val _isSoundNotificationEnabled = MutableStateFlow(true)
    val isSoundNotificationEnabled: StateFlow<Boolean> = _isSoundNotificationEnabled.asStateFlow()

    fun toggleSoundNotification(enabled: Boolean? = null) {
        _isSoundNotificationEnabled.value = enabled ?: !_isSoundNotificationEnabled.value
        _activeAlert.value = if (_isSoundNotificationEnabled.value) "Notification sounds turned ON" else "Notification sounds turned OFF"
    }

    val allChatMessages = repository.allChatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedChatChannel = MutableStateFlow("General Society")
    val selectedChatChannel: StateFlow<String> = _selectedChatChannel.asStateFlow()

    fun selectChatChannel(channel: String) {
        _selectedChatChannel.value = channel
    }

    fun sendChatMessage(messageText: String, channel: String = _selectedChatChannel.value) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))
            val newMsg = SocietyChatMessageEntity(
                senderName = _currentUserName.value,
                senderFlatId = _currentFlatId.value,
                senderRole = _currentRole.value.displayName,
                message = messageText.trim(),
                timestamp = "Today, $timeFormat",
                epochMillis = now,
                channel = channel,
                isAnnouncement = _currentRole.value.isCommitteeMember() && channel == "General Society"
            )
            repository.sendChatMessage(newMsg)
        }
    }

    init {
        viewModelScope.launch {
            repository.societyConfig.collect { config ->
                if (config != null) {
                    _currentAppTheme.value = AppTheme.fromId(config.appTheme)
                }
            }
        }
        viewModelScope.launch {
            repository.allPatrolCheckpoints.collect { checkpoints ->
                checkMissedPatrols(checkpoints)
            }
        }
        viewModelScope.launch {
            while (isActive) {
                checkMissedPatrols(allPatrolCheckpoints.value)
                delay(15000)
            }
        }
    }

    fun getComplaintComments(complaintId: String): Flow<List<ComplaintCommentEntity>> = repository.getComplaintComments(complaintId)

    fun setRole(role: UserRole) {
        _currentRole.value = role
        when (role) {
            UserRole.SUPER_ADMIN -> {
                _currentUserName.value = "Super Administrator"
                _currentFlatId.value = "Management Office"
            }
            UserRole.CHAIRMAN -> {
                _currentUserName.value = "Capt. Shailesh B. Kulkarni"
                _currentFlatId.value = "K-2401"
            }
            UserRole.SECRETARY -> {
                _currentUserName.value = "Anand Deshpande"
                _currentFlatId.value = "B1-1802"
            }
            UserRole.TREASURER -> {
                _currentUserName.value = "Pradeep Shenoy"
                _currentFlatId.value = "Z-1404"
            }
            UserRole.COMMITTEE_MEMBER -> {
                _currentUserName.value = "Sunil Patil (Committee Member)"
                _currentFlatId.value = "B2-901"
            }
            UserRole.RESIDENT_OWNER -> {
                _currentUserName.value = "Rahul Sharma"
                _currentFlatId.value = "K-302"
            }
            UserRole.FAMILY_MEMBER -> {
                _currentUserName.value = "Priya Sharma"
                _currentFlatId.value = "K-302"
            }
            UserRole.RESIDENT_TENANT -> {
                _currentUserName.value = "Suresh Mehta"
                _currentFlatId.value = "B1-402"
            }
            UserRole.SECURITY_INCHARGE -> {
                _currentUserName.value = "Inspector R. D. Shinde"
                _currentFlatId.value = "Main Security Control"
            }
            UserRole.SECURITY_GUARD -> {
                _currentUserName.value = "Head Guard Ram Singh"
                _currentFlatId.value = "Main Gate 1"
            }
            UserRole.HOUSEKEEPING_SUPERVISOR -> {
                _currentUserName.value = "Sunita Jadhav (HK Supervisor)"
                _currentFlatId.value = "Facility Management"
            }
            UserRole.HOUSEKEEPING_STAFF -> {
                _currentUserName.value = "Ramesh Pawar (HK Staff)"
                _currentFlatId.value = "Housekeeping Bay"
            }
            UserRole.SOCIETY_MANAGER -> {
                _currentUserName.value = "Sanjay Patil"
                _currentFlatId.value = "Society Office"
            }
        }
        checkMissedPatrols()
    }

    fun switchFlat(flatId: String, userName: String? = null) {
        _currentFlatId.value = flatId
        if (userName != null) {
            _currentUserName.value = userName
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setTowerFilter(tower: String) {
        _selectedTowerFilter.value = tower
    }

    fun setParkingLevel(level: String) {
        _selectedParkingLevel.value = level
    }

    fun setAuditQuery(q: String) {
        _auditSearchQuery.value = q
    }

    fun setAuditModule(m: String) {
        _auditModuleFilter.value = m
    }

    fun clearAlert() {
        _activeAlert.value = null
    }

    // Business Actions
    fun submitTenantRegistration(
        flatId: String,
        name: String,
        phone: String,
        email: String,
        permanentAddress: String,
        occupation: String,
        employer: String,
        moveInDate: String,
        moveOutDate: String,
        agreementDocName: String,
        startDate: String,
        expiryDate: String
    ) {
        viewModelScope.launch {
            // Check expiry calculation
            val tenant = TenantEntity(
                flatId = flatId,
                fullName = name,
                phone = phone,
                email = email,
                permanentAddress = permanentAddress,
                occupation = occupation,
                employer = employer,
                moveInDate = moveInDate,
                expectedMoveOutDate = moveOutDate,
                status = "Pending Verification",
                agreementDocument = agreementDocName,
                agreementStartDate = startDate,
                agreementExpiryDate = expiryDate,
                agreementStatus = "Active",
                verificationStatus = "Pending",
                verificationComments = "New tenant verification requested by owner."
            )
            repository.addTenant(tenant, _currentUserName.value, _currentRole.value.displayName)
            repository.updateFlatOccupancy(flatId, "Rented", _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Tenant registered! Verification sent to Society Manager."
        }
    }

    fun verifyTenant(tenantId: Long, decision: String, comment: String) {
        viewModelScope.launch {
            repository.verifyTenant(tenantId, decision, comment, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Tenant verification updated to: $decision"
        }
    }

    fun addFamilyMember(
        name: String,
        relationship: String,
        gender: String,
        dob: String,
        age: Int,
        phone: String,
        email: String,
        isEmergency: Boolean,
        isChild: Boolean,
        school: String,
        grade: String,
        photoUri: String = ""
    ) {
        viewModelScope.launch {
            val member = FamilyMemberEntity(
                flatId = _currentFlatId.value,
                fullName = name,
                relationship = relationship,
                gender = gender,
                dob = dob,
                calculatedAge = age,
                phone = phone,
                email = email,
                isEmergencyContact = isEmergency,
                isResident = true,
                isChild = isChild,
                schoolName = school,
                grade = grade,
                photoUri = photoUri
            )
            repository.addFamilyMember(member, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Family member $name added."
        }
    }

    fun removeFamilyMember(id: Long, name: String) {
        viewModelScope.launch {
            repository.removeFamilyMember(id, name, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Removed $name."
        }
    }

    fun addVehicle(
        type: String,
        makeModel: String,
        regNumber: String,
        color: String,
        fuel: String,
        isEv: Boolean,
        slot: String
    ) {
        viewModelScope.launch {
            val vehicle = VehicleEntity(
                flatId = _currentFlatId.value,
                tower = "Kaveh",
                vehicleType = type,
                makeModel = makeModel,
                registrationNumber = regNumber,
                color = color,
                fuelType = fuel,
                isEv = isEv,
                allottedSlot = slot
            )
            repository.addVehicle(vehicle, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Vehicle $regNumber registered."
        }
    }

    fun addPet(
        name: String,
        type: String,
        breed: String,
        gender: String,
        age: String,
        vaccinationExpiry: String,
        certDoc: String
    ) {
        viewModelScope.launch {
            val pet = PetEntity(
                flatId = _currentFlatId.value,
                petName = name,
                petType = type,
                breed = breed,
                gender = gender,
                age = age,
                registrationNumber = "PET-AA-2026-${(100..999).random()}",
                vaccinationStatus = "Valid",
                vaccinationExpiryDate = vaccinationExpiry,
                certificateDoc = certDoc
            )
            repository.addPet(pet, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Pet '$name' registered successfully!"
        }
    }

    fun addDomesticHelp(name: String, category: String, phone: String) {
        viewModelScope.launch {
            val help = DomesticHelpEntity(
                flatId = _currentFlatId.value,
                name = name,
                category = category,
                phone = phone,
                passId = "DH-${(200..999).random()}",
                validity = "31-Dec-2026"
            )
            repository.addDomesticHelp(help, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Domestic help $name registered with Pass ${help.passId}."
        }
    }

    fun reportComplaint(
        category: String,
        subcategory: String,
        tower: String,
        floor: Int,
        location: String,
        description: String,
        priority: String,
        photoPlaceholder: String = "",
        visibility: String = "All Authorised Members of This Flat"
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val randNum = (1000..9999).random()
            val cid = "CMP-2026-$randNum"
            val complaint = ComplaintEntity(
                id = cid,
                ticketIdFormatted = cid,
                category = category,
                subcategory = subcategory,
                tower = tower,
                floor = floor,
                location = location,
                description = description,
                photoUri = photoPlaceholder,
                priority = priority,
                status = "Submitted",
                submittedBy = _currentFlatId.value,
                raisedByMemberName = _currentUserName.value,
                raisedByMemberType = _currentRole.value.displayName,
                visibility = visibility,
                createdAt = sdf.format(Date()),
                slaHours = if (priority == "Critical") 2 else if (priority == "High") 6 else 24
            )
            repository.createComplaint(complaint, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Ticket $cid submitted successfully!"
        }
    }

    // Invitations
    fun createInvitation(
        targetFlat: String,
        targetTower: String,
        assignedRole: String,
        recipientName: String,
        recipientPhone: String,
        validityHours: Int = 48
    ) {
        viewModelScope.launch {
            val invite = repository.createInvitation(
                targetFlat = targetFlat,
                targetTower = targetTower,
                assignedRole = assignedRole,
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                createdBy = _currentUserName.value,
                validityHours = validityHours
            )
            _activeAlert.value = "Secure invitation ${invite.inviteCode} generated for $recipientName."
        }
    }

    fun revokeInvitation(inviteCode: String) {
        viewModelScope.launch {
            repository.revokeInvitation(inviteCode, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Invitation $inviteCode revoked."
        }
    }

    // Profile Correction Requests (Controlled workflow for flat owners)
    fun submitProfileCorrection(
        fieldToChange: String,
        currentValue: String,
        proposedValue: String,
        reason: String
    ) {
        viewModelScope.launch {
            repository.submitProfileCorrectionRequest(
                flatId = _currentFlatId.value,
                ownerName = _currentUserName.value,
                requestedBy = _currentUserName.value,
                fieldToChange = fieldToChange,
                currentVal = currentValue,
                proposedVal = proposedValue,
                reason = reason
            )
            _activeAlert.value = "Correction request for $fieldToChange submitted to Super Admin."
        }
    }

    fun reviewProfileCorrection(id: Long, isApproved: Boolean, reviewNotes: String) {
        viewModelScope.launch {
            repository.reviewProfileCorrectionRequest(id, isApproved, _currentUserName.value, reviewNotes)
            _activeAlert.value = "Profile correction request #${id} ${if (isApproved) "Approved" else "Rejected"}."
        }
    }

    fun markRentNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markRentNotificationRead(id)
        }
    }

    fun updateOwnerProfessionalAndVolunteer(
        occupation: String,
        industry: String,
        company: String,
        skills: String,
        howHelp: String,
        volunteer: String,
        volunteerAreas: String
    ) {
        viewModelScope.launch {
            repository.updateFlatProfessionalAndVolunteer(
                flatId = _currentFlatId.value,
                occupation = occupation,
                industry = industry,
                company = company,
                skills = skills,
                howHelp = howHelp,
                volunteer = volunteer,
                volunteerAreas = volunteerAreas,
                currentUser = _currentUserName.value,
                userRole = _currentRole.value.displayName
            )
            _activeAlert.value = "Professional details & volunteer preferences saved."
        }
    }

    fun updateComplaintStatus(complaintId: String, newStatus: String, assignedTo: String, note: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, newStatus, assignedTo, note, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Complaint $complaintId marked as $newStatus."
        }
    }

    fun reopenComplaint(complaintId: String, reason: String) {
        viewModelScope.launch {
            repository.reopenComplaint(complaintId, reason, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Complaint $complaintId reopened."
        }
    }

    fun addComplaintComment(complaintId: String, comment: String) {
        viewModelScope.launch {
            repository.addComplaintComment(complaintId, comment, _currentUserName.value, _currentRole.value.displayName)
        }
    }

    fun createVisitorPass(
        name: String,
        phone: String,
        type: String,
        company: String,
        expectedTime: String
    ) {
        viewModelScope.launch {
            val pass = "AA-${(1000..9999).random()}"
            val vid = "VIS-${(9300..9999).random()}"
            val visitor = VisitorEntity(
                id = vid,
                passCode = pass,
                visitorName = name,
                phone = phone,
                flat = _currentFlatId.value,
                tower = myFlat.value?.tower ?: "Kaveh",
                type = type,
                company = company,
                expectedArrival = expectedTime,
                status = "Approved"
            )
            repository.createVisitorPass(visitor, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Visitor Pass created! Pass Code: $pass"
        }
    }

    fun updateVisitorGateStatus(visitorId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateVisitorStatus(visitorId, newStatus, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Visitor status updated to $newStatus"
        }
    }

    fun bookAmenity(amenityId: String, amenityName: String, date: String, slot: String, fee: Int) {
        viewModelScope.launch {
            val bid = "BK-${(850..999).random()}"
            val booking = AmenityBookingEntity(
                id = bid,
                amenityId = amenityId,
                amenityName = amenityName,
                flat = _currentFlatId.value,
                residentName = _currentUserName.value,
                bookingDate = date,
                timeSlot = slot,
                status = "Confirmed",
                feePaid = fee
            )
            repository.bookAmenity(booking, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "$amenityName booked for $date ($slot)!"
        }
    }

    fun payMaintenanceBill(billId: String, method: String) {
        viewModelScope.launch {
            repository.payMaintenanceBill(billId, method, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Payment successful via $method! Receipt generated."
        }
    }

    fun updateParkingSlot(slot: ParkingSlotEntity) {
        viewModelScope.launch {
            repository.updateParkingSlot(slot, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Parking slot ${slot.slotNumber} updated."
        }
    }

    fun createServiceRequest(type: String, details: String, contractor: String, workers: Int) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val srid = "SR-${(150..999).random()}"
            val req = ServiceRequestEntity(
                id = srid,
                type = type,
                flat = _currentFlatId.value,
                tower = "Kaveh",
                details = details,
                contractorName = contractor,
                workerCount = workers,
                status = "Pending",
                createdAt = sdf.format(Date())
            )
            repository.createServiceRequest(req, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Service Request $srid ($type) submitted."
        }
    }

    fun updateServiceRequestStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateServiceRequestStatus(id, status, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Service request $id $status."
        }
    }

    fun updateTaskStatus(taskId: String, status: String) {
        viewModelScope.launch {
            val taskList = allCommitteeTasks.value
            val task = taskList.find { it.id == taskId }
            val taskTitle = task?.title ?: taskId
            val category = task?.category ?: "General"

            repository.updateCommitteeTaskStatus(taskId, status, _currentUserName.value, _currentRole.value.displayName)

            if (status.equals("Completed", ignoreCase = true)) {
                if (_isSoundNotificationEnabled.value) {
                    com.example.ui.util.SoundNotificationHelper.playTaskCompletionSound(getApplication(), true)
                }
                _taskCompletionEvent.value = TaskCompletionEvent(
                    taskId = taskId,
                    taskTitle = taskTitle,
                    category = category
                )
                _activeAlert.value = "✓ Task '$taskTitle' completed successfully!"
            } else {
                _activeAlert.value = "Task $taskId updated to $status."
            }
        }
    }

    // Super Admin Master & User Management
    fun addMasterUnit(master: MasterUnitEntity) {
        viewModelScope.launch {
            repository.addMasterUnit(master, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Master '${master.masterName}' created successfully."
        }
    }

    fun addMasterUnit(
        name: String,
        type: String,
        headOfMaster: String,
        phone: String,
        email: String,
        assignedUnit: String,
        maxUsers: Int = 10,
        notes: String = ""
    ) {
        val rand = (100..999).random()
        val masterId = "MST-${assignedUnit.replace(" ", "").replace("-", "").ifEmpty { "UNIT" }}-$rand"
        val master = MasterUnitEntity(
            masterId = masterId,
            masterName = name,
            masterType = type,
            headOfMaster = headOfMaster,
            contactPhone = phone,
            contactEmail = email,
            assignedUnit = assignedUnit,
            status = "Active",
            maxUsersAllowed = maxUsers,
            notes = notes,
            createdDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        )
        addMasterUnit(master)
    }

    fun updateMasterUnit(master: MasterUnitEntity) {
        viewModelScope.launch {
            repository.updateMasterUnit(master, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Master '${master.masterName}' updated."
        }
    }

    fun setMasterStatus(masterId: String, status: String) {
        viewModelScope.launch {
            repository.setMasterStatus(masterId, status, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Master $masterId status updated to $status."
        }
    }

    fun setMasterUnitActive(masterId: String, isActive: Boolean) {
        setMasterStatus(masterId, if (isActive) "Active" else "Deactivated")
    }

    fun deleteMasterUnit(masterId: String) {
        viewModelScope.launch {
            repository.deleteMasterUnit(masterId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Master $masterId removed."
        }
    }

    fun addSystemUser(user: SystemUserEntity) {
        viewModelScope.launch {
            repository.addSystemUser(user, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "User '${user.fullName}' added and linked to ${user.linkedMasterName}."
        }
    }

    fun addSystemUser(
        name: String,
        phone: String,
        email: String,
        role: String,
        masterId: String,
        flatNumber: String,
        permissions: String = "Standard Household Access"
    ) {
        val rand = (1000..9999).random()
        val userId = "USR-$rand"
        val master = allMasterUnits.value.find { it.masterId == masterId }
        val masterName = master?.masterName ?: "Master Unit $masterId"
        val user = SystemUserEntity(
            userId = userId,
            fullName = name,
            email = email,
            phone = phone,
            roleName = role,
            linkedMasterId = masterId,
            linkedMasterName = masterName,
            status = "Active",
            permissionsSummary = permissions,
            flatId = flatNumber
        )
        addSystemUser(user)
    }

    fun reassignUserMaster(userId: String, newMasterId: String, newMasterName: String) {
        viewModelScope.launch {
            repository.reassignUserMaster(userId, newMasterId, newMasterName, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "User $userId reassigned to Master $newMasterName."
        }
    }

    fun reassignUserMaster(userId: String, newMasterId: String) {
        val master = allMasterUnits.value.find { it.masterId == newMasterId }
        val newMasterName = master?.masterName ?: "Master Unit $newMasterId"
        reassignUserMaster(userId, newMasterId, newMasterName)
    }

    fun updateUserRoleAndPermissions(userId: String, newRole: String, permissions: String) {
        viewModelScope.launch {
            repository.updateUserRoleAndPermissions(userId, newRole, permissions, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Updated role & permissions for user $userId."
        }
    }

    fun setUserStatus(userId: String, status: String) {
        viewModelScope.launch {
            repository.setUserStatus(userId, status, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "User $userId status set to $status."
        }
    }

    fun updateUserStatus(userId: String, status: String) {
        setUserStatus(userId, status)
    }

    fun deleteSystemUser(userId: String) {
        viewModelScope.launch {
            repository.deleteSystemUser(userId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "User $userId removed."
        }
    }


    fun publishNotice(title: String, content: String, category: String, tower: String, priority: String, circularNo: String = "", isPinned: Boolean = false) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val nid = "NOT-${(105..999).random()}"
            val cNo = if (circularNo.isNotBlank()) circularNo else "CIR/2026/${(100..999).random()}"
            val notice = SocietyNoticeEntity(
                id = nid,
                title = title,
                content = content,
                category = category,
                targetTower = tower,
                priority = priority,
                publishedBy = _currentUserName.value,
                publishDate = sdf.format(Date()),
                isPinned = isPinned,
                circularNo = cNo,
                acknowledgedCount = 0
            )
            repository.createNotice(notice, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Notice '$title' published to $tower residents."
        }
    }

    fun amendNotice(notice: SocietyNoticeEntity) {
        viewModelScope.launch {
            repository.amendNotice(notice, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Notice '${notice.title}' amended."
        }
    }

    fun togglePinNotice(noticeId: String) {
        viewModelScope.launch {
            repository.togglePinNotice(noticeId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Notice pin status toggled."
        }
    }

    fun deleteNotice(noticeId: String) {
        viewModelScope.launch {
            repository.deleteNotice(noticeId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Notice $noticeId deleted."
        }
    }

    fun acknowledgeNotice(noticeId: String) {
        viewModelScope.launch {
            repository.acknowledgeNotice(noticeId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Notice acknowledged."
        }
    }

    // Master Data Operations (Amendable by Committee Members)
    fun amendSocietyConfig(config: SocietyConfigEntity) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val updated = config.copy(
                lastAmendedBy = "${_currentUserName.value} (${_currentRole.value.displayName})",
                lastAmendedDate = sdf.format(Date())
            )
            repository.amendSocietyConfig(updated, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Society Master Config & Tariffs amended successfully."
        }
    }

    fun amendFlat(flat: FlatEntity) {
        viewModelScope.launch {
            repository.amendFlat(flat, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Flat ${flat.flatId} master record updated."
        }
    }

    fun addFlat(flat: FlatEntity) {
        viewModelScope.launch {
            repository.addFlat(flat, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Flat ${flat.flatId} added to registry."
        }
    }

    fun deleteFlat(flatId: String) {
        viewModelScope.launch {
            repository.deleteFlat(flatId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Flat $flatId removed from registry."
        }
    }

    fun amendParkingSlot(slot: ParkingSlotEntity) {
        viewModelScope.launch {
            repository.amendParkingSlot(slot, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Parking slot ${slot.slotNumber} amended."
        }
    }

    fun amendAmenity(amenity: AmenityEntity) {
        viewModelScope.launch {
            repository.amendAmenity(amenity, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Amenity ${amenity.name} amended."
        }
    }

    fun amendVendor(vendor: VendorMasterEntity) {
        viewModelScope.launch {
            repository.amendVendor(vendor, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Vendor ${vendor.agencyName} contract amended."
        }
    }

    fun addVendor(vendor: VendorMasterEntity) {
        viewModelScope.launch {
            repository.addVendor(vendor, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Vendor ${vendor.agencyName} registered."
        }
    }

    fun deleteVendor(vendorId: String) {
        viewModelScope.launch {
            repository.deleteVendor(vendorId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Vendor record $vendorId removed."
        }
    }

    fun amendCommitteeMember(member: CommitteeMasterEntity) {
        viewModelScope.launch {
            repository.amendCommitteeMember(member, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Committee member ${member.fullName} amended."
        }
    }

    fun addCommitteeMember(member: CommitteeMasterEntity) {
        viewModelScope.launch {
            repository.addCommitteeMember(member, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "${member.fullName} added to Committee Directory."
        }
    }

    fun deleteCommitteeMember(id: String) {
        viewModelScope.launch {
            repository.deleteCommitteeMember(id, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Committee member $id removed."
        }
    }

    // Theme Management (Super Admin)
    fun setAppTheme(theme: AppTheme) {
        _currentAppTheme.value = theme
        viewModelScope.launch {
            repository.updateAppTheme(theme.id, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Active Theme set to ${theme.displayName}"
        }
    }

    // Rules & Bylaws Operations (Super Admin & Committee)
    fun addSocietyRule(
        category: String,
        title: String,
        description: String,
        penalty: Int,
        isMandatory: Boolean
    ) {
        viewModelScope.launch {
            val rule = SocietyRuleEntity(
                category = category,
                ruleTitle = title,
                ruleDescription = description,
                penaltyAmount = penalty,
                isMandatory = isMandatory,
                displayOrder = (_allRulesCount() + 1),
                lastUpdated = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            )
            repository.addSocietyRule(rule, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "New society rule added: $title"
        }
    }

    private fun _allRulesCount(): Int = allRules.value.size

    fun amendSocietyRule(rule: SocietyRuleEntity) {
        viewModelScope.launch {
            val updated = rule.copy(
                lastUpdated = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            )
            repository.updateSocietyRule(updated, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Rule '${rule.ruleTitle}' amended successfully."
        }
    }

    fun deleteSocietyRule(id: Long, title: String) {
        viewModelScope.launch {
            repository.deleteSocietyRule(id, title, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Rule '$title' deleted."
        }
    }

    // Tenant and Member Administration (Super Admin)
    fun addTenant(tenant: TenantEntity) {
        viewModelScope.launch {
            repository.addTenant(tenant, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Tenant ${tenant.fullName} registered for Flat ${tenant.flatId}."
        }
    }

    fun amendTenant(tenant: TenantEntity) {
        viewModelScope.launch {
            repository.updateTenant(tenant, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Tenant record for ${tenant.fullName} updated."
        }
    }

    fun addFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            repository.addFamilyMember(member, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Family member ${member.fullName} added to Flat ${member.flatId}."
        }
    }

    fun updateFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            repository.updateFamilyMember(member, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Family member ${member.fullName} updated."
        }
    }

    fun deleteFamilyMember(id: Long, name: String) {
        viewModelScope.launch {
            repository.removeFamilyMember(id, name, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Family member $name removed."
        }
    }

    // ==========================================
    // Guard Patrol & Hourly QR Checkpoints
    // ==========================================

    fun checkMissedPatrols(checkpoints: List<PatrolCheckpointEntity> = allPatrolCheckpoints.value) {
        val now = System.currentTimeMillis()
        val overdueCheckpoint = checkpoints.firstOrNull { cp ->
            val elapsed = now - cp.lastScanEpochMillis
            val isHourOverdue = elapsed > (cp.scanIntervalMinutes * 60 * 1000L)
            (cp.isMissedAlertActive || isHourOverdue) &&
            !cp.missedAlertAcknowledged &&
            !_dismissedAlertCheckpointIds.value.contains(cp.checkpointId)
        }
        if (_currentRole.value.isCommitteeMember()) {
            _activeMissedAlertPopup.value = overdueCheckpoint
        } else {
            _activeMissedAlertPopup.value = null
        }
    }

    fun dismissMissedAlertPopup(checkpointId: String? = null) {
        if (checkpointId != null) {
            _dismissedAlertCheckpointIds.value = _dismissedAlertCheckpointIds.value + checkpointId
        }
        _activeMissedAlertPopup.value = null
    }

    fun acknowledgeMissedAlert(checkpointId: String, resolutionNotes: String = "Acknowledged by Committee Member on duty.") {
        viewModelScope.launch {
            repository.acknowledgeMissedScanAlert(
                checkpointId = checkpointId,
                acknowledgedBy = _currentUserName.value,
                userRole = _currentRole.value.displayName,
                resolutionNotes = resolutionNotes
            )
            _activeAlert.value = "Missed patrol alert for $checkpointId resolved and acknowledged."
            _activeMissedAlertPopup.value = null
        }
    }

    fun simulateMissedScan(checkpointId: String) {
        viewModelScope.launch {
            _dismissedAlertCheckpointIds.value = _dismissedAlertCheckpointIds.value - checkpointId
            repository.simulateMissedScan(checkpointId, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Simulated missed 1-hour patrol at $checkpointId."
            checkMissedPatrols()
        }
    }

    fun scanPatrolCheckpoint(checkpointId: String, notes: String = "") {
        viewModelScope.launch {
            _dismissedAlertCheckpointIds.value = _dismissedAlertCheckpointIds.value - checkpointId
            repository.scanPatrolCheckpoint(
                checkpointId = checkpointId,
                guardName = _currentUserName.value,
                guardRole = _currentRole.value.displayName,
                notes = notes
            )
            _activeAlert.value = "QR Checkpoint $checkpointId scanned successfully. Next scan due in 60 mins."
            _activeMissedAlertPopup.value = null
        }
    }

    // ==========================================
    // Staff Attendance with Geo Location & Selfie
    // ==========================================

    fun recordStaffPunch(
        staffId: String,
        staffName: String,
        staffRole: String,
        punchType: String,
        latitude: Double,
        longitude: Double,
        locationAddress: String,
        isGeoFenceVerified: Boolean,
        photoSource: String,
        photoUri: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val timestamp = sdf.format(Date(now))

            val record = StaffAttendanceEntity(
                staffId = staffId,
                staffName = staffName,
                staffRole = staffRole,
                punchType = punchType,
                timestamp = timestamp,
                epochMillis = now,
                latitude = latitude,
                longitude = longitude,
                locationAddress = locationAddress,
                isGeoFenceVerified = isGeoFenceVerified,
                photoSource = photoSource,
                photoUri = photoUri,
                remarks = remarks
            )
            repository.recordStaffAttendance(record, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "$punchType recorded for $staffName ($staffRole) with geo-location & $photoSource photo."
        }
    }
}
