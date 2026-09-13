package com.MBA.myapplication.data

import java.util.UUID

data class PaymentRecord(
    val id: String = "pay-${UUID.randomUUID()}",
    val date: String, // YYYY-MM-DD
    val amount: Double,
    val principal: Double,
    val interest: Double,
    val status: String = "Paid",
    val paymentMethod: String = "Online Banking",
    val notes: String = ""
)

data class Loan(
    val id: String = "loan-${UUID.randomUUID()}",
    val name: String,
    val category: String = "Personal Loan",
    val bankName: String = "General Lender",
    val principal: Double,
    val outstanding: Double = principal,
    val emiAmount: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val remainingMonths: Int = tenureMonths,
    val dueDay: Int = 5,
    val startDate: String = "",
    val notes: String = "",
    val colorHex: String = "#3b82f6",
    val isCurrentMonthPaid: Boolean = false,
    val lastPaidDate: String = "",
    val paymentHistory: List<PaymentRecord> = emptyList()
)

data class UserSettings(
    val currencySymbol: String = "₹",
    val currencyCode: String = "INR",
    val notificationsEnabled: Boolean = true,
    val emailAlerts: Boolean = true,
    val whatsappAlerts: Boolean = false,
    val theme: String = "light"
)

enum class AlertType {
    OVERDUE,
    DUE_TODAY,
    DUE_SOON,
    PAID
}

data class LoanAlert(
    val id: String,
    val loanId: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val date: String,
    val emiAmount: Double,
    val bankName: String
)

data class LoanStats(
    val totalActiveLoans: Int = 0,
    val totalMonthlyEMI: Double = 0.0,
    val totalOutstanding: Double = 0.0,
    val totalPrincipal: Double = 0.0,
    val totalRepaid: Double = 0.0,
    val overallRepaidPercentage: Double = 0.0,
    val upcomingEmis: List<UpcomingEmiItem> = emptyList(),
    val nextEmiDue: UpcomingEmiItem? = null,
    val highestEmiLoan: Loan? = null,
    val highestOutstandingLoan: Loan? = null
)

data class UpcomingEmiItem(
    val loan: Loan,
    val nextDueDateStr: String,
    val daysLeft: Int,
    val status: AlertType
)

data class AmortizationItem(
    val monthNumber: Int,
    val date: String,
    val displayDate: String,
    val emiAmount: Double,
    val principal: Double,
    val interest: Double,
    val remainingBalance: Double,
    val totalInterestPaid: Double,
    val totalPrincipalPaid: Double
)

data class PrepaymentResult(
    val newTenure: Int,
    val monthsSaved: Int,
    val interestSaved: Double
)

val LOAN_CATEGORIES = listOf(
    "Personal Loan",
    "Home Loan",
    "Education Loan",
    "Vehicle Loan",
    "Credit Card EMI",
    "Gold Loan",
    "Business Loan",
    "Other"
)

val POPULAR_BANKS = listOf(
    "HDFC Bank",
    "State Bank of India (SBI)",
    "ICICI Bank",
    "Axis Bank",
    "Kotak Mahindra Bank",
    "Punjab National Bank (PNB)",
    "Bank of Baroda",
    "Bajaj Finserv",
    "Tata Capital",
    "Other / NBFC"
)

val COLOR_PALETTE = listOf(
    "#3b82f6", "#6366f1", "#0ea5e9", "#8b5cf6",
    "#10b981", "#f59e0b", "#ec4899", "#14b8a6"
)

