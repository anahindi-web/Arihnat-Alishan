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

    private val _currentFlatId = MutableStateFlow("K-1204")
    val currentFlatId: StateFlow<String> = _currentFlatId.asStateFlow()

    private val _currentUserName = MutableStateFlow("Rajesh Sharma")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // Application Theme State
    private val _currentAppTheme = MutableStateFlow(AppTheme.ROYAL_GOLD)
    val currentAppTheme: StateFlow<AppTheme> = _currentAppTheme.asStateFlow()

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

    // Data streams from repository
    val allFlats = repository.allFlats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myFlat = repository.getFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val myFamilyMembers = repository.getFamilyMembers("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myTenants = repository.getTenantsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTenants = repository.allTenants.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myVehicles = repository.getVehiclesForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allVehicles = repository.allVehicles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myPets = repository.getPetsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myDomesticHelp = repository.getDomesticHelpForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allComplaints = repository.allComplaints.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myComplaints = repository.getComplaintsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allVisitors = repository.allVisitors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myVisitors = repository.getVisitorsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAmenities = repository.allAmenities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allAmenityBookings = repository.allAmenityBookings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myAmenityBookings = repository.getBookingsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMaintenanceBills = repository.allMaintenanceBills.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myMaintenanceBills = repository.getBillsForFlat("K-1204").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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
                _currentFlatId.value = "System Root (Admin)"
            }
            UserRole.RESIDENT_OWNER -> {
                _currentUserName.value = "Rajesh Sharma"
                _currentFlatId.value = "K-1204"
            }
            UserRole.RESIDENT_TENANT -> {
                _currentUserName.value = "Rohan Mehta"
                _currentFlatId.value = "K-1204"
            }
            UserRole.SOCIETY_MANAGER -> {
                _currentUserName.value = "Sanjay Patil"
                _currentFlatId.value = "Society Office"
            }
            UserRole.CHAIRMAN -> {
                _currentUserName.value = "Shailesh B. Kulkarni"
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
                _currentUserName.value = "Sunil Patil (Parking Head)"
                _currentFlatId.value = "B2-901"
            }
            UserRole.SECURITY_GUARD -> {
                _currentUserName.value = "Head Guard Ram Singh"
                _currentFlatId.value = "Main Gate 1"
            }
            UserRole.MAINTENANCE_STAFF -> {
                _currentUserName.value = "Prakash Nair (Supervisor)"
                _currentFlatId.value = "Engineering Unit"
            }
        }
        checkMissedPatrols()
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
        grade: String
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
                grade = grade
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
        photoPlaceholder: String = ""
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val cid = "AA-${(1030..9999).random()}"
            val complaint = ComplaintEntity(
                id = cid,
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
                createdAt = sdf.format(Date()),
                slaHours = if (priority == "Critical") 2 else if (priority == "High") 6 else 24
            )
            repository.createComplaint(complaint, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Complaint $cid submitted successfully!"
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
                tower = "Kaveh",
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
            repository.updateCommitteeTaskStatus(taskId, status, _currentUserName.value, _currentRole.value.displayName)
            _activeAlert.value = "Task $taskId updated to $status."
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
