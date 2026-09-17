package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ClaimStatus(val titleHe: String, val stepIndex: Int) {
    SUBMITTED("תביעה הוגשה במערכת", 1),
    LEGAL_NOTICE_SENT("נשלח מכתב דרישה רשמי", 2),
    AIRLINE_REVIEW("בבדיקה משפטית מול חברת התעופה", 3),
    APPROVED("הפיצוי אושר לתשלום", 4),
    PAID("הכספים הועברו לחשבונך", 5)
}

enum class FlightDistance(val titleHe: String, val statutoryNis: Int, val exampleDest: String) {
    SHORT("עד 2,000 ק\"מ (לרנקה, אתונה, כרתים)", 1510, "יוון, קפריסין, בולגריה"),
    MEDIUM("2,000 עד 4,500 ק\"מ (אירופה, רומא, פריז, לונדון)", 2420, "איטליה, צרפת, אנגליה, גרמניה"),
    LONG("מעל 4,500 ק\"מ (ניו יורק, מיאמי, המזרח הרחוק)", 3620, "ארה\"ב, תאילנד, יפן, הודו")
}

@Entity(tableName = "flight_claims")
data class FlightClaim(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val passengerName: String,
    val passengerPhone: String,
    val passengerEmail: String,
    val bookingReference: String, // PNR
    val flightNumber: String, // e.g. LY001
    val airlineName: String, // e.g. אל על
    val departureAirport: String = "נתב\"ג (TLV)",
    val destinationAirport: String,
    val flightDate: String,
    val delayHours: Float, // e.g. 2.0, 3.5, 8.0
    val isCancelled: Boolean = false,
    val delayReasonReported: String = "עיכוב תפעולי",
    val distanceCategory: FlightDistance = FlightDistance.MEDIUM,
    val grossCompensationNis: Int = 2420,
    val commissionPercent: Int = 20, // Business model: 20% success fee
    val commissionFeeNis: Int = 484, // 20% of gross
    val netPayoutNis: Int = 1936, // 80% to passenger
    val terminalExpensesNis: Int = 0, // Receipts kept at Natbag (100% to passenger)
    val status: ClaimStatus = ClaimStatus.SUBMITTED,
    val createdAt: Long = System.currentTimeMillis(),
    val legalDemandLetter: String = "",
    val ticketImageUri: String? = null
)
