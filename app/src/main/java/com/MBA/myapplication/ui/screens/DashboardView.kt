package com.MBA.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.AlertType
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.data.LoanStats
import com.MBA.myapplication.data.UpcomingEmiItem
import com.MBA.myapplication.utils.EmiCalculatorUtils

@Composable
fun DashboardView(
    stats: LoanStats,
    loans: List<Loan>,
    onOpenAddModal: () -> Unit,
    onViewLoan: (Loan) -> Unit,
    onMarkPaid: (String) -> Unit,
    onNavigateTab: (String) -> Unit,
    currencySymbol: String = "₹"
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Add Loan Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "One Dashboard. Every Loan. Zero Confusion.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Button(
                    onClick = onOpenAddModal,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Loan")
                }
            }
        }

        // Stats Cards Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Active Loans",
                        value = "${stats.totalActiveLoans}",
                        subtitle = "Total: ${EmiCalculatorUtils.formatCurrency(stats.totalPrincipal, currencySymbol)}",
                        icon = Icons.Default.CreditCard,
                        iconBg = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Monthly EMI",
                        value = EmiCalculatorUtils.formatCurrency(stats.totalMonthlyEMI, currencySymbol),
                        subtitle = "Across all active loans",
                        icon = Icons.Default.EventRepeat,
                        iconBg = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Outstanding",
                        value = EmiCalculatorUtils.formatCurrency(stats.totalOutstanding, currencySymbol),
                        subtitle = "Remaining liability",
                        icon = Icons.Default.Payments,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Repaid",
                        value = EmiCalculatorUtils.formatCurrency(stats.totalRepaid, currencySymbol),
                        subtitle = "${stats.overallRepaidPercentage.toInt()}% of principal paid",
                        icon = Icons.Default.TrendingUp,
                        iconBg = Color(0xFFD1FAE5),
                        iconTint = Color(0xFF059669),
                        progress = stats.overallRepaidPercentage / 100.0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Debt Health Meter Card
        item {
            DebtHealthMeterCard(
                overallRepaidPercent = stats.overallRepaidPercentage,
                activeLoansCount = stats.totalActiveLoans,
                totalOutstanding = stats.totalOutstanding,
                totalPrincipal = stats.totalPrincipal,
                currencySymbol = currencySymbol
            )
        }

        // Upcoming Payments Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Upcoming EMI Due Dates",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedButton(
                            onClick = { onNavigateTab("calendar") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Calendar View", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (stats.upcomingEmis.isEmpty()) {
                        Text("No active loan payments scheduled.", fontSize = 13.sp, color = Color(0xFF64748B))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            stats.upcomingEmis.take(4).forEach { item ->
                                UpcomingEmiRow(
                                    item = item,
                                    onViewLoan = onViewLoan,
                                    onMarkPaid = onMarkPaid,
                                    currencySymbol = currencySymbol
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loans Quick Overview List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Accounts (${loans.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                TextButton(onClick = { onNavigateTab("loans") }) {
                    Text("View All")
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        items(loans) { loan ->
            DashboardLoanCard(
                loan = loan,
                onView = { onViewLoan(loan) },
                onMarkPaid = { onMarkPaid(loan.id) },
                currencySymbol = currencySymbol
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    progress: Double? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(title, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = iconTint
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun DebtHealthMeterCard(
    overallRepaidPercent: Double,
    activeLoansCount: Int,
    totalOutstanding: Double,
    totalPrincipal: Double,
    currencySymbol: String = "₹"
) {
    // Determine debt score
    val score = when {
        overallRepaidPercent >= 50.0 -> 85
        overallRepaidPercent >= 25.0 -> 70
        activeLoansCount <= 2 -> 65
        else -> 45
    }

    val (healthLabel, healthColor, healthBg) = when {
        score >= 80 -> Triple("Excellent Debt Health", Color(0xFF059669), Color(0xFFD1FAE5))
        score >= 60 -> Triple("Good Repayment Track", Color(0xFF2563EB), Color(0xFFDBEAFE))
        score >= 40 -> Triple("Moderate EMI Burden", Color(0xFFD97706), Color(0xFFFEF3C7))
        else -> Triple("High Debt Warning", Color(0xFFDC2626), Color(0xFFFEE2E2))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = healthColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Debt Health Meter", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Surface(
                    color = healthBg,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        healthLabel,
                        color = healthColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (score / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = healthColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Repayment Ratio: ${overallRepaidPercent.toInt()}%", fontSize = 12.sp, color = Color(0xFF64748B))
                Text("Score: $score / 100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = healthColor)
            }
        }
    }
}

@Composable
fun UpcomingEmiRow(
    item: UpcomingEmiItem,
    onViewLoan: (Loan) -> Unit,
    onMarkPaid: (String) -> Unit,
    currencySymbol: String = "₹"
) {
    val loan = item.loan
    val (statusLabel, statusColor, statusBg) = when {
        loan.isCurrentMonthPaid -> Triple("Paid", Color(0xFF059669), Color(0xFFD1FAE5))
        item.daysLeft < 0 -> Triple("Overdue ${Math.abs(item.daysLeft)}d", Color(0xFFDC2626), Color(0xFFFEE2E2))
        item.daysLeft == 0 -> Triple("Due Today", Color(0xFF2563EB), Color(0xFFDBEAFE))
        else -> Triple("Due in ${item.daysLeft} days", Color(0xFFD97706), Color(0xFFFEF3C7))
    }

    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewLoan(loan) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(loan.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${loan.bankName} • Due: ${loan.dueDay}th", fontSize = 12.sp, color = Color(0xFF64748B))
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    EmiCalculatorUtils.formatCurrency(loan.emiAmount, currencySymbol),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!loan.isCurrentMonthPaid) {
                IconButton(
                    onClick = { onMarkPaid(loan.id) },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFD1FAE5), CircleShape)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Mark Paid", tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardLoanCard(
    loan: Loan,
    onView: () -> Unit,
    onMarkPaid: () -> Unit,
    currencySymbol: String = "₹"
) {
    val repaid = (loan.principal - loan.outstanding).coerceAtLeast(0.0)
    val pct = if (loan.principal > 0) (repaid / loan.principal) * 100.0 else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(loan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${loan.bankName} • ${loan.category}", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${loan.interestRate}% p.a.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Outstanding", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        EmiCalculatorUtils.formatCurrency(loan.outstanding, currencySymbol),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Monthly EMI", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        EmiCalculatorUtils.formatCurrency(loan.emiAmount, currencySymbol),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { (pct / 100.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${pct.toInt()}% Paid (${loan.remainingMonths} mos left)", fontSize = 11.sp, color = Color(0xFF64748B))
                if (!loan.isCurrentMonthPaid) {
                    TextButton(onClick = onMarkPaid) {
                        Text("Mark Paid", fontSize = 12.sp, color = Color(0xFF059669))
                    }
                } else {
                    Text("Paid for cycle ✓", fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
