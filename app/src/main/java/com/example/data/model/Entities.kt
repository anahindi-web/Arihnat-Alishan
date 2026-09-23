package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val displayName: String) {
    SUPER_ADMIN("Super Admin"),
    CHAIRMAN("Chairman"),
    SECRETARY("Secretary"),
    TREASURER("Treasurer"),
    COMMITTEE_MEMBER("Committee Member"),
    SOCIETY_MANAGER("Society Manager"),
    SECURITY_INCHARGE("Security Incharge"),
    SECURITY_GUARD("Security Guard"),
    HOUSEKEEPING_SUPERVISOR("Housekeeping Supervisor"),
    HOUSEKEEPING_STAFF("Housekeeping Staff"),
    RESIDENT_OWNER("Flat Owner"),
    FAMILY_MEMBER("Family Member"),
    RESIDENT_TENANT("Resident (Tenant)");

    fun isCommitteeMember(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == TREASURER ||
        this == COMMITTEE_MEMBER || this == SOCIETY_MANAGER

    fun isSuperAdmin(): Boolean = this == SUPER_ADMIN

    // Role-based access methods
    fun canViewAllSocietyComplaints(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == TREASURER ||
        this == COMMITTEE_MEMBER || this == SOCIETY_MANAGER

    fun canAmendComplaints(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == TREASURER ||
        this == COMMITTEE_MEMBER || this == SOCIETY_MANAGER

    fun canAmendNotices(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == COMMITTEE_MEMBER ||
        this == SOCIETY_MANAGER

    fun canManageMembers(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER ||
        this == COMMITTEE_MEMBER

    fun canAddMember(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER

    fun canGenerateInvitations(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER ||
        this == COMMITTEE_MEMBER

    fun canViewOtherFlatsAndResidents(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == TREASURER ||
        this == COMMITTEE_MEMBER || this == SOCIETY_MANAGER

    fun canViewSocietyFinances(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == TREASURER ||
        this == SOCIETY_MANAGER

    // Operational Masters vs Protected Flat Owner Personal Details
    fun canManageOperationalMasters(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER

    fun canDirectlyEditFlatOwnerPersonalDetails(): Boolean =
        this == SUPER_ADMIN // Chairman CANNOT directly edit protected Flat Owner personal details!

    fun canViewEmergencyVolunteerDirectory(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER

    fun canManageSecurity(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER ||
        this == SECURITY_INCHARGE

    fun canManageHousekeeping(): Boolean =
        this == SUPER_ADMIN || this == CHAIRMAN || this == SECRETARY || this == SOCIETY_MANAGER ||
        this == HOUSEKEEPING_SUPERVISOR
}

@Entity(tableName = "flats")
data class FlatEntity(
    @PrimaryKey val flatId: String, // e.g. "K-302", "K-1204"
    val tower: String, // "Kaveh", "Baraz-1", "Baraz-2", "Zenath"
    val floor: Int,
    val flatNumber: String, // "302", "1204"
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val possessionDate: String,
    val occupancyStatus: String, // "Self Occupied", "Rented", "Vacant", "Temporarily Occupied"
    val isVerified: Boolean = true,
    val flatType: String = "2 BHK Royal",
    val areaSqFt: Int = 1150,
    val coOwnerName: String = "",
    val coOwnerPhone: String = "",
    // Optional Professional / Business Information
    val occupation: String = "Salaried", // Salaried, Self-employed, Business Owner, Professional, Retired, Homemaker, Other
    val jobOrEmployment: String = "",
    val industry: String = "",
    val companyOrBusinessName: String = "",
    val skills: String = "",
    val howCanHelpSociety: String = "", // e.g. "IT / Technology, First Aid"
    // Optional Emergency Volunteer Information
    val emergencyVolunteer: String = "No", // "Yes", "No", "Maybe"
    val volunteerAreas: String = "", // e.g. "First Aid, Evacuation Support"
    val volunteerPhone: String = ""
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val fullName: String,
    val relationship: String, // "Spouse", "Son", "Daughter", "Parent", "Sibling", "Other Family Member"
    val memberType: String = "Spouse",
    val gender: String,
    val dob: String,
    val calculatedAge: Int,
    val phone: String = "",
    val email: String = "",
    val photoUri: String = "", // Compulsory profile photo
    val isChild: Boolean = false,
    val isChildBelow16: Boolean = false,
    val parentGuardianName: String = "",
    val parentGuardianPhone: String = "",
    val hasAppAccess: Boolean = false,
    val inviteStatus: String = "Not Invited", // "Not Invited", "Invited", "Active"
    val isEmergencyContact: Boolean = false,
    val isResident: Boolean = true,
    val schoolName: String = "",
    val schoolAddress: String = "",
    val grade: String = "",
    val schoolContact: String = "",
    val notes: String = "",
    val status: String = "Active" // "Active", "Deactivated"
)

@Entity(tableName = "tenants")
data class TenantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val fullName: String,
    val phone: String,
    val altPhone: String = "",
    val email: String,
    val photoUri: String = "", // Compulsory profile photo
    val dobOrAge: String = "",
    val permanentAddress: String,
    val occupation: String,
    val employer: String = "",
    val familyMemberCount: Int = 1,
    val moveInDate: String,
    val expectedMoveOutDate: String,
    val status: String, // "Pending Verification", "Active", "Expiring Soon", "Expired", "Vacated"
    val agreementDocument: String, // URI or File Path
    val agreementStartDate: String,
    val agreementExpiryDate: String,
    val agreementStatus: String, // "Valid", "Expiring Soon", "Expired", "Document Missing"
    val daysRemaining: Int = 0,
    val policeVerificationDocument: String = "",
    val policeVerificationStatus: String = "Pending", // "Verified", "Submitted", "Pending"
    val verificationStatus: String = "Pending", // "Pending", "Verified", "Correction Required", "Rejected"
    val verificationComments: String = "",
    val isComplete: Boolean = false,
    val tenantVehicleType: String = "None", // "Car", "Bike", "Scooter", "None"
    val tenantVehicleNumber: String = "",
    val tenantVehicleModel: String = "",
    val tenantParkingSlot: String = ""
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val tower: String,
    val vehicleType: String, // "Four Wheeler", "Two Wheeler"
    val makeModel: String,
    val registrationNumber: String,
    val color: String,
    val fuelType: String, // "Petrol", "Diesel", "EV", "CNG"
    val isEv: Boolean = false,
    val allottedSlot: String = "",
    val status: String = "Active"
)

@Entity(tableName = "parking_slots")
data class ParkingSlotEntity(
    @PrimaryKey val slotNumber: String, // "P1-034"
    val level: String, // "P1", "P2", "P3", "P4", "P5"
    val status: String, // "Available", "Allocated", "Visitor", "Blocked", "Maintenance"
    val allottedFlat: String = "",
    val allottedTower: String = "",
    val ownerName: String = "",
    val vehiclePlate: String = "",
    val hasEvCharger: Boolean = false,
    val slotType: String = "Car",
    val remarks: String = ""
)

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val petName: String,
    val petType: String, // "Dog", "Cat", "Bird", "Other"
    val breed: String,
    val gender: String,
    val age: String,
    val registrationNumber: String,
    val vaccinationStatus: String, // "Valid", "Expiring Soon", "Expired"
    val vaccinationExpiryDate: String,
    val certificateDoc: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "domestic_help")
data class DomesticHelpEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val name: String,
    val category: String, // "House Maid", "Cook", "Driver", "Nanny", "Caretaker"
    val phone: String,
    val passId: String,
    val validity: String,
    val status: String = "Active",
    val currentlyInside: Boolean = false
)

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey val id: String, // "AA-1024"
    val category: String, // "Water", "Lift", "Parking", "Plumbing", "Garbage", etc.
    val subcategory: String,
    val tower: String,
    val floor: Int,
    val location: String,
    val description: String,
    val photoUri: String = "",
    val priority: String, // "Critical", "High", "Medium", "Low"
    val status: String, // "Submitted", "Acknowledged", "Assigned", "In Progress", "Resolved", "Closed"
    val submittedBy: String,
    val assignedTo: String = "",
    val expectedResolution: String = "",
    val createdAt: String,
    val resolvedAt: String = "",
    val slaHours: Int = 24,
    val isSlaBreached: Boolean = false,
    val duplicateReportCount: Int = 1,
    val isRecurring: Boolean = false,
    val reopenedCount: Int = 0,
    val ticketIdFormatted: String = "",
    val raisedByMemberName: String = "",
    val raisedByMemberType: String = "Flat Owner", // "Flat Owner", "Family Member", "Tenant"
    val visibility: String = "All Authorised Members of This Flat" // "All Authorised Members of This Flat", "Raised By Only", "Owner and Raised By Only", "Committee Only"
)

@Entity(tableName = "complaint_comments")
data class ComplaintCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val complaintId: String,
    val authorName: String,
    val authorRole: String,
    val commentText: String,
    val timestamp: String
)

@Entity(tableName = "visitors")
data class VisitorEntity(
    @PrimaryKey val id: String, // "VIS-9201"
    val passCode: String,
    val visitorName: String,
    val phone: String,
    val flat: String,
    val tower: String,
    val type: String, // "Guest", "Delivery", "Cab", "Domestic Help", "Vendor"
    val company: String = "", // "Amazon", "Swiggy", "Zomato", "Uber"
    val expectedArrival: String,
    val status: String, // "Waiting Approval", "Approved", "Inside", "Exited", "Rejected"
    val inTime: String = "",
    val outTime: String = ""
)

@Entity(tableName = "amenities")
data class AmenityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val rules: String,
    val timings: String,
    val capacity: Int,
    val bookingFee: Int,
    val securityDeposit: Int,
    val iconType: String, // "clubhouse", "gym", "pool", "party_hall", "sports", "play_area"
    val status: String = "Available"
)

@Entity(tableName = "amenity_bookings")
data class AmenityBookingEntity(
    @PrimaryKey val id: String, // "BK-802"
    val amenityId: String,
    val amenityName: String,
    val flat: String,
    val residentName: String,
    val bookingDate: String,
    val timeSlot: String,
    val status: String, // "Pending", "Approved", "Confirmed", "Completed", "Cancelled"
    val feePaid: Int
)

@Entity(tableName = "maintenance_bills")
data class MaintenanceBillEntity(
    @PrimaryKey val id: String, // "INV-2026-09"
    val flat: String,
    val tower: String,
    val monthYear: String,
    val totalAmount: Int,
    val dueDate: String,
    val status: String, // "Paid", "Pending", "Overdue"
    val paymentMethod: String = "",
    val paidAt: String = "",
    val receiptNumber: String = "",
    val sinkingFund: Int = 1200,
    val repairFund: Int = 850,
    val waterCharges: Int = 1100,
    val serviceCharges: Int = 1500,
    val electricityCharges: Int = 1200
)

@Entity(tableName = "notices")
data class SocietyNoticeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val category: String, // "Water", "Lift", "Maintenance", "Emergency", "Security", "Event", "General", "AGM/EGM", "Finance"
    val targetTower: String, // "ALL", "Kaveh", "Baraz-1", "Baraz-2", "Zenath"
    val priority: String, // "Normal", "High", "Emergency", "Critical"
    val publishedBy: String,
    val publishDate: String,
    val isAcknowledged: Boolean = false,
    val isPinned: Boolean = false,
    val circularNo: String = "",
    val acknowledgedCount: Int = 18
)

@Entity(tableName = "committee_tasks")
data class CommitteeTaskEntity(
    @PrimaryKey val id: String, // "TSK-301"
    val title: String,
    val description: String,
    val category: String, // "Maintenance", "Parking", "Security", "Finance", "Governance"
    val priority: String, // "Critical", "High", "Medium", "Low"
    val assignedTo: String,
    val assignedRole: String,
    val dueDate: String,
    val tower: String = "",
    val location: String = "",
    val status: String // "New", "In Progress", "Completed", "Verified"
)

@Entity(tableName = "society_meetings")
data class SocietyMeetingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val meetingDate: String,
    val agenda: String,
    val decisions: String,
    val actionItem: String,
    val attendeeCount: Int,
    val location: String = "Clubhouse Banquet Hall",
    val status: String = "Scheduled"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: String,
    val userName: String,
    val userRole: String,
    val action: String,
    val module: String, // "Tenant", "Resident", "Parking", "Complaint", "Payment", "Verification"
    val recordAffected: String,
    val details: String,
    val oldValue: String = "",
    val newValue: String = ""
)

@Entity(tableName = "society_documents")
data class SocietyDocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Bylaws", "AMC", "Compliance", "Insurance", "Fire Safety", "Meeting Minutes"
    val docType: String, // "PDF", "DOC"
    val uploadDate: String,
    val expiryDate: String,
    val verifiedBy: String,
    val version: String
)

@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey val id: String, // "SR-101"
    val type: String, // "Move-In NOC", "Move-Out Clearance", "Renovation & Contractor Permit", "NOC Issuance", "Parking Reallocation"
    val flat: String,
    val tower: String,
    val details: String,
    val contractorName: String = "",
    val workerCount: Int = 0,
    val status: String, // "Pending", "Approved", "Rejected"
    val createdAt: String
)

@Entity(tableName = "society_config")
data class SocietyConfigEntity(
    @PrimaryKey val id: String = "MAIN_CONFIG",
    val societyName: String = "Arihant Alishan Co-operative Housing Society Ltd.",
    val regNumber: String = "NMMC/WAR/CHS/2026/894",
    val registrationDate: String = "14-Aug-2022",
    val actCompliance: String = "Maharashtra Co-operative Societies Act 1960",
    val address: String = "Plot No. 1, Sector 35, Kharghar, Navi Mumbai - 410210",
    val totalTowers: Int = 4,
    val totalFloors: Int = 53,
    val totalUnits: Int = 960,
    val baseMaintenanceRateSqFt: Double = 3.50,
    val sinkingFundRate: Double = 0.25,
    val repairFundRate: Double = 0.75,
    val latePaymentInterestPercent: Double = 18.0,
    val moveInCharges: Int = 5000,
    val moveOutClearanceCharges: Int = 2500,
    val emergencyPhone: String = "+91 22 2774 9900",
    val officeHours: String = "10:00 AM - 1:00 PM & 5:00 PM - 8:00 PM (Tue Closed)",
    val bankName: String = "HDFC Bank Ltd., Kharghar Sector 35 Branch",
    val bankAccountNumber: String = "50200088192301",
    val bankIfsc: String = "HDFC0004921",
    val appTheme: String = "ROYAL_GOLD",
    val lastAmendedBy: String = "Shailesh B. Kulkarni (Chairman)",
    val lastAmendedDate: String = "12 Sep 2026, 11:30 AM"
)

@Entity(tableName = "society_rules")
data class SocietyRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Parking & Vehicles", "Pets & Animals", "Quiet Hours & Noise", "Waste & Sanitation", "Clubhouse & Sports", "Renovation & Construction", "Security & Visitors"
    val ruleTitle: String,
    val ruleDescription: String,
    val penaltyAmount: Int = 0,
    val isMandatory: Boolean = true,
    val displayOrder: Int = 1,
    val lastUpdated: String = "13 Sep 2026"
)

@Entity(tableName = "vendor_master")
data class VendorMasterEntity(
    @PrimaryKey val id: String, // "VND-101"
    val category: String, // "Elevator AMC", "Security Services", "Water Systems", "Housekeeping", "Fire Fighting", "Pest Control", "DG & Electrical"
    val agencyName: String,
    val contactPerson: String,
    val contactPhone: String,
    val contactEmail: String = "",
    val contractStartDate: String,
    val contractExpiryDate: String,
    val monthlyCharges: Int,
    val emergencyHelpline: String,
    val status: String = "Active", // "Active", "Under Review", "Terminated"
    val amcRefNumber: String = "",
    val notes: String = ""
)

@Entity(tableName = "committee_directory")
data class CommitteeMasterEntity(
    @PrimaryKey val id: String, // "CM-01"
    val roleTitle: String, // "Chairman", "Hon. Secretary", "Hon. Treasurer", "Member - Parking", "Member - Security", "Member - Cultural & Sports"
    val fullName: String,
    val flatId: String,
    val phone: String,
    val email: String,
    val term: String = "2024 - 2029",
    val isBankSignatory: Boolean = false,
    val portfolioDescription: String = ""
)

@Entity(tableName = "staff_attendance")
data class StaffAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffId: String,
    val staffName: String,
    val staffRole: String, // "Security Guard", "Night Shift Guard", "Housekeeping Supervisor", "Facility Tech", "Gardener", "Estate Manager"
    val punchType: String, // "Punch In", "Punch Out"
    val timestamp: String,
    val epochMillis: Long,
    val latitude: Double,
    val longitude: Double,
    val locationAddress: String,
    val isGeoFenceVerified: Boolean = true,
    val photoSource: String, // "Camera", "Gallery"
    val photoUri: String = "", // URI or image asset/Base64 identifier
    val remarks: String = ""
)

