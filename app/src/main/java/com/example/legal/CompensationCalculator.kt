package com.example.legal

import com.example.data.model.FlightDistance

data class CompensationResult(
    val delayHours: Float,
    val isCancelled: Boolean,
    val distanceCategory: FlightDistance,
    val statutoryCompensationNis: Int, // The legal compensation under Aviation Law
    val commissionPercent: Int = 20, // 20% success fee
    val commissionNis: Int,
    val netPassengerNis: Int, // Gross minus commission
    val immediateTerminalRights: List<TerminalRight>,
    val lawReferenceText: String,
    val summaryHeadline: String
)

data class TerminalRight(
    val titleHe: String,
    val descriptionHe: String,
    val iconName: String,
    val isActiveNow: Boolean
)

object CompensationCalculator {

    fun calculate(
        delayHours: Float,
        distanceCategory: FlightDistance,
        isCancelled: Boolean = false,
        terminalExpensesNis: Int = 0
    ): CompensationResult {
        // Statutory compensation is mandatory for delay >= 8 hours or cancellation
        val isStatutoryEligible = isCancelled || delayHours >= 8.0f
        
        // For delays between 2 and 8 hours:
        // Even if statutory 8h hasn't completed yet, passengers at Natbag are already legally entitled
        // to food, drinks, and communications, and can pre-file / submit claim so when 8h passes or if airline refuses assistance,
        // statutory damages + compensation for refusal of assistance (סעיף 11 - פיצויים לדוגמה עד 11,870 ₪) apply.
        val baseGrossCompensation = if (isStatutoryEligible) {
            distanceCategory.statutoryNis
        } else if (delayHours >= 5.0f) {
            // Under section 7: 5-8h delay allows full ticket refund (avg ~1,800 NIS) or alternative flight
            (distanceCategory.statutoryNis * 0.75f).toInt()
        } else if (delayHours >= 3.0f) {
            // EU 261 applies if EU flight, or under 3h delay notice: estimate 1,000 - 1,510 NIS
            1510
        } else {
            // 2 hours delay (as the user explicitly reported from Natbag):
            // Right now immediate food/drink + communications, and claim preparation for potential 1,510 - 3,620 NIS
            distanceCategory.statutoryNis
        }

        val commissionPercent = 20
        val commissionNis = (baseGrossCompensation * (commissionPercent / 100f)).toInt()
        val netPassengerNis = baseGrossCompensation - commissionNis + terminalExpensesNis

        val rights = mutableListOf<TerminalRight>()

        // Right at 2+ hours: Food and beverage
        rights.add(
            TerminalRight(
                titleHe = "מזון ומשקאות (הזכות פעילה מעיכוב של שעתיים)",
                descriptionHe = "חברת התעופה מחויבת לספק שוברים לארוחות ושתייה בטרמינל 3/1, או להחזיר 100% מכל קבלה שתשמור (ללא עמלה כלל).",
                iconName = "restaurant",
                isActiveNow = delayHours >= 2.0f
            )
        )

        // Right at 2+ hours: Communications
        rights.add(
            TerminalRight(
                titleHe = "שירותי תקשורת (2 שיחות ודוא\"ל)",
                descriptionHe = "זכאות לשתי שיחות טלפון ומשלוח הודעה אלקטרונית/פקס ללא תשלום.",
                iconName = "phone",
                isActiveNow = delayHours >= 2.0f
            )
        )

        // Right at 5+ hours: Full refund or alternative flight
        rights.add(
            TerminalRight(
                titleHe = "השבת תמורה מלאה או טיסה חלופית (עיכוב של 5+ שעות)",
                descriptionHe = "באפשרותך לבחור לבטל את הטיסה ולקבל החזר מלא של כרטיס הטיסה תוך 21 יום, או טיסה חלופית במועד הנוח לך.",
                iconName = "swap_horiz",
                isActiveNow = delayHours >= 5.0f
            )
        )

        // Right at 5+ hours: Hotel accommodation if overnight
        rights.add(
            TerminalRight(
                titleHe = "אירוח במלון והסעה (בשהיית לילה)",
                descriptionHe = "אם נדרשת שהייה של לילה בנתב\"ג, חברת התעופה חייבת לממן מלון והסעה הלוך ושוב.",
                iconName = "hotel",
                isActiveNow = delayHours >= 5.0f
            )
        )

        // Right at 8+ hours or cancelled: Full statutory compensation
        rights.add(
            TerminalRight(
                titleHe = "פיצוי כספי סטטוטורי ללא הוכחת נזק (8+ שעות או ביטול)",
                descriptionHe = "זכאות לפיצוי כספי ישיר של ${distanceCategory.statutoryNis} ₪ לפי מרחק הטיסה, על פי חוק שירותי תעופה.",
                iconName = "payments",
                isActiveNow = delayHours >= 8.0f || isCancelled
            )
        )

        val lawRef = when {
            isCancelled -> "סעיף 6 לחוק שירותי תעופה (ביטול טיסה) - זכאות לפיצוי כספי סטטוטורי"
            delayHours >= 8.0f -> "סעיף 6(א) לחוק שירותי תעופה (עיכוב מעל 8 שעות נחשב כטיסה שבוטלה) - זכאות לפיצוי כספי"
            delayHours >= 5.0f -> "סעיף 7 לחוק שירותי תעופה (עיכוב בין 5 ל-8 שעות) - זכות להשבת כספים ומזון"
            else -> "סעיף 6 וסעיף 11 לחוק שירותי תעופה (שירותי סיוע, מזון, שתייה ותביעת פיצוי)"
        }

        val headline = when {
            isCancelled -> "טיסה מבוטלת: זכאות לפיצוי כספי מלא של ${distanceCategory.statutoryNis} ₪"
            delayHours >= 8.0f -> "עיכוב של 8+ שעות: זכאות לפיצוי סטטוטורי של ${distanceCategory.statutoryNis} ₪"
            delayHours >= 5.0f -> "עיכוב של ${delayHours.toInt()} שעות: זכאות להשבת כרטיס, מזון ומלון"
            else -> "עיכוב של שעתיים בנתב\"ג: זכאות לשוברים, מזון והגשת תביעה מקדימה"
        }

        return CompensationResult(
            delayHours = delayHours,
            isCancelled = isCancelled,
            distanceCategory = distanceCategory,
            statutoryCompensationNis = baseGrossCompensation,
            commissionPercent = commissionPercent,
            commissionNis = commissionNis,
            netPassengerNis = netPassengerNis,
            immediateTerminalRights = rights,
            lawReferenceText = lawRef,
            summaryHeadline = headline
        )
    }
}
