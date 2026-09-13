package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmenityBookingEntity
import com.example.data.model.AmenityEntity
import com.example.ui.ArihantViewModel
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
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PureWhiteSurface,
            contentColor = NavyPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Society Amenities", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Bookings (${myBookings.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
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
                            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No bookings yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Select an amenity to reserve slots.", fontSize = 12.sp, color = TextMuted)
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldContainer),
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
                            contentDescription = null,
                            tint = OnGoldContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(amenity.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Timings: ${amenity.timings}", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                StatusBadge(status = amenity.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(amenity.description, fontSize = 12.sp, color = TextPrimary)

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (amenity.bookingFee == 0) "Fee: Included in Maintenance" else "Booking Fee: ₹${amenity.bookingFee}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (amenity.bookingFee == 0) StatusSuccess else NavyPrimary
                    )
                    Text(
                        text = "Max Capacity: ${amenity.capacity} Persons",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Reserve Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("#${booking.id}", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(booking.amenityName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                }
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Date: ${booking.bookingDate} • ${booking.timeSlot}", fontSize = 13.sp, color = NavyLight, fontWeight = FontWeight.SemiBold)
            Text("Reserved by: ${booking.residentName} (Flat ${booking.flat})", fontSize = 11.sp, color = TextSecondary)
            if (booking.feePaid > 0) {
                Text("Fee Paid: ₹${booking.feePaid} (Digital Receipt Generated)", fontSize = 11.sp, color = StatusSuccess)
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
                Text("Book ${amenity.name}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("Capacity: ${amenity.capacity} • Fee: ₹${amenity.bookingFee}", fontSize = 11.sp, color = TextSecondary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Reservation Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Select Time Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                availableSlots.forEach { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { slot = s },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = slot == s, onClick = { slot = s })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(s, fontSize = 12.sp)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Rules & Guidelines:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text(amenity.rules, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.bookAmenity(amenity.id, amenity.name, date, slot, amenity.bookingFee)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Confirm Booking")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
