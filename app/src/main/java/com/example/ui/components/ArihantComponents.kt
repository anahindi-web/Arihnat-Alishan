package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.CommitteeTaskEntity
import com.example.data.model.UserRole
import com.example.ui.ArihantViewModel
import com.example.ui.theme.*
import com.example.ui.util.ImageCaptureHelper
import com.example.ui.util.SoundNotificationHelper

enum class CandyFlavor(
    val topGradient: Color,
    val bottomGradient: Color,
    val bottomRim: Color,
    val contentColor: Color = Color.White
) {
    SAPPHIRE(
        topGradient = Color(0xFF38BDF8),
        bottomGradient = Color(0xFF0284C7),
        bottomRim = Color(0xFF0369A1)
    ),
    RUBY(
        topGradient = Color(0xFFFB7185),
        bottomGradient = Color(0xFFE11D48),
        bottomRim = Color(0xFF9F1239)
    ),
    EMERALD(
        topGradient = Color(0xFF34D399),
        bottomGradient = Color(0xFF059669),
        bottomRim = Color(0xFF065F46)
    ),
    AMBER(
        topGradient = Color(0xFFFDE047),
        bottomGradient = Color(0xFFD97706),
        bottomRim = Color(0xFF92400E),
        contentColor = Color(0xFF1E293B)
    ),
    PURPLE(
        topGradient = Color(0xFFD8B4FE),
        bottomGradient = Color(0xFF9333EA),
        bottomRim = Color(0xFF6B21A8)
    ),
    NAVY(
        topGradient = Color(0xFF3B82F6),
        bottomGradient = Color(0xFF1E3A8A),
        bottomRim = Color(0xFF0F172A)
    ),
    GOLD(
        topGradient = Color(0xFFFEF08A),
        bottomGradient = Color(0xFFCA8A04),
        bottomRim = Color(0xFF854D0E),
        contentColor = Color(0xFF1E293B)
    ),
    TEAL(
        topGradient = Color(0xFF2DD4BF),
        bottomGradient = Color(0xFF0D9488),
        bottomRim = Color(0xFF115E59)
    ),
    CORAL(
        topGradient = Color(0xFFFB923C),
        bottomGradient = Color(0xFFEA580C),
        bottomRim = Color(0xFF9A3412)
    ),
    INDIGO(
        topGradient = Color(0xFF818CF8),
        bottomGradient = Color(0xFF4F46E5),
        bottomRim = Color(0xFF3730A3)
    )
}

enum class LightCandyFlavor(
    val topGradient: Color,
    val bottomGradient: Color,
    val borderStroke: Color,
    val contentColor: Color,
    val iconTint: Color,
    val shadowTint: Color
) {
    PASTEL_SKY(
        topGradient = Color(0xFFF0F9FF),
        bottomGradient = Color(0xFFE0F2FE),
        borderStroke = Color(0xFF7DD3FC),
        contentColor = Color(0xFF0369A1),
        iconTint = Color(0xFF0284C7),
        shadowTint = Color(0xFF38BDF8)
    ),
    PASTEL_ROSE(
        topGradient = Color(0xFFFFF1F2),
        bottomGradient = Color(0xFFFCE7F3),
        borderStroke = Color(0xFFF472B6),
        contentColor = Color(0xFF9D174D),
        iconTint = Color(0xFFE11D48),
        shadowTint = Color(0xFFFB7185)
    ),
    PASTEL_MINT(
        topGradient = Color(0xFFF0FDF4),
        bottomGradient = Color(0xFFD1FAE5),
        borderStroke = Color(0xFF6EE7B7),
        contentColor = Color(0xFF065F46),
        iconTint = Color(0xFF059669),
        shadowTint = Color(0xFF34D399)
    ),
    PASTEL_PEACH(
        topGradient = Color(0xFFFFF7ED),
        bottomGradient = Color(0xFFFFEDD5),
        borderStroke = Color(0xFFFDBA74),
        contentColor = Color(0xFF9A3412),
        iconTint = Color(0xFFEA580C),
        shadowTint = Color(0xFFFB923C)
    ),
    PASTEL_LAVENDER(
        topGradient = Color(0xFFF5F3FF),
        bottomGradient = Color(0xFFEDE9FE),
        borderStroke = Color(0xFFC4B5FD),
        contentColor = Color(0xFF5B21B6),
        iconTint = Color(0xFF7C3AED),
        shadowTint = Color(0xFFC084FC)
    ),
    PASTEL_LEMON(
        topGradient = Color(0xFFFEFCE8),
        bottomGradient = Color(0xFFFEF9C3),
        borderStroke = Color(0xFFFDE047),
        contentColor = Color(0xFF854D0E),
        iconTint = Color(0xFFCA8A04),
        shadowTint = Color(0xFFFACC15)
    ),
    PASTEL_CORAL(
        topGradient = Color(0xFFFFF1F2),
        bottomGradient = Color(0xFFFFE4E6),
        borderStroke = Color(0xFFFDA4AF),
        contentColor = Color(0xFF9F1239),
        iconTint = Color(0xFFE11D48),
        shadowTint = Color(0xFFFB7185)
    ),
    PASTEL_INDIGO(
        topGradient = Color(0xFFEEF2FF),
        bottomGradient = Color(0xFFE0E7FF),
        borderStroke = Color(0xFFA5B4FC),
        contentColor = Color(0xFF3730A3),
        iconTint = Color(0xFF4F46E5),
        shadowTint = Color(0xFF818CF8)
    )
}

