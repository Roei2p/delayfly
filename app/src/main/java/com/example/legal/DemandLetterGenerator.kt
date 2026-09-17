package com.example.legal

import com.example.data.model.FlightClaim
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DemandLetterGenerator {

    fun generate(claim: FlightClaim): String {
        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        
        val cancellationOrDelayClause = if (claim.isCancelled) {
            "ביטול טיסה ללא מתן הודעה מוקדמת כדין, בניגוד לסעיף 6 לחוק שירותי תעופה."
        } else {
            "עיכוב חריג של כ-${claim.delayHours} שעות בהמראת הטיסה מנמל התעופה בן גוריון (TLV) ליעד ${claim.destinationAirport}, כאשר עיכוב העולה על 8 שעות מהווה ביטול טיסה סטטוטורי לפי סעיף 6(א) לחוק, ולמצער עולה על שעתיים ומקים חובת סיוע מידית במזון ושתייה."
        }

        val expensesClause = if (claim.terminalExpensesNis > 0) {
            "\nבנוסף, מרשי/ה נאלץ/ה לשאת בהוצאות מזון ושתייה בנמל התעופה על סך ${claim.terminalExpensesNis} ₪, משום שלא הוענקו שוברים כנדרש. קבלות מצורפות לפנייה זו."
        } else {
            ""
        }

        return """
תאריך: $todayStr
לכבוד:
מחלקת פניות הציבור והמחלקה המשפטית
חברת התעופה: ${claim.airlineName}

הנדון: התראה לפני נקיטת הליכים משפטיים ודרישת פיצוי סטטוטורי
לפי חוק שירותי תעופה (פיצוי וסיוע בשל ביטול טיסה או שינוי בתנאיה), תשע"ב-2012

פרטי הנוסע/ת:
שם מלא: ${claim.passengerName}
טלפון: ${claim.passengerPhone}
דוא"ל: ${claim.passengerEmail}
מספר הזמנה (PNR): ${claim.bookingReference}
מספר טיסה: ${claim.flightNumber}
תאריך הטיסה: ${claim.flightDate}
מסלול: ${claim.departureAirport} אל ${claim.destinationAirport}

מבוא ועובדות המקרה:
1. מרשי/ה היה/הייתה רשומ/ה כנוסע/ת בטיסה שבנדון אשר אמורה הייתה להמריא במועד הנקוב.
2. הטיסה סבלה מ$cancellationOrDelayClause
3. עיכוב זה גרם לטרחה מרובה, עוגמת נפש ושיבוש מהותי של סדר היום.$expensesClause

הזכאות המשפטית והדרישה:
4. בהתאם לתוספת הראשונה לחוק שירותי תעופה, ולמרחק הטיסה (${claim.distanceCategory.titleHe}), זכאי/ת מרשי/ה לפיצוי כספי סטטוטורי ללא הוכחת נזק על סך ${claim.grossCompensationNis} ₪.
5. ככל ששירותי הסיוע הנדרשים בסעיף 6 לחוק (מזון ושתייה) לא סופקו במועד בנתב"ג, הדבר מהווה הפרה נוספת של הוראות החוק המזכה בפיצויים לדוגמה בהתאם לסעיף 11 לחוק.

דרישה לתשלום:
6. הנכם נדרשים להעביר את סכום הפיצוי הכולל בסך ${claim.grossCompensationNis + claim.terminalExpensesNis} ₪ לחשבון הנוסע/ת תוך 21 ימים ממועד קבלת מכתב זה, כקבוע בסעיף 6(ב) לחוק.
7. ככל שהסכום לא ישולם במלואו במועד, תוגש תביעה משפטית לערכאות המוסמכות, לרבות תביעה לפיצויים לדוגמה (עד 11,870 ₪) בתוספת הוצאות משפט ושכ"ט עורך דין.

בכבוד רב,
צוות הטיפול המשפטי FlightClaim
בשם: ${claim.passengerName}
        """.trimIndent()
    }
}
