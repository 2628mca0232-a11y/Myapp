package com.MBA.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.utils.EmiCalculatorUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PaymentCalendarView(
    loans: List<Loan>,
    onMarkPaid: (String) -> Unit,
    onViewLoan: (Loan) -> Unit,
    currencySymbol: String = "₹"
) {
    val currentCalendar = remember { Calendar.getInstance() }
    var monthOffset by remember { mutableIntStateOf(0) }

    val activeCal = remember(monthOffset) {
        val cal = currentCalendar.clone() as Calendar
        cal.add(Calendar.MONTH, monthOffset)
        cal
    }

    val currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val isCurrentMonthSelected = monthOffset == 0

    var selectedDay by remember(monthOffset) { mutableIntStateOf(currentDayOfMonth) }

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(activeCal.time)
    val daysInMonth = activeCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val firstDayOfWeek = remember(activeCal) {
        val cal = activeCal.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
    }

    val dueDayLoansMap = remember(loans) {
        val map = mutableMapOf<Int, MutableList<Loan>>()
        loans.forEach { loan ->
            val day = loan.dueDay.coerceIn(1, 31)
            map.getOrPut(day) { mutableListOf() }.add(loan)
        }
        map
    }

    val dayGridItems = remember(firstDayOfWeek, daysInMonth) {
        val list = mutableListOf<Int?>()
        for (i in 0 until firstDayOfWeek) {
            list.add(null)
        }
        for (day in 1..daysInMonth) {
            list.add(day)
        }
        list
    }

    val selectedDayLoans = dueDayLoansMap[selectedDay] ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Payment Calendar", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text("Visual breakdown of upcoming loan EMI due dates", fontSize = 12.sp, color = Color(0xFF475569))
        }

        // Calendar Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { monthOffset-- }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = Color(0xFF0F172A))
                        }
                        Text(monthName, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                        IconButton(onClick = { monthOffset++ }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Color(0xFF0F172A))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Days of week header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Calendar Days Grid
                    Column {
                        val chunked = dayGridItems.chunked(7)
                        chunked.forEach { rowDays ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowDays.forEach { dayNum ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                    ) {
                                        if (dayNum != null) {
                                            val loansOnDay = dueDayLoansMap[dayNum] ?: emptyList()
                                            val isSelected = selectedDay == dayNum
                                            val isToday = isCurrentMonthSelected && dayNum == currentDayOfMonth

                                            val hasUnpaid = loansOnDay.any { !it.isCurrentMonthPaid }
                                            val hasPaid = loansOnDay.any { it.isCurrentMonthPaid }

                                            val bgColor = when {
                                                isSelected -> Color(0xFF2563EB)
                                                isToday -> Color(0xFFEFF6FF)
                                                else -> Color.Transparent
                                            }

                                            val textColor = when {
                                                isSelected -> Color.White
                                                isToday -> Color(0xFF2563EB)
                                                else -> Color(0xFF0F172A)
                                            }

                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { selectedDay = dayNum },
                                                color = bgColor
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        "$dayNum",
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isToday || isSelected) FontWeight.Black else FontWeight.Bold,
                                                        color = textColor
                                                    )

                                                    if (loansOnDay.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            if (hasUnpaid) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(6.dp)
                                                                        .background(if (isSelected) Color.White else Color(0xFFEF4444), CircleShape)
                                                                )
                                                            }
                                                            if (hasPaid) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(6.dp)
                                                                        .background(if (isSelected) Color.White else Color(0xFF10B981), CircleShape)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowDays.size < 7) {
                                    for (k in 0 until (7 - rowDays.size)) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Day Details Header
        item {
            Text(
                "Payments Due on Day $selectedDay ($monthName)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        if (selectedDayLoans.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No EMI payments scheduled for day $selectedDay.", fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(selectedDayLoans) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewLoan(loan) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(loan.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text("${loan.bankName} • ${loan.category}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                EmiCalculatorUtils.formatCurrency(loan.emiAmount, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (loan.isCurrentMonthPaid) {
                                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(8.dp)) {
                                    Text("Paid ✓", color = Color(0xFF059669), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            } else {
                                Button(
                                    onClick = { onMarkPaid(loan.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
