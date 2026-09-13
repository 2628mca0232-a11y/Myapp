package com.MBA.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.MBA.myapplication.utils.EmiCalculatorUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LoanRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("emiease_prefs", Context.MODE_PRIVATE)

    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans.asStateFlow()

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _dismissedAlerts = MutableStateFlow<List<String>>(emptyList())
    val dismissedAlerts: StateFlow<List<String>> = _dismissedAlerts.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Load settings
        val currencySymbol = prefs.getString("setting_currencySymbol", "₹") ?: "₹"
        val currencyCode = prefs.getString("setting_currencyCode", "INR") ?: "INR"
        val notificationsEnabled = prefs.getBoolean("setting_notificationsEnabled", true)
        val emailAlerts = prefs.getBoolean("setting_emailAlerts", true)
        val whatsappAlerts = prefs.getBoolean("setting_whatsappAlerts", false)
        val theme = prefs.getString("setting_theme", "light") ?: "light"
        _settings.value = UserSettings(
            currencySymbol = currencySymbol,
            currencyCode = currencyCode,
            notificationsEnabled = notificationsEnabled,
            emailAlerts = emailAlerts,
            whatsappAlerts = whatsappAlerts,
            theme = theme
        )

        // Load dismissed alerts
        val dismissedStr = prefs.getString("dismissed_alerts_json", "[]") ?: "[]"
        try {
            val jsonArr = JSONArray(dismissedStr)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArr.length()) {
                list.add(jsonArr.getString(i))
            }
            _dismissedAlerts.value = list
        } catch (e: Exception) {
            _dismissedAlerts.value = emptyList()
        }

        // Load loans
        val loansJson = prefs.getString("loans_json", null)
        if (loansJson.isNull_or_blank_custom()) {
            _loans.value = INITIAL_LOANS
            saveLoansToPrefs(INITIAL_LOANS)
        } else {
            try {
                val parsed = parseLoansJson(loansJson ?: "")
                if (parsed.isNotEmpty()) {
                    _loans.value = parsed
                } else {
                    _loans.value = INITIAL_LOANS
                }
            } catch (e: Exception) {
                _loans.value = INITIAL_LOANS
            }
        }
    }

    private fun String?.isNull_or_blank_custom(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    private fun saveLoansToPrefs(loanList: List<Loan>) {
        val jsonArray = JSONArray()
        for (loan in loanList) {
            val obj = JSONObject()
            obj.put("id", loan.id)
            obj.put("name", loan.name)
            obj.put("category", loan.category)
            obj.put("bankName", loan.bankName)
            obj.put("principal", loan.principal)
            obj.put("outstanding", loan.outstanding)
            obj.put("emiAmount", loan.emiAmount)
            obj.put("interestRate", loan.interestRate)
            obj.put("tenureMonths", loan.tenureMonths)
            obj.put("remainingMonths", loan.remainingMonths)
            obj.put("dueDay", loan.dueDay)
            obj.put("startDate", loan.startDate)
            obj.put("notes", loan.notes)
            obj.put("colorHex", loan.colorHex)
            obj.put("isCurrentMonthPaid", loan.isCurrentMonthPaid)
            obj.put("lastPaidDate", loan.lastPaidDate)

            val historyArray = JSONArray()
            for (rec in loan.paymentHistory) {
                val hObj = JSONObject()
                hObj.put("id", rec.id)
                hObj.put("date", rec.date)
                hObj.put("amount", rec.amount)
                hObj.put("principal", rec.principal)
                hObj.put("interest", rec.interest)
                hObj.put("status", rec.status)
                hObj.put("paymentMethod", rec.paymentMethod)
                hObj.put("notes", rec.notes)
                historyArray.put(hObj)
            }
            obj.put("paymentHistory", historyArray)

            jsonArray.put(obj)
        }
        prefs.edit().putString("loans_json", jsonArray.toString()).apply()
    }

    private fun parseLoansJson(jsonStr: String): List<Loan> {
        val list = mutableListOf<Loan>()
        val jsonArr = JSONArray(jsonStr)
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)

            val historyList = mutableListOf<PaymentRecord>()
            if (obj.has("paymentHistory")) {
                val hArr = obj.getJSONArray("paymentHistory")
                for (j in 0 until hArr.length()) {
                    val hObj = hArr.getJSONObject(j)
                    historyList.add(
                        PaymentRecord(
                            id = hObj.optString("id", "pay-${UUID.randomUUID()}"),
                            date = hObj.optString("date", ""),
                            amount = hObj.optDouble("amount", 0.0),
                            principal = hObj.optDouble("principal", 0.0),
                            interest = hObj.optDouble("interest", 0.0),
                            status = hObj.optString("status", "Paid"),
                            paymentMethod = hObj.optString("paymentMethod", "Online Banking"),
                            notes = hObj.optString("notes", "")
                        )
                    )
                }
            }

            list.add(
                Loan(
                    id = obj.optString("id", "loan-${UUID.randomUUID()}"),
                    name = obj.optString("name", "Untitled Loan"),
                    category = obj.optString("category", "Personal Loan"),
                    bankName = obj.optString("bankName", "General Lender"),
                    principal = obj.optDouble("principal", 0.0),
                    outstanding = obj.optDouble("outstanding", 0.0),
                    emiAmount = obj.optDouble("emiAmount", 0.0),
                    interestRate = obj.optDouble("interestRate", 0.0),
                    tenureMonths = obj.optInt("tenureMonths", 12),
                    remainingMonths = obj.optInt("remainingMonths", 12),
                    dueDay = obj.optInt("dueDay", 5),
                    startDate = obj.optString("startDate", ""),
                    notes = obj.optString("notes", ""),
                    colorHex = obj.optString("colorHex", "#3b82f6"),
                    isCurrentMonthPaid = obj.optBoolean("isCurrentMonthPaid", false),
                    lastPaidDate = obj.optString("lastPaidDate", ""),
                    paymentHistory = historyList
                )
            )
        }
        return list
    }

    fun addLoan(loan: Loan): Loan {
        val calculatedEmi = EmiCalculatorUtils.calculateEMI(
            loan.principal,
            loan.interestRate,
            loan.tenureMonths
        )
        val finalEmi = if (loan.emiAmount > 0) loan.emiAmount else calculatedEmi
        val color = if (loan.colorHex.isNotBlank()) loan.colorHex else COLOR_PALETTE[_loans.value.size % COLOR_PALETTE.size]

        val newLoan = loan.copy(
            id = if (loan.id.isNotBlank() && !loan.id.startsWith("preset")) loan.id else "loan-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}",
            emiAmount = finalEmi,
            colorHex = color,
            outstanding = if (loan.outstanding > 0) loan.outstanding else loan.principal,
            remainingMonths = if (loan.remainingMonths > 0) loan.remainingMonths else loan.tenureMonths
        )

        val updated = listOf(newLoan) + _loans.value
        _loans.value = updated
        saveLoansToPrefs(updated)
        return newLoan
    }

    fun updateLoan(id: String, updatedLoan: Loan) {
        val updated = _loans.value.map { existing ->
            if (existing.id == id) {
                updatedLoan.copy(id = id)
            } else {
                existing
            }
        }
        _loans.value = updated
        saveLoansToPrefs(updated)
    }

    fun deleteLoan(id: String) {
        val updated = _loans.value.filter { it.id != id }
        _loans.value = updated
        saveLoansToPrefs(updated)
    }

    fun markEmiPaid(loanId: String, amountPaid: Double = 0.0, method: String = "Online Banking", notes: String = "Monthly EMI Payment") {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())

        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) {
                val actualPaid = if (amountPaid > 0) amountPaid else loan.emiAmount
                val monthlyRate = loan.interestRate / 12.0 / 100.0
                val interestComp = (loan.outstanding * monthlyRate).coerceAtLeast(0.0)
                val principalComp = (actualPaid - interestComp).coerceIn(0.0, loan.outstanding)

                val newOutstanding = (loan.outstanding - principalComp).coerceAtLeast(0.0)
                val newRemainingMonths = (loan.remainingMonths - 1).coerceAtLeast(0)

                val newHistoryRecord = PaymentRecord(
                    id = "pay-${System.currentTimeMillis()}",
                    date = todayStr,
                    amount = actualPaid,
                    principal = principalComp,
                    interest = interestComp,
                    status = "Paid",
                    paymentMethod = method,
                    notes = notes
                )

                loan.copy(
                    outstanding = newOutstanding,
                    remainingMonths = newRemainingMonths,
                    isCurrentMonthPaid = true,
                    lastPaidDate = todayStr,
                    paymentHistory = listOf(newHistoryRecord) + loan.paymentHistory
                )
            } else {
                loan
            }
        }
        _loans.value = updated
        saveLoansToPrefs(updated)
    }

    fun prepayLoan(loanId: String, amount: Double, notes: String = "Lump sum prepayment") {
        if (amount <= 0) return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())

        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) {
                val newOutstanding = (loan.outstanding - amount).coerceAtLeast(0.0)
                val newHistoryRecord = PaymentRecord(
                    id = "prepay-${System.currentTimeMillis()}",
                    date = todayStr,
                    amount = amount,
                    principal = amount,
                    interest = 0.0,
                    status = "Paid",
                    paymentMethod = "Prepayment / Lump-Sum",
                    notes = notes
                )

                loan.copy(
                    outstanding = newOutstanding,
                    paymentHistory = listOf(newHistoryRecord) + loan.paymentHistory
                )
            } else {
                loan
            }
        }
        _loans.value = updated
        saveLoansToPrefs(updated)
    }

    fun resetDemoData() {
        _loans.value = INITIAL_LOANS
        _dismissedAlerts.value = emptyList()
        saveLoansToPrefs(INITIAL_LOANS)
        prefs.edit().putString("dismissed_alerts_json", "[]").apply()
    }

    fun clearAllData() {
        _loans.value = emptyList()
        _dismissedAlerts.value = emptyList()
        saveLoansToPrefs(emptyList())
        prefs.edit().putString("dismissed_alerts_json", "[]").apply()
    }

    fun exportJson(): String {
        val jsonArray = JSONArray()
        for (loan in _loans.value) {
            val obj = JSONObject()
            obj.put("id", loan.id)
            obj.put("name", loan.name)
            obj.put("category", loan.category)
            obj.put("bankName", loan.bankName)
            obj.put("principal", loan.principal)
            obj.put("outstanding", loan.outstanding)
            obj.put("emiAmount", loan.emiAmount)
            obj.put("interestRate", loan.interestRate)
            obj.put("tenureMonths", loan.tenureMonths)
            obj.put("remainingMonths", loan.remainingMonths)
            obj.put("dueDay", loan.dueDay)
            obj.put("startDate", loan.startDate)
            obj.put("notes", loan.notes)
            obj.put("colorHex", loan.colorHex)
            obj.put("isCurrentMonthPaid", loan.isCurrentMonthPaid)
            obj.put("lastPaidDate", loan.lastPaidDate)

            val historyArray = JSONArray()
            for (rec in loan.paymentHistory) {
                val hObj = JSONObject()
                hObj.put("id", rec.id)
                hObj.put("date", rec.date)
                hObj.put("amount", rec.amount)
                hObj.put("principal", rec.principal)
                hObj.put("interest", rec.interest)
                hObj.put("status", rec.status)
                hObj.put("paymentMethod", rec.paymentMethod)
                hObj.put("notes", rec.notes)
                historyArray.put(hObj)
            }
            obj.put("paymentHistory", historyArray)

            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importJson(jsonStr: String): Boolean {
        return try {
            val parsed = parseLoansJson(jsonStr)
            if (parsed.isNotEmpty()) {
                _loans.value = parsed
                saveLoansToPrefs(parsed)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun dismissAlert(alertId: String) {
        val updated = _dismissedAlerts.value + alertId
        _dismissedAlerts.value = updated
        val jsonArr = JSONArray()
        updated.forEach { jsonArr.put(it) }
        prefs.edit().putString("dismissed_alerts_json", jsonArr.toString()).apply()
    }

    fun updateSettings(newSettings: UserSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putString("setting_currencySymbol", newSettings.currencySymbol)
            .putString("setting_currencyCode", newSettings.currencyCode)
            .putBoolean("setting_notificationsEnabled", newSettings.notificationsEnabled)
            .putBoolean("setting_emailAlerts", newSettings.emailAlerts)
            .putBoolean("setting_whatsappAlerts", newSettings.whatsappAlerts)
            .putString("setting_theme", newSettings.theme)
            .apply()
    }

    fun getCalculatedStats(): LoanStats {
        val currentLoans = _loans.value
        val activeLoans = currentLoans.filter { it.outstanding > 0 }
        val totalActiveLoans = activeLoans.size
        val totalMonthlyEMI = activeLoans.sumOf { it.emiAmount }
        val totalOutstanding = currentLoans.sumOf { it.outstanding }
        val totalPrincipal = currentLoans.sumOf { it.principal }
        val totalRepaid = (totalPrincipal - totalOutstanding).coerceAtLeast(0.0)
        val overallRepaidPercentage = if (totalPrincipal > 0) (totalRepaid / totalPrincipal) * 100.0 else 0.0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val upcomingList = activeLoans.map { loan ->
            val nextDueCal = EmiCalculatorUtils.getNextDueDate(loan.dueDay)
            val daysLeft = EmiCalculatorUtils.getDaysRemaining(nextDueCal)

            val status = when {
                loan.isCurrentMonthPaid -> AlertType.PAID
                daysLeft < 0 -> AlertType.OVERDUE
                daysLeft <= 3 -> AlertType.DUE_SOON
                else -> AlertType.DUE_TODAY // or upcoming
            }

            UpcomingEmiItem(
                loan = loan,
                nextDueDateStr = sdf.format(nextDueCal.time),
                daysLeft = daysLeft,
                status = status
            )
        }.sortedWith { a, b ->
            if (a.loan.isCurrentMonthPaid && !b.loan.isCurrentMonthPaid) 1
            else if (!a.loan.isCurrentMonthPaid && b.loan.isCurrentMonthPaid) -1
            else a.daysLeft.compareTo(b.daysLeft)
        }

        val nextUnpaid = upcomingList.firstOrNull { !it.loan.isCurrentMonthPaid } ?: upcomingList.firstOrNull()

        val highestEmi = activeLoans.maxByOrNull { it.emiAmount }
        val highestOutstanding = activeLoans.maxByOrNull { it.outstanding }

        return LoanStats(
            totalActiveLoans = totalActiveLoans,
            totalMonthlyEMI = totalMonthlyEMI,
            totalOutstanding = totalOutstanding,
            totalPrincipal = totalPrincipal,
            totalRepaid = totalRepaid,
            overallRepaidPercentage = overallRepaidPercentage,
            upcomingEmis = upcomingList,
            nextEmiDue = nextUnpaid,
            highestEmiLoan = highestEmi,
            highestOutstandingLoan = highestOutstanding
        )
    }

    fun getCalculatedAlerts(): List<LoanAlert> {
        val stats = getCalculatedStats()
        val dismissed = _dismissedAlerts.value
        val list = mutableListOf<LoanAlert>()

        stats.upcomingEmis.forEach { item ->
            val loan = item.loan
            if (item.status == AlertType.OVERDUE) {
                val alertId = "alert-overdue-${loan.id}"
                if (!dismissed.contains(alertId)) {
                    list.add(
                        LoanAlert(
                            id = alertId,
                            loanId = loan.id,
                            title = "Overdue EMI: ${loan.name}",
                            message = "Your EMI of ${settings.value.currencySymbol}${loan.emiAmount.toInt()} for ${loan.name} (${loan.bankName}) is overdue by ${Math.abs(item.daysLeft)} days.",
                            type = AlertType.OVERDUE,
                            date = item.nextDueDateStr,
                            emiAmount = loan.emiAmount,
                            bankName = loan.bankName
                        )
                    )
                }
            } else if (item.daysLeft == 0 && !loan.isCurrentMonthPaid) {
                val alertId = "alert-today-${loan.id}"
                if (!dismissed.contains(alertId)) {
                    list.add(
                        LoanAlert(
                            id = alertId,
                            loanId = loan.id,
                            title = "Due Today: ${loan.name}",
                            message = "Your EMI of ${settings.value.currencySymbol}${loan.emiAmount.toInt()} is due today for ${loan.name}.",
                            type = AlertType.DUE_TODAY,
                            date = item.nextDueDateStr,
                            emiAmount = loan.emiAmount,
                            bankName = loan.bankName
                        )
                    )
                }
            } else if (item.daysLeft in 1..3 && !loan.isCurrentMonthPaid) {
                val alertId = "alert-soon-${loan.id}"
                if (!dismissed.contains(alertId)) {
                    list.add(
                        LoanAlert(
                            id = alertId,
                            loanId = loan.id,
                            title = "Upcoming EMI: ${loan.name}",
                            message = "${loan.name} EMI of ${settings.value.currencySymbol}${loan.emiAmount.toInt()} is due in ${item.daysLeft} days.",
                            type = AlertType.DUE_SOON,
                            date = item.nextDueDateStr,
                            emiAmount = loan.emiAmount,
                            bankName = loan.bankName
                        )
                    )
                }
            } else if (loan.isCurrentMonthPaid) {
                val alertId = "alert-paid-${loan.id}"
                if (!dismissed.contains(alertId)) {
                    list.add(
                        LoanAlert(
                            id = alertId,
                            loanId = loan.id,
                            title = "EMI Paid: ${loan.name}",
                            message = "${loan.name} EMI of ${settings.value.currencySymbol}${loan.emiAmount.toInt()} has been paid successfully for this cycle.",
                            type = AlertType.PAID,
                            date = loan.lastPaidDate.ifBlank { item.nextDueDateStr },
                            emiAmount = loan.emiAmount,
                            bankName = loan.bankName
                        )
                    )
                }
            }
        }

        return list
    }
}
