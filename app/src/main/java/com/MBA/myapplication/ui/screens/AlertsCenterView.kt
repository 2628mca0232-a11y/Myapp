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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.AlertType
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.data.LoanAlert
import com.MBA.myapplication.utils.EmiCalculatorUtils

@Composable
fun AlertsCenterView(
    alerts: List<LoanAlert>,
    loans: List<Loan>,
    onDismissAlert: (String) -> Unit,
    onMarkPaid: (String) -> Unit,
    onViewLoan: (Loan) -> Unit,
    currencySymbol: String = "₹"
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Alerts & Reminders", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Real-time notifications for upcoming and overdue EMIs", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        }

        if (alerts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF94A3B8))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("All clear! No active payment alerts.", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("You are up to date with all your loan EMI payments.", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        } else {
            items(alerts) { alert ->
                val targetLoan = loans.find { it.id == alert.loanId }
                AlertCard(
                    alert = alert,
                    onDismiss = { onDismissAlert(alert.id) },
                    onMarkPaid = { onMarkPaid(alert.loanId) },
                    onViewLoan = { if (targetLoan != null) onViewLoan(targetLoan) },
                    currencySymbol = currencySymbol
                )
            }
        }
    }
}

@Composable
fun AlertCard(
    alert: LoanAlert,
    onDismiss: () -> Unit,
    onMarkPaid: () -> Unit,
    onViewLoan: () -> Unit,
    currencySymbol: String = "₹"
) {
    val (cardBg, borderColor, iconTint) = when (alert.type) {
        AlertType.OVERDUE -> Triple(Color(0xFFFEE2E2), Color(0xFFEF4444), Color(0xFFDC2626))
        AlertType.DUE_TODAY -> Triple(Color(0xFFDBEAFE), Color(0xFF3B82F6), Color(0xFF2563EB))
        AlertType.DUE_SOON -> Triple(Color(0xFFFEF3C7), Color(0xFFF59E0B), Color(0xFFD97706))
        AlertType.PAID -> Triple(Color(0xFFD1FAE5), Color(0xFF10B981), Color(0xFF059669))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewLoan() },
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
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        alert.title,
                        color = iconTint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(alert.message, fontSize = 13.sp, color = Color(0xFF334155))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "EMI: ${EmiCalculatorUtils.formatCurrency(alert.emiAmount, currencySymbol)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )

                if (alert.type != AlertType.PAID) {
                    Button(
                        onClick = onMarkPaid,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Paid", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
