package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClaimStatus
import com.example.data.model.FlightClaim
import com.example.ui.components.ClaimStatusTimeline
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClaimViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClaimsScreen(
    viewModel: ClaimViewModel,
    onSelectClaim: (FlightClaim) -> Unit,
    modifier: Modifier = Modifier
) {
    val claims by viewModel.allClaims.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "מעקב תביעות (${claims.size})",
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { innerPadding ->
        if (claims.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AviationSkyLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = AviationBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "אין עדיין תביעות פעילות",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AviationNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "העלה את פרטי הטיסה והעיכוב במסך הראשי כדי להפיק מכתב דרישה לחברת התעופה!",
                        fontSize = 13.sp,
                        color = Slate500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setTab(0) },
                        colors = ButtonDefaults.buttonColors(containerColor = AviationBlue)
                    ) {
                        Text("הגש תביעה ראשונה")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(claims, key = { it.id }) { claim ->
                    ClaimListItemCard(
                        claim = claim,
                        onClick = { onSelectClaim(claim) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClaimListItemCard(
    claim: FlightClaim,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("claim_item_${claim.id}"),
        shape = RoundedCornerShape(16.dp),
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AviationSkyLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flight,
                            contentDescription = null,
                            tint = AviationBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${claim.flightNumber} • ${claim.airlineName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AviationNavy
                        )
                        Text(
                            text = "${claim.departureAirport} ➔ ${claim.destinationAirport}",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (claim.status) {
                        ClaimStatus.PAID -> SuccessGreenLight
                        ClaimStatus.APPROVED -> SuccessGreenLight
                        ClaimStatus.AIRLINE_REVIEW -> GoldLegalLight
                        else -> AviationSkyLight
                    }
                ) {
                    Text(
                        text = claim.status.titleHe,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (claim.status) {
                            ClaimStatus.PAID, ClaimStatus.APPROVED -> SuccessGreen
                            ClaimStatus.AIRLINE_REVIEW -> GoldLegal
                            else -> AviationBlue
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate100, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "עיכוב: ${claim.delayHours.toInt()} שעות (${claim.flightDate})",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Text(
                        text = "פיצוי סטטוטורי: ${claim.grossCompensationNis} ₪",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "סה\"כ המגיע לך:",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Text(
                        text = "${claim.totalPayoutNis} ₪",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ClaimStatusTimeline(currentStatus = claim.status)
        }
    }
}