@Composable
fun CandyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    flavor: CandyFlavor = CandyFlavor.SAPPHIRE,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
    isTransparent: Boolean = false
) {
    val context = LocalContext.current
    Surface(
        onClick = {
            SoundNotificationHelper.playClickSound(context)
            onClick()
        },
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = flavor.bottomRim.copy(alpha = 0.35f),
                spotColor = flavor.bottomRim.copy(alpha = 0.45f)
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = if (isTransparent) {
                            listOf(
                                flavor.topGradient.copy(alpha = 0.70f),
                                flavor.bottomGradient.copy(alpha = 0.50f)
                            )
                        } else {
                            listOf(
                                flavor.topGradient,
                                flavor.bottomGradient
                            )
                        }
                    ),
                    shape = shape
                )
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.85f),
                                Color.White.copy(alpha = 0.20f)
                            )
                        )
                    ),
                    shape = shape
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            // Glossy reflection overlay on top half
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.40f),
                            0.45f to Color.White.copy(alpha = 0.10f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.08f)
                        )
                    )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = flavor.contentColor,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    color = flavor.contentColor,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

/**
 * Light Transparent Candy Style Button
 * Frosted translucent pastel glass backdrop, luminous crystal outline, specular top-half shine,
 * and tactile click sound feedback.
 */