@Entity(tableName = "patrol_checkpoints")
data class PatrolCheckpointEntity(
    @PrimaryKey val checkpointId: String, // "P1", "P2", "P3", "P4", "P5", "NIGHT_GUARD"
    val code: String, // "P1", "P2", "P3", "P4", "P5", "NG"
    val checkpointName: String,
    val locationDescription: String,
    val qrPayload: String, // "ARIHANT-CHECKPOINT-P1-8931"
    val assignedShift: String, // "24x7 Rotational", "Night Shift (10 PM - 6 AM)"
    val assignedGuard: String,
    val assignedGuardPhone: String,
    val lastScanEpochMillis: Long,
    val lastScanFormatted: String,
    val lastScanByGuard: String,
    val scanIntervalMinutes: Int = 60, // Scanned every 1 hour
    val isMissedAlertActive: Boolean = false,
    val missedAlertAcknowledged: Boolean = false,
    val totalScansToday: Int = 0
)

@Entity(tableName = "patrol_scan_logs")
data class PatrolScanLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val checkpointId: String,
    val checkpointName: String,
    val guardName: String,
    val guardRole: String,
    val scanTimestamp: String,
    val scanEpochMillis: Long,
    val scanStatus: String, // "On-Time", "Delayed", "Missed Recovered"
    val latitude: Double = 19.0435,
    val longitude: Double = 73.0721,
    val notes: String = ""
)

