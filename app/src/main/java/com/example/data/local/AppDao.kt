package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Flats
    @Query("SELECT * FROM flats")
    fun getAllFlats(): Flow<List<FlatEntity>>

    @Query("SELECT * FROM flats WHERE flatId = :flatId LIMIT 1")
    fun getFlatById(flatId: String): Flow<FlatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlat(flat: FlatEntity)

    @Update
    suspend fun updateFlat(flat: FlatEntity)

    @Query("DELETE FROM flats WHERE flatId = :flatId")
    suspend fun deleteFlat(flatId: String)

    // Family Members
    @Query("SELECT * FROM family_members WHERE flatId = :flatId")
    fun getFamilyMembers(flatId: String): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity)

    @Update
    suspend fun updateFamilyMember(member: FamilyMemberEntity)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteFamilyMember(id: Long)

    // Tenants
    @Query("SELECT * FROM tenants WHERE flatId = :flatId ORDER BY id DESC")
    fun getTenantsForFlat(flatId: String): Flow<List<TenantEntity>>

    @Query("SELECT * FROM tenants ORDER BY id DESC")
    fun getAllTenants(): Flow<List<TenantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: TenantEntity)

    @Update
    suspend fun updateTenant(tenant: TenantEntity)

    @Query("DELETE FROM tenants WHERE id = :id")
    suspend fun deleteTenant(id: Long)

    // Vehicles
    @Query("SELECT * FROM vehicles WHERE flatId = :flatId")
    fun getVehiclesForFlat(flatId: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicle(id: Long)

    // Parking
    @Query("SELECT * FROM parking_slots ORDER BY slotNumber ASC")
    fun getAllParkingSlots(): Flow<List<ParkingSlotEntity>>

    @Query("SELECT * FROM parking_slots WHERE level = :level ORDER BY slotNumber ASC")
    fun getParkingSlotsByLevel(level: String): Flow<List<ParkingSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingSlot(slot: ParkingSlotEntity)

    @Update
    suspend fun updateParkingSlot(slot: ParkingSlotEntity)

    // Pets
    @Query("SELECT * FROM pets WHERE flatId = :flatId")
    fun getPetsForFlat(flatId: String): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<PetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    @Update
    suspend fun updatePet(pet: PetEntity)

    // Domestic Help
    @Query("SELECT * FROM domestic_help WHERE flatId = :flatId")
    fun getDomesticHelpForFlat(flatId: String): Flow<List<DomesticHelpEntity>>

    @Query("SELECT * FROM domestic_help")
    fun getAllDomesticHelp(): Flow<List<DomesticHelpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomesticHelp(help: DomesticHelpEntity)

    // Complaints
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE submittedBy = :flatId ORDER BY createdAt DESC")
    fun getComplaintsForFlat(flatId: String): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1")
    fun getComplaintById(id: String): Flow<ComplaintEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    // Complaint comments
    @Query("SELECT * FROM complaint_comments WHERE complaintId = :complaintId ORDER BY id ASC")
    fun getCommentsForComplaint(complaintId: String): Flow<List<ComplaintCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaintComment(comment: ComplaintCommentEntity)

    // Visitors
    @Query("SELECT * FROM visitors ORDER BY id DESC")
    fun getAllVisitors(): Flow<List<VisitorEntity>>

    @Query("SELECT * FROM visitors WHERE flat = :flat ORDER BY id DESC")
    fun getVisitorsForFlat(flat: String): Flow<List<VisitorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: VisitorEntity)

    @Update
    suspend fun updateVisitor(visitor: VisitorEntity)

    // Amenities
    @Query("SELECT * FROM amenities")
    fun getAllAmenities(): Flow<List<AmenityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenity(amenity: AmenityEntity)

    @Query("SELECT * FROM amenity_bookings ORDER BY id DESC")
    fun getAllAmenityBookings(): Flow<List<AmenityBookingEntity>>

    @Query("SELECT * FROM amenity_bookings WHERE flat = :flat ORDER BY id DESC")
    fun getBookingsForFlat(flat: String): Flow<List<AmenityBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenityBooking(booking: AmenityBookingEntity)

    @Update
    suspend fun updateAmenityBooking(booking: AmenityBookingEntity)

    // Maintenance Bills
    @Query("SELECT * FROM maintenance_bills ORDER BY id DESC")
    fun getAllMaintenanceBills(): Flow<List<MaintenanceBillEntity>>

    @Query("SELECT * FROM maintenance_bills WHERE flat = :flat ORDER BY id DESC")
    fun getMaintenanceBillsForFlat(flat: String): Flow<List<MaintenanceBillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceBill(bill: MaintenanceBillEntity)

    @Update
    suspend fun updateMaintenanceBill(bill: MaintenanceBillEntity)

    // Notices
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<SocietyNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: SocietyNoticeEntity)

    @Update
    suspend fun updateNotice(notice: SocietyNoticeEntity)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNotice(id: String)

    // Committee Tasks
    @Query("SELECT * FROM committee_tasks ORDER BY id DESC")
    fun getAllCommitteeTasks(): Flow<List<CommitteeTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeTask(task: CommitteeTaskEntity)

    @Update
    suspend fun updateCommitteeTask(task: CommitteeTaskEntity)

    // Meetings
    @Query("SELECT * FROM society_meetings ORDER BY meetingDate DESC")
    fun getAllMeetings(): Flow<List<SocietyMeetingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: SocietyMeetingEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Society Documents
    @Query("SELECT * FROM society_documents")
    fun getAllDocuments(): Flow<List<SocietyDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: SocietyDocumentEntity)

    // Service Requests
    @Query("SELECT * FROM service_requests ORDER BY id DESC")
    fun getAllServiceRequests(): Flow<List<ServiceRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceRequest(request: ServiceRequestEntity)

    @Update
    suspend fun updateServiceRequest(request: ServiceRequestEntity)

    // Society Config Master
    @Query("SELECT * FROM society_config WHERE id = 'MAIN_CONFIG' LIMIT 1")
    fun getSocietyConfig(): Flow<SocietyConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocietyConfig(config: SocietyConfigEntity)

    @Update
    suspend fun updateSocietyConfig(config: SocietyConfigEntity)

    // Vendor Master
    @Query("SELECT * FROM vendor_master ORDER BY id ASC")
    fun getAllVendors(): Flow<List<VendorMasterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: VendorMasterEntity)

    @Update
    suspend fun updateVendor(vendor: VendorMasterEntity)

    @Query("DELETE FROM vendor_master WHERE id = :id")
    suspend fun deleteVendor(id: String)

    // Committee Directory Master
    @Query("SELECT * FROM committee_directory ORDER BY id ASC")
    fun getCommitteeDirectory(): Flow<List<CommitteeMasterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeMember(member: CommitteeMasterEntity)

    @Update
    suspend fun updateCommitteeMember(member: CommitteeMasterEntity)

    @Query("DELETE FROM committee_directory WHERE id = :id")
    suspend fun deleteCommitteeMember(id: String)

    // Amenity update
    @Update
    suspend fun updateAmenity(amenity: AmenityEntity)

    // Society Rules & Bylaws
    @Query("SELECT * FROM society_rules ORDER BY displayOrder ASC, id ASC")
    fun getAllSocietyRules(): Flow<List<SocietyRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocietyRule(rule: SocietyRuleEntity)

    @Update
    suspend fun updateSocietyRule(rule: SocietyRuleEntity)

    @Query("DELETE FROM society_rules WHERE id = :id")
    suspend fun deleteSocietyRule(id: Long)

    // All family members across society
    @Query("SELECT * FROM family_members ORDER BY id DESC")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    // Staff Attendance
    @Query("SELECT * FROM staff_attendance ORDER BY epochMillis DESC")
    fun getAllStaffAttendance(): Flow<List<StaffAttendanceEntity>>

    @Query("SELECT * FROM staff_attendance WHERE staffId = :staffId ORDER BY epochMillis DESC")
    fun getAttendanceForStaff(staffId: String): Flow<List<StaffAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffAttendance(attendance: StaffAttendanceEntity)

    // Patrol Checkpoints (P1, P2, P3, P4, P5, Night Shift Guard)
    @Query("SELECT * FROM patrol_checkpoints ORDER BY checkpointId ASC")
    fun getAllPatrolCheckpoints(): Flow<List<PatrolCheckpointEntity>>

    @Query("SELECT * FROM patrol_checkpoints WHERE checkpointId = :id LIMIT 1")
    fun getCheckpointById(id: String): Flow<PatrolCheckpointEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatrolCheckpoint(checkpoint: PatrolCheckpointEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPatrolCheckpoints(checkpoints: List<PatrolCheckpointEntity>)

    @Update
    suspend fun updatePatrolCheckpoint(checkpoint: PatrolCheckpointEntity)

    // Patrol Scan Logs
    @Query("SELECT * FROM patrol_scan_logs ORDER BY scanEpochMillis DESC")
    fun getAllPatrolScanLogs(): Flow<List<PatrolScanLogEntity>>

    @Query("SELECT * FROM patrol_scan_logs WHERE checkpointId = :checkpointId ORDER BY scanEpochMillis DESC")
    fun getScanLogsForCheckpoint(checkpointId: String): Flow<List<PatrolScanLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatrolScanLog(log: PatrolScanLogEntity)
}
