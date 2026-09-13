package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        FlatEntity::class,
        FamilyMemberEntity::class,
        TenantEntity::class,
        VehicleEntity::class,
        ParkingSlotEntity::class,
        PetEntity::class,
        DomesticHelpEntity::class,
        ComplaintEntity::class,
        ComplaintCommentEntity::class,
        VisitorEntity::class,
        AmenityEntity::class,
        AmenityBookingEntity::class,
        MaintenanceBillEntity::class,
        SocietyNoticeEntity::class,
        CommitteeTaskEntity::class,
        SocietyMeetingEntity::class,
        AuditLogEntity::class,
        SocietyDocumentEntity::class,
        ServiceRequestEntity::class,
        SocietyConfigEntity::class,
        VendorMasterEntity::class,
        CommitteeMasterEntity::class,
        SocietyRuleEntity::class,
        StaffAttendanceEntity::class,
        PatrolCheckpointEntity::class,
        PatrolScanLogEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arihant_alishan.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
