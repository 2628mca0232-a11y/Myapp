package com.MBA.myapplication.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.data.LoanStats
import com.MBA.myapplication.utils.EmiCalculatorUtils

@Composable
fun AnalyticsView(
    loans: List<Loan>,
    stats: LoanStats,
    currencySymbol: String = "₹"
) {
    val categoryBreakdown = remember(loans) {
        val map = mutableMapOf<String, Double>()
        loans.forEach { loan ->
            map[loan.category] = (map[loan.category] ?: 0.0) + loan.outstanding
        }
        map.toList().sortedByDescending { it.second }
    }

    val bankBreakdown = remember(loans) {
        val map = mutableMapOf<String, Double>()
        loans.forEach { loan ->
            map[loan.bankName] = (map[loan.bankName] ?: 0.0) + loan.outstanding
        }
        map.toList().sortedByDescending { it.second }
    }

    val totalDebt = stats.totalOutstanding.coerceAtLeast(1.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Debt Analytics & Insights", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("Visual distribution of your loans, banks, and repayment progress", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2563EB))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Repayment Trajectory", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Total Borrowed", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(EmiCalculatorUtils.formatCurrency(stats.totalPrincipal, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Repaid So Far", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(EmiCalculatorUtils.formatCurrency(stats.totalRepaid, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF059669))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current Outstanding", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(EmiCalculatorUtils.formatCurrency(stats.totalOutstanding, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2563EB))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (stats.overallRepaidPercentage / 100.0).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF059669)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${stats.overallRepaidPercentage.toInt()}% of total principal paid off", fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF7C3AED))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Outstanding Debt by Category", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (categoryBreakdown.isEmpty()) {
                        Text("No active loans.", fontSize = 12.sp, color = Color(0xFF64748B))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            categoryBreakdown.forEach { (catName, amount) ->
                                val pct = ((amount / totalDebt) * 100.0).coerceIn(0.0, 100.0)
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(catName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${EmiCalculatorUtils.formatCurrency(amount, currencySymbol)} (${pct.toInt()}%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (pct / 100.0).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF7C3AED)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Lender Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Outstanding Debt by Lender", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (bankBreakdown.isEmpty()) {
                        Text("No active loans.", fontSize = 12.sp, color = Color(0xFF64748B))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            bankBreakdown.forEach { (bankName, amount) ->
                                val pct = ((amount / totalDebt) * 100.0).coerceIn(0.0, 100.0)
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(bankName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(EmiCalculatorUtils.formatCurrency(amount, currencySymbol), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { (pct / 100.0).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
