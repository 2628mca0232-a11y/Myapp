package com.MBA.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MBA.myapplication.data.AlertType
import com.MBA.myapplication.ui.components.AddEditLoanDialog
import com.MBA.myapplication.ui.components.ConfirmDeleteDialog
import com.MBA.myapplication.ui.components.LoanDetailDialog
import com.MBA.myapplication.ui.components.PrepayModal
import com.MBA.myapplication.ui.screens.AlertsCenterView
import com.MBA.myapplication.ui.screens.AnalyticsView
import com.MBA.myapplication.ui.screens.DashboardView
import com.MBA.myapplication.ui.screens.EmiCalculatorView
import com.MBA.myapplication.ui.screens.LoanListView
import com.MBA.myapplication.ui.screens.PaymentCalendarView
import com.MBA.myapplication.ui.screens.SettingsView
import com.MBA.myapplication.ui.theme.MyApplicationTheme
import com.MBA.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EmiEaseApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiEaseApp(viewModel: MainViewModel) {
    val loans by viewModel.loans.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    val isAddModalOpen by viewModel.isAddModalOpen.collectAsState()
    val editingLoan by viewModel.editingLoan.collectAsState()
    val viewingLoan by viewModel.viewingLoan.collectAsState()
    val prepayingLoan by viewModel.prepayingLoan.collectAsState()
    val deletingLoanId by viewModel.deletingLoanId.collectAsState()

    val stats = remember(loans) { viewModel.getStats() }
    val alerts = remember(loans, settings) { viewModel.getAlerts() }
    val unreadAlertsCount = alerts.count { it.type != AlertType.PAID }

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("E", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("EMIease", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF0F172A))
                        }
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Drawer", tint = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Items
                    listOf(
                        Triple("dashboard", "Dashboard", Icons.Default.Dashboard),
                        Triple("loans", "My Loans", Icons.Default.CreditCard),
                        Triple("calendar", "EMI Calendar", Icons.Default.CalendarMonth),
                        Triple("calculator", "EMI Calculator", Icons.Default.Calculate),
                        Triple("analytics", "Analytics", Icons.Default.Analytics),
                        Triple("alerts", "EMI Alerts", Icons.Default.Notifications),
                        Triple("settings", "Settings", Icons.Default.Settings)
                    ).forEach { (id, title, icon) ->
                        val isSelected = activeTab == id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setActiveTab(id)
                                    scope.launch { drawerState.close() }
                                },
                            color = if (isSelected) Color(0xFF2563EB) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else Color(0xFF0F172A)
                                    )
                                }
                                if (id == "alerts" && unreadAlertsCount > 0) {
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            "$unreadAlertsCount",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "EMIease Mobile • Design Thinking Prototype",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("E", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EMIease",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color(0xFF0F172A)
                            )
                        }
                    },
                    actions = {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "${settings.currencySymbol} (${settings.currencyCode})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.setActiveTab("alerts") }) {
                            BadgedBox(
                                badge = {
                                    if (unreadAlertsCount > 0) {
                                        Badge { Text("$unreadAlertsCount") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color(0xFF0F172A))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == "dashboard",
                        onClick = { viewModel.setActiveTab("dashboard") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "loans",
                        onClick = { viewModel.setActiveTab("loans") },
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = "Loans") },
                        label = { Text("Loans", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.openAddModal() },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Loan", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        },
                        label = { Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB)) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "calendar",
                        onClick = { viewModel.setActiveTab("calendar") },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                        label = { Text("Calendar", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "calculator" || activeTab == "analytics" || activeTab == "settings",
                        onClick = { scope.launch { drawerState.open() } },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "More") },
                        label = { Text("More", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    "dashboard" -> DashboardView(
                        stats = stats,
                        loans = loans,
                        onOpenAddModal = { viewModel.openAddModal() },
                        onViewLoan = { loan -> viewModel.viewLoanDetails(loan) },
                        onMarkPaid = { id -> viewModel.markEmiPaid(id) },
                        onNavigateTab = { tab -> viewModel.setActiveTab(tab) },
                        currencySymbol = settings.currencySymbol
                    )
                    "loans" -> LoanListView(
                        loans = loans,
                        onOpenAddModal = { viewModel.openAddModal() },
                        onViewDetails = { loan -> viewModel.viewLoanDetails(loan) },
                        onMarkPaid = { id -> viewModel.markEmiPaid(id) },
                        onEdit = { loan -> viewModel.editLoan(loan) },
                        onDelete = { id -> viewModel.confirmDelete(id) },
                        currencySymbol = settings.currencySymbol
                    )
                    "calendar" -> PaymentCalendarView(
                        loans = loans,
                        onMarkPaid = { id -> viewModel.markEmiPaid(id) },
                        onViewLoan = { loan -> viewModel.viewLoanDetails(loan) },
                        currencySymbol = settings.currencySymbol
                    )
                    "calculator" -> EmiCalculatorView(
                        onExportToNewLoan = { loan -> viewModel.openAddModal(loan) },
                        currencySymbol = settings.currencySymbol
                    )
                    "analytics" -> AnalyticsView(
                        loans = loans,
                        stats = stats,
                        currencySymbol = settings.currencySymbol
                    )
                    "alerts" -> AlertsCenterView(
                        alerts = alerts,
                        loans = loans,
                        onDismissAlert = { id -> viewModel.dismissAlert(id) },
                        onMarkPaid = { id -> viewModel.markEmiPaid(id) },
                        onViewLoan = { loan -> viewModel.viewLoanDetails(loan) },
                        currencySymbol = settings.currencySymbol
                    )
                    "settings" -> SettingsView(
                        settings = settings,
                        onUpdateSettings = { s -> viewModel.updateSettings(s) },
                        onResetDemoData = { viewModel.resetDemoData() },
                        onClearAllData = { viewModel.clearAllData() },
                        onExportData = { viewModel.exportData() },
                        onImportData = { json -> viewModel.importData(json) }
                    )
                }
            }

            // Dialogs Layer
            AddEditLoanDialog(
                isOpen = isAddModalOpen,
                onClose = { viewModel.closeAddModal() },
                onSubmit = { loan -> viewModel.saveLoan(loan) },
                initialData = editingLoan,
                currencySymbol = settings.currencySymbol
            )

            LoanDetailDialog(
                isOpen = viewingLoan != null,
                onClose = { viewModel.closeViewLoanDetails() },
                loan = viewingLoan,
                onMarkPaid = { id -> viewModel.markEmiPaid(id) },
                onOpenPrepay = { loan -> viewModel.openPrepay(loan) },
                onEdit = { loan -> viewModel.editLoan(loan) },
                onDelete = { id -> viewModel.confirmDelete(id) },
                currencySymbol = settings.currencySymbol
            )

            PrepayModal(
                isOpen = prepayingLoan != null,
                onClose = { viewModel.closePrepay() },
                loan = prepayingLoan,
                onPrepay = { id, amount, notes -> viewModel.prepayLoan(id, amount, notes) },
                currencySymbol = settings.currencySymbol
            )

            ConfirmDeleteDialog(
                isOpen = deletingLoanId != null,
                onClose = { viewModel.cancelDelete() },
                onConfirm = { viewModel.deleteLoanConfirmed() }
            )
        }
    }
}
