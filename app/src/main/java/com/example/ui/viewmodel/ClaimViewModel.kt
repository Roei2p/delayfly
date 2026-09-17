package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClaimStatus
import com.example.data.model.FlightClaim
import com.example.data.model.FlightDistance
import com.example.data.repository.FlightClaimRepository
import com.example.legal.CompensationCalculator
import com.example.legal.CompensationResult
import com.example.legal.DemandLetterGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FlightDraftState(
    val passengerName: String = "",
    val passengerPhone: String = "",
    val passengerEmail: String = "",
    val bookingReference: String = "LY7X9K",
    val flightNumber: String = "LY001",
    val airlineName: String = "אל על (El Al)",
    val departureAirport: String = "נתב\"ג (TLV - טרמינל 3)",
    val destinationAirport: String = "ניו יורק (JFK)",
    val flightDate: String = "17/09/2026",
    val delayHours: Float = 2.0f, // 2-hour delay as user stated!
    val isCancelled: Boolean = false,
    val delayReasonReported: String = "עיכוב תפעולי / תקלה טכנית במטוס",
    val distanceCategory: FlightDistance = FlightDistance.LONG,
    val terminalExpensesNis: Int = 85, // e.g. coffee & food at Natbag
    val ticketImageUri: String? = null
)

class ClaimViewModel(
    private val repository: FlightClaimRepository
) : ViewModel() {

    val allClaims: StateFlow<List<FlightClaim>> = repository.getAllClaims()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _draft = MutableStateFlow(FlightDraftState())
    val draft: StateFlow<FlightDraftState> = _draft.asStateFlow()

    private val _selectedClaim = MutableStateFlow<FlightClaim?>(null)
    val selectedClaim: StateFlow<FlightClaim?> = _selectedClaim.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Airport Hub / Quick Claim, 1: My Claims, 2: Rights Guide
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _claimSubmissionSuccess = MutableStateFlow(false)
    val claimSubmissionSuccess: StateFlow<Boolean> = _claimSubmissionSuccess.asStateFlow()

    init {
        // Seed initial sample claim if database is empty so user can immediately see claims flow
        viewModelScope.launch {
            repository.getAllClaims().collect { claims ->
                if (claims.isEmpty()) {
                    seedSampleClaim()
                }
            }
        }
    }

    private suspend fun seedSampleClaim() {
        val sampleClaim = FlightClaim(
            passengerName = "ישראל ישראלי",
            passengerPhone = "050-9876543",
            passengerEmail = "israel@example.com",
            bookingReference = "WZ94TL",
            flightNumber = "W6 2325",
            airlineName = "Wizz Air",
            departureAirport = "נתב\"ג (TLV - טרמינל 1)",
            destinationAirport = "בודפשט (BUD)",
            flightDate = "10/09/2026",
            delayHours = 8.5f,
            isCancelled = false,
            delayReasonReported = "איחור הגעת מטוס / עיכוב תפעולי",
            distanceCategory = FlightDistance.MEDIUM,
            grossCompensationNis = 2420,
            terminalExpensesNis = 120,
            totalPayoutNis = 2540,
            status = ClaimStatus.AIRLINE_REVIEW,
            createdAt = System.currentTimeMillis() - 86400000L * 3,
            legalDemandLetter = "דרישה לפיצוי סטטוטורי לפי חוק שירותי תעופה נשלחה למחלקה המשפטית של Wizz Air."
        )
        repository.insertClaim(sampleClaim)
    }

    fun setTab(index: Int) {
        _activeTab.value = index
    }

    fun selectClaim(claim: FlightClaim?) {
        _selectedClaim.value = claim
    }

    fun updateDraft(updater: (FlightDraftState) -> FlightDraftState) {
        _draft.value = updater(_draft.value)
    }

    fun setDelayHours(hours: Float) {
        _draft.value = _draft.value.copy(delayHours = hours)
    }

    fun setCancelled(isCancelled: Boolean) {
        _draft.value = _draft.value.copy(isCancelled = isCancelled)
    }

    fun selectSampleFlight(flightNum: String, airline: String, dest: String, dist: FlightDistance) {
        _draft.value = _draft.value.copy(
            flightNumber = flightNum,
            airlineName = airline,
            destinationAirport = dest,
            distanceCategory = dist
        )
    }

    fun addTerminalExpense(amount: Int) {
        _draft.value = _draft.value.copy(
            terminalExpensesNis = _draft.value.terminalExpensesNis + amount
        )
    }

    fun getCalculation(): CompensationResult {
        val d = _draft.value
        return CompensationCalculator.calculate(
            delayHours = d.delayHours,
            distanceCategory = d.distanceCategory,
            isCancelled = d.isCancelled,
            terminalExpensesNis = d.terminalExpensesNis
        )
    }

    fun submitClaim() {
        viewModelScope.launch {
            val d = _draft.value
            val calc = getCalculation()

            var newClaim = FlightClaim(
                passengerName = d.passengerName.ifBlank { "נוסע בנתב\"ג" },
                passengerPhone = d.passengerPhone.ifBlank { "054-0000000" },
                passengerEmail = d.passengerEmail.ifBlank { "passenger@natbag.co.il" },
                bookingReference = d.bookingReference.ifBlank { "TL7799" },
                flightNumber = d.flightNumber.ifBlank { "LY001" },
                airlineName = d.airlineName.ifBlank { "חברת תעופה" },
                departureAirport = d.departureAirport,
                destinationAirport = d.destinationAirport.ifBlank { "יעד בחו\"ל" },
                flightDate = d.flightDate,
                delayHours = d.delayHours,
                isCancelled = d.isCancelled,
                delayReasonReported = d.delayReasonReported,
                distanceCategory = d.distanceCategory,
                grossCompensationNis = calc.statutoryCompensationNis,
                terminalExpensesNis = d.terminalExpensesNis,
                totalPayoutNis = calc.totalEntitlementNis,
                status = ClaimStatus.LEGAL_NOTICE_SENT,
                createdAt = System.currentTimeMillis(),
                ticketImageUri = d.ticketImageUri
            )

            val letter = DemandLetterGenerator.generate(newClaim)
            newClaim = newClaim.copy(legalDemandLetter = letter)

            val id = repository.insertClaim(newClaim)
            _selectedClaim.value = newClaim.copy(id = id)
            _claimSubmissionSuccess.value = true
            _activeTab.value = 1 // Switch to claims list
        }
    }

    fun dismissSuccessDialog() {
        _claimSubmissionSuccess.value = false
    }

    fun updateClaimStatus(claim: FlightClaim, newStatus: ClaimStatus) {
        viewModelScope.launch {
            val updated = claim.copy(status = newStatus)
            repository.updateClaim(updated)
            if (_selectedClaim.value?.id == claim.id) {
                _selectedClaim.value = updated
            }
        }
    }

    fun addExpenseToClaim(claim: FlightClaim, amountNis: Int) {
        viewModelScope.launch {
            val newExpenses = claim.terminalExpensesNis + amountNis
            val updated = claim.copy(
                terminalExpensesNis = newExpenses,
                totalPayoutNis = claim.grossCompensationNis + newExpenses
            )
            repository.updateClaim(updated)
            if (_selectedClaim.value?.id == claim.id) {
                _selectedClaim.value = updated
            }
        }
    }

    fun deleteClaim(claim: FlightClaim) {
        viewModelScope.launch {
            repository.deleteClaimById(claim.id)
            if (_selectedClaim.value?.id == claim.id) {
                _selectedClaim.value = null
            }
        }
    }
}

class ClaimViewModelFactory(
    private val repository: FlightClaimRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClaimViewModel::class.java)) {
            return ClaimViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
