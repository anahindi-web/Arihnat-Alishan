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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceBillEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MaintenanceScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val myBills by viewModel.myMaintenanceBills.collectAsState()
    var selectedBillForPayment by remember { mutableStateOf<MaintenanceBillEntity?>(null) }
    var selectedReceiptForView by remember { mutableStateOf<MaintenanceBillEntity?>(null) }

    val activeBill = myBills.firstOrNull { it.status == "Unpaid" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Outstanding Bill Card
            if (activeBill != null) {
                item {
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
                                Column {
                                    Text(
                                        text = "Current Maintenance Bill",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = activeBill.monthYear,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                }
                                StatusBadge(status = "Due Soon")
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("Total Amount Due", fontSize = 12.sp, color = TextMuted)
                                    Text(
                                        "₹${activeBill.totalAmount}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Due Date", fontSize = 11.sp, color = TextMuted)
                                    Text(
                                        activeBill.dueDate,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StatusCritical
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = BorderSubtle)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Itemized Charges Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))

                            ItemizedChargeRow("Sinking Fund", "₹${activeBill.sinkingFund}")
                            ItemizedChargeRow("Repair & Maintenance Fund", "₹${activeBill.repairFund}")
                            ItemizedChargeRow("Water & Borewell Charges", "₹${activeBill.waterCharges}")
                            ItemizedChargeRow("Society Service & Security", "₹${activeBill.serviceCharges}")
                            ItemizedChargeRow("Common Electricity & Lift Power", "₹${activeBill.electricityCharges}")

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { selectedBillForPayment = activeBill },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("pay_maintenance_button")
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pay Now ₹${activeBill.totalAmount}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(StatusSuccessBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusSuccess)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("No Outstanding Dues!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                Text("All society maintenance bills are clear.", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Payment History & Receipts
            item {
                Text(
                    text = "Payment History & Official Receipts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val paidBills = myBills.filter { it.status == "Paid" }
            if (paidBills.isEmpty()) {
                item {
                    Text("No past receipts found.", fontSize = 12.sp, color = TextMuted)
                }
            } else {
                items(paidBills) { bill ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReceiptForView = bill }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = StatusSuccess)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(bill.monthYear, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Text("Paid on ${bill.paidAt} via ${bill.paymentMethod}", fontSize = 11.sp, color = TextSecondary)
                                    Text("Receipt: ${bill.receiptNumber}", fontSize = 10.sp, color = TextMuted)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${bill.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                                TextButton(
                                    onClick = { selectedReceiptForView = bill },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("View Receipt", fontSize = 11.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedBillForPayment != null) {
        PayMaintenanceDialog(
            bill = selectedBillForPayment!!,
            onPay = { method ->
                viewModel.payMaintenanceBill(selectedBillForPayment!!.id, method)
                selectedBillForPayment = null
            },
            onDismiss = { selectedBillForPayment = null }
        )
    }

    if (selectedReceiptForView != null) {
        OfficialReceiptDialog(
            bill = selectedReceiptForView!!,
            onDismiss = { selectedReceiptForView = null }
        )
    }
}

@Composable
fun ItemizedChargeRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
fun PayMaintenanceDialog(
    bill: MaintenanceBillEntity,
    onPay: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI - Google Pay") }
    val methods = listOf("UPI - Google Pay", "UPI - PhonePe", "Net Banking - HDFC", "Debit / Credit Card")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Pay Society Maintenance", fontWeight = FontWeight.Bold, color = NavyPrimary)
                Text("${bill.monthYear} • Flat ${bill.flat} • Total: ₹${bill.totalAmount}", fontSize = 12.sp, color = TextSecondary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                methods.forEach { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = m },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedMethod == m, onClick = { selectedMethod = m })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(m, fontSize = 13.sp)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = StatusSuccessBg), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Instant Society Receipt generated with Treasurer digital authorization.", fontSize = 11.sp, color = StatusSuccess)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPay(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Authorize Payment ₹${bill.totalAmount}")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun OfficialReceiptDialog(
    bill: MaintenanceBillEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("ARIHANT ALISHAN CHS LTD.", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 15.sp)
                Text("Plot 10/11, Sector 35, Kharghar, Navi Mumbai", fontSize = 10.sp, color = TextMuted)
                Text("Reg No: MCS/NMMC/2026/CHS/892 • MCS Act 1960", fontSize = 10.sp, color = TextMuted)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Receipt No: ${bill.receiptNumber.ifBlank { "RCT-2026-9824" }}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyPrimary)
                    Text("Date: ${bill.paidAt.ifBlank { "Today" }}", fontSize = 11.sp, color = TextSecondary)
                }

                Text("Received from: Rajesh Sharma (Flat ${bill.flat})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                Text("Maintenance for: ${bill.monthYear}", fontSize = 11.sp, color = TextSecondary)
                Text("Payment Mode: ${bill.paymentMethod} • Ref: ${bill.receiptNumber}", fontSize = 10.sp, color = TextMuted)

                Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

                ItemizedChargeRow("Sinking Fund", "₹${bill.sinkingFund}")
                ItemizedChargeRow("Repair Fund", "₹${bill.repairFund}")
                ItemizedChargeRow("Water Supply", "₹${bill.waterCharges}")
                ItemizedChargeRow("Service & Security", "₹${bill.serviceCharges}")
                ItemizedChargeRow("Lift & Common Electricity", "₹${bill.electricityCharges}")

                Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL AMOUNT PAID:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                    Text("₹${bill.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StatusSuccess)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pradeep Shenoy", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Hon. Treasurer, Arihant Alishan", fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)) {
                Text("Download / Print")
            }
        }
    )
}