@Composable
fun LightCandyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    flavor: LightCandyFlavor = LightCandyFlavor.PASTEL_SKY,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(18.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
    isTransparent: Boolean = true
) {
    val context = LocalContext.current
    Surface(
        onClick = {
            SoundNotificationHelper.playClickSound(context)
            onClick()
        },
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier
            .shadow(
                elevation = 2.5.dp,
                shape = shape,
                ambientColor = flavor.shadowTint.copy(alpha = 0.20f),
                spotColor = flavor.shadowTint.copy(alpha = 0.28f)
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = if (isTransparent) {
                            listOf(
                                flavor.topGradient.copy(alpha = 0.65f),
                                flavor.bottomGradient.copy(alpha = 0.45f)
                            )
                        } else {
                            listOf(
                                flavor.topGradient,
                                flavor.bottomGradient
                            )
                        }
                    ),
                    shape = shape
                )
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.90f),
                                flavor.borderStroke.copy(alpha = 0.65f)
                            )
                        )
                    ),
                    shape = shape
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            // Specular glass reflection overlay on top half
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.65f),
                            0.45f to Color.White.copy(alpha = 0.15f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.03f)
                        )
                    )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = flavor.iconTint,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(
                    text = text,
                    color = flavor.contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

@Composable
fun LightCandyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    flavor: LightCandyFlavor = LightCandyFlavor.PASTEL_SKY,
    contentDescription: String? = null,
    iconSize: Dp = 17.dp,
    size: Dp = 36.dp
) {
    val context = LocalContext.current
    Surface(
        onClick = {
            SoundNotificationHelper.playClickSound(context)
            onClick()
        },
        shape = CircleShape,
        color = Color.Transparent,
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                ambientColor = flavor.shadowTint.copy(alpha = 0.20f),
                spotColor = flavor.shadowTint.copy(alpha = 0.28f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            flavor.topGradient.copy(alpha = 0.65f),
                            flavor.bottomGradient.copy(alpha = 0.45f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.90f),
                                flavor.borderStroke.copy(alpha = 0.65f)
                            )
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.65f),
                            0.45f to Color.White.copy(alpha = 0.15f),
                            0.5f to Color.Transparent
                        )
                    )
            )
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = flavor.iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun CandyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    flavor: CandyFlavor = CandyFlavor.SAPPHIRE,
    contentDescription: String? = null,
    iconSize: Dp = 22.dp
) {
    val context = LocalContext.current
    Surface(
        onClick = {
            SoundNotificationHelper.playClickSound(context)
            onClick()
        },
        shape = CircleShape,
        color = Color.Transparent,
        modifier = modifier
            .size(42.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = flavor.bottomRim.copy(alpha = 0.35f),
                spotColor = flavor.bottomRim.copy(alpha = 0.45f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            flavor.topGradient,
                            flavor.bottomGradient
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.85f),
                                Color.White.copy(alpha = 0.2f)
                            )
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.4f),
                            0.45f to Color.White.copy(alpha = 0.08f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.15f)
                        )
                    )
            )
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = flavor.contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun ArihantHeader(
    currentRole: UserRole,
    userName: String,
    flatId: String,
    onRoleSelected: (UserRole) -> Unit,
    onEmergencyClicked: () -> Unit,
    onNoticesClicked: () -> Unit,
    onSuperAdminClicked: (() -> Unit)? = null,
    backgroundWallResId: Int = com.example.R.drawable.img_alishan_sunset,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppThemePalette.current
    var showRoleMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(theme.primary)
    ) {
        // Architectural Background Wall Image
        Image(
            painter = painterResource(id = backgroundWallResId),
            contentDescription = "Arihant Alishan Architectural Background Wall",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.32f
        )

        // Luxury Gradient Scrim for crystal clear readability and rich theme tint
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            theme.primary.copy(alpha = 0.85f),
                            theme.secondary.copy(alpha = 0.93f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
        // Society brand & registry status bar + Small SOS Hotline Candy Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(0.8.dp, theme.accent.copy(alpha = 0.35f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ARIHANT ALISHAN • KHARGHAR",
                        color = theme.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Small Candy Emergency SOS Button in Header
            CandyButton(
                text = "🚨 SOS",
                onClick = onEmergencyClicked,
                flavor = CandyFlavor.RUBY,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                modifier = Modifier.testTag("header_emergency_sos_btn")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Greeting and Role switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Good Day,",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentRole.isSuperAdmin()) Icons.Default.Shield else Icons.Default.Apartment,
                        contentDescription = null,
                        tint = theme.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (currentRole.isSuperAdmin()) "Root Super Administrator • Full Access"
                        else if (flatId.startsWith("K-")) "Kaveh Tower • Flat ${flatId.removePrefix("K-")}"
                        else if (flatId.startsWith("B1-")) "Baraz-1 • Flat ${flatId.removePrefix("B1-")}"
                        else if (flatId.startsWith("B2-")) "Baraz-2 • Flat ${flatId.removePrefix("B2-")}"
                        else if (flatId.startsWith("Z-")) "Zenath • Flat ${flatId.removePrefix("Z-")}"
                        else flatId,
                        color = theme.accent,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Interactive Actions & Candy Role Switcher
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentRole.isSuperAdmin()) {
                    CandyIconButton(
                        icon = Icons.Default.Shield,
                        onClick = { onSuperAdminClicked?.invoke() },
                        flavor = CandyFlavor.PURPLE,
                        contentDescription = "Super Admin Console",
                        modifier = Modifier.testTag("super_admin_header_btn")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                CandyIconButton(
                    icon = Icons.Default.Notifications,
                    onClick = onNoticesClicked,
                    flavor = CandyFlavor.SAPPHIRE,
                    contentDescription = "Notices",
                    modifier = Modifier.testTag("notices_button")
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Candy Role Dropdown Trigger Pill
                Box {
                    CandyButton(
                        text = when (currentRole) {
                            UserRole.SUPER_ADMIN -> "Admin 👑"
                            UserRole.RESIDENT_OWNER -> "Owner"
                            UserRole.FAMILY_MEMBER -> "Family"
                            UserRole.RESIDENT_TENANT -> "Tenant"
                            UserRole.SOCIETY_MANAGER -> "Manager"
                            UserRole.CHAIRMAN -> "Chairman"
                            UserRole.SECRETARY -> "Secretary"
                            UserRole.TREASURER -> "Treasurer"
                            UserRole.COMMITTEE_MEMBER -> "Committee"
                            UserRole.SECURITY_INCHARGE -> "Sec Incharge"
                            UserRole.SECURITY_GUARD -> "Guard"
                            UserRole.HOUSEKEEPING_SUPERVISOR -> "HK Sup"
                            UserRole.HOUSEKEEPING_STAFF -> "HK Staff"
                        },
                        onClick = { showRoleMenu = true },
                        flavor = CandyFlavor.GOLD,
                        icon = Icons.Default.ArrowDropDown,
                        shape = RoundedCornerShape(22.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("role_switcher_button")
                    )

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false },
                        modifier = Modifier.background(theme.secondary)
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    if (role == UserRole.SUPER_ADMIN) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = theme.accent, modifier = Modifier.size(20.dp))
                                    }
                                },
                                text = {
                                    Text(
                                        text = role.displayName,
                                        color = if (role == currentRole) theme.accent else Color.White,
                                        fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.5.sp
                                    )
                                },
                                onClick = {
                                    showRoleMenu = false
                                    onRoleSelected(role)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun ArihantBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("home", "Home", Icons.Default.Home),
        Triple("complaints", "Complaints", Icons.Default.Build),
        Triple("chat", "Chat", Icons.Default.Forum),
        Triple("amenities", "Amenities", Icons.Default.Pool),
        Triple("more", "More", Icons.Default.Dashboard)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = PureWhiteSurface,
        tonalElevation = 10.dp
    ) {
        items.forEachIndexed { index, (route, label, icon) ->
            val selected = currentScreen == route
            val flavor = when (index) {
                0 -> LightCandyFlavor.PASTEL_SKY
                1 -> LightCandyFlavor.PASTEL_CORAL
                2 -> LightCandyFlavor.PASTEL_MINT
                3 -> LightCandyFlavor.PASTEL_LAVENDER
                else -> LightCandyFlavor.PASTEL_PEACH
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(route) },
                icon = {
                    if (selected) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = flavor.shadowTint.copy(alpha = 0.35f),
                                    spotColor = flavor.shadowTint.copy(alpha = 0.45f)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(flavor.topGradient, flavor.bottomGradient)
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        BorderStroke(1.dp, flavor.borderStroke),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                0.0f to Color.White.copy(alpha = 0.7f),
                                                0.45f to Color.White.copy(alpha = 0.15f),
                                                0.5f to Color.Transparent
                                            )
                                        )
                                )
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp),
                                    tint = flavor.iconTint
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp),
                            tint = TextMuted
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        color = if (selected) flavor.contentColor else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.testTag("nav_$route")
            )
        }
    }
}

