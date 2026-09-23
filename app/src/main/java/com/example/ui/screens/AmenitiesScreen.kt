package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmenityBookingEntity
import com.example.data.model.AmenityEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.CandyButton
import com.example.ui.components.CandyFlavor
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AmenitiesScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val amenities by viewModel.allAmenities.collectAsState()
    val myBookings by viewModel.myAmenityBookings.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedAmenityForBooking by remember { mutableStateOf<AmenityEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        // Top Header and Navigation Bar
        Surface(
            color = PureWhiteSurface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Clubhouse & Amenities",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "World-class recreational facilities for residents",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${amenities.count { it.status.equals("Available", true) }} Open",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Modern Pill Segmented Tabs (Clean, evenly spaced, fully responsive)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        val tab0Active = selectedTab == 0
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (tab0Active) PureWhiteSurface else Color.Transparent,
                            shadowElevation = if (tab0Active) 2.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = if (tab0Active) NavyPrimary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "All Amenities",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (tab0Active) FontWeight.Bold else FontWeight.Medium,
                                    color = if (tab0Active) NavyPrimary else TextSecondary
                                )
                            }
                        }

                        val tab1Active = selectedTab == 1
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (tab1Active) PureWhiteSurface else Color.Transparent,
                            shadowElevation = if (tab1Active) 2.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = null,
                                    tint = if (tab1Active) NavyPrimary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "My Bookings (${myBookings.size})",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (tab1Active) FontWeight.Bold else FontWeight.Medium,
                                    color = if (tab1Active) NavyPrimary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(amenities) { amenity ->
                    AmenityCard(
                        amenity = amenity,
                        onBook = { selectedAmenityForBooking = amenity }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (myBookings.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(36.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(30.dp))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No bookings yet", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = NavyPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Explore the society amenities list to book a slot for gym, banquet, or sports.",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(myBookings) { booking ->
                        AmenityBookingCard(booking = booking)
                    }
                }
            }
        }
    }

    if (selectedAmenityForBooking != null) {
        BookAmenityDialog(
            amenity = selectedAmenityForBooking!!,
            viewModel = viewModel,
            onDismiss = { selectedAmenityForBooking = null }
        )
    }
}

@Composable
fun AmenityCard(
    amenity: AmenityEntity,
    onBook: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(GoldContainer)
                            .border(1.dp, OnGoldContainer.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (amenity.id) {
                                "AM-POOL" -> Icons.Default.Pool
                                "AM-GYM" -> Icons.Default.FitnessCenter
                                "AM-BANQUET" -> Icons.Default.Celebration
                                "AM-PLAY" -> Icons.Default.ChildCare
                                "AM-TURF" -> Icons.Default.SportsTennis
                                else -> Icons.Default.Apartment
                            },
                            contentDescription = amenity.name,
                            tint = OnGoldContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = amenity.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = amenity.timings,
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                StatusBadge(status = amenity.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = amenity.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = if (amenity.bookingFee == 0) "Fee: Included in Maintenance" else "Booking Fee: ₹${amenity.bookingFee}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (amenity.bookingFee == 0) StatusSuccess else NavyPrimary
                    )
                    Text(
                        text = "Capacity: ${amenity.capacity} Persons • Rules apply",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )
                }

                CandyButton(
                    text = "Reserve Slot",
                    onClick = onBook,
                    flavor = CandyFlavor.GOLD,
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AmenityBookingCard(booking: AmenityBookingEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("#${booking.id}", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        booking.amenityName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = NavyLight, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${booking.bookingDate} • ${booking.timeSlot}",
                    fontSize = 12.sp,
                    color = NavyLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("Reserved by: ${booking.residentName} (Flat ${booking.flat})", fontSize = 11.sp, color = TextSecondary)
            if (booking.feePaid > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Fee Paid: ₹${booking.feePaid} (Digital Receipt Generated)", fontSize = 10.5.sp, color = StatusSuccess, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun BookAmenityDialog(
    amenity: AmenityEntity,
    viewModel: ArihantViewModel,
    onDismiss: () -> Unit
) {
    var date by remember { mutableStateOf("Saturday, 19-Sep-2026") }
    var slot by remember { mutableStateOf("06:00 PM - 08:00 PM") }
    val scrollState = rememberScrollState()

    val availableSlots = listOf(
        "06:00 AM - 08:00 AM",
        "09:00 AM - 11:00 AM",
        "04:00 PM - 06:00 PM",
        "06:00 PM - 08:00 PM",
        "08:00 PM - 10:00 PM"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Book ${amenity.name}", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 17.sp)
                Text("Capacity: ${amenity.capacity} • Fee: ₹${amenity.bookingFee}", fontSize = 11.5.sp, color = TextSecondary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Reservation Date") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Select Time Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                availableSlots.forEach { s ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (slot == s) Color(0xFFEFF6FF) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (slot == s) Color(0xFF3B82F6) else CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { slot = s }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = slot == s, onClick = { slot = s }, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s,
                                fontSize = 12.sp,
                                fontWeight = if (slot == s) FontWeight.Bold else FontWeight.Normal,
                                color = if (slot == s) NavyPrimary else TextPrimary
                            )
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Rules & Guidelines:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(amenity.rules, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            CandyButton(
                text = "Confirm Booking",
                onClick = {
                    viewModel.bookAmenity(amenity.id, amenity.name, date, slot, amenity.bookingFee)
                    onDismiss()
                },
                flavor = CandyFlavor.EMERALD
            )
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

