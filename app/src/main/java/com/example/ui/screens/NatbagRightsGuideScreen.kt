package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FreeToolNoticeBanner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NatbagRightsGuideScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "מדריך זכויות נוסע בנתב\"ג",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AviationNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("guide_back_btn")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "חזרה",
                            tint = AviationNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(AviationNavy, AviationBlue)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "חוק שירותי תעופה (חוק טיבי)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "החוק הישראלי מגן עליך באופן מלא מרגע שהטיסה מתעכבת בשעתיים ומעלה בנתב\"ג!",
                        fontSize = 13.sp,
                        color = AviationSkyLight,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FreeToolNoticeBanner()

            Spacer(modifier = Modifier.height(16.dp))

            // Stage 1: 2 Hours Delay (User's current situation!)
            GuideSectionCard(
                stageTitle = "עיכוב של שעתיים (המצב שלך כעת)",
                badgeText = "פעיל כעת",
                badgeColor = GoldLegal,
                icon = Icons.Default.Restaurant,
                iconTint = GoldLegal,
                items = listOf(
                    "מזון ומשקאות: חברת התעופה מחויבת להעניק לך שוברים לארוחות ושתייה בטרמינל 3 או טרמינל 1.",
                    "שמירת קבלות: אם נציגי החברה לא חילקו שוברים, שמור כל קבלה על קפה, כריך או שתייה שרכשת בנתב\"ג. חברת התעופה מחויבת להחזיר את מלוא הסכום לפי הקבלות שתצרף.",
                    "שירותי תקשורת: זכאות לשתי שיחות טלפון ומשלוח פקס/דוא\"ל."
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stage 2: 5 to 8 Hours Delay
            GuideSectionCard(
                stageTitle = "עיכוב של 5 עד 8 שעות",
                badgeText = "השבת כרטיס ומלון",
                badgeColor = AviationBlue,
                icon = Icons.Default.Hotel,
                iconTint = AviationBlue,
                items = listOf(
                    "השבת תמורה מלאה: הנוסע רשאי לבחור לבטל את הנסיעה ולקבל החזר כספי מלא של כרטיס הטיסה תוך 21 יום.",
                    "טיסה חלופית: חלופת טיסה מוקדמת ככל האפשר ליעד, או במועד מאוחר הנוח לנוסע.",
                    "לינה והסעות: אם נדרשת שהיית לילה, החברה חייבת לספק חדר במלון והסעה הלוך ושוב."
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stage 3: 8+ Hours or Cancellation
            GuideSectionCard(
                stageTitle = "עיכוב של 8+ שעות או ביטול טיסה",
                badgeText = "פיצוי כספי עד 3,620 ₪",
                badgeColor = SuccessGreen,
                icon = Icons.Default.Payments,
                iconTint = SuccessGreen,
                items = listOf(
                    "העיכוב מוגדר בחוק כ'טיסה שבוטלה' ומזכה בפיצוי כספי ישיר ללא צורך בהוכחת נזק:",
                    "• טיסה עד 2,000 ק\"מ (לרנקה, אתונה, כרתים): 1,510 ₪ לנוסע",
                    "• טיסה 2,000 עד 4,500 ק\"מ (רומא, פריז, לונדון, ברלין): 2,420 ₪ לנוסע",
                    "• טיסה מעל 4,500 ק\"מ (ניו יורק, מיאמי, המזרח הרחוק): 3,620 ₪ לנוסע",
                    "פיצויים לדוגמה: בית המשפט רשאי לפסוק עד 11,870 ₪ נוספים אם החברה סירבה לספק סיוע בזדון."
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Natbag Terminal Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, Slate300)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AviationNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "איפה מוצאים נציגים בנתב\"ג?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AviationNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• טרמינל 3 (דיוטי פרי והשרוולים): דלפקי שירות הלקוחות של אל על, לאופר, QAS ממוקמים ברוטונדה המרכזית וליד שער היציאה (Gate).\n• דרוש שובר מזון ישירות מהדיילים בשער!\n• צלם את לוח הטיסות (FIDS) המציג את העיכוב.",
                        fontSize = 13.sp,
                        color = Slate700,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GuideSectionCard(
    stageTitle: String,
    badgeText: String,
    badgeColor: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate300)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stageTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AviationNavy
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = AviationBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = item,
                        fontSize = 12.sp,
                        color = Slate800,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
