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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.MBA.myapplication.data.LOAN_CATEGORIES
import com.MBA.myapplication.utils.EmiCalculatorUtils

@Composable
fun LoanListView(
    loans: List<Loan>,
    onOpenAddModal: () -> Unit,
    onViewDetails: (Loan) -> Unit,
    onMarkPaid: (String) -> Unit,
    onEdit: (Loan) -> Unit,
    onDelete: (String) -> Unit,
    currencySymbol: String = "₹"
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember { listOf("All") + LOAN_CATEGORIES }

    val filteredLoans = remember(loans, searchQuery, selectedCategory) {
        loans.filter { loan ->
            val matchesCategory = (selectedCategory == "All" || loan.category == selectedCategory)
            val matchesSearch = loan.name.contains(searchQuery, ignoreCase = true) ||
                    loan.bankName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Loan Accounts",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Manage all active credit cards, personal, and vehicle loans",
                fontSize = 12.sp,
                color = Color(0xFF475569)
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by loan or bank name...", color = Color(0xFF64748B)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = Color(0xFF0F172A)
                        )
                    )
                }
            }
        }

        // Loan List
        if (filteredLoans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching loan accounts found.", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("Try adjusting your search or add a new loan.", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(filteredLoans) { loan ->
                LoanAccountCard(
                    loan = loan,
                    onViewDetails = { onViewDetails(loan) },
                    onMarkPaid = { onMarkPaid(loan.id) },
                    onEdit = { onEdit(loan) },
                    onDelete = { onDelete(loan.id) },
                    currencySymbol = currencySymbol
                )
            }
        }
    }
}

@Composable
fun LoanAccountCard(
    loan: Loan,
    onViewDetails: () -> Unit,
    onMarkPaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    currencySymbol: String = "₹"
) {
    val repaid = (loan.principal - loan.outstanding).coerceAtLeast(0.0)
    val pct = if (loan.principal > 0) ((repaid / loan.principal) * 100.0).coerceIn(0.0, 100.0) else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(loan.colorHex)))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(loan.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                        Text("${loan.bankName} • ${loan.category}", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${loan.interestRate}% p.a.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Outstanding", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        EmiCalculatorUtils.formatCurrency(loan.outstanding, currencySymbol),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Monthly EMI", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        EmiCalculatorUtils.formatCurrency(loan.emiAmount, currencySymbol),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (pct / 100.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2563EB),
                trackColor = Color(0xFFEFF6FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${pct.toInt()}% Paid", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Text("Due: ${loan.dueDay}th of month", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onMarkPaid,
                    enabled = !loan.isCurrentMonthPaid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (loan.isCurrentMonthPaid) "Paid" else "Mark Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF64748B))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}