@Entity(tableName = "invitations")
data class InvitationEntity(
    @PrimaryKey val inviteCode: String, // e.g. "INV-9821"
    val token: String,
    val targetFlat: String,
    val targetTower: String,
    val assignedRole: String, // "Resident (Owner)", "Resident (Tenant)", "Family Member"
    val recipientName: String,
    val recipientPhone: String = "",
    val inviteLink: String,
    val createdAtEpoch: Long,
    val expiresAtEpoch: Long,
    val isSingleUse: Boolean = true,
    val isUsed: Boolean = false,
    val usedAtEpoch: Long = 0,
    val createdBy: String,
    val status: String = "ACTIVE" // "ACTIVE", "USED", "EXPIRED", "REVOKED"
)

@Entity(tableName = "owner_profile_corrections")
data class OwnerProfileCorrectionRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val ownerName: String,
    val requestedBy: String,
    val requestedAt: String,
    val fieldToChange: String, // "Full Name", "Mobile Number", "Email", "Co-Owner", "Ownership Document"
    val currentValue: String,
    val proposedValue: String,
    val reason: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val reviewedBy: String = "",
    val reviewedAt: String = "",
    val reviewNotes: String = ""
)

@Entity(tableName = "rent_agreement_notifications")
data class RentAgreementNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatId: String,
    val tenantName: String,
    val ownerName: String,
    val expiryDate: String,
    val daysExpiredOrRemaining: Int,
    val notificationMessage: String,
    val isExpired: Boolean = true,
    val isRead: Boolean = false,
    val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "society_chat_messages")
