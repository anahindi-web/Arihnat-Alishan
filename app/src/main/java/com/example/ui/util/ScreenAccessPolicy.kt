package com.example.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.UserRole

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * Central role access policy for all application screens in Arihant Alishan.
 * Used for route guarding in ViewModel navigation, filtering More menu items,
 * and dynamically adjusting bottom navigation tabs.
 */
object ScreenAccessPolicy {

    fun isScreenAccessible(screen: String, role: UserRole): Boolean {
        return when (screen) {
            "home", "more", "emergency", "notices" -> true
            "complaints" -> true
            "chat" -> {
                // Chat accessible to residents, committee, security incharge, super admin
                role != UserRole.SECURITY_GUARD && role != UserRole.HOUSEKEEPING_STAFF
            }
            "visitors" -> {
                // Visitors screen accessible to residents, security, committee, super admin
                role != UserRole.HOUSEKEEPING_STAFF
            }
            "amenities" -> {
                // Amenities bookings accessible to residents and committee
                role.isCommitteeMember() || isResidentRole(role)
            }
            "profile" -> {
                // Profile & Household master accessible to residents and committee
                role.isCommitteeMember() || isResidentRole(role)
            }
            "parking" -> {
                // Parking accessible to residents, security team, and committee
                role.isCommitteeMember() || isResidentRole(role) ||
                        role == UserRole.SECURITY_GUARD || role == UserRole.SECURITY_INCHARGE
            }
            "water_lifts" -> {
                // Water & lift telemetry accessible to residents, committee, facility supervisors
                role.isCommitteeMember() || isResidentRole(role) ||
                        role == UserRole.SECURITY_INCHARGE || role == UserRole.HOUSEKEEPING_SUPERVISOR
            }
            "maintenance" -> {
                // Maintenance accessible to residents and committee/finance
                role.isCommitteeMember() || isResidentRole(role)
            }
            "service_requests" -> {
                // Service requests accessible to residents and committee
                role.isCommitteeMember() || isResidentRole(role)
            }
            "governance" -> {
                // Governance & approvals restricted to committee members & super admin
                role.isCommitteeMember()
            }
            "master_data" -> {
                // Operational Master Data restricted to Chairman, Secretary, Manager, Super Admin
                role.canManageOperationalMasters()
            }
            "super_admin" -> {
                // Super Admin console restricted exclusively to Root Super Administrator
                role.isSuperAdmin()
            }
            "attendance" -> {
                // Staff attendance system for on-ground staff, supervisors, and committee
                role.isCommitteeMember() ||
                        role == UserRole.SECURITY_GUARD || role == UserRole.SECURITY_INCHARGE ||
                        role == UserRole.HOUSEKEEPING_STAFF || role == UserRole.HOUSEKEEPING_SUPERVISOR
            }
            "patrol" -> {
                // Guard patrol checkpoints for security personnel and committee
                role.isCommitteeMember() ||
                        role == UserRole.SECURITY_GUARD || role == UserRole.SECURITY_INCHARGE
            }
            else -> true
        }
    }

    fun isResidentRole(role: UserRole): Boolean {
        return role == UserRole.RESIDENT_OWNER ||
                role == UserRole.FAMILY_MEMBER ||
                role == UserRole.RESIDENT_TENANT
    }

    fun canSwitchHouseholdFlat(role: UserRole): Boolean {
        return role.isSuperAdmin() ||
                role.isCommitteeMember() ||
                role.canViewOtherFlatsAndResidents()
    }

    fun getScreenTitle(screen: String): String {
        return when (screen) {
            "home" -> "Home"
            "complaints" -> "Complaints & Grievances"
            "chat" -> "Community Chat"
            "visitors" -> "Visitor Gate Passes"
            "amenities" -> "Amenity Reservations"
            "more" -> "More Menu"
            "profile" -> "Household Master Record"
            "parking" -> "Multi-Level Parking (P1-P5)"
            "water_lifts" -> "Water & Lift Telemetry"
            "emergency" -> "Emergency Action Center"
            "maintenance" -> "Maintenance & Receipts"
            "service_requests" -> "Permits & NOC Requests"
            "governance" -> "Governance & Approvals"
            "notices" -> "Society Notice Board"
            "master_data" -> "Master Data & Rules"
            "super_admin" -> "Super Admin Console"
            "attendance" -> "Staff Attendance"
            "patrol" -> "Guard Patrol System"
            else -> screen.replace('_', ' ').replaceFirstChar { it.uppercase() }
        }
    }

    fun getAccessibleBottomBarItems(role: UserRole): List<BottomNavItem> {
        val allPossibleItems = when {
            role == UserRole.SECURITY_GUARD -> listOf(
                BottomNavItem("home", "Home", Icons.Default.Home),
                BottomNavItem("visitors", "Visitors", Icons.Default.Badge),
                BottomNavItem("patrol", "Patrol", Icons.Default.QrCodeScanner),
                BottomNavItem("emergency", "SOS", Icons.Default.Emergency),
                BottomNavItem("more", "More", Icons.Default.Dashboard)
            )
            role == UserRole.HOUSEKEEPING_STAFF -> listOf(
                BottomNavItem("home", "Home", Icons.Default.Home),
                BottomNavItem("attendance", "Punch In", Icons.Default.HowToReg),
                BottomNavItem("complaints", "Tasks", Icons.Default.Build),
                BottomNavItem("emergency", "SOS", Icons.Default.Emergency),
                BottomNavItem("more", "More", Icons.Default.Dashboard)
            )
            role.isCommitteeMember() -> listOf(
                BottomNavItem("home", "Home", Icons.Default.Home),
                BottomNavItem("governance", "Gov", Icons.Default.AccountBalance),
                BottomNavItem("complaints", "Complaints", Icons.Default.Build),
                BottomNavItem("chat", "Chat", Icons.Default.Forum),
                BottomNavItem("more", "More", Icons.Default.Dashboard)
            )
            else -> listOf(
                BottomNavItem("home", "Home", Icons.Default.Home),
                BottomNavItem("complaints", "Complaints", Icons.Default.Build),
                BottomNavItem("chat", "Chat", Icons.Default.Forum),
                BottomNavItem("amenities", "Amenities", Icons.Default.Pool),
                BottomNavItem("more", "More", Icons.Default.Dashboard)
            )
        }

        return allPossibleItems.filter { isScreenAccessible(it.route, role) }
    }
}
