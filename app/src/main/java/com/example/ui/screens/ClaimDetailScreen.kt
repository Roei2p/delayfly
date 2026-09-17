package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimStatus
import com.example.data.model.FlightClaim
import com.example.ui.components.ClaimStatusTimeline
import com.example.ui.components.SuccessFeeNoticeBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClaimViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailScreen(
    claim: FlightClaim,
    viewModel: ClaimViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showExpenseDialog by remember { mutableStateOf(false) }
    var expenseAmountInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "תביעה: ${claim.flightNumber} (${claim.airlineName})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AviationNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_btn")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "חזרה",
                            tint = AviationNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, claim.legalDemandLetter)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "שתף מכתב דרישה"))
                        },
                        modifier = Modifier.testTag("share_letter_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "שתף מכתב דרישה",
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
            // Header summary
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Column {
                            Text(
                                text = "${claim.flightNumber} • ${claim.airlineName}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationNavy
                            )
                            Text(
                                text = "${claim.departureAirport} ➔ ${claim.destinationAirport}",
                                fontSize = 13.sp,
                                color = Slate500
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (claim.status) {
                                ClaimStatus.PAID, ClaimStatus.APPROVED -> SuccessGreenLight
                                ClaimStatus.AIRLINE_REVIEW -> GoldLegalLight
                                else -> AviationSkyLight
                            }
                        ) {
                            Text(
                                text = claim.status.titleHe,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = when (claim.status) {
                                    ClaimStatus.PAID, ClaimStatus.APPROVED -> SuccessGreen
                                    ClaimStatus.AIRLINE_REVIEW -> GoldLegal
                                    else -> AviationBlue
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    ClaimStatusTimeline(currentStatus = claim.status)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Financial Payout Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreenLight),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "תחשיב כספי לפי חוק שירותי תעופה (הצלחה בלבד)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("פיצוי סטטוטורי מלא:", fontSize = 13.sp, color = Slate700)
                        Text("${claim.grossCompensationNis} ₪", fontWeight = FontWeight.Bold, color = AviationNavy)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("עמלת הצלחה (20% רק בזכייה):", fontSize = 13.sp, color = Slate500)
                        Text("-${claim.commissionFeeNis} ₪", color = DangerRed, fontWeight = FontWeight.SemiBold)
                    }

                    if (claim.terminalExpensesNis > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("קבלות בנתב\"ג (100% החזר מלא אליך):", fontSize = 13.sp, color = SuccessGreen)
                            Text("+${claim.terminalExpensesNis} ₪", fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = SuccessGreen.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "סכום שיועבר לחשבונך:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${claim.netPayoutNis} ₪",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SuccessFeeNoticeBanner()

            Spacer(modifier = Modifier.height(14.dp))

            // Add terminal expense button
            OutlinedButton(
                onClick = { showExpenseDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AviationBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = AviationBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "הוסף קבלה על קפה / ארוחה בטרמינל (${claim.terminalExpensesNis} ₪ כעת)",
                    fontSize = 13.sp,
                    color = AviationBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Formal Legal Demand Letter Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("legal_letter_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
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
                                text = "מכתב דרישה רשמי לחברת התעופה",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "התראה לפני תביעה לפי סעיף 6 לחוק שירותי תעופה",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(claim.legalDemandLetter))
                                Toast.makeText(context, "המכתב הועתק ללוח!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AviationBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("העתק", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate50,
                        border = BorderStroke(1.dp, Slate300),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = claim.legalDemandLetter,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Slate800,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advance status simulator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "עדכון סטטוס התביעה מול חברת התעופה:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AviationNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.updateClaimStatus(claim, ClaimStatus.AIRLINE_REVIEW) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("בדיקת חברה", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.updateClaimStatus(claim, ClaimStatus.APPROVED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("אושר פיצוי", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.updateClaimStatus(claim, ClaimStatus.PAID) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("הועברו כספים", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showExpenseDialog) {
        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text("הוספת קבלה לתביעה זו") },
            text = {
                OutlinedTextField(
                    value = expenseAmountInput,
                    onValueChange = { expenseAmountInput = it },
                    label = { Text("סכום בש\"ח (100% החזר ללא עמלה)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = expenseAmountInput.toIntOrNull() ?: 0
                        if (amt > 0) {
                            viewModel.addExpenseToClaim(claim, amt)
                        }
                        showExpenseDialog = false
                        expenseAmountInput = ""
                    }
                ) {
                    Text("הוסף")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpenseDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }
}