data class SocietyChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderFlatId: String,
    val senderRole: String,
    val message: String,
    val timestamp: String,
    val epochMillis: Long = System.currentTimeMillis(),
    val channel: String = "General Society", // "General Society", "Tower Kaveh", "Buy & Sell / Help", "Events & Sports"
    val isAnnouncement: Boolean = false
)

@Entity(tableName = "master_units")
data class MasterUnitEntity(
    @PrimaryKey val masterId: String, // e.g. "MST-K302", "MST-K1204", "MST-OPS-01", "MST-SEC-01"
    val masterName: String,
    val masterType: String, // "Residential Unit", "Society Committee", "Security Wing", "Facility Maintenance"
    val headOfMaster: String,
    val contactPhone: String,
    val contactEmail: String,
    val assignedUnit: String = "",
    val status: String = "Active", // "Active", "Inactive", "Deactivated"
    val maxUsersAllowed: Int = 10,
    val notes: String = "",
    val createdDate: String = "15-Aug-2022"
)

@Entity(tableName = "system_users")
data class SystemUserEntity(
    @PrimaryKey val userId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val roleName: String, // e.g. "Flat Owner", "Family Member", "Resident (Tenant)", "Security Guard", "Committee Member"
    val linkedMasterId: String, // Links directly to a MasterUnitEntity
    val linkedMasterName: String,
    val status: String = "Active", // "Active", "Deactivated", "Suspended"
    val permissionsSummary: String = "Standard Household Access",
    val photoUri: String = "",
    val flatId: String = "K-302"
)




