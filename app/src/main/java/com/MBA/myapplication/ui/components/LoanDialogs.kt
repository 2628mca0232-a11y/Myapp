package com.MBA.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.data.LOAN_CATEGORIES
import com.MBA.myapplication.data.POPULAR_BANKS
import com.MBA.myapplication.utils.EmiCalculatorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLoanDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onSubmit: (Loan) -> Unit,
    initialData: Loan? = null,
    currencySymbol: String = "₹"
) {
    if (!isOpen) return

    var name by remember(initialData) { mutableStateOf(initialData?.name ?: "") }
    var category by remember(initialData) { mutableStateOf(initialData?.category ?: LOAN_CATEGORIES.first()) }
    var bankName by remember(initialData) { mutableStateOf(initialData?.bankName ?: POPULAR_BANKS.first()) }
    var principal by remember(initialData) { mutableStateOf(if ((initialData?.principal ?: 0.0) > 0) initialData?.principal?.toInt()?.toString() ?: "" else "") }
    var outstanding by remember(initialData) { mutableStateOf(if ((initialData?.outstanding ?: 0.0) > 0) initialData?.outstanding?.toInt()?.toString() ?: "" else "") }
    var interestRate by remember(initialData) { mutableStateOf(if ((initialData?.interestRate ?: 0.0) > 0) initialData?.interestRate?.toString() ?: "" else "10.5") }
    var tenureMonths by remember(initialData) { mutableStateOf(if ((initialData?.tenureMonths ?: 0) > 0) initialData?.tenureMonths?.toString() ?: "" else "36") }
    var dueDay by remember(initialData) { mutableStateOf(if ((initialData?.dueDay ?: 0) > 0) initialData?.dueDay?.toString() ?: "" else "5") }
    var notes by remember(initialData) { mutableStateOf(initialData?.notes ?: "") }

    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var bankDropdownOpen by remember { mutableStateOf(false) }

    val calcPrincipal = principal.toDoubleOrNull() ?: 0.0
    val calcRate = interestRate.toDoubleOrNull() ?: 0.0
    val calcTenure = tenureMonths.toIntOrNull() ?: 0
    val calculatedEmi = EmiCalculatorUtils.calculateEMI(calcPrincipal, calcRate, calcTenure)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialData != null && initialData.id.isNotBlank()) "Edit Loan Account" else "Add New Loan Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Loan Name *") },
                            placeholder = { Text("e.g., Home Loan, Car Loan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { categoryDropdownOpen = true },
                                trailingIcon = {
                                    IconButton(onClick = { categoryDropdownOpen = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Select")
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false }
                            ) {
                                LOAN_CATEGORIES.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            categoryDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = bankName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lender / Bank Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { bankDropdownOpen = true },
                                trailingIcon = {
                                    IconButton(onClick = { bankDropdownOpen = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Select")
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = bankDropdownOpen,
                                onDismissRequest = { bankDropdownOpen = false }
                            ) {
                                POPULAR_BANKS.forEach { bank ->
                                    DropdownMenuItem(
                                        text = { Text(bank) },
                                        onClick = {
                                            bankName = bank
                                            bankDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = principal,
                                onValueChange = { principal = it },
                                label = { Text("Principal Amount ($currencySymbol)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = outstanding,
                                onValueChange = { outstanding = it },
                                label = { Text("Current Outstanding ($currencySymbol)") },
                                placeholder = { Text("Default = Principal") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = interestRate,
                                onValueChange = { interestRate = it },
                                label = { Text("Interest Rate (% p.a.)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = tenureMonths,
                                onValueChange = { tenureMonths = it },
                                label = { Text("Tenure (Months)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = dueDay,
                            onValueChange = { dueDay = it },
                            label = { Text("EMI Due Day of Month (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (calculatedEmi > 0) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Calculated Monthly EMI", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            EmiCalculatorUtils.formatCurrency(calculatedEmi, currencySymbol),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Purpose (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onClose) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val pVal = principal.toDoubleOrNull() ?: 0.0
                            if (name.isBlank() || pVal <= 0.0) return@Button
                            val outVal = outstanding.toDoubleOrNull() ?: pVal
                            val rVal = interestRate.toDoubleOrNull() ?: 0.0
                            val tVal = tenureMonths.toIntOrNull() ?: 12
                            val dVal = (dueDay.toIntOrNull() ?: 5).coerceIn(1, 31)

                            val loanToSave = Loan(
                                id = initialData?.id ?: "",
                                name = name.trim(),
                                category = category,
                                bankName = bankName,
                                principal = pVal,
                                outstanding = outVal,
                                emiAmount = calculatedEmi,
                                interestRate = rVal,
                                tenureMonths = tVal,
                                remainingMonths = initialData?.remainingMonths ?: tVal,
                                dueDay = dVal,
                                notes = notes.trim(),
                                colorHex = initialData?.colorHex ?: "#3b82f6",
                                isCurrentMonthPaid = initialData?.isCurrentMonthPaid ?: false,
                                lastPaidDate = initialData?.lastPaidDate ?: "",
                                paymentHistory = initialData?.paymentHistory ?: emptyList()
                            )
                            onSubmit(loanToSave)
                        },
                        enabled = name.isNotBlank() && (principal.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text(if (initialData != null && initialData.id.isNotBlank()) "Update Loan" else "Add Loan")
                    }
                }
            }
        }
    }
}

@Composable
fun LoanDetailDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    loan: Loan?,
    onMarkPaid: (String) -> Unit,
    onOpenPrepay: (Loan) -> Unit,
    onEdit: (Loan) -> Unit,
    onDelete: (String) -> Unit,
    currencySymbol: String = "₹"
) {
    if (!isOpen || loan == null) return

    var selectedTab by remember { mutableIntStateOf(0) }

    val repaid = (loan.principal - loan.outstanding).coerceAtLeast(0.0)
    val repaidPercent = if (loan.principal > 0) ((repaid / loan.principal) * 100.0).coerceIn(0.0, 100.0) else 0.0

    val amortizationSchedule = remember(loan) {
        EmiCalculatorUtils.generateAmortizationSchedule(
            loan.principal,
            loan.interestRate,
            loan.tenureMonths,
            loan.startDate
        )
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(loan.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${loan.bankName} • ${loan.category}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Outstanding", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    EmiCalculatorUtils.formatCurrency(loan.outstanding, currencySymbol),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Monthly EMI", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    EmiCalculatorUtils.formatCurrency(loan.emiAmount, currencySymbol),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (repaidPercent / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Repaid: ${repaidPercent.toInt()}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Principal: ${EmiCalculatorUtils.formatCurrency(loan.principal, currencySymbol)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onMarkPaid(loan.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !loan.isCurrentMonthPaid,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (loan.isCurrentMonthPaid) "Paid" else "Mark Paid", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onOpenPrepay(loan) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prepay", fontSize = 12.sp)
                    }

                    IconButton(onClick = { onEdit(loan) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }

                    IconButton(onClick = { onDelete(loan.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: 0 -> Schedule, 1 -> History, 2 -> Overview
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Schedule") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("History (${loan.paymentHistory.size})") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Details") })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(amortizationSchedule) { item ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Month #${item.monthNumber} • ${item.displayDate}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("Principal: ${EmiCalculatorUtils.formatCurrency(item.principal, currencySymbol)} | Interest: ${EmiCalculatorUtils.formatCurrency(item.interest, currencySymbol)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(
                                                EmiCalculatorUtils.formatCurrency(item.remainingBalance, currencySymbol),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            if (loan.paymentHistory.isEmpty()) {
                                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                    Text("No payment records logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(loan.paymentHistory) { rec ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(rec.date, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("${rec.paymentMethod} • ${rec.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Text(
                                                    "+${EmiCalculatorUtils.formatCurrency(rec.amount, currencySymbol)}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                DetailRow("Interest Rate", "${loan.interestRate}% p.a.")
                                DetailRow("Tenure", "${loan.tenureMonths} Months (${loan.remainingMonths} Remaining)")
                                DetailRow("Due Day of Month", "${loan.dueDay}th of every month")
                                DetailRow("Start Date", loan.startDate.ifBlank { "Not specified" })
                                if (loan.notes.isNotBlank()) {
                                    DetailRow("Notes", loan.notes)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PrepayModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    loan: Loan?,
    onPrepay: (String, Double, String) -> Unit,
    currencySymbol: String = "₹"
) {
    if (!isOpen || loan == null) return

    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("Lump sum prepayment") }

    val prepayAmount = amountStr.toDoubleOrNull() ?: 0.0
    val result = remember(prepayAmount, loan) {
        EmiCalculatorUtils.calculatePrepaymentSavings(
            loan.outstanding,
            loan.interestRate,
            loan.remainingMonths,
            prepayAmount,
            loan.emiAmount
        )
    }

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prepay Loan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    "Apply lump-sum principal prepayment to reduce loan tenure and interest for ${loan.name}.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Prepayment Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Payment Notes / Source") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (prepayAmount > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("⚡ Savings Impact Preview", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Months Saved:", fontSize = 13.sp)
                                Text("${result.monthsSaved} Months", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Interest Saved:", fontSize = 13.sp)
                                Text(EmiCalculatorUtils.formatCurrency(result.interestSaved, currencySymbol), fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onClose) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (prepayAmount > 0) {
                                onPrepay(loan.id, prepayAmount, notes)
                            }
                        },
                        enabled = prepayAmount > 0
                    ) {
                        Text("Confirm Prepayment")
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Delete Loan Account?",
    message: String = "Are you sure you want to remove this loan account? This action cannot be undone."
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Cancel")
            }
        }
    )
}
