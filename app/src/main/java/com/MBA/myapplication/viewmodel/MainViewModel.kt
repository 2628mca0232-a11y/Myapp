package com.MBA.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.MBA.myapplication.data.Loan
import com.MBA.myapplication.data.LoanAlert
import com.MBA.myapplication.data.LoanRepository
import com.MBA.myapplication.data.LoanStats
import com.MBA.myapplication.data.UserSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LoanRepository(application)

    val loans: StateFlow<List<Loan>> = repository.loans
    val settings: StateFlow<UserSettings> = repository.settings

    private val _activeTab = MutableStateFlow("dashboard")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Dialog & Modal state
    private val _isAddModalOpen = MutableStateFlow(false)
    val isAddModalOpen: StateFlow<Boolean> = _isAddModalOpen.asStateFlow()

    private val _editingLoan = MutableStateFlow<Loan?>(null)
    val editingLoan: StateFlow<Loan?> = _editingLoan.asStateFlow()

    private val _viewingLoan = MutableStateFlow<Loan?>(null)
    val viewingLoan: StateFlow<Loan?> = _viewingLoan.asStateFlow()

    private val _prepayingLoan = MutableStateFlow<Loan?>(null)
    val prepayingLoan: StateFlow<Loan?> = _prepayingLoan.asStateFlow()

    private val _deletingLoanId = MutableStateFlow<String?>(null)
    val deletingLoanId: StateFlow<String?> = _deletingLoanId.asStateFlow()

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    fun openAddModal(presetLoan: Loan? = null) {
        _editingLoan.value = presetLoan
        _isAddModalOpen.value = true
    }

    fun closeAddModal() {
        _isAddModalOpen.value = false
        _editingLoan.value = null
    }

    fun editLoan(loan: Loan) {
        _editingLoan.value = loan
        _isAddModalOpen.value = true
    }

    fun viewLoanDetails(loan: Loan) {
        _viewingLoan.value = loan
    }

    fun closeViewLoanDetails() {
        _viewingLoan.value = null
    }

    fun openPrepay(loan: Loan) {
        _viewingLoan.value = null
        _prepayingLoan.value = loan
    }

    fun closePrepay() {
        _prepayingLoan.value = null
    }

    fun confirmDelete(loanId: String) {
        _deletingLoanId.value = loanId
    }

    fun cancelDelete() {
        _deletingLoanId.value = null
    }

    fun saveLoan(loan: Loan) {
        if (_editingLoan.value != null && _editingLoan.value?.id?.isNotBlank() == true) {
            repository.updateLoan(_editingLoan.value!!.id, loan)
            showToast("Loan \"${loan.name}\" updated successfully!")
        } else {
            val created = repository.addLoan(loan)
            showToast("Loan \"${created.name}\" added successfully!")
        }
        closeAddModal()
    }

    fun deleteLoanConfirmed() {
        val id = _deletingLoanId.value ?: return
        val target = loans.value.find { it.id == id }
        repository.deleteLoan(id)
        if (_viewingLoan.value?.id == id) {
            _viewingLoan.value = null
        }
        _deletingLoanId.value = null
        showToast("Loan \"${target?.name ?: "Item"}\" deleted.")
    }

    fun markEmiPaid(loanId: String, amount: Double = 0.0, method: String = "Online Banking") {
        val target = loans.value.find { it.id == loanId }
        val amountPaid = if (amount > 0) amount else (target?.emiAmount ?: 0.0)
        repository.markEmiPaid(loanId, amountPaid, method)

        // update viewing loan if open
        _viewingLoan.value?.let { currentViewing ->
            if (currentViewing.id == loanId) {
                _viewingLoan.value = repository.loans.value.find { it.id == loanId }
            }
        }

        showToast("🎉 Marked EMI of ${settings.value.currencySymbol}${amountPaid.toInt()} as Paid for ${target?.name ?: "Loan"}!")
    }

    fun prepayLoan(loanId: String, amount: Double, notes: String) {
        val target = loans.value.find { it.id == loanId }
        repository.prepayLoan(loanId, amount, notes)
        closePrepay()
        showToast("Lump-sum payment of ${settings.value.currencySymbol}${amount.toInt()} applied to ${target?.name ?: "Loan"}!")
    }

    fun resetDemoData() {
        repository.resetDemoData()
        showToast("Reset back to default realistic demo loans!")
    }

    fun clearAllData() {
        repository.clearAllData()
        showToast("All loan data cleared.")
    }

    fun exportData(): String {
        val json = repository.exportJson()
        showToast("Loans data JSON ready for backup!")
        return json
    }

    fun importData(jsonStr: String): Boolean {
        val success = repository.importJson(jsonStr)
        if (success) {
            showToast("Successfully imported loans data!")
        } else {
            showToast("Failed to import JSON data. Invalid format.")
        }
        return success
    }

    fun dismissAlert(alertId: String) {
        repository.dismissAlert(alertId)
    }

    fun updateSettings(newSettings: UserSettings) {
        repository.updateSettings(newSettings)
        showToast("Settings saved successfully.")
    }

    fun getStats(): LoanStats {
        return repository.getCalculatedStats()
    }

    fun getAlerts(): List<LoanAlert> {
        return repository.getCalculatedAlerts()
    }
}
