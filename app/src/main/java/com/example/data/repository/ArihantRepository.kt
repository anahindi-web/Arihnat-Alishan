package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ArihantRepository(private val appDao: AppDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    // Flows
    val allFlats: Flow<List<FlatEntity>> = appDao.getAllFlats()
    val allComplaints: Flow<List<ComplaintEntity>> = appDao.getAllComplaints()
    val allVisitors: Flow<List<VisitorEntity>> = appDao.getAllVisitors()
    val allAmenities: Flow<List<AmenityEntity>> = appDao.getAllAmenities()
    val allAmenityBookings: Flow<List<AmenityBookingEntity>> = appDao.getAllAmenityBookings()
    val allMaintenanceBills: Flow<List<MaintenanceBillEntity>> = appDao.getAllMaintenanceBills()
    val allNotices: Flow<List<SocietyNoticeEntity>> = appDao.getAllNotices()
    val allParkingSlots: Flow<List<ParkingSlotEntity>> = appDao.getAllParkingSlots()
    val allCommitteeTasks: Flow<List<CommitteeTaskEntity>> = appDao.getAllCommitteeTasks()
    val allMeetings: Flow<List<SocietyMeetingEntity>> = appDao.getAllMeetings()
    val allAuditLogs: Flow<List<AuditLogEntity>> = appDao.getAllAuditLogs()
    val allDocuments: Flow<List<SocietyDocumentEntity>> = appDao.getAllDocuments()
    val allServiceRequests: Flow<List<ServiceRequestEntity>> = appDao.getAllServiceRequests()
    val allTenants: Flow<List<TenantEntity>> = appDao.getAllTenants()
    val allVehicles: Flow<List<VehicleEntity>> = appDao.getAllVehicles()
    val allPets: Flow<List<PetEntity>> = appDao.getAllPets()
    val allDomesticHelp: Flow<List<DomesticHelpEntity>> = appDao.getAllDomesticHelp()
    val societyConfig: Flow<SocietyConfigEntity?> = appDao.getSocietyConfig()
    val allVendors: Flow<List<VendorMasterEntity>> = appDao.getAllVendors()
    val committeeDirectory: Flow<List<CommitteeMasterEntity>> = appDao.getCommitteeDirectory()
    val allSocietyRules: Flow<List<SocietyRuleEntity>> = appDao.getAllSocietyRules()
    val allFamilyMembers: Flow<List<FamilyMemberEntity>> = appDao.getAllFamilyMembers()
    val allStaffAttendance: Flow<List<StaffAttendanceEntity>> = appDao.getAllStaffAttendance()
    val allPatrolCheckpoints: Flow<List<PatrolCheckpointEntity>> = appDao.getAllPatrolCheckpoints()
    val allPatrolScanLogs: Flow<List<PatrolScanLogEntity>> = appDao.getAllPatrolScanLogs()

    fun getFlat(flatId: String): Flow<FlatEntity?> = appDao.getFlatById(flatId)
    fun getFamilyMembers(flatId: String): Flow<List<FamilyMemberEntity>> = appDao.getFamilyMembers(flatId)
    fun getTenantsForFlat(flatId: String): Flow<List<TenantEntity>> = appDao.getTenantsForFlat(flatId)
    fun getVehiclesForFlat(flatId: String): Flow<List<VehicleEntity>> = appDao.getVehiclesForFlat(flatId)
    fun getPetsForFlat(flatId: String): Flow<List<PetEntity>> = appDao.getPetsForFlat(flatId)
    fun getDomesticHelpForFlat(flatId: String): Flow<List<DomesticHelpEntity>> = appDao.getDomesticHelpForFlat(flatId)
    fun getComplaintsForFlat(flatId: String): Flow<List<ComplaintEntity>> = appDao.getComplaintsForFlat(flatId)
    fun getComplaintComments(complaintId: String): Flow<List<ComplaintCommentEntity>> = appDao.getCommentsForComplaint(complaintId)
    fun getParkingSlotsByLevel(level: String): Flow<List<ParkingSlotEntity>> = appDao.getParkingSlotsByLevel(level)
    fun getBookingsForFlat(flat: String): Flow<List<AmenityBookingEntity>> = appDao.getBookingsForFlat(flat)
    fun getBillsForFlat(flat: String): Flow<List<MaintenanceBillEntity>> = appDao.getMaintenanceBillsForFlat(flat)
    fun getVisitorsForFlat(flat: String): Flow<List<VisitorEntity>> = appDao.getVisitorsForFlat(flat)

    // Actions & Business Rules
    suspend fun updateFlatOccupancy(flatId: String, newStatus: String, currentUser: String, userRole: String) {
        val flat = appDao.getFlatById(flatId).firstOrNull() ?: return
        val updated = flat.copy(occupancyStatus = newStatus)
        appDao.updateFlat(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_OCCUPANCY",
            module = "Flat",
            record = flatId,
            details = "Changed occupancy status of $flatId to $newStatus",
            oldVal = flat.occupancyStatus,
            newVal = newStatus
        )
    }

    suspend fun addTenant(tenant: TenantEntity, currentUser: String, userRole: String) {
        appDao.insertTenant(tenant)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "SUBMIT_TENANT_VERIFICATION",
            module = "Tenant",
            record = tenant.fullName,
            details = "Submitted tenant verification for Flat ${tenant.flatId} with Rent Agreement"
        )
    }

    suspend fun verifyTenant(tenantId: Long, status: String, comments: String, currentUser: String, userRole: String) {
        val tenants = appDao.getAllTenants().firstOrNull() ?: emptyList()
        val tenant = tenants.find { it.id == tenantId } ?: return
        val updated = tenant.copy(
            verificationStatus = status,
            verificationComments = comments,
            status = if (status == "Verified") "Active" else if (status == "Rejected") "Rejected" else "Pending Verification"
        )
        appDao.updateTenant(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "VERIFY_TENANT",
            module = "Tenant",
            record = tenant.fullName,
            details = "Decision: $status. Comments: $comments",
            oldVal = tenant.verificationStatus,
            newVal = status
        )
    }

    suspend fun addFamilyMember(member: FamilyMemberEntity, currentUser: String, userRole: String) {
        appDao.insertFamilyMember(member)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_FAMILY_MEMBER",
            module = "Resident",
            record = member.fullName,
            details = "Added ${member.relationship}: ${member.fullName} (Age ${member.calculatedAge})"
        )
    }

    suspend fun removeFamilyMember(id: Long, name: String, currentUser: String, userRole: String) {
        appDao.deleteFamilyMember(id)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "REMOVE_FAMILY_MEMBER",
            module = "Resident",
            record = name,
            details = "Removed family member $name"
        )
    }

    suspend fun addVehicle(vehicle: VehicleEntity, currentUser: String, userRole: String) {
        appDao.insertVehicle(vehicle)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_VEHICLE",
            module = "Vehicle",
            record = vehicle.registrationNumber,
            details = "Registered ${vehicle.vehicleType}: ${vehicle.makeModel} (${vehicle.registrationNumber})"
        )
    }

    suspend fun addPet(pet: PetEntity, currentUser: String, userRole: String) {
        appDao.insertPet(pet)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "REGISTER_PET",
            module = "Pet",
            record = pet.petName,
            details = "Registered ${pet.petType} '${pet.petName}' (${pet.breed}) with vaccination valid till ${pet.vaccinationExpiryDate}"
        )
    }

    suspend fun addDomesticHelp(help: DomesticHelpEntity, currentUser: String, userRole: String) {
        appDao.insertDomesticHelp(help)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "REGISTER_STAFF",
            module = "Domestic Help",
            record = help.name,
            details = "Registered ${help.category}: ${help.name} (Pass: ${help.passId})"
        )
    }

    suspend fun updateParkingSlot(slot: ParkingSlotEntity, currentUser: String, userRole: String) {
        appDao.updateParkingSlot(slot)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_PARKING_SLOT",
            module = "Parking",
            record = slot.slotNumber,
            details = "Slot ${slot.slotNumber} status set to ${slot.status}, assigned to ${slot.allottedFlat}"
        )
    }

    suspend fun createComplaint(complaint: ComplaintEntity, currentUser: String, userRole: String) {
        appDao.insertComplaint(complaint)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "CREATE_COMPLAINT",
            module = "Complaint",
            record = complaint.id,
            details = "Reported [${complaint.category}] at ${complaint.tower} - ${complaint.location}, Priority: ${complaint.priority}"
        )
    }

    suspend fun updateComplaintStatus(
        complaintId: String,
        newStatus: String,
        assignedTo: String,
        note: String,
        currentUser: String,
        userRole: String
    ) {
        val complaint = appDao.getComplaintById(complaintId).firstOrNull() ?: return
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val now = sdf.format(Date())
        val updated = complaint.copy(
            status = newStatus,
            assignedTo = if (assignedTo.isNotBlank()) assignedTo else complaint.assignedTo,
            resolvedAt = if (newStatus == "Resolved" || newStatus == "Closed") now else complaint.resolvedAt
        )
        appDao.updateComplaint(updated)
        if (note.isNotBlank()) {
            appDao.insertComplaintComment(
                ComplaintCommentEntity(
                    complaintId = complaintId,
                    authorName = currentUser,
                    authorRole = userRole,
                    commentText = note,
                    timestamp = now
                )
            )
        }
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_COMPLAINT_STATUS",
            module = "Complaint",
            record = complaintId,
            details = "Status changed to $newStatus. Assigned to: ${updated.assignedTo}. Note: $note",
            oldVal = complaint.status,
            newVal = newStatus
        )
    }

    suspend fun addComplaintComment(complaintId: String, comment: String, currentUser: String, userRole: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val now = sdf.format(Date())
        appDao.insertComplaintComment(
            ComplaintCommentEntity(
                complaintId = complaintId,
                authorName = currentUser,
                authorRole = userRole,
                commentText = comment,
                timestamp = now
            )
        )
    }

    suspend fun reopenComplaint(complaintId: String, reason: String, currentUser: String, userRole: String) {
        val complaint = appDao.getComplaintById(complaintId).firstOrNull() ?: return
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val now = sdf.format(Date())
        val updated = complaint.copy(
            status = "In Progress",
            reopenedCount = complaint.reopenedCount + 1
        )
        appDao.updateComplaint(updated)
        appDao.insertComplaintComment(
            ComplaintCommentEntity(
                complaintId = complaintId,
                authorName = currentUser,
                authorRole = userRole,
                commentText = "REOPENED: $reason",
                timestamp = now
            )
        )
        logAudit(
            user = currentUser,
            role = userRole,
            action = "REOPEN_COMPLAINT",
            module = "Complaint",
            record = complaintId,
            details = "Complaint reopened by resident. Reason: $reason"
        )
    }

    suspend fun createVisitorPass(visitor: VisitorEntity, currentUser: String, userRole: String) {
        appDao.insertVisitor(visitor)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "CREATE_VISITOR_PASS",
            module = "Visitor",
            record = visitor.visitorName,
            details = "Generated ${visitor.type} Pass [${visitor.passCode}] for ${visitor.visitorName} visiting ${visitor.flat}"
        )
    }

    suspend fun updateVisitorStatus(visitorId: String, newStatus: String, currentUser: String, userRole: String) {
        val visitors = appDao.getAllVisitors().firstOrNull() ?: emptyList()
        val visitor = visitors.find { it.id == visitorId } ?: return
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeNow = sdf.format(Date())
        val updated = visitor.copy(
            status = newStatus,
            inTime = if (newStatus == "Inside" && visitor.inTime.isBlank()) timeNow else visitor.inTime,
            outTime = if (newStatus == "Exited") timeNow else visitor.outTime
        )
        appDao.updateVisitor(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "VISITOR_$newStatus".uppercase(),
            module = "Security Gate",
            record = visitor.visitorName,
            details = "Visitor ${visitor.visitorName} marked as $newStatus at Gate"
        )
    }

    suspend fun bookAmenity(booking: AmenityBookingEntity, currentUser: String, userRole: String) {
        appDao.insertAmenityBooking(booking)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "BOOK_AMENITY",
            module = "Amenity",
            record = booking.amenityName,
            details = "Booked ${booking.amenityName} for ${booking.bookingDate} at ${booking.timeSlot} by ${booking.flat}"
        )
    }

    suspend fun payMaintenanceBill(billId: String, paymentMethod: String, currentUser: String, userRole: String) {
        val bills = appDao.getAllMaintenanceBills().firstOrNull() ?: emptyList()
        val bill = bills.find { it.id == billId } ?: return
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val receipt = "REC-AA-${(1000..9999).random()}"
        val updated = bill.copy(
            status = "Paid",
            paymentMethod = paymentMethod,
            paidAt = sdf.format(Date()),
            receiptNumber = receipt
        )
        appDao.updateMaintenanceBill(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "PAY_MAINTENANCE",
            module = "Finance",
            record = bill.id,
            details = "Paid maintenance bill of ₹${bill.totalAmount} for ${bill.flat} via $paymentMethod. Receipt: $receipt"
        )
    }

    suspend fun createServiceRequest(req: ServiceRequestEntity, currentUser: String, userRole: String) {
        appDao.insertServiceRequest(req)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "SERVICE_REQUEST",
            module = "Service Request",
            record = req.id,
            details = "Submitted ${req.type} for Flat ${req.flat}"
        )
    }

    suspend fun updateServiceRequestStatus(reqId: String, newStatus: String, currentUser: String, userRole: String) {
        val requests = appDao.getAllServiceRequests().firstOrNull() ?: emptyList()
        val req = requests.find { it.id == reqId } ?: return
        val updated = req.copy(status = newStatus)
        appDao.updateServiceRequest(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_SERVICE_REQUEST",
            module = "Service Request",
            record = reqId,
            details = "Service Request $reqId status updated to $newStatus"
        )
    }

    suspend fun updateCommitteeTaskStatus(taskId: String, newStatus: String, currentUser: String, userRole: String) {
        val tasks = appDao.getAllCommitteeTasks().firstOrNull() ?: emptyList()
        val task = tasks.find { it.id == taskId } ?: return
        val updated = task.copy(status = newStatus)
        appDao.updateCommitteeTask(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_TASK",
            module = "Governance",
            record = taskId,
            details = "Task '$taskId - ${task.title}' marked as $newStatus"
        )
    }

    suspend fun createNotice(notice: SocietyNoticeEntity, currentUser: String, userRole: String) {
        appDao.insertNotice(notice)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "PUBLISH_NOTICE",
            module = "Notice Board",
            record = notice.id,
            details = "Published notice: '${notice.title}' to Target: ${notice.targetTower}, Priority: ${notice.priority}"
        )
    }

    suspend fun amendNotice(notice: SocietyNoticeEntity, currentUser: String, userRole: String) {
        appDao.updateNotice(notice)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_NOTICE",
            module = "Notice Board",
            record = notice.id,
            details = "Amended notice '${notice.title}' [Ref: ${notice.circularNo}]. Category: ${notice.category}, Priority: ${notice.priority}"
        )
    }

    suspend fun togglePinNotice(noticeId: String, currentUser: String, userRole: String) {
        val notices = appDao.getAllNotices().firstOrNull() ?: emptyList()
        val notice = notices.find { it.id == noticeId } ?: return
        val updated = notice.copy(isPinned = !notice.isPinned)
        appDao.updateNotice(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = if (updated.isPinned) "PIN_NOTICE" else "UNPIN_NOTICE",
            module = "Notice Board",
            record = noticeId,
            details = "${if (updated.isPinned) "Pinned" else "Unpinned"} notice '${notice.title}' to top of board"
        )
    }

    suspend fun deleteNotice(noticeId: String, currentUser: String, userRole: String) {
        appDao.deleteNotice(noticeId)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_NOTICE",
            module = "Notice Board",
            record = noticeId,
            details = "Removed notice $noticeId from Notice Board"
        )
    }

    suspend fun acknowledgeNotice(noticeId: String, currentUser: String, userRole: String) {
        val notices = appDao.getAllNotices().firstOrNull() ?: emptyList()
        val notice = notices.find { it.id == noticeId } ?: return
        val updated = notice.copy(
            isAcknowledged = true,
            acknowledgedCount = notice.acknowledgedCount + 1
        )
        appDao.updateNotice(updated)
    }

    // Master Data Operations (Can be amended by committee members)
    suspend fun amendSocietyConfig(config: SocietyConfigEntity, currentUser: String, userRole: String) {
        val old = appDao.getSocietyConfig().firstOrNull()
        appDao.insertSocietyConfig(config)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_SOCIETY_CONFIG",
            module = "Master Data",
            record = config.societyName,
            details = "Amended Society Config & Tariffs: Maintenance=₹${config.baseMaintenanceRateSqFt}/sqft, Late Fee=${config.latePaymentInterestPercent}%, Office Hours=${config.officeHours}",
            oldVal = "Maint: ₹${old?.baseMaintenanceRateSqFt ?: 3.5}/sqft",
            newVal = "Maint: ₹${config.baseMaintenanceRateSqFt}/sqft"
        )
    }

    suspend fun amendFlat(flat: FlatEntity, currentUser: String, userRole: String) {
        val old = appDao.getFlatById(flat.flatId).firstOrNull()
        appDao.updateFlat(flat)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_FLAT_MASTER",
            module = "Master Data",
            record = flat.flatId,
            details = "Amended Flat ${flat.flatId}: Owner=${flat.ownerName}, Phone=${flat.ownerPhone}, Status=${flat.occupancyStatus}, Type=${flat.flatType}",
            oldVal = "${old?.ownerName} (${old?.occupancyStatus})",
            newVal = "${flat.ownerName} (${flat.occupancyStatus})"
        )
    }

    suspend fun addFlat(flat: FlatEntity, currentUser: String, userRole: String) {
        appDao.insertFlat(flat)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_FLAT_MASTER",
            module = "Master Data",
            record = flat.flatId,
            details = "Added Flat ${flat.flatId} in ${flat.tower} Tower (${flat.flatType}) to society registry"
        )
    }

    suspend fun deleteFlat(flatId: String, currentUser: String, userRole: String) {
        appDao.deleteFlat(flatId)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_FLAT_MASTER",
            module = "Master Data",
            record = flatId,
            details = "Removed Flat $flatId from master registry"
        )
    }

    suspend fun amendParkingSlot(slot: ParkingSlotEntity, currentUser: String, userRole: String) {
        appDao.updateParkingSlot(slot)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_PARKING_MASTER",
            module = "Master Data",
            record = slot.slotNumber,
            details = "Amended Parking Slot ${slot.slotNumber} (Level ${slot.level}): Flat=${slot.allottedFlat}, Plate=${slot.vehiclePlate}, EV=${slot.hasEvCharger}, Status=${slot.status}"
        )
    }

    suspend fun amendAmenity(amenity: AmenityEntity, currentUser: String, userRole: String) {
        appDao.updateAmenity(amenity)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_AMENITY_MASTER",
            module = "Master Data",
            record = amenity.name,
            details = "Amended Amenity ${amenity.name}: Timings=${amenity.timings}, Fee=₹${amenity.bookingFee}, Deposit=₹${amenity.securityDeposit}, Status=${amenity.status}"
        )
    }

    suspend fun amendVendor(vendor: VendorMasterEntity, currentUser: String, userRole: String) {
        appDao.updateVendor(vendor)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_VENDOR_MASTER",
            module = "Master Data",
            record = vendor.agencyName,
            details = "Amended Vendor contract for ${vendor.agencyName} (${vendor.category}): Expiry=${vendor.contractExpiryDate}, Monthly=₹${vendor.monthlyCharges}, Contact=${vendor.contactPerson}"
        )
    }

    suspend fun addVendor(vendor: VendorMasterEntity, currentUser: String, userRole: String) {
        appDao.insertVendor(vendor)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_VENDOR_MASTER",
            module = "Master Data",
            record = vendor.agencyName,
            details = "Registered new vendor ${vendor.agencyName} for ${vendor.category} with AMC Ref: ${vendor.amcRefNumber}"
        )
    }

    suspend fun deleteVendor(vendorId: String, currentUser: String, userRole: String) {
        appDao.deleteVendor(vendorId)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_VENDOR_MASTER",
            module = "Master Data",
            record = vendorId,
            details = "Terminated and removed vendor record $vendorId"
        )
    }

    suspend fun amendCommitteeMember(member: CommitteeMasterEntity, currentUser: String, userRole: String) {
        appDao.updateCommitteeMember(member)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_COMMITTEE_DIRECTORY",
            module = "Master Data",
            record = member.fullName,
            details = "Amended committee role ${member.roleTitle} for ${member.fullName} (Flat ${member.flatId}), Term: ${member.term}"
        )
    }

    suspend fun addCommitteeMember(member: CommitteeMasterEntity, currentUser: String, userRole: String) {
        appDao.insertCommitteeMember(member)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_COMMITTEE_MEMBER",
            module = "Master Data",
            record = member.fullName,
            details = "Appointed ${member.fullName} as ${member.roleTitle} for term ${member.term}"
        )
    }

    suspend fun deleteCommitteeMember(id: String, currentUser: String, userRole: String) {
        appDao.deleteCommitteeMember(id)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_COMMITTEE_MEMBER",
            module = "Master Data",
            record = id,
            details = "Removed committee member record $id"
        )
    }

    suspend fun addSocietyRule(rule: SocietyRuleEntity, currentUser: String, userRole: String) {
        appDao.insertSocietyRule(rule)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "ADD_SOCIETY_RULE",
            module = "Bylaws & Rules",
            record = rule.ruleTitle,
            details = "Added rule in '${rule.category}': ${rule.ruleTitle} (Penalty: ₹${rule.penaltyAmount})"
        )
    }

    suspend fun updateSocietyRule(rule: SocietyRuleEntity, currentUser: String, userRole: String) {
        appDao.updateSocietyRule(rule)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "AMEND_SOCIETY_RULE",
            module = "Bylaws & Rules",
            record = rule.ruleTitle,
            details = "Amended rule #${rule.id} '${rule.ruleTitle}' in '${rule.category}' (Penalty: ₹${rule.penaltyAmount})"
        )
    }

    suspend fun deleteSocietyRule(id: Long, title: String, currentUser: String, userRole: String) {
        appDao.deleteSocietyRule(id)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_SOCIETY_RULE",
            module = "Bylaws & Rules",
            record = title,
            details = "Deleted rule #$id '$title'"
        )
    }

    suspend fun updateAppTheme(themeKey: String, currentUser: String, userRole: String) {
        val current = appDao.getSocietyConfig().firstOrNull() ?: SocietyConfigEntity()
        val updated = current.copy(appTheme = themeKey, lastAmendedBy = "$currentUser ($userRole)")
        appDao.insertSocietyConfig(updated)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "CHANGE_APP_THEME",
            module = "System Theme",
            record = themeKey,
            details = "Super Admin changed system theme to $themeKey",
            oldVal = current.appTheme,
            newVal = themeKey
        )
    }

    suspend fun updateTenant(tenant: TenantEntity, currentUser: String, userRole: String) {
        appDao.updateTenant(tenant)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_TENANT",
            module = "Members",
            record = tenant.fullName,
            details = "Amended tenant record ${tenant.fullName} for Flat ${tenant.flatId}"
        )
    }

    suspend fun deleteTenant(tenantId: Long, tenantName: String, currentUser: String, userRole: String) {
        appDao.deleteTenant(tenantId)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_TENANT",
            module = "Members",
            record = tenantName,
            details = "Removed tenant record #$tenantId ($tenantName)"
        )
    }

    suspend fun updateFamilyMember(member: FamilyMemberEntity, currentUser: String, userRole: String) {
        appDao.updateFamilyMember(member)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_FAMILY_MEMBER",
            module = "Members",
            record = member.fullName,
            details = "Updated family member ${member.fullName} for Flat ${member.flatId}"
        )
    }

    suspend fun deleteFamilyMember(id: Long, name: String, currentUser: String, userRole: String) {
        appDao.deleteFamilyMember(id)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "DELETE_FAMILY_MEMBER",
            module = "Members",
            record = name,
            details = "Removed family member #$id ($name)"
        )
    }

    suspend fun updateFlat(flat: FlatEntity, currentUser: String, userRole: String) {
        appDao.insertFlat(flat)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "UPDATE_FLAT",
            module = "Flats",
            record = flat.flatId,
            details = "Updated Flat ${flat.flatId}: Owner=${flat.ownerName}, Status=${flat.occupancyStatus}"
        )
    }

    suspend fun recordStaffAttendance(attendance: StaffAttendanceEntity, currentUser: String, userRole: String) {
        appDao.insertStaffAttendance(attendance)
        logAudit(
            user = currentUser,
            role = userRole,
            action = "STAFF_ATTENDANCE_${attendance.punchType.replace(" ", "_").uppercase()}",
            module = "Attendance",
            record = attendance.staffName,
            details = "${attendance.punchType} for ${attendance.staffName} (${attendance.staffRole}) via ${attendance.photoSource}. Loc: ${attendance.locationAddress} [GPS: ${attendance.latitude}, ${attendance.longitude} - GeoFence: ${if (attendance.isGeoFenceVerified) "Verified" else "Outside Bounds"}]"
        )
    }

    suspend fun scanPatrolCheckpoint(checkpointId: String, guardName: String, guardRole: String, notes: String = "") {
        val checkpoint = appDao.getCheckpointById(checkpointId).firstOrNull() ?: return
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedTime = sdf.format(Date(now))

        val updatedCheckpoint = checkpoint.copy(
            lastScanEpochMillis = now,
            lastScanFormatted = formattedTime,
            lastScanByGuard = guardName,
            isMissedAlertActive = false,
            missedAlertAcknowledged = false,
            totalScansToday = checkpoint.totalScansToday + 1
        )
        appDao.updatePatrolCheckpoint(updatedCheckpoint)

        val log = PatrolScanLogEntity(
            checkpointId = checkpointId,
            checkpointName = checkpoint.checkpointName,
            guardName = guardName,
            guardRole = guardRole,
            scanTimestamp = formattedTime,
            scanEpochMillis = now,
            scanStatus = "On-Time (Verified)",
            notes = if (notes.isNotBlank()) notes else "Hourly QR Patrol scan recorded at ${checkpoint.checkpointName}"
        )
        appDao.insertPatrolScanLog(log)

        logAudit(
            user = guardName,
            role = guardRole,
            action = "PATROL_QR_SCAN",
            module = "Patrol Guard",
            record = checkpoint.code,
            details = "Guard $guardName successfully scanned QR checkpoint ${checkpoint.checkpointName} (${checkpoint.code}). 1-Hour patrol timer reset."
        )
    }

    suspend fun acknowledgeMissedScanAlert(checkpointId: String, acknowledgedBy: String, userRole: String, resolutionNotes: String) {
        val checkpoint = appDao.getCheckpointById(checkpointId).firstOrNull() ?: return
        val updated = checkpoint.copy(
            isMissedAlertActive = false,
            missedAlertAcknowledged = true
        )
        appDao.updatePatrolCheckpoint(updated)

        logAudit(
            user = acknowledgedBy,
            role = userRole,
            action = "ACKNOWLEDGE_MISSED_PATROL",
            module = "Security & Patrol",
            record = checkpoint.code,
            details = "Missed patrol alert for ${checkpoint.checkpointName} acknowledged by $acknowledgedBy ($userRole). Resolution: $resolutionNotes"
        )
    }

    suspend fun simulateMissedScan(checkpointId: String, currentUser: String, userRole: String) {
        val checkpoint = appDao.getCheckpointById(checkpointId).firstOrNull() ?: return
        val overdueEpoch = System.currentTimeMillis() - (75 * 60 * 1000L)
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedTime = sdf.format(Date(overdueEpoch))

        val updated = checkpoint.copy(
            lastScanEpochMillis = overdueEpoch,
            lastScanFormatted = formattedTime,
            isMissedAlertActive = true,
            missedAlertAcknowledged = false
        )
        appDao.updatePatrolCheckpoint(updated)

        logAudit(
            user = currentUser,
            role = userRole,
            action = "SIMULATE_MISSED_PATROL_ALERT",
            module = "Security & Patrol",
            record = checkpoint.code,
            details = "Simulated 1-hour missed QR patrol scan for ${checkpoint.checkpointName}. Alert triggered for Committee & Society Managers."
        )
    }

    suspend fun logAudit(
        user: String,
        role: String,
        action: String,
        module: String,
        record: String,
        details: String,
        oldVal: String = "",
        newVal: String = ""
    ) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
        val timestamp = sdf.format(Date())
        val entry = AuditLogEntity(
            timestamp = timestamp,
            userName = user,
            userRole = role,
            action = action,
            module = module,
            recordAffected = record,
            details = details,
            oldValue = oldVal,
            newValue = newVal
        )
        appDao.insertAuditLog(entry)
    }

    private suspend fun seedDatabaseIfEmpty() {
        val existingFlats = appDao.getAllFlats().firstOrNull()
        if (!existingFlats.isNullOrEmpty()) return

        // Seed Flats
        val flats = listOf(
            FlatEntity("K-1204", "Kaveh", 12, "1204", "Rajesh Sharma", "+91 98201 12345", "rajesh.sharma@example.com", "15-Dec-2022", "Self Occupied"),
            FlatEntity("B1-802", "Baraz-1", 8, "802", "Vikram Malhotra", "+91 98202 23456", "vikram.m@example.com", "10-Jan-2023", "Rented"),
            FlatEntity("B2-1503", "Baraz-2", 15, "1503", "Sunita Deshmukh", "+91 98203 34567", "sunita.d@example.com", "20-Mar-2023", "Self Occupied"),
            FlatEntity("Z-401", "Zenath", 4, "401", "Arun Varma", "+91 98204 45678", "arun.v@example.com", "05-Apr-2023", "Vacant")
        )
        flats.forEach { appDao.insertFlat(it) }

        // Seed Family Members for K-1204
        val members = listOf(
            FamilyMemberEntity(
                flatId = "K-1204",
                fullName = "Priya Sharma",
                relationship = "Wife",
                gender = "Female",
                dob = "14-May-1988",
                calculatedAge = 38,
                phone = "+91 98201 99887",
                email = "priya.sharma@example.com",
                isEmergencyContact = true,
                isResident = true,
                isChild = false
            ),
            FamilyMemberEntity(
                flatId = "K-1204",
                fullName = "Aarav Sharma",
                relationship = "Son",
                gender = "Male",
                dob = "20-Aug-2015",
                calculatedAge = 11,
                isEmergencyContact = false,
                isResident = true,
                isChild = true,
                schoolName = "Delhi Public School, Kharghar",
                grade = "Grade 6-B",
                schoolContact = "022-27745500"
            ),
            FamilyMemberEntity(
                flatId = "K-1204",
                fullName = "Ananya Sharma",
                relationship = "Daughter",
                gender = "Female",
                dob = "12-Nov-2010",
                calculatedAge = 15,
                isEmergencyContact = false,
                isResident = true,
                isChild = true,
                schoolName = "Delhi Public School, Kharghar",
                grade = "Grade 10-A",
                schoolContact = "022-27745500"
            ),
            FamilyMemberEntity(
                flatId = "K-1204",
                fullName = "Meena Sharma",
                relationship = "Mother",
                gender = "Female",
                dob = "05-Jan-1958",
                calculatedAge = 68,
                phone = "+91 98201 33441",
                isEmergencyContact = false,
                isResident = true,
                isChild = false
            )
        )
        members.forEach { appDao.insertFamilyMember(it) }

        // Seed Vehicles for K-1204
        val vehicles = listOf(
            VehicleEntity(
                flatId = "K-1204",
                tower = "Kaveh",
                vehicleType = "Four Wheeler",
                makeModel = "Hyundai Creta SX (O)",
                registrationNumber = "MH-46-AZ-1204",
                color = "Polar White",
                fuelType = "Petrol",
                isEv = false,
                allottedSlot = "P1-034"
            ),
            VehicleEntity(
                flatId = "K-1204",
                tower = "Kaveh",
                vehicleType = "Two Wheeler",
                makeModel = "Honda Activa 6G Premium",
                registrationNumber = "MH-46-BK-4512",
                color = "Pearl Siren Blue",
                fuelType = "Petrol",
                isEv = false,
                allottedSlot = "P1-T12"
            )
        )
        vehicles.forEach { appDao.insertVehicle(it) }

        // Seed Parking Slots across P1 to P5
        val parkingList = mutableListOf<ParkingSlotEntity>()
        // Level P1
        for (i in 1..20) {
            val num = String.format(Locale.getDefault(), "P1-%03d", i)
            when (num) {
                "P1-001" -> parkingList.add(ParkingSlotEntity(num, "P1", "Visitor", hasEvCharger = true, remarks = "Guest EV Bay"))
                "P1-002" -> parkingList.add(ParkingSlotEntity(num, "P1", "Visitor", remarks = "Guest Bay"))
                "P1-003" -> parkingList.add(ParkingSlotEntity(num, "P1", "Blocked", remarks = "Sensor repair required"))
                "P1-010" -> parkingList.add(ParkingSlotEntity(num, "P1", "Allocated", "B1-802", "Baraz-1", "Vikram Malhotra", "MH-46-CM-8802", true))
                "P1-015" -> parkingList.add(ParkingSlotEntity(num, "P1", "Allocated", "B2-1503", "Baraz-2", "Sunita Deshmukh", "MH-46-DL-1503"))
                else -> {
                    if (i % 5 == 0) {
                        parkingList.add(ParkingSlotEntity(num, "P1", "Available", hasEvCharger = (i % 2 == 0)))
                    } else {
                        parkingList.add(ParkingSlotEntity(num, "P1", "Allocated", "K-${100 + i}", "Kaveh", "Resident", "MH-46-AB-${1000 + i}"))
                    }
                }
            }
        }
        // Specific Slot P1-034 for K-1204
        parkingList.add(ParkingSlotEntity("P1-034", "P1", "Allocated", "K-1204", "Kaveh", "Rajesh Sharma", "MH-46-AZ-1204", true, "Dedicated 4-Wheeler Slot"))

        // Add representative slots for P2, P3, P4, P5
        listOf("P2", "P3", "P4", "P5").forEach { lvl ->
            for (j in 1..10) {
                val num = String.format(Locale.getDefault(), "%s-%03d", lvl, j)
                val st = when {
                    j == 1 -> "Available"
                    j == 2 -> "Visitor"
                    j == 7 -> "Blocked"
                    else -> "Allocated"
                }
                parkingList.add(ParkingSlotEntity(num, lvl, st, if (st == "Allocated") "Tower Unit" else "", if (st == "Allocated") "Baraz-2" else "", hasEvCharger = (j % 4 == 0)))
            }
        }
        parkingList.forEach { appDao.insertParkingSlot(it) }

        // Seed Pet
        appDao.insertPet(
            PetEntity(
                flatId = "K-1204",
                petName = "Buddy",
                petType = "Dog",
                breed = "Golden Retriever",
                gender = "Male",
                age = "3 Years",
                registrationNumber = "PET-AA-2026-084",
                vaccinationStatus = "Valid",
                vaccinationExpiryDate = "15-Nov-2026",
                certificateDoc = "Buddy_Rabies_DHPPIL_Nov2026.pdf",
                isVerified = true
            )
        )

        // Seed Domestic Help
        listOf(
            DomesticHelpEntity(
                flatId = "K-1204",
                name = "Sunita Devi",
                category = "House Maid",
                phone = "+91 98334 55661",
                passId = "DH-302",
                validity = "31-Dec-2026",
                status = "Active",
                currentlyInside = true
            ),
            DomesticHelpEntity(
                flatId = "K-1204",
                name = "Ramesh Kumar",
                category = "Cook",
                phone = "+91 98205 77882",
                passId = "DH-118",
                validity = "31-Dec-2026",
                status = "Active",
                currentlyInside = false
            )
        ).forEach { appDao.insertDomesticHelp(it) }

        // Seed Existing Tenant for B1-802
        appDao.insertTenant(
            TenantEntity(
                flatId = "B1-802",
                fullName = "Rohan Mehta",
                phone = "+91 98190 66772",
                altPhone = "+91 98190 66773",
                email = "rohan.mehta@example.com",
                permanentAddress = "Flat 302, Green Glen, Vashi, Navi Mumbai",
                occupation = "Senior Software Architect",
                employer = "Tata Consultancy Services, Airoli",
                moveInDate = "01-Apr-2025",
                expectedMoveOutDate = "31-Mar-2027",
                status = "Active",
                agreementDocument = "Leave_and_License_Agreement_B1_802_2025_2027.pdf",
                agreementStartDate = "01-Apr-2025",
                agreementExpiryDate = "31-Mar-2027",
                agreementStatus = "Active",
                verificationStatus = "Verified",
                verificationComments = "All NMMC police verification and stamp-duty registration verified."
            )
        )

        // Seed Realistic Complaints
        val complaints = listOf(
            ComplaintEntity(
                id = "AA-1024",
                category = "Lift",
                subcategory = "Door Problem & Jerk",
                tower = "Kaveh",
                floor = 12,
                location = "Lift 2 – 12th Floor Lobby",
                description = "Lift 2 door stutters while closing and jerks between 11th and 12th floor.",
                priority = "High",
                status = "In Progress",
                submittedBy = "K-1204",
                assignedTo = "OTIS Lift Engineering AMC",
                expectedResolution = "Today, 5:00 PM",
                createdAt = "12 Sep 2026, 09:30 AM",
                slaHours = 6,
                isSlaBreached = false,
                duplicateReportCount = 4,
                isRecurring = true
            ),
            ComplaintEntity(
                id = "AA-1019",
                category = "Water",
                subcategory = "Low water pressure",
                tower = "Baraz-2",
                floor = 15,
                location = "Kitchen & Master Bathroom",
                description = "Very low water pressure since 7 AM morning supply cycle.",
                priority = "Critical",
                status = "Assigned",
                submittedBy = "B2-1503",
                assignedTo = "Society Water Systems Plumber",
                expectedResolution = "Today, 2:00 PM",
                createdAt = "12 Sep 2026, 07:45 AM",
                slaHours = 2,
                isSlaBreached = false,
                duplicateReportCount = 7
            ),
            ComplaintEntity(
                id = "AA-1012",
                category = "Cleanliness",
                subcategory = "Staircase cleaning",
                tower = "Kaveh",
                floor = 11,
                location = "Fire Staircase landing",
                description = "Dry construction debris left on landing after flat renovation.",
                priority = "Medium",
                status = "Resolved",
                submittedBy = "K-1204",
                assignedTo = "CleanPro Housekeeping",
                expectedResolution = "11 Sep 2026",
                createdAt = "10 Sep 2026, 02:15 PM",
                resolvedAt = "11 Sep 2026, 11:30 AM",
                slaHours = 24,
                isSlaBreached = false
            ),
            ComplaintEntity(
                id = "AA-1008",
                category = "Parking",
                subcategory = "Unauthorized parking",
                tower = "Baraz-1",
                floor = 1,
                location = "P1-010 Car Bay",
                description = "Unknown white Swift blocking allotted parking bay.",
                priority = "High",
                status = "Closed",
                submittedBy = "B1-802",
                assignedTo = "Main Gate Security Marshals",
                expectedResolution = "10 Sep 2026",
                createdAt = "09 Sep 2026, 08:30 PM",
                resolvedAt = "09 Sep 2026, 09:15 PM",
                slaHours = 2,
                isSlaBreached = false
            )
        )
        complaints.forEach { appDao.insertComplaint(it) }

        // Seed Complaint comments
        appDao.insertComplaintComment(
            ComplaintCommentEntity(
                complaintId = "AA-1024",
                authorName = "Society Manager",
                authorRole = "Society Manager",
                commentText = "Acknowledged. Escalated to OTIS Lift technician Mr. Sanjay. Ticket #OTIS-4491 generated.",
                timestamp = "12 Sep 2026, 10:15 AM"
            )
        )
        appDao.insertComplaintComment(
            ComplaintCommentEntity(
                complaintId = "AA-1024",
                authorName = "OTIS Lift Engineering AMC",
                authorRole = "Vendor",
                commentText = "Technician arrived at Kaveh motor room. Door sensor realignment underway.",
                timestamp = "12 Sep 2026, 11:40 AM"
            )
        )

        // Seed Visitors
        val visitors = listOf(
            VisitorEntity(
                id = "VIS-9201",
                passCode = "AA-8492",
                visitorName = "Rahul Sharma",
                phone = "+91 98334 11223",
                flat = "K-1204",
                tower = "Kaveh",
                type = "Guest",
                expectedArrival = "Today, 6:30 PM",
                status = "Approved"
            ),
            VisitorEntity(
                id = "VIS-9202",
                passCode = "AA-3189",
                visitorName = "Zomato Delivery Executive",
                phone = "+91 98112 33445",
                flat = "K-1204",
                tower = "Kaveh",
                type = "Delivery",
                company = "Zomato",
                expectedArrival = "Today, 1:15 PM",
                status = "Waiting Approval"
            ),
            VisitorEntity(
                id = "VIS-9190",
                passCode = "AA-1094",
                visitorName = "Amazon Courier (Ramesh)",
                phone = "+91 98770 12121",
                flat = "K-1204",
                tower = "Kaveh",
                type = "Delivery",
                company = "Amazon",
                expectedArrival = "Yesterday, 3:00 PM",
                status = "Exited",
                inTime = "3:10 PM",
                outTime = "3:25 PM"
            )
        )
        visitors.forEach { appDao.insertVisitor(it) }

        // Seed Amenities
        val amenities = listOf(
            AmenityEntity(
                id = "AMEN-1",
                name = "Grand Clubhouse",
                description = "Air-conditioned recreational lounge, board games, library & Wi-Fi hub.",
                rules = "Quiet hours after 10 PM. No external catering without prior NOC.",
                timings = "6:00 AM - 10:30 PM",
                capacity = 50,
                bookingFee = 500,
                securityDeposit = 1000,
                iconType = "clubhouse"
            ),
            AmenityEntity(
                id = "AMEN-2",
                name = "Royal Banquet & Party Hall",
                description = "Luxurious hall with audio-visual system, pantry & dining setup for private celebrations.",
                rules = "Music permitted till 10 PM as per NMMC guidelines. Cleanup mandatory.",
                timings = "10:00 AM - 11:00 PM",
                capacity = 120,
                bookingFee = 3500,
                securityDeposit = 5000,
                iconType = "party_hall"
            ),
            AmenityEntity(
                id = "AMEN-3",
                name = "Olympus Gym & Fitness",
                description = "State-of-the-art cardiovascular and strength training equipment with certified trainers.",
                rules = "Gym shoes and towel mandatory. Age 16+ only.",
                timings = "5:30 AM - 11:00 AM & 5:00 PM - 10:00 PM",
                capacity = 30,
                bookingFee = 0,
                securityDeposit = 0,
                iconType = "gym"
            ),
            AmenityEntity(
                id = "AMEN-4",
                name = "Infinity Swimming Pool",
                description = "Podium-level temperature-controlled pool with dedicated children's splash pool.",
                rules = "Synthetic swim wear compulsory. Shower before entering.",
                timings = "6:00 AM - 10:00 AM & 4:30 PM - 8:30 PM",
                capacity = 25,
                bookingFee = 0,
                securityDeposit = 0,
                iconType = "pool"
            ),
            AmenityEntity(
                id = "AMEN-5",
                name = "Podium Children's Play Park",
                description = "Safe EPDM cushioned play area with swings, slides, sand pit and climbing web.",
                rules = "Parental supervision required for toddlers under 6.",
                timings = "6:00 AM - 9:00 PM",
                capacity = 40,
                bookingFee = 0,
                securityDeposit = 0,
                iconType = "play_area"
            ),
            AmenityEntity(
                id = "AMEN-6",
                name = "Multipurpose Sports Turf",
                description = "Floodlit synthetic turf for box cricket, futsal and basketball.",
                rules = "Non-marking studs/sports shoes only. 1 hr slot limit during peak weekends.",
                timings = "6:00 AM - 10:00 PM",
                capacity = 16,
                bookingFee = 200,
                securityDeposit = 500,
                iconType = "sports"
            )
        )
        amenities.forEach { appDao.insertAmenity(it) }

        // Seed Amenity Booking for K-1204
        appDao.insertAmenityBooking(
            AmenityBookingEntity(
                id = "BK-802",
                amenityId = "AMEN-1",
                amenityName = "Grand Clubhouse",
                flat = "K-1204",
                residentName = "Rajesh Sharma",
                bookingDate = "Saturday, 19 Sep 2026",
                timeSlot = "7:00 PM - 9:00 PM",
                status = "Confirmed",
                feePaid = 500
            )
        )

        // Seed Maintenance Bills
        val bills = listOf(
            MaintenanceBillEntity(
                id = "INV-2026-09",
                flat = "K-1204",
                tower = "Kaveh",
                monthYear = "September 2026",
                totalAmount = 5850,
                dueDate = "25-Sep-2026",
                status = "Pending",
                sinkingFund = 1200,
                repairFund = 850,
                waterCharges = 1100,
                serviceCharges = 1500,
                electricityCharges = 1200
            ),
            MaintenanceBillEntity(
                id = "INV-2026-08",
                flat = "K-1204",
                tower = "Kaveh",
                monthYear = "August 2026",
                totalAmount = 5850,
                dueDate = "25-Aug-2026",
                status = "Paid",
                paymentMethod = "UPI (GPay)",
                paidAt = "18 Aug 2026, 04:30 PM",
                receiptNumber = "REC-AA-8812"
            ),
            MaintenanceBillEntity(
                id = "INV-2026-07",
                flat = "K-1204",
                tower = "Kaveh",
                monthYear = "July 2026",
                totalAmount = 5850,
                dueDate = "25-Jul-2026",
                status = "Paid",
                paymentMethod = "Net Banking (HDFC)",
                paidAt = "15 Jul 2026, 11:15 AM",
                receiptNumber = "REC-AA-7629"
            )
        )
        bills.forEach { appDao.insertMaintenanceBill(it) }

        // Seed Society Notices
        val notices = listOf(
            SocietyNoticeEntity(
                id = "NOT-101",
                title = "Water Supply Schedule - Baraz-2 Pressure Tank Maintenance",
                content = "Water supply in Baraz-2 Tower will be regulated from 10:00 AM to 1:00 PM today due to mandatory 6-month pneumatic pressure tank inspection. Residents are requested to store adequate drinking water.",
                category = "Water",
                targetTower = "Baraz-2",
                priority = "High",
                publishedBy = "Hon. Secretary",
                publishDate = "12 Sep 2026, 07:00 AM",
                isAcknowledged = false,
                isPinned = true,
                circularNo = "CIR/2026/089",
                acknowledgedCount = 42
            ),
            SocietyNoticeEntity(
                id = "NOT-102",
                title = "Ganesh Chaturthi Utsav 2026 Celebrations at Podium Lawn",
                content = "The Managing Committee cordially invites all families to the 5-day Ganesh Chaturthi celebrations on the Podium lawn starting 19th Sep. Eco-friendly idol immersion pool arrangements finalized. Cultural event registrations open on app.",
                category = "Event",
                targetTower = "ALL",
                priority = "Normal",
                publishedBy = "Cultural Committee Lead",
                publishDate = "11 Sep 2026, 05:00 PM",
                isAcknowledged = true,
                isPinned = true,
                circularNo = "CIR/2026/088",
                acknowledgedCount = 184
            ),
            SocietyNoticeEntity(
                id = "NOT-103",
                title = "Mandatory Tenant & Domestic Help Police Verification Directive",
                content = "In accordance with Navi Mumbai Police Commissionerate guidelines & MCS Act 1960 bylaws, all rented flats must have active registered lease agreements and police verification forms uploaded. Fine of ₹2,500 applicable for non-compliance.",
                category = "Security",
                targetTower = "ALL",
                priority = "High",
                publishedBy = "Society Manager",
                publishDate = "08 Sep 2026, 10:00 AM",
                isAcknowledged = true,
                isPinned = false,
                circularNo = "CIR/2026/085",
                acknowledgedCount = 129
            ),
            SocietyNoticeEntity(
                id = "NOT-104",
                title = "Upcoming Annual General Body Meeting (AGM) 2026 Notice & Agenda",
                content = "Notice is hereby given that the 4th Annual General Body Meeting of Arihant Alishan CHS will be held on Sunday, 27th Sep 2026 at 10:30 AM in the Grand Banquet Hall. Agenda includes approval of audited accounts, statutory auditor appointment, and EV charging policy.",
                category = "AGM/EGM",
                targetTower = "ALL",
                priority = "Emergency",
                publishedBy = "Chairman",
                publishDate = "05 Sep 2026, 09:00 AM",
                isAcknowledged = false,
                isPinned = true,
                circularNo = "CIR/2026/AGM-04",
                acknowledgedCount = 210
            ),
            SocietyNoticeEntity(
                id = "NOT-105",
                title = "Electric Vehicle (EV) Charging Bay Operating Rules",
                content = "Designated EV charging stations on P1 and P2 levels are equipped with 22kW AC Type-2 guns. Dedicated 4-hour max charging slot per vehicle applies to ensure fair rotation. Automated tariff billing @ ₹9.50/kWh via society wallet.",
                category = "Maintenance",
                targetTower = "ALL",
                priority = "Normal",
                publishedBy = "Hon. Treasurer",
                publishDate = "01 Sep 2026, 11:30 AM",
                isAcknowledged = true,
                isPinned = false,
                circularNo = "CIR/2026/081",
                acknowledgedCount = 98
            )
        )
        notices.forEach { appDao.insertNotice(it) }

        // Seed Committee Tasks
        val tasks = listOf(
            CommitteeTaskEntity(
                id = "TSK-301",
                title = "Inspect Lift 2 Sensor & Motor Alignment",
                description = "Supervise OTIS technicians and test leveling across floors 10 to 14 in Kaveh.",
                category = "Maintenance",
                priority = "High",
                assignedTo = "Prakash Nair (Maintenance Supervisor)",
                assignedRole = "Maintenance Staff",
                dueDate = "12 Sep 2026",
                tower = "Kaveh",
                location = "Motor Room & 12th Fl",
                status = "In Progress"
            ),
            CommitteeTaskEntity(
                id = "TSK-302",
                title = "Review P2 Parking CCTV Blind Spots",
                description = "Verify camera 14 and 16 angle adjustments at Level P2 ramp.",
                category = "Security",
                priority = "Medium",
                assignedTo = "Sunil Patil (Security Committee)",
                assignedRole = "Committee Member",
                dueDate = "14 Sep 2026",
                tower = "ALL",
                location = "Level P2 Parking",
                status = "New"
            ),
            CommitteeTaskEntity(
                id = "TSK-303",
                title = "Reconcile Q2 Maintenance Dues & Sinking Fund",
                description = "Prepare quarterly financial statement for Treasurer review prior to next committee meeting.",
                category = "Finance",
                priority = "Critical",
                assignedTo = "Society Manager",
                assignedRole = "Society Manager",
                dueDate = "15 Sep 2026",
                status = "In Progress"
            )
        )
        tasks.forEach { appDao.insertCommitteeTask(it) }

        // Seed Meetings
        appDao.insertMeeting(
            SocietyMeetingEntity(
                id = "MTG-2026-08",
                title = "Monthly Managing Committee Meeting - August 2026",
                meetingDate = "28 Aug 2026, 7:30 PM",
                agenda = "Review of water filtration plant AMC, Lift overhaul quotes, EV charging policy.",
                decisions = "Approved OTIS annual comprehensive AMC. Resolved to mandate EV charging meters on individual flat lines.",
                actionItem = "Form EV Charging Sub-Committee and issue circular to all tower leads.",
                attendeeCount = 14
            )
        )

        // Seed Documents
        val docs = listOf(
            SocietyDocumentEntity(
                id = "DOC-01",
                title = "Model Bye-Laws (Maharashtra Co-op Housing Societies)",
                category = "Bylaws",
                docType = "PDF",
                uploadDate = "01 Jan 2026",
                expiryDate = "Permanent",
                verifiedBy = "Hon. Secretary",
                version = "v2026.1"
            ),
            SocietyDocumentEntity(
                id = "DOC-02",
                title = "NMMC Fire Safety Compliance Certificate 2026",
                category = "Fire Safety",
                docType = "PDF",
                uploadDate = "15 Mar 2026",
                expiryDate = "14 Mar 2027",
                verifiedBy = "Chief Fire Officer NMMC",
                version = "v1.0"
            ),
            SocietyDocumentEntity(
                id = "DOC-03",
                title = "OTIS Elevator Comprehensive Annual Maintenance Contract",
                category = "AMC",
                docType = "PDF",
                uploadDate = "01 Jun 2026",
                expiryDate = "31 May 2027",
                verifiedBy = "Hon. Treasurer",
                version = "v2.3"
            )
        )
        docs.forEach { appDao.insertDocument(it) }

        // Seed Service Requests
        val reqs = listOf(
            ServiceRequestEntity(
                id = "SR-101",
                type = "Renovation & Contractor Permit",
                flat = "K-1204",
                tower = "Kaveh",
                details = "Balcony waterproofing & interior painting. Work hours 9:30 AM - 6:00 PM.",
                contractorName = "Shree Balaji Interiors",
                workerCount = 3,
                status = "Approved",
                createdAt = "05 Sep 2026"
            ),
            ServiceRequestEntity(
                id = "SR-102",
                type = "Move-In NOC",
                flat = "B1-802",
                tower = "Baraz-1",
                details = "Household goods moving truck entry permit.",
                contractorName = "Agarwal Packers & Movers",
                workerCount = 4,
                status = "Approved",
                createdAt = "01 Apr 2025"
            )
        )
        reqs.forEach { appDao.insertServiceRequest(it) }

        // Seed Society Config Master
        appDao.insertSocietyConfig(
            SocietyConfigEntity(
                id = "MAIN_CONFIG",
                societyName = "Arihant Alishan Co-operative Housing Society Ltd.",
                regNumber = "NMMC/WAR/CHS/2026/894",
                registrationDate = "14-Aug-2022",
                actCompliance = "Maharashtra Co-operative Societies Act 1960",
                address = "Plot No. 1, Sector 35, Kharghar, Navi Mumbai - 410210",
                totalTowers = 4,
                totalFloors = 53,
                totalUnits = 960,
                baseMaintenanceRateSqFt = 3.50,
                sinkingFundRate = 0.25,
                repairFundRate = 0.75,
                latePaymentInterestPercent = 18.0,
                moveInCharges = 5000,
                moveOutClearanceCharges = 2500,
                emergencyPhone = "+91 22 2774 9900",
                officeHours = "10:00 AM - 1:00 PM & 5:00 PM - 8:00 PM (Tue Closed)",
                bankName = "HDFC Bank Ltd., Kharghar Sector 35 Branch",
                bankAccountNumber = "50200088192301",
                bankIfsc = "HDFC0004921",
                lastAmendedBy = "Shailesh B. Kulkarni (Chairman)",
                lastAmendedDate = "12 Sep 2026, 11:30 AM"
            )
        )

        // Seed Vendors Master
        val vendors = listOf(
            VendorMasterEntity(
                id = "VND-101",
                category = "Elevator AMC",
                agencyName = "OTIS Elevator Company India Ltd.",
                contactPerson = "Sanjay Patil (Senior Field Engineer)",
                contactPhone = "+91 98201 55432",
                contactEmail = "service.mumbai@otis.com",
                contractStartDate = "01 Jun 2026",
                contractExpiryDate = "31 May 2027",
                monthlyCharges = 145000,
                emergencyHelpline = "1800 22 6847",
                status = "Active",
                amcRefNumber = "AMC-OTIS-2026-981",
                notes = "Comprehensive 24x7 AMC covering all 16 high-speed passenger & fire-evacuation lifts."
            ),
            VendorMasterEntity(
                id = "VND-102",
                category = "Security Services",
                agencyName = "SIS Security Marshals Ltd.",
                contactPerson = "Capt. Ranveer Singh (Security Head)",
                contactPhone = "+91 98190 77654",
                contactEmail = "operations@sisindia.com",
                contractStartDate = "01 Jan 2026",
                contractExpiryDate = "31 Dec 2026",
                monthlyCharges = 280000,
                emergencyHelpline = "+91 22 2774 9901",
                status = "Active",
                amcRefNumber = "SEC-SIS-2026-44",
                notes = "32 security personnel deployed across 3 shifts with ANPR gate camera oversight."
            ),
            VendorMasterEntity(
                id = "VND-103",
                category = "Water Systems & STP",
                agencyName = "AquaFlow Technologies & Pumping Systems",
                contactPerson = "Mahesh Shinde",
                contactPhone = "+91 97690 12345",
                contactEmail = "support@aquaflowtech.in",
                contractStartDate = "01 Apr 2026",
                contractExpiryDate = "31 Mar 2027",
                monthlyCharges = 65000,
                emergencyHelpline = "+91 97690 99999",
                status = "Active",
                amcRefNumber = "STP-AQ-2026-12",
                notes = "Pneumatic booster pumps, RO drinking water plant, STP recycling maintenance."
            ),
            VendorMasterEntity(
                id = "VND-104",
                category = "Fire Fighting Systems",
                agencyName = "FirePro Safety Systems India",
                contactPerson = "Vikram Deshmukh",
                contactPhone = "+91 98212 90876",
                contractStartDate = "15 Mar 2026",
                contractExpiryDate = "14 Mar 2027",
                monthlyCharges = 42000,
                emergencyHelpline = "101 / +91 98212 99999",
                status = "Active",
                amcRefNumber = "FIRE-FP-2026-07",
                notes = "Smoke detectors, wet riser inspection, fire sprinkler system certification under NMMC CFO guidelines."
            ),
            VendorMasterEntity(
                id = "VND-105",
                category = "Housekeeping & Waste",
                agencyName = "CleanCity Facility Management Services",
                contactPerson = "Sunita Jadhav",
                contactPhone = "+91 98670 54321",
                contractStartDate = "01 Feb 2026",
                contractExpiryDate = "31 Jan 2027",
                monthlyCharges = 195000,
                emergencyHelpline = "+91 98670 99999",
                status = "Active",
                amcRefNumber = "HK-CC-2026-90",
                notes = "Daily corridor scrubbing, podium sanitization, organic waste compost operation."
            )
        )
        vendors.forEach { appDao.insertVendor(it) }

        // Seed Committee Directory Master
        val committee = listOf(
            CommitteeMasterEntity(
                id = "CM-01",
                roleTitle = "Chairman",
                fullName = "Shailesh B. Kulkarni",
                flatId = "K-2401",
                phone = "+91 98200 44321",
                email = "chairman.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = true,
                portfolioDescription = "Overall society governance, legal compliance under MCS Act 1960, AGM/SGM presiding officer."
            ),
            CommitteeMasterEntity(
                id = "CM-02",
                roleTitle = "Hon. Secretary",
                fullName = "Anand Deshpande",
                flatId = "B1-1802",
                phone = "+91 98211 98765",
                email = "secretary.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = true,
                portfolioDescription = "General administration, official circulars, vendor contracts, police verification and NOCs."
            ),
            CommitteeMasterEntity(
                id = "CM-03",
                roleTitle = "Hon. Treasurer",
                fullName = "Pradeep Shenoy",
                flatId = "Z-1404",
                phone = "+91 98330 65432",
                email = "treasurer.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = true,
                portfolioDescription = "Financial budgeting, maintenance fee billing, statutory audits, investment of sinking & repair funds."
            ),
            CommitteeMasterEntity(
                id = "CM-04",
                roleTitle = "Member - Parking & EV Infrastructure",
                fullName = "Sunil Patil",
                flatId = "B2-901",
                phone = "+91 98670 12890",
                email = "parking.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = false,
                portfolioDescription = "P1-P5 parking bay allocations, EV charging point rollout, visitor parking enforcement."
            ),
            CommitteeMasterEntity(
                id = "CM-05",
                roleTitle = "Member - Infrastructure & Lifts",
                fullName = "Dr. Rajiv Tandon",
                flatId = "K-1502",
                phone = "+91 98199 87123",
                email = "infra.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = false,
                portfolioDescription = "Lift AMC performance oversight, water distribution, DG backup system audits."
            ),
            CommitteeMasterEntity(
                id = "CM-06",
                roleTitle = "Member - Sports & Cultural Affairs",
                fullName = "Meera Joshi",
                flatId = "Z-2103",
                phone = "+91 98205 34567",
                email = "cultural.alishan@gmail.com",
                term = "2024 - 2029",
                isBankSignatory = false,
                portfolioDescription = "Festival celebrations, sports turf scheduling, clubhouse amenity maintenance."
            )
        )
        committee.forEach { appDao.insertCommitteeMember(it) }

        // Seed Society Rules & Bylaws
        val rules = listOf(
            SocietyRuleEntity(
                id = 1,
                category = "Parking & Vehicles",
                ruleTitle = "Designated Bay Parking & Visitor Time Limit",
                ruleDescription = "All residents must park strictly within the demarcated lines of their assigned slot (P1-P5). Visitor parking is capped at 4 hours maximum with gate token validation. Double parking or blocking driveways will incur instant towing.",
                penaltyAmount = 1000,
                isMandatory = true,
                displayOrder = 1
            ),
            SocietyRuleEntity(
                id = 2,
                category = "Parking & Vehicles",
                ruleTitle = "Podium Speed Limit 10 km/h & Zero Honking",
                ruleDescription = "The maximum speed limit across all podium levels, ramps, and basement bays is strictly 10 km/h. Horn honking is completely prohibited on society premises day and night.",
                penaltyAmount = 500,
                isMandatory = true,
                displayOrder = 2
            ),
            SocietyRuleEntity(
                id = 3,
                category = "Pets & Animals",
                ruleTitle = "Mandatory Leash & Designated Service Lift",
                ruleDescription = "All pets must be kept on short non-retractable leashes when navigating lobbies, garden paths, and common corridors. Pet owners must exclusively use Service Elevator #2 for pet transit.",
                penaltyAmount = 1000,
                isMandatory = true,
                displayOrder = 3
            ),
            SocietyRuleEntity(
                id = 4,
                category = "Pets & Animals",
                ruleTitle = "Pet Waste Clean-up & Scooper Mandate",
                ruleDescription = "Pet owners must carry waste disposal bags and immediately pick up any pet defecation. Soiled waste must be deposited in marked green pet receptacles near Tower perimeter gates.",
                penaltyAmount = 1500,
                isMandatory = true,
                displayOrder = 4
            ),
            SocietyRuleEntity(
                id = 5,
                category = "Quiet Hours & Noise",
                ruleTitle = "Night Quiet Hours (10:00 PM - 07:00 AM)",
                ruleDescription = "All high-decibel music systems, private terrace gatherings, subwoofer bass, and noisy activities must cease by 10:00 PM to honor resident rest and comfort.",
                penaltyAmount = 2000,
                isMandatory = true,
                displayOrder = 5
            ),
            SocietyRuleEntity(
                id = 6,
                category = "Waste & Sanitation",
                ruleTitle = "Three-Way Waste Segregation at Source",
                ruleDescription = "Under NMMC Solid Waste Directives, households must separate Wet (green bin), Dry (blue bin), and Domestic Hazardous waste (red pouch). Unsegregated waste will be returned and fined.",
                penaltyAmount = 500,
                isMandatory = true,
                displayOrder = 6
            ),
            SocietyRuleEntity(
                id = 7,
                category = "Waste & Sanitation",
                ruleTitle = "Zero Littering & Balcony Sweeping Ban",
                ruleDescription = "Throwing garbage, cigarette stubs, cleaning water, or debris from high-rise balconies or windows is strictly hazardous and prohibited under MCS Act Section 154B.",
                penaltyAmount = 5000,
                isMandatory = true,
                displayOrder = 7
            ),
            SocietyRuleEntity(
                id = 8,
                category = "Clubhouse & Sports",
                ruleTitle = "Mandatory Nylon/Lycra Swimwear in Pool",
                ruleDescription = "Cotton tees, shorts, or non-swim attire are banned in the temperature-controlled pool. Pre-swim cleansing shower and swimming cap are compulsory for all users.",
                penaltyAmount = 1000,
                isMandatory = true,
                displayOrder = 8
            ),
            SocietyRuleEntity(
                id = 9,
                category = "Renovation & Construction",
                ruleTitle = "Civil Work & Drilling Timings (10 AM - 6 PM)",
                ruleDescription = "All heavy drilling, tiling, carpentry, and electrical demolitions are strictly restricted to 10:00 AM to 6:00 PM, Monday through Saturday only. Strictly prohibited on Sundays and National Holidays.",
                penaltyAmount = 5000,
                isMandatory = true,
                displayOrder = 9
            ),
            SocietyRuleEntity(
                id = 10,
                category = "Renovation & Construction",
                ruleTitle = "Service Elevator Usage & Debris Packaging",
                ruleDescription = "Construction materials, cement bags, and rubble must be transported in heavy sealed sacks solely using Service Lift #1. Passenger lifts are strictly off-limits.",
                penaltyAmount = 3000,
                isMandatory = true,
                displayOrder = 10
            )
        )
        rules.forEach { appDao.insertSocietyRule(it) }

        // Initial Audit Logs
        logAudit("SYSTEM_INIT", "System", "DATABASE_INITIALIZATION", "System", "Arihant Alishan", "Seeded 4 Towers (Kaveh, Baraz-1, Baraz-2, Zenath) and Master Profile for K-1204")
        logAudit("Rajesh Sharma", "Resident (Owner)", "REGISTER_VEHICLE", "Vehicle", "MH-46-AZ-1204", "Allocated to P1-034")

        // Seed Patrol Checkpoints (P1, P2, P3, P4, P5, and Night Shift Guard)
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        val checkpoints = listOf(
            PatrolCheckpointEntity(
                checkpointId = "P1",
                code = "P1",
                checkpointName = "Parking Level 1 (North Ramp & EV Hub)",
                locationDescription = "Basement Level 1 near North Driveway, EV Quick Chargers & Main Substation Panel",
                qrPayload = "ARIHANT-PATROL-P1-908123",
                assignedShift = "24x7 Rotational Patrol",
                assignedGuard = "Surendra Singh",
                assignedGuardPhone = "+91 98201 55662",
                lastScanEpochMillis = now - (25 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (25 * 60 * 1000L))),
                lastScanByGuard = "Surendra Singh",
                scanIntervalMinutes = 60,
                isMissedAlertActive = false,
                totalScansToday = 12
            ),
            PatrolCheckpointEntity(
                checkpointId = "P2",
                code = "P2",
                checkpointName = "Parking Level 2 (South Ramp & Pump House)",
                locationDescription = "Basement Level 2, Hydro-Pneumatic Water Pumps & Diesel Generator Room B",
                qrPayload = "ARIHANT-PATROL-P2-817264",
                assignedShift = "24x7 Rotational Patrol",
                assignedGuard = "Surendra Singh",
                assignedGuardPhone = "+91 98201 55662",
                lastScanEpochMillis = now - (48 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (48 * 60 * 1000L))),
                lastScanByGuard = "Surendra Singh",
                scanIntervalMinutes = 60,
                isMissedAlertActive = false,
                totalScansToday = 11
            ),
            PatrolCheckpointEntity(
                checkpointId = "P3",
                code = "P3",
                checkpointName = "Podium Garden & Swimming Pool Deck",
                locationDescription = "Level 4 Podium Promenade between Tower Kaveh and Baraz-1 Clubhouse Entry",
                qrPayload = "ARIHANT-PATROL-P3-726152",
                assignedShift = "24x7 Rotational Patrol",
                assignedGuard = "Vikas Patil",
                assignedGuardPhone = "+91 98204 77119",
                lastScanEpochMillis = now - (15 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (15 * 60 * 1000L))),
                lastScanByGuard = "Vikas Patil",
                scanIntervalMinutes = 60,
                isMissedAlertActive = false,
                totalScansToday = 14
            ),
            PatrolCheckpointEntity(
                checkpointId = "P4",
                code = "P4",
                checkpointName = "Perimeter Security Fence & Rear Service Gate",
                locationDescription = "Sector 35 Boundary Perimeter wall, Fire Hydrant Post #4 & Service Gate 2",
                qrPayload = "ARIHANT-PATROL-P4-635241",
                assignedShift = "24x7 Rotational Patrol",
                assignedGuard = "Vikas Patil",
                assignedGuardPhone = "+91 98204 77119",
                lastScanEpochMillis = now - (55 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (55 * 60 * 1000L))),
                lastScanByGuard = "Vikas Patil",
                scanIntervalMinutes = 60,
                isMissedAlertActive = false,
                totalScansToday = 10
            ),
            PatrolCheckpointEntity(
                checkpointId = "P5",
                code = "P5",
                checkpointName = "Clubhouse Rooftop & Solar Inverter Deck",
                locationDescription = "Clubhouse Terrace Level 6, Solar Photovoltaic Grid & Overhead Tank Isolation Valves",
                qrPayload = "ARIHANT-PATROL-P5-544332",
                assignedShift = "24x7 Rotational Patrol",
                assignedGuard = "Ramesh Yadav",
                assignedGuardPhone = "+91 98209 44321",
                lastScanEpochMillis = now - (30 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (30 * 60 * 1000L))),
                lastScanByGuard = "Ramesh Yadav",
                scanIntervalMinutes = 60,
                isMissedAlertActive = false,
                totalScansToday = 13
            ),
            PatrolCheckpointEntity(
                checkpointId = "NIGHT_GUARD",
                code = "NG",
                checkpointName = "Night Shift Guard Patrol (Perimeter & Towers)",
                locationDescription = "Tower Kaveh, Baraz-1, Baraz-2, Zenath Stilt Lobbies & Main Security Gate",
                qrPayload = "ARIHANT-PATROL-NG-112233",
                assignedShift = "Night Shift (10:00 PM - 06:00 AM)",
                assignedGuard = "Om Prakash (Night Guard Lead)",
                assignedGuardPhone = "+91 98202 88441",
                lastScanEpochMillis = now - (78 * 60 * 1000L),
                lastScanFormatted = sdf.format(Date(now - (78 * 60 * 1000L))),
                lastScanByGuard = "Om Prakash (Night Guard Lead)",
                scanIntervalMinutes = 60,
                isMissedAlertActive = true,
                missedAlertAcknowledged = false,
                totalScansToday = 7
            )
        )
        appDao.insertAllPatrolCheckpoints(checkpoints)

        // Seed initial staff attendance
        val staffPunchList = listOf(
            StaffAttendanceEntity(
                staffId = "SEC-01",
                staffName = "Surendra Singh",
                staffRole = "Security Guard",
                punchType = "Punch In",
                timestamp = "Today, 08:00 AM",
                epochMillis = now - (4 * 3600 * 1000L),
                latitude = 19.0436,
                longitude = 73.0722,
                locationAddress = "Main Security Gate & Visitor Gate, Arihant Alishan",
                isGeoFenceVerified = true,
                photoSource = "Camera",
                remarks = "Morning shift check-in. Uniform & badge inspected."
            ),
            StaffAttendanceEntity(
                staffId = "SEC-02",
                staffName = "Vikas Patil",
                staffRole = "Security Guard",
                punchType = "Punch In",
                timestamp = "Today, 08:05 AM",
                epochMillis = now - (3 * 3600 * 1000L - 10000),
                latitude = 19.0438,
                longitude = 73.0725,
                locationAddress = "Tower Kaveh Stilt & Podium Gate, Sector 35",
                isGeoFenceVerified = true,
                photoSource = "Camera",
                remarks = "Podium and lobby beat deployment."
            ),
            StaffAttendanceEntity(
                staffId = "HK-01",
                staffName = "Sunita Jadhav",
                staffRole = "Housekeeping Supervisor",
                punchType = "Punch In",
                timestamp = "Today, 08:30 AM",
                epochMillis = now - (3 * 3600 * 1000L),
                latitude = 19.0435,
                longitude = 73.0721,
                locationAddress = "Clubhouse & Baraz Lobby, Arihant Alishan",
                isGeoFenceVerified = true,
                photoSource = "Gallery",
                remarks = "Team briefing and floor mop attendance."
            ),
            StaffAttendanceEntity(
                staffId = "SEC-NG",
                staffName = "Om Prakash",
                staffRole = "Night Shift Guard",
                punchType = "Punch In",
                timestamp = "Yesterday, 10:00 PM",
                epochMillis = now - (9 * 3600 * 1000L),
                latitude = 19.0436,
                longitude = 73.0722,
                locationAddress = "Main Security Cabin, Arihant Alishan",
                isGeoFenceVerified = true,
                photoSource = "Camera",
                remarks = "Night shift reporting. Torch, walkie-talkie & batons issued."
            )
        )
        staffPunchList.forEach { appDao.insertStaffAttendance(it) }
    }
}