@Composable
fun CandyPillTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(tabs) { index, tabTitle ->
                val isSelected = index == selectedIndex
                val tabFlavor = when (index % 6) {
                    0 -> LightCandyFlavor.PASTEL_SKY
                    1 -> LightCandyFlavor.PASTEL_ROSE
                    2 -> LightCandyFlavor.PASTEL_MINT
                    3 -> LightCandyFlavor.PASTEL_LAVENDER
                    4 -> LightCandyFlavor.PASTEL_PEACH
                    else -> LightCandyFlavor.PASTEL_LEMON
                }

                Surface(
                    onClick = { onTabSelected(index) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .then(
                            if (isSelected) {
                                Modifier.shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = tabFlavor.shadowTint.copy(alpha = 0.35f),
                                    spotColor = tabFlavor.shadowTint.copy(alpha = 0.45f)
                                )
                            } else Modifier
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(
                                            tabFlavor.topGradient,
                                            tabFlavor.bottomGradient
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Transparent
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        BorderStroke(
                                            1.dp,
                                            tabFlavor.borderStroke
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                } else Modifier
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to Color.White.copy(alpha = 0.6f),
                                            0.45f to Color.White.copy(alpha = 0.1f),
                                            0.5f to Color.Transparent
                                        )
                                    )
                            )
                        }

                        Text(
                            text = tabTitle,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) tabFlavor.contentColor else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CandySectionHeading(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    flavor: LightCandyFlavor = LightCandyFlavor.PASTEL_SKY,
    badgeText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .size(38.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = flavor.shadowTint.copy(alpha = 0.25f),
                        spotColor = flavor.shadowTint.copy(alpha = 0.35f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(flavor.topGradient, flavor.bottomGradient)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            BorderStroke(1.dp, flavor.borderStroke),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color.White.copy(alpha = 0.65f),
                                    0.45f to Color.White.copy(alpha = 0.15f),
                                    0.5f to Color.Transparent
                                )
                            )
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = flavor.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = flavor.topGradient,
                        border = BorderStroke(1.dp, flavor.borderStroke),
                        modifier = Modifier.padding(1.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = flavor.contentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CandyIconBadge(
    icon: ImageVector,
    flavor: LightCandyFlavor = LightCandyFlavor.PASTEL_SKY,
    size: Dp = 38.dp,
    iconSize: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = flavor.shadowTint.copy(alpha = 0.25f),
                spotColor = flavor.shadowTint.copy(alpha = 0.35f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(flavor.topGradient, flavor.bottomGradient)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .border(
                    BorderStroke(1.dp, flavor.borderStroke),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.65f),
                            0.45f to Color.White.copy(alpha = 0.15f),
                            0.5f to Color.Transparent
                        )
                    )
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = flavor.iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun LightCandyQuickActionItem(
    icon: ImageVector,
    label: String,
    flavor: LightCandyFlavor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "candy_press"
    )

    Column(
        modifier = modifier
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 28.dp),
                onClick = {
                    SoundNotificationHelper.playClickSound(context)
                    onClick()
                }
            )
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = if (isPressed) 1.dp else 3.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = flavor.shadowTint.copy(alpha = 0.2f),
                    spotColor = flavor.shadowTint.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            flavor.topGradient.copy(alpha = 0.75f),
                            flavor.bottomGradient.copy(alpha = 0.45f)
                        )
                    )
                )
                .border(
                    BorderStroke(1.dp, flavor.borderStroke.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glass sheen top reflection
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.6f),
                            0.45f to Color.White.copy(alpha = 0.15f),
                            0.55f to Color.Transparent
                        )
                    )
            )
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = flavor.iconTint,
                modifier = Modifier.size(22.dp)
            )

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-3).dp)
                        .background(StatusDanger, CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MemberPhotoPicker(
    photoUri: String,
    onPhotoSelected: (String) -> Unit,
    onPhotoCleared: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        if (bitmap != null) {
            val path = ImageCaptureHelper.saveBitmapToFile(context, bitmap)
            if (path.isNotBlank()) {
                onPhotoSelected(path)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onPhotoSelected(it.toString())
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = OffWhiteBackground),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Member Profile Photo",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Photo Preview
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SurfaceVariant)
                    .border(2.dp, NavyPrimary.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri.isNotBlank()) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Profile Photo Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "No photo selected",
                        tint = TextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Camera / Gallery / Remove Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Take Photo", fontSize = 11.sp, color = NavyPrimary)
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gallery", fontSize = 11.sp, color = NavyPrimary)
                }

                if (photoUri.isNotBlank()) {
                    IconButton(
                        onClick = onPhotoCleared,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove Photo", tint = StatusDanger, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (photoUri.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("✓ Photo selected & ready to save", fontSize = 10.sp, color = StatusSuccess, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFamilyMemberDialog(
    member: FamilyMemberEntity,
    onDismiss: () -> Unit,
    onSave: (FamilyMemberEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(member.fullName) }
    var relationship by remember { mutableStateOf(member.relationship) }
    var gender by remember { mutableStateOf(member.gender) }
    var calculatedAge by remember { mutableStateOf(member.calculatedAge.toString()) }
    var phone by remember { mutableStateOf(member.phone) }
    var email by remember { mutableStateOf(member.email) }
    var isEmergencyContact by remember { mutableStateOf(member.isEmergencyContact) }
    var isChild by remember { mutableStateOf(member.isChild) }
    var schoolName by remember { mutableStateOf(member.schoolName) }
    var grade by remember { mutableStateOf(member.grade) }
    var schoolContact by remember { mutableStateOf(member.schoolContact) }
    var photoUri by remember { mutableStateOf(member.photoUri) }

    val relationships = listOf("Spouse", "Son", "Daughter", "Father", "Mother", "Brother", "Sister", "Grandparent", "Other")
    val genders = listOf("Male", "Female", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            CandySectionHeading(
                title = "Edit Family Member",
                subtitle = "Flat ${member.flatId} • Household Record",
                icon = Icons.Default.Edit,
                flavor = LightCandyFlavor.PASTEL_SKY
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    MemberPhotoPicker(
                        photoUri = photoUri,
                        onPhotoSelected = { photoUri = it },
                        onPhotoCleared = { photoUri = "" }
                    )
                }
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Text("Relationship", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(relationships) { rel ->
                            FilterChip(
                                selected = relationship == rel,
                                onClick = { relationship = rel },
                                label = { Text(rel, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = calculatedAge,
                            onValueChange = { calculatedAge = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Age (Years)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gender", fontSize = 11.sp, color = TextMuted)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                genders.forEach { g ->
                                    FilterChip(
                                        selected = gender == g,
                                        onClick = { gender = g },
                                        label = { Text(g.take(1), fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Primary Emergency Contact", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Call first during medical/fire alarms", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = isEmergencyContact,
                            onCheckedChange = { isEmergencyContact = it }
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("School Going Child / Minor", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Record school bus / emergency info", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = isChild,
                            onCheckedChange = { isChild = it }
                        )
                    }
                }
                if (isChild) {
                    item {
                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("School / College Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Grade / Standard / Division") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = schoolContact,
                            onValueChange = { schoolContact = it },
                            label = { Text("School Admin / Bus Contact") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            CandyButton(
                text = "Save Changes",
                onClick = {
                    if (fullName.isNotBlank()) {
                        val ageInt = calculatedAge.toIntOrNull() ?: member.calculatedAge
                        val updated = member.copy(
                            fullName = fullName.trim(),
                            relationship = relationship,
                            gender = gender,
                            calculatedAge = ageInt,
                            phone = phone.trim(),
                            email = email.trim(),
                            isEmergencyContact = isEmergencyContact,
                            isChild = isChild || ageInt < 18,
                            schoolName = if (isChild) schoolName.trim() else "",
                            grade = if (isChild) grade.trim() else "",
                            schoolContact = if (isChild) schoolContact.trim() else "",
                            photoUri = photoUri
                        )
                        onSave(updated)
                    }
                },
                flavor = CandyFlavor.EMERALD
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

// Interactive Animated Task Card Item with Task Type Graphics & Role Adaptability
@Composable
fun TaskCardItem(
    task: CommitteeTaskEntity,
    currentRole: UserRole,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status.equals("Completed", ignoreCase = true)
    val isInProgress = task.status.equals("In Progress", ignoreCase = true)

    // Smooth graphic rotation / oscillation for active tasks
    val infiniteTransition = rememberInfiniteTransition(label = "task_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isInProgress) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gear_spin"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Category styling & icon
    val (catIcon, catColor, catBg) = when {
        task.category.contains("Maintenance", true) || task.category.contains("Water", true) || task.category.contains("Lift", true) ->
            Triple(Icons.Default.Build, Color(0xFF0284C7), Color(0xFFE0F2FE))
        task.category.contains("Security", true) || task.category.contains("Patrol", true) ->
            Triple(Icons.Default.Security, Color(0xFF059669), Color(0xFFD1FAE5))
        task.category.contains("Finance", true) || task.category.contains("Audit", true) || task.category.contains("Bill", true) ->
            Triple(Icons.Default.AccountBalance, Color(0xFFD97706), Color(0xFFFEF3C7))
        task.category.contains("Governance", true) || task.category.contains("AGM", true) || task.category.contains("Rule", true) ->
            Triple(Icons.Default.Gavel, Color(0xFF7C3AED), Color(0xFFEDE9FE))
        else ->
            Triple(Icons.Default.Assignment, Color(0xFF475569), Color(0xFFF1F5F9))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isCompleted) Color(0xFF10B981).copy(alpha = 0.35f) else CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 1.dp else 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Task Graphic with smooth subtle animation
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCompleted) Color(0xFFD1FAE5) else catBg)
                            .then(
                                if (isInProgress && !isCompleted) Modifier.rotate(rotation)
                                else if (!isCompleted && task.priority.equals("High", true)) Modifier.scale(pulseScale)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else catIcon,
                            contentDescription = task.category,
                            tint = if (isCompleted) Color(0xFF059669) else catColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = if (isCompleted) TextMuted else NavyPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = catBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = task.category,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = catColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "Due: ${task.dueDate}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                StatusBadge(status = task.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Assigned: ${task.assignedRole}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!isCompleted) {
                    CandyButton(
                        text = "Complete Task",
                        onClick = onComplete,
                        flavor = CandyFlavor.EMERALD,
                        modifier = Modifier.height(34.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Done",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusSuccess
                        )
                    }
                }
            }
        }
    }
}

// Clear Task Completion Celebration Overlay with Smooth Spring Animation
@Composable
fun TaskCompletionCelebrationOverlay(
    event: ArihantViewModel.TaskCompletionEvent,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        visible = true
        kotlinx.coroutines.delay(3500)
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "celebrate_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(24.dp)
                .scale(scale)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebratory animated green checkmark
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1FAE5))
                        .border(3.dp, Color(0xFF10B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Task Completed!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.taskTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Category: ${event.category} • Recorded in Audit Log",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = CardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Sound notification quick setting toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = if (soundEnabled) NavyPrimary else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sound Feedback", fontSize = 11.5.sp, color = TextPrimary)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        modifier = Modifier.scale(0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Great! Dismiss", fontSize = 12.5.sp)
                }
            }
        }
    }
}


@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "super admin", "root", "super_admin" ->
            Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
        "available", "verified", "active", "resolved", "closed", "paid", "operational", "approved", "confirmed" ->
            Pair(StatusSuccessBg, StatusSuccess)
        "in progress", "assigned", "acknowledged", "under review", "visitor", "expiring soon" ->
            Pair(StatusInfoBg, StatusInfo)
        "pending", "pending verification", "waiting approval", "correction required", "new" ->
            Pair(StatusWarningBg, StatusWarning)
        "critical", "high", "blocked", "emergency", "breakdown", "overdue", "rejected", "expired" ->
            Pair(StatusCriticalBg, StatusCritical)
        else ->
            Pair(Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, textColor.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status,
                color = textColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    iconTint: Color = NavyPrimary,
    bgColor: Color = SurfaceVariant,
    flavor: CandyFlavor? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resolvedFlavor = flavor ?: when {
        iconTint == StatusCritical || bgColor == StatusCriticalBg -> CandyFlavor.RUBY
        iconTint == StatusSuccess || bgColor == StatusSuccessBg -> CandyFlavor.EMERALD
        iconTint == StatusInfo || bgColor == StatusInfoBg -> CandyFlavor.SAPPHIRE
        iconTint == GoldAccent || iconTint == GoldChampagne || bgColor == GoldContainer -> CandyFlavor.AMBER
        bgColor == Color(0xFFEDE9FE) -> CandyFlavor.PURPLE
        bgColor == Color(0xFFCCFBF1) -> CandyFlavor.TEAL
        bgColor == Color(0xFFFFEBEE) -> CandyFlavor.CORAL
        else -> CandyFlavor.SAPPHIRE
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag("action_${label.lowercase().replace(" ", "_")}")
    ) {
        // Candy 3D glossy jewel button
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            modifier = Modifier
                .size(62.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = resolvedFlavor.bottomRim.copy(alpha = 0.35f),
                    spotColor = resolvedFlavor.bottomRim.copy(alpha = 0.50f)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                resolvedFlavor.topGradient,
                                resolvedFlavor.bottomGradient
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        BorderStroke(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color.White.copy(alpha = 0.20f)
                                )
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Specular highlight gloss overlay on upper half
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = 0.42f),
                                0.45f to Color.White.copy(alpha = 0.08f),
                                0.50f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.15f)
                            )
                        )
                )

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = resolvedFlavor.contentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CandyOperationStatusButton(
    title: String,
    statusText: String,
    subtitleText: String,
    icon: ImageVector,
    flavor: CandyFlavor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = flavor.bottomRim.copy(alpha = 0.35f),
                spotColor = flavor.bottomRim.copy(alpha = 0.50f)
            )
            .testTag("status_btn_${title.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            flavor.topGradient,
                            flavor.bottomGradient
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.85f),
                                Color.White.copy(alpha = 0.20f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(13.dp)
        ) {
            // Candy gloss specular reflection overlay across upper half
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.38f),
                            0.45f to Color.White.copy(alpha = 0.08f),
                            0.50f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.15f)
                        )
                    )
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular glossy emblem for icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = flavor.contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Live pulse indicator & chevron
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.10f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(flavor.contentColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Details",
                            tint = flavor.contentColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = flavor.contentColor.copy(alpha = 0.9f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = statusText,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = flavor.contentColor,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitleText,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = flavor.contentColor.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SocietyAlertBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class AlishanWallpaperItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val drawableResId: Int,
    val timeBadge: String
)

val ALISHAN_WALLPAPERS = listOf(
    AlishanWallpaperItem(
        id = "sunset",
        title = "Royal Sunset Twilight",
        subtitle = "Iconic twin Persian towers at evening dusk",
        drawableResId = com.example.R.drawable.img_alishan_sunset,
        timeBadge = "Dusk • Twilight"
    ),
    AlishanWallpaperItem(
        id = "daylight",
        title = "Kharghar Hills Daylight",
        subtitle = "Panoramic aerial view with lush green hillscapes",
        drawableResId = com.example.R.drawable.img_alishan_daylight,
        timeBadge = "Daylight • Vista"
    ),
    AlishanWallpaperItem(
        id = "night",
        title = "Illuminated Palace Night",
        subtitle = "Warm glowing arches & royal podium arcade",
        drawableResId = com.example.R.drawable.img_alishan_night,
        timeBadge = "Night • Royal Glow"
    )
)

@Composable
fun ArihantWallpaperGalleryCard(
    currentWallResId: Int,
    onSelectWall: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var previewWall by remember(currentWallResId) { mutableStateOf(currentWallResId) }
    var showFullDialog by remember { mutableStateOf(false) }

    val activeItem = ALISHAN_WALLPAPERS.find { it.drawableResId == previewWall } ?: ALISHAN_WALLPAPERS.first()

    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            CandySectionHeading(
                title = "Arihant Alishan Background Wall",
                subtitle = "Royal Persian towers & Kharghar architectural vistas",
                icon = Icons.Default.Wallpaper,
                flavor = LightCandyFlavor.PASTEL_PEACH,
                badgeText = "8 Original Views"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hero Wall Preview Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showFullDialog = true }
            ) {
                Image(
                    painter = painterResource(id = activeItem.drawableResId),
                    contentDescription = activeItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Bottom Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Details & Fullscreen Affordance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFF59E0B).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = activeItem.timeBadge,
                                    color = Color.Black,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (activeItem.drawableResId == currentWallResId) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF10B981),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Active Wall ✓",
                                        color = Color.White,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = activeItem.title,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = activeItem.subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    LightCandyIconButton(
                        icon = Icons.Default.ZoomIn,
                        onClick = { showFullDialog = true },
                        flavor = LightCandyFlavor.PASTEL_SKY,
                        size = 36.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Row
            Text(
                text = "Select Background Wall Theme:",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ALISHAN_WALLPAPERS.forEach { item ->
                    val isSelected = previewWall == item.drawableResId
                    val isApplied = currentWallResId == item.drawableResId

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                previewWall = item.drawableResId
                                com.example.ui.util.SoundNotificationHelper.playClickSound(context)
                            },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFF0F9FF) else Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = item.drawableResId),
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (isApplied) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(3.dp)
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title.substringBefore(" "),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0284C7) else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Light Candy Style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LightCandyButton(
                    text = "Preview Full Screen",
                    icon = Icons.Default.Fullscreen,
                    onClick = { showFullDialog = true },
                    flavor = LightCandyFlavor.PASTEL_LAVENDER,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                )

                LightCandyButton(
                    text = if (currentWallResId == previewWall) "Current Wall ✓" else "Apply As Wall",
                    icon = Icons.Default.CheckCircle,
                    onClick = {
                        onSelectWall(previewWall)
                        com.example.ui.util.SoundNotificationHelper.playNotificationSound(context)
                    },
                    flavor = LightCandyFlavor.PASTEL_MINT,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }

    if (showFullDialog) {
        AlertDialog(
            onDismissRequest = { showFullDialog = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = activeItem.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Arihant Alishan • Kharghar, Navi Mumbai",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = { showFullDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        Image(
                            painter = painterResource(id = activeItem.drawableResId),
                            contentDescription = activeItem.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = activeItem.subtitle,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                LightCandyButton(
                    text = "Apply As Wall Wallpaper",
                    icon = Icons.Default.Check,
                    onClick = {
                        onSelectWall(activeItem.drawableResId)
                        com.example.ui.util.SoundNotificationHelper.playNotificationSound(context)
                        showFullDialog = false
                    },
                    flavor = LightCandyFlavor.PASTEL_MINT,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { showFullDialog = false }) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}
