package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.FlightDistance
import com.example.ui.components.CompensationBreakdownCard
import com.example.ui.components.FreeToolNoticeBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClaimViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ClaimViewModel,
    onNavigateToClaim: (Long) -> Unit,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val draft by viewModel.draft.collectAsState()
    val calc = viewModel.getCalculation()
    var showExpenseDialog by remember { mutableStateOf(false) }
    var expenseAmountInput by remember { mutableStateOf("") }
    var expenseDescInput by remember { mutableStateOf("") }

    // Android Photo Picker for Ticket / Boarding pass
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateDraft { it.copy(ticketImageUri = uri.toString()) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AviationBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = "FlightClaim",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "פיצוי טיסה בנתב\"ג",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "תביעות לפי חוק שירותי תעופה (חוק טיבי)",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToGuide,
                        modifier = Modifier.testTag("help_guide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "מדריך זכויות בנתב\"ג",
                            tint = AviationNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite
                )
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
            // Live Natbag Airport Alert
            AirportLiveAlertCard(
                delayHours = draft.delayHours,
                flightNumber = draft.flightNumber,
                onAddExpenseClick = { showExpenseDialog = true },
                onGuideClick = onNavigateToGuide
            )

            Spacer(modifier = Modifier.height(16.dp))

            FreeToolNoticeBanner()

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Upload Boarding Pass or Select Flight
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ticket_upload_card"),
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
                        Text(
                            text = "1. כרטיס עליה למטוס (Boarding Pass)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AviationNavy
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100
                        ) {
                            Text(
                                text = "צילום / סריקה",
                                fontSize = 11.sp,
                                color = Slate700,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // If photo uploaded, show preview
                    if (draft.ticketImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                        ) {
                            AsyncImage(
                                model = draft.ticketImageUri,
                                contentDescription = "כרטיס עלייה למטוס",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.updateDraft { it.copy(ticketImageUri = null) } },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(PureWhite.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "הסר תמונה",
                                    tint = DangerRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        // Upload button row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("upload_ticket_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, AviationBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = AviationBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("העלה כרטיס מהגלריה", color = AviationBlue, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Quick select presets from popular Natbag flights
                    Text(
                        text = "או בחר טיסה נפוצה מנתב\"ג לבדיקה מהירה:",
                        fontSize = 12.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetFlightChip(
                            label = "אל על LY001 ניו יורק",
                            isSelected = draft.flightNumber == "LY001",
                            onClick = {
                                viewModel.selectSampleFlight("LY001", "אל על (El Al)", "ניו יורק (JFK)", FlightDistance.LONG)
                            }
                        )
                        PresetFlightChip(
                            label = "Wizz W62325 בודפשט",
                            isSelected = draft.flightNumber == "W6 2325",
                            onClick = {
                                viewModel.selectSampleFlight("W6 2325", "Wizz Air", "בודפשט (BUD)", FlightDistance.MEDIUM)
                            }
                        )
                        PresetFlightChip(
                            label = "Ryanair FR4022 רומא",
                            isSelected = draft.flightNumber == "FR4022",
                            onClick = {
                                viewModel.selectSampleFlight("FR4022", "Ryanair", "רומא (FCO)", FlightDistance.MEDIUM)
                            }
                        )
                        PresetFlightChip(
                            label = "ארקיע IZ821 אתונה",
                            isSelected = draft.flightNumber == "IZ821",
                            onClick = {
                                viewModel.selectSampleFlight("IZ821", "ארקיע (Arkia)", "אתונה (ATH)", FlightDistance.SHORT)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Flight details edit fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.flightNumber,
                            onValueChange = { num -> viewModel.updateDraft { it.copy(flightNumber = num) } },
                            label = { Text("מספר טיסה", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = draft.airlineName,
                            onValueChange = { name -> viewModel.updateDraft { it.copy(airlineName = name) } },
                            label = { Text("חברת תעופה", fontSize = 11.sp) },
                            modifier = Modifier.weight(1.4f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draft.destinationAirport,
                            onValueChange = { dest -> viewModel.updateDraft { it.copy(destinationAirport = dest) } },
                            label = { Text("יעד הטיסה", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = draft.bookingReference,
                            onValueChange = { pnr -> viewModel.updateDraft { it.copy(bookingReference = pnr) } },
                            label = { Text("קוד הזמנה (PNR)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Delay duration slider & status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delay_duration_card"),
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
                        Text(
                            text = "2. משך העיכוב בטיסה",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AviationNavy
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (draft.isCancelled) DangerRedLight else GoldLegalLight
                        ) {
                            Text(
                                text = if (draft.isCancelled) "טיסה בוטלה!" else "${draft.delayHours.toInt()} שעות עיכוב",
                                color = if (draft.isCancelled) DangerRed else GoldLegal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset buttons: 2h (as user prompt says), 3h, 5h, 8h+, or Cancelled
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DelaySelectPill(
                            label = "שעתיים (כעת)",
                            selected = !draft.isCancelled && draft.delayHours == 2.0f,
                            onClick = {
                                viewModel.setCancelled(false)
                                viewModel.setDelayHours(2.0f)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DelaySelectPill(
                            label = "3-4 שעות",
                            selected = !draft.isCancelled && draft.delayHours == 3.5f,
                            onClick = {
                                viewModel.setCancelled(false)
                                viewModel.setDelayHours(3.5f)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DelaySelectPill(
                            label = "5 שעות",
                            selected = !draft.isCancelled && draft.delayHours == 5.0f,
                            onClick = {
                                viewModel.setCancelled(false)
                                viewModel.setDelayHours(5.0f)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        DelaySelectPill(
                            label = "8+ שעות",
                            selected = !draft.isCancelled && draft.delayHours == 8.0f,
                            onClick = {
                                viewModel.setCancelled(false)
                                viewModel.setDelayHours(8.0f)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cancelled switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (draft.isCancelled) DangerRedLight else Slate100)
                            .clickable { viewModel.setCancelled(!draft.isCancelled) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (draft.isCancelled) DangerRed else Slate500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "הטיסה בוטלה לחלוטין (זכאות מלאה מידית)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (draft.isCancelled) DangerRed else Slate800
                            )
                        }
                        Switch(
                            checked = draft.isCancelled,
                            onCheckedChange = { viewModel.setCancelled(it) },
                            modifier = Modifier.testTag("flight_cancelled_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slider for fine tuning
                    if (!draft.isCancelled) {
                        Slider(
                            value = draft.delayHours,
                            onValueChange = { viewModel.setDelayHours(it) },
                            valueRange = 1f..12f,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = AviationBlue,
                                activeTrackColor = AviationBlue
                            ),
                            modifier = Modifier.testTag("delay_hours_slider")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1 שעה", fontSize = 10.sp, color = Slate500)
                            Text("שעתיים (מזון ושתייה)", fontSize = 10.sp, color = AviationNavy, fontWeight = FontWeight.Bold)
                            Text("5 שעות (ביטול/מלון)", fontSize = 10.sp, color = GoldLegal, fontWeight = FontWeight.Bold)
                            Text("8+ שעות (פיצוי סטטוטורי)", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 3: Immediate Rights at Natbag Right Now (2h delay)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                border = BorderStroke(1.dp, Slate300)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "הזכויות שלך כעת בנתב\"ג (עיכוב של ${draft.delayHours.toInt()} שעות)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AviationNavy
                        )
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = AviationBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    calc.immediateTerminalRights.filter { it.isActiveNow }.forEach { right ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = right.titleHe,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AviationNavyDark
                                )
                                Text(
                                    text = right.descriptionHe,
                                    fontSize = 11.sp,
                                    color = Slate700,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Receipts / Expenses button
                    OutlinedButton(
                        onClick = { showExpenseDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_terminal_expense_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AviationBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = AviationBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (draft.terminalExpensesNis > 0)
                                "קבלות בנתב\"ג שנרשמו: ${draft.terminalExpensesNis} ₪ (הוסף עוד)"
                            else
                                "קנית קפה/אוכל בנתב\"ג? הוסף קבלה לתחשיב הפיצוי",
                            fontSize = 12.sp,
                            color = AviationBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 4: Full Financial Compensation Calculation Card
            CompensationBreakdownCard(
                calc = calc,
                terminalExpensesNis = draft.terminalExpensesNis
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Big CTA: Submit claim & handle legal demand to airline
            Button(
                onClick = { viewModel.submitClaim() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_claim_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AviationBlue,
                    contentColor = PureWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "הגש תביעה והפק מכתב דרישה לחברת התעופה",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "כלי חינמי לחלוטין • המכתב מוכן להעתקה ושליחה עצמאית",
                        fontSize = 11.sp,
                        color = AviationSkyLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog to add Natbag expense receipt
    if (showExpenseDialog) {
        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = {
                Text(
                    text = "הוספת הוצאה / קבלה בנתב\"ג",
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "חוק שירותי תעופה מחייב את חברת התעופה לספק מזון ושתייה לאחר שעתיים עיכוב. אם לא חילקו שוברים, שמור את הקבלה וחברת התעופה תחזיר את מלוא הסכום.",
                        fontSize = 12.sp,
                        color = Slate700,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = expenseAmountInput,
                        onValueChange = { expenseAmountInput = it },
                        label = { Text("סכום בש\"ח (למשל 85)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expenseDescInput,
                        onValueChange = { expenseDescInput = it },
                        label = { Text("תיאור (למשל: קפה וכריך בארומה טרמינל 3)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = expenseAmountInput.toIntOrNull() ?: 0
                        if (amt > 0) {
                            viewModel.addTerminalExpense(amt)
                        }
                        showExpenseDialog = false
                        expenseAmountInput = ""
                        expenseDescInput = ""
                    }
                ) {
                    Text("הוסף לתביעה")
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

@Composable
private fun AirportLiveAlertCard(
    delayHours: Float,
    flightNumber: String,
    onAddExpenseClick: () -> Unit,
    onGuideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(AviationNavy, AviationBlue)))
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
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldLegal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "נתב\"ג (TLV) • מצב עיכוב חי",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldLegal
                ) {
                    Text(
                        text = "עיכוב של ${delayHours.toInt()} שעות",
                        color = AviationNavyDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "טיסה $flightNumber מתעכבת כעת. החוק מעניק לך זכויות מזון ושתייה מידיות בטרמינל, וזכאות לפיצוי של אלפי שקלים בהמשך!",
                color = AviationSkyLight,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAddExpenseClick() },
                    shape = RoundedCornerShape(10.dp),
                    color = Slate800
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = GoldLegal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "שמור קבלות אוכל",
                            fontSize = 11.sp,
                            color = PureWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGuideClick() },
                    shape = RoundedCornerShape(10.dp),
                    color = Slate800
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = AviationSky,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "מדריך זכויות מלא",
                            fontSize = 11.sp,
                            color = PureWhite,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetFlightChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AviationBlue else Slate100,
        border = BorderStroke(1.dp, if (isSelected) AviationBlue else Slate300),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PureWhite else Slate800,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DelaySelectPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (selected) AviationBlue else Slate100,
        border = BorderStroke(1.dp, if (selected) AviationBlue else Slate300)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) PureWhite else Slate800,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        )
    }
}
