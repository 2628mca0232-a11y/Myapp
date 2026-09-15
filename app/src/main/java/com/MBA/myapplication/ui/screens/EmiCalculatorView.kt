package com.MBA.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.utils.EmiCalculatorUtils

@Composable
fun EmiCalculatorView(
    onExportToNewLoan: (Loan) -> Unit,
    currencySymbol: String = "₹"
) {
    var selectedCalculatorTab by remember { mutableIntStateOf(0) }

    // EMI Calculator States
    var principalAmount by remember { mutableFloatStateOf(500000f) }
    var interestRate by remember { mutableFloatStateOf(10.5f) }
    var tenureMonths by remember { mutableFloatStateOf(36f) }

    // Prepayment Calculator States
    var prepayOutstanding by remember { mutableFloatStateOf(500000f) }
    var prepayInterestRate by remember { mutableFloatStateOf(10.5f) }
    var prepayTenure by remember { mutableFloatStateOf(36f) }
    var prepayLumpSum by remember { mutableFloatStateOf(100000f) }

    val monthlyEMI = EmiCalculatorUtils.calculateEMI(
        principalAmount.toDouble(),
        interestRate.toDouble(),
        tenureMonths.toInt()
    )
    val totalPayable = monthlyEMI * tenureMonths.toInt()
    val totalInterest = (totalPayable - principalAmount.toDouble()).coerceAtLeast(0.0)

    val principalRatio = if (totalPayable > 0) (principalAmount.toDouble() / totalPayable) * 100.0 else 0.0
    val interestRatio = if (totalPayable > 0) (totalInterest / totalPayable) * 100.0 else 0.0

    val prepayResult = remember(prepayOutstanding, prepayInterestRate, prepayTenure, prepayLumpSum) {
        val regularEmi = EmiCalculatorUtils.calculateEMI(prepayOutstanding.toDouble(), prepayInterestRate.toDouble(), prepayTenure.toInt())
        EmiCalculatorUtils.calculatePrepaymentSavings(
            prepayOutstanding.toDouble(),
            prepayInterestRate.toDouble(),
            prepayTenure.toInt(),
            prepayLumpSum.toDouble(),
            regularEmi
        )
    }

    val sliderColors = SliderDefaults.colors(
        thumbColor = Color(0xFF2563EB),
        activeTrackColor = Color(0xFF2563EB),
        inactiveTrackColor = Color(0xFFE2E8F0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Smart EMI Calculator",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
            Text(
                "Simulate monthly payments & prepayment savings impact",
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedCalculatorTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = Color(0xFF2563EB),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCalculatorTab]),
                            color = Color(0xFF2563EB),
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedCalculatorTab == 0,
                        onClick = { selectedCalculatorTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Calculate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedCalculatorTab == 0) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Standard EMI",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCalculatorTab == 0) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedCalculatorTab == 1,
                        onClick = { selectedCalculatorTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Savings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedCalculatorTab == 1) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Prepayment Savings",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCalculatorTab == 1) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                            }
                        }
                    )
                }
            }
        }

        if (selectedCalculatorTab == 0) {
            // Standard EMI Calculator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Loan Principal Amount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF475569))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            EmiCalculatorUtils.formatCurrency(principalAmount.toDouble(), currencySymbol),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2563EB)
                        )
                        Slider(
                            value = principalAmount,
                            onValueChange = { principalAmount = it },
                            valueRange = 10000f..5000000f,
                            colors = sliderColors
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Interest Rate (% p.a.)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF475569))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${"%.1f".format(interestRate)}% p.a.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF7C3AED)
                        )
                        Slider(
                            value = interestRate,
                            onValueChange = { interestRate = it },
                            valueRange = 5f..30f,
                            colors = sliderColors
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            "Tenure (${tenureMonths.toInt()} Months / ${(tenureMonths / 12).toInt()} Years)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                        Slider(
                            value = tenureMonths,
                            onValueChange = { tenureMonths = it },
                            valueRange = 6f..360f,
                            colors = sliderColors
                        )
                    }
                }
            }

            // Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Calculated Monthly EMI", fontSize = 13.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            EmiCalculatorUtils.formatCurrency(monthlyEMI, currencySymbol),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total Interest", fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                                Text(
                                    EmiCalculatorUtils.formatCurrency(totalInterest, currencySymbol),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFDC2626)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Amount Payable", fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                                Text(
                                    EmiCalculatorUtils.formatCurrency(totalPayable, currencySymbol),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { (principalRatio / 100.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2563EB),
                            trackColor = Color(0xFFEF4444)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Principal: ${principalRatio.toInt()}%", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                            Text("Interest: ${interestRatio.toInt()}%", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val exported = Loan(
                                    name = "New Calculated Loan",
                                    principal = principalAmount.toDouble(),
                                    outstanding = principalAmount.toDouble(),
                                    emiAmount = monthlyEMI,
                                    interestRate = interestRate.toDouble(),
                                    tenureMonths = tenureMonths.toInt(),
                                    remainingMonths = tenureMonths.toInt(),
                                    dueDay = 5
                                )
                                onExportToNewLoan(exported)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add as New Loan Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else {
            // Prepayment Calculator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Current Loan Outstanding", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF475569))
                        Text(
                            EmiCalculatorUtils.formatCurrency(prepayOutstanding.toDouble(), currencySymbol),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2563EB)
                        )
                        Slider(
                            value = prepayOutstanding,
                            onValueChange = { prepayOutstanding = it },
                            valueRange = 10000f..3000000f,
                            colors = sliderColors
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Lump-sum Prepayment Amount", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF475569))
                        Text(
                            EmiCalculatorUtils.formatCurrency(prepayLumpSum.toDouble(), currencySymbol),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF059669)
                        )
                        Slider(
                            value = prepayLumpSum,
                            onValueChange = { prepayLumpSum = it },
                            valueRange = 5000f..prepayOutstanding.coerceAtLeast(10000f),
                            colors = sliderColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            "Interest Rate: ${"%.1f".format(prepayInterestRate)}% p.a. | Tenure: ${prepayTenure.toInt()} Months",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("⚡ Savings Impact Summary", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF065F46))

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Months Saved", fontSize = 12.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
                                Text("${prepayResult.monthsSaved} Months", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF065F46))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Interest Saved", fontSize = 12.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
                                Text(
                                    EmiCalculatorUtils.formatCurrency(prepayResult.interestSaved, currencySymbol),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("New Loan Tenure: ${prepayResult.newTenure} months (Reduced from ${prepayTenure.toInt()} months)", fontSize = 12.sp, color = Color(0xFF047857), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