val INITIAL_LOANS = listOf(
    Loan(
        id = "loan-1",
        name = "Personal Loan",
        category = "Personal Loan",
        bankName = "HDFC Bank",
        principal = 200000.0,
        outstanding = 125000.0,
        emiAmount = 8500.0,
        interestRate = 12.5,
        tenureMonths = 36,
        remainingMonths = 16,
        dueDay = 15,
        startDate = "2025-05-15",
        notes = "Used for home renovation and emergency family expenses.",
        colorHex = "#3b82f6",
        lastPaidDate = "2026-08-15",
        isCurrentMonthPaid = false,
        paymentHistory = listOf(
            PaymentRecord(
                id = "pay-1-1",
                date = "2026-08-15",
                amount = 8500.0,
                principal = 7198.0,
                interest = 1302.0,
                status = "Paid",
                paymentMethod = "Auto Debit (HDFC)"
            ),
            PaymentRecord(
                id = "pay-1-2",
                date = "2026-07-15",
                amount = 8500.0,
                principal = 7124.0,
                interest = 1376.0,
                status = "Paid",
                paymentMethod = "UPI AutoPay"
            ),
            PaymentRecord(
                id = "pay-1-3",
                date = "2026-06-15",
                amount = 8500.0,
                principal = 7050.0,
                interest = 1450.0,
                status = "Paid",
                paymentMethod = "Net Banking"
            )
        )
    ),
    Loan(
        id = "loan-2",
        name = "Education Loan",
        category = "Education Loan",
        bankName = "State Bank of India (SBI)",
        principal = 400000.0,
        outstanding = 310000.0,
        emiAmount = 9000.0,
        interestRate = 9.0,
        tenureMonths = 60,
        remainingMonths = 42,
        dueDay = 20,
        startDate = "2025-01-20",
        notes = "Engineering Masters Tuition & books loan under central scheme.",
        colorHex = "#6366f1",
        lastPaidDate = "2026-08-20",
        isCurrentMonthPaid = false,
        paymentHistory = listOf(
            PaymentRecord(
                id = "pay-2-1",
                date = "2026-08-20",
                amount = 9000.0,
                principal = 6675.0,
                interest = 2325.0,
                status = "Paid",
                paymentMethod = "SBI NetBanking"
            ),
            PaymentRecord(
                id = "pay-2-2",
                date = "2026-07-20",
                amount = 9000.0,
                principal = 6625.0,
                interest = 2375.0,
                status = "Paid",
                paymentMethod = "SBI NetBanking"
            ),
            PaymentRecord(
                id = "pay-2-3",
                date = "2026-06-20",
                amount = 9000.0,
                principal = 6575.0,
                interest = 2425.0,
                status = "Paid",
                paymentMethod = "SBI NetBanking"
            )
        )
    ),
    Loan(
        id = "loan-3",
        name = "Vehicle Loan",
        category = "Vehicle Loan",
        bankName = "ICICI Bank",
        principal = 600000.0,
        outstanding = 380000.0,
        emiAmount = 8500.0,
        interestRate = 10.5,
        tenureMonths = 60,
        remainingMonths = 38,
        dueDay = 10,
        startDate = "2024-11-10",
        notes = "Sedan car financing with fixed interest rate.",
        colorHex = "#0284c7",
        lastPaidDate = "2026-09-10",
        isCurrentMonthPaid = true,
        paymentHistory = listOf(
            PaymentRecord(
                id = "pay-3-1",
                date = "2026-09-10",
                amount = 8500.0,
                principal = 5175.0,
                interest = 3325.0,
                status = "Paid",
                paymentMethod = "ICICI Auto-Debit"
            ),
            PaymentRecord(
                id = "pay-3-2",
                date = "2026-08-10",
                amount = 8500.0,
                principal = 5130.0,
                interest = 3370.0,
                status = "Paid",
                paymentMethod = "ICICI Auto-Debit"
            ),
            PaymentRecord(
                id = "pay-3-3",
                date = "2026-07-10",
                amount = 8500.0,
                principal = 5085.0,
                interest = 3415.0,
                status = "Paid",
                paymentMethod = "ICICI Auto-Debit"
            )
        )
    ),
    Loan(
        id = "loan-4",
        name = "Credit Card EMI",
        category = "Credit Card EMI",
        bankName = "Axis Bank",
        principal = 50000.0,
        outstanding = 27000.0,
        emiAmount = 2500.0,
        interestRate = 15.0,
        tenureMonths = 24,
        remainingMonths = 11,
        dueDay = 5,
        startDate = "2025-10-05",
        notes = "No-cost EMI converted for laptop and monitor purchase.",
        colorHex = "#8b5cf6",
        lastPaidDate = "2026-09-05",
        isCurrentMonthPaid = true,
        paymentHistory = listOf(
            PaymentRecord(
                id = "pay-4-1",
                date = "2026-09-05",
                amount = 2500.0,
                principal = 2162.0,
                interest = 338.0,
                status = "Paid",
                paymentMethod = "Axis Mobile App"
            ),
            PaymentRecord(
                id = "pay-4-2",
                date = "2026-08-05",
                amount = 2500.0,
                principal = 2135.0,
                interest = 365.0,
                status = "Paid",
                paymentMethod = "UPI"
            ),
            PaymentRecord(
                id = "pay-4-3",
                date = "2026-07-05",
                amount = 2500.0,
                principal = 2108.0,
                interest = 392.0,
                status = "Paid",
                paymentMethod = "UPI"
            )
        )
    )
)
