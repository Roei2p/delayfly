package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AppDatabase
import com.example.data.repository.FlightClaimRepositoryImpl
import com.example.ui.screens.ClaimDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyClaimsScreen
import com.example.ui.screens.NatbagRightsGuideScreen
import com.example.ui.theme.AviationBlue
import com.example.ui.theme.AviationNavy
import com.example.ui.theme.FlightClaimTheme
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.ClaimViewModel
import com.example.ui.viewmodel.ClaimViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: ClaimViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FlightClaimRepositoryImpl(database.flightClaimDao())
        ClaimViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightClaimTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FlightClaimApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FlightClaimApp(viewModel: ClaimViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedClaim by viewModel.selectedClaim.collectAsState()
    val showSuccessDialog by viewModel.claimSubmissionSuccess.collectAsState()
    var showGuideScreen by remember { mutableStateOf(false) }

    if (showGuideScreen) {
        NatbagRightsGuideScreen(
            onBack = { showGuideScreen = false }
        )
        return
    }

    if (selectedClaim != null) {
        ClaimDetailScreen(
            claim = selectedClaim!!,
            viewModel = viewModel,
            onBack = { viewModel.selectClaim(null) }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = PureWhite,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = "עיכוב בנתב\"ג"
                        )
                    },
                    label = {
                        Text(
                            text = "עיכוב בנתב\"ג",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AviationBlue,
                        selectedTextColor = AviationBlue,
                        indicatorColor = AviationBlue.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_home_tab")
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "מעקב תביעות"
                        )
                    },
                    label = {
                        Text(
                            text = "התביעות שלי",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AviationBlue,
                        selectedTextColor = AviationBlue,
                        indicatorColor = AviationBlue.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_claims_tab")
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "חוק וזכויות"
                        )
                    },
                    label = {
                        Text(
                            text = "זכויות בחוק",
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AviationBlue,
                        selectedTextColor = AviationBlue,
                        indicatorColor = AviationBlue.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_rights_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToClaim = { claimId ->
                        // handled by viewModel.selectedClaim
                    },
                    onNavigateToGuide = { showGuideScreen = true }
                )
                1 -> MyClaimsScreen(
                    viewModel = viewModel,
                    onSelectClaim = { claim -> viewModel.selectClaim(claim) }
                )
                2 -> NatbagRightsGuideScreen(
                    onBack = { viewModel.setTab(0) }
                )
            }
        }
    }

    // Success dialog after filing claim
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSuccessDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AviationBlue,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "התביעה הוגשה בהצלחה!",
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "מכתב התראה רשמי הופק ונשלח למחלקה המשפטית של חברת התעופה בהתאם לחוק שירותי תעופה (חוק טיבי).",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "מודל עמלת הצלחה (20%): לא משלמים אגורה מראש! העמלה מנוכה רק לאחר שכספי הפיצוי מועברים לחשבונך. הוצאות בנתב\"ג מוחזרות ב-100% נקי.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AviationBlue,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissSuccessDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationBlue)
                ) {
                    Text("צפה בפרטי התביעה ובמכתב")
                }
            }
        )
    }
}
