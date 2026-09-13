package com.MBA.myapplication.utils

import com.MBA.myapplication.data.AmortizationItem
import com.MBA.myapplication.data.PrepaymentResult
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

object EmiCalculatorUtils {

    /**
     * Calculate Monthly EMI using standard formula:
     * E = P * r * (1 + r)^n / ((1 + r)^n - 1)
     */
    fun calculateEMI(principal: Double, annualRate: Double, tenureMonths: Int): Double {
        if (principal <= 0 || tenureMonths <= 0) return 0.0
        if (annualRate <= 0) return (principal / tenureMonths).roundToInt().toDouble()

        val r = annualRate / 12.0 / 100.0
        val n = tenureMonths.toDouble()
        val compoundFactor = (1 + r).pow(n)
        val emi = (principal * r * compoundFactor) / (compoundFactor - 1)
        return emi.roundToInt().toDouble()
    }

    /**
     * Generate month-by-month amortization schedule
     */
    fun generateAmortizationSchedule(
        principal: Double,
        annualRate: Double,
        tenureMonths: Int,
        startDateStr: String = ""
    ): List<AmortizationItem> {
        val n = if (tenureMonths <= 0) 1 else tenureMonths
        val r = annualRate / 12.0 / 100.0
        val emi = calculateEMI(principal, annualRate, n)

        var remainingBalance = principal
        var totalPaidInterest = 0.0
        var totalPaidPrincipal = 0.0
        val schedule = mutableListOf<AmortizationItem>()

        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfDisplay = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()
        if (startDateStr.isNotBlank()) {
            try {
                val parsedDate = sdfInput.parse(startDateStr)
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            } catch (e: Exception) {
                // fallback to current date
            }
        }

        for (month in 1..n) {
            if (remainingBalance <= 0) break

            val interestForMonth = (remainingBalance * r).roundToInt().toDouble()
            var principalForMonth = emi - interestForMonth

            if (month == n || principalForMonth > remainingBalance) {
                principalForMonth = remainingBalance
            }

            val currentEMI = principalForMonth + interestForMonth
            remainingBalance = (remainingBalance - principalForMonth).coerceAtLeast(0.0)
            totalPaidInterest += interestForMonth
            totalPaidPrincipal += principalForMonth

            calendar.add(Calendar.MONTH, 1)

            schedule.add(
                AmortizationItem(
                    monthNumber = month,
                    date = sdfInput.format(calendar.time),
                    displayDate = sdfDisplay.format(calendar.time),
                    emiAmount = currentEMI,
                    principal = principalForMonth,
                    interest = interestForMonth,
                    remainingBalance = remainingBalance,
                    totalInterestPaid = totalPaidInterest,
                    totalPrincipalPaid = totalPaidPrincipal
                )
            )
        }

        return schedule
    }

    /**
     * Calculate Prepayment Savings Impact
     */
    fun calculatePrepaymentSavings(
        outstanding: Double,
        annualRate: Double,
        remainingMonths: Int,
        prepaymentAmount: Double,
        regularEMI: Double
    ): PrepaymentResult {
        if (outstanding <= 0 || prepaymentAmount <= 0) {
            return PrepaymentResult(
                newTenure = remainingMonths,
                monthsSaved = 0,
                interestSaved = 0.0
            )
        }

        val emi = if (regularEMI > 0) regularEMI else calculateEMI(outstanding, annualRate, remainingMonths)
        val r = annualRate / 12.0 / 100.0
        val newPrincipal = (outstanding - prepaymentAmount).coerceAtLeast(0.0)

        if (newPrincipal <= 0.0) {
            val originalInterest = (emi * remainingMonths) - outstanding
            return PrepaymentResult(
                newTenure = 0,
                monthsSaved = remainingMonths,
                interestSaved = originalInterest.coerceAtLeast(0.0)
            )
        }

        var newTenure = remainingMonths
        if (r > 0 && emi > newPrincipal * r) {
            val num = 1.0 - (newPrincipal * r) / emi
            if (num > 0) {
                newTenure = ceil(-ln(num) / ln(1.0 + r)).toInt()
            }
        }

        val originalTotalInterest = ((emi * remainingMonths) - outstanding).coerceAtLeast(0.0)
        val newTotalInterest = ((emi * newTenure) - newPrincipal).coerceAtLeast(0.0)
        val interestSaved = (originalTotalInterest - newTotalInterest).coerceAtLeast(0.0)
        val monthsSaved = (remainingMonths - newTenure).coerceAtLeast(0)

        return PrepaymentResult(
            newTenure = newTenure,
            monthsSaved = monthsSaved,
            interestSaved = interestSaved.roundToInt().toDouble()
        )
    }

    /**
     * Get Next Due Date Calendar object for a specific day of month
     */
    fun getNextDueDate(dueDay: Int): Calendar {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val targetDay = dueDay.coerceIn(1, 31)

        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.set(Calendar.MILLISECOND, 0)

        val maxDaysInCurrentMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayToSet = targetDay.coerceAtMost(maxDaysInCurrentMonth)
        dueDate.set(Calendar.DAY_OF_MONTH, dayToSet)

        if (currentDay > dayToSet) {
            dueDate.add(Calendar.MONTH, 1)
            val maxDaysInNextMonth = dueDate.getActualMaximum(Calendar.DAY_OF_MONTH)
            dueDate.set(Calendar.DAY_OF_MONTH, targetDay.coerceAtMost(maxDaysInNextMonth))
        }

        return dueDate
    }

    /**
     * Calculate days remaining until due date
     */
    fun getDaysRemaining(nextDueDate: Calendar): Int {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val diffMillis = nextDueDate.timeInMillis - today.timeInMillis
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * Format currency amount with symbol
     */
    fun formatCurrency(amount: Double, symbol: String = "₹"): String {
        val formatter = DecimalFormat("#,##,##0")
        return "$symbol${formatter.format(amount.roundToInt())}"
    }

    /**
     * Format numbers
     */
    fun formatNumber(amount: Double): String {
        val formatter = DecimalFormat("#,##,##0")
        return formatter.format(amount.roundToInt())
    }

    /**
     * Format percent
     */
    fun formatPercent(percent: Double): String {
        val formatter = DecimalFormat("0.0")
        return "${formatter.format(percent)}%"
    }

    /**
     * Format Date string YYYY-MM-DD into readable date
     */
    fun formatDateReadable(dateStr: String): String {
        if (dateStr.isBlank()) return "N/A"
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = sdfInput.parse(dateStr)
            if (date != null) sdfOutput.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getCurrentDateStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
