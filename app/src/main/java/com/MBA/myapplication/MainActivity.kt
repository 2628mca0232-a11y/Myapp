package com.MBA.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class NavTab(val id: String, val title: String, val icon: ImageVector) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard),
    LOANS("loans", "Loans", Icons.Default.CreditCard),
    CALENDAR("calendar", "Calendar", Icons.Default.CalendarMonth),
    CALCULATOR("calculator", "Calculator", Icons.Default.Calculate),
    ANALYTICS("analytics", "Analytics", Icons.Default.Analytics),
    ALERTS("alerts", "Alerts", Icons.Default.Notifications),
    SETTINGS("settings", "Settings", Icons.Default.Settings)
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

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EMIease",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${settings.currencySymbol} (${settings.currencyCode})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
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
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
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
                val bottomItems = listOf(
                    NavTab.DASHBOARD,
                    NavTab.LOANS,
                    NavTab.CALENDAR,
                    NavTab.CALCULATOR,
                    NavTab.ANALYTICS,
                    NavTab.SETTINGS
                )

                bottomItems.forEach { tab ->
                    NavigationBarItem(
                        selected = activeTab == tab.id,
                        onClick = { viewModel.setActiveTab(tab.id) },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 10.sp) }
                    )
                }
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
