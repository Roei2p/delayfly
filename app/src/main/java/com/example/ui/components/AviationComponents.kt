package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimStatus
import com.example.legal.CompensationResult
import com.example.ui.theme.*

@Composable
fun SuccessFeeNoticeBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("success_fee_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoldLegalLight
        ),
        border = BorderStroke(1.5.dp, GoldLegalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GoldLegal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Success Fee",
                    tint = PureWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "המודל העסקי: עמלת הצלחה בלבד (20%)",
                        fontWeight = FontWeight.Bold,
                        color = AviationNavyDark,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "No Win – No Fee! 0 ₪ תשלום מראש. העמלה מנוכה רק לאחר שכספי הפיצוי נכנסים לחשבונך. הוצאות בנתב\"ג מוחזרות ב-100% ללא עמלה!",
                    fontSize = 12.sp,
                    color = Slate700,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun CompensationBreakdownCard(
    calc: CompensationResult,
    terminalExpensesNis: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("compensation_breakdown_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = PureWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate300)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "תחשיב פיצוי לפי חוק שירותי תעופה",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500
                    )
                    Text(
                        text = calc.summaryHeadline,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AviationSkyLight
                ) {
                    Text(
                        text = "חוק טיבי",
                        color = AviationBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Slate100, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Gross statutory compensation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "פיצוי סטטוטורי מלא מחברת התעופה:",
                    color = Slate700,
                    fontSize = 13.sp
                )
                Text(
                    text = "${calc.statutoryCompensationNis} ₪",
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Commission
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "עמלת טיפול והצלחה (20% רק בזכייה):",
                    color = Slate500,
                    fontSize = 13.sp
                )
                Text(
                    text = "-${calc.commissionNis} ₪",
                    color = DangerRed,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            if (terminalExpensesNis > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "החזר הוצאות בנתב\"ג (קבלות 100% נקי אליך):",
                        color = SuccessGreen,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "+$terminalExpensesNis ₪",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Net total payout highlight box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SuccessGreenLight,
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "סכום נטו לחשבון הבנק שלך:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate800
                        )
                        Text(
                            text = "ללא תשלום מראש • ללא דמי פתיחת תיק",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                    Text(
                        text = "${calc.netPassengerNis} ₪",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
fun ClaimStatusTimeline(
    currentStatus: ClaimStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        ClaimStatus.SUBMITTED to "הגשת תביעה",
        ClaimStatus.LEGAL_NOTICE_SENT to "מכתב התראה",
        ClaimStatus.AIRLINE_REVIEW to "בדיקת חברת תעופה",
        ClaimStatus.APPROVED to "אישור פיצוי",
        ClaimStatus.PAID to "העברת כספים"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate100)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "שלבי הטיפול בתביעה מול חברת התעופה",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AviationNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, (status, label) ->
                    val isPastOrCurrent = currentStatus.stepIndex >= status.stepIndex
                    val isCurrent = currentStatus == status

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> AviationBlue
                                        isPastOrCurrent -> SuccessGreen
                                        else -> Slate300
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPastOrCurrent && !isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isPastOrCurrent) PureWhite else Slate700,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) AviationBlue else Slate700,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
