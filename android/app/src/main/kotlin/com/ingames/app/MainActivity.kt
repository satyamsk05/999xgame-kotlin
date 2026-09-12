package com.ingames.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ingames.app.data.ApiClient
import com.ingames.app.data.TokenManager
import com.ingames.app.ui.components.CustomBottomNavBar
import com.ingames.app.ui.components.MobileDeviceFrame
import com.ingames.app.ui.components.NavItem
import com.ingames.app.ui.screens.*
import com.ingames.app.ui.theme.InGamesTheme
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

enum class Screen {
    HOME,
    WALLET,
    ADD_CASH,
    WITHDRAW,
    TRANSACTIONS,
    LOGIN,
    PROFILE,
    SHARE,
    SETTINGS,
    HELP,
    FAIR_PLAY,
    GAME_SEVEN_UP_DOWN,
    GAME_CRUSH,
    GAME_DRAGON_TIGER,
    GAME_HTML5
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.parseColor("#15001F")
        window.navigationBarColor = android.graphics.Color.parseColor("#15001F")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            InGamesTheme {
                MobileDeviceFrame {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedNav by remember { mutableStateOf(NavItem.HOME) }
    var activeHtml5Title by remember { mutableStateOf("7 Up Down Classic") }
    var activeHtml5Path by remember { mutableStateOf("games/seven_up_down/index.html") }

    val showBottomBar = currentScreen in listOf(Screen.HOME, Screen.WALLET, Screen.SHARE, Screen.PROFILE, Screen.ADD_CASH)

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFF15001F),
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNavBar(
                    selectedItem = selectedNav,
                    onItemSelected = { item ->
                        selectedNav = item
                        currentScreen = when (item) {
                            NavItem.HOME -> Screen.HOME
                            NavItem.WALLET -> Screen.WALLET
                            NavItem.ADD_CASH -> Screen.ADD_CASH
                            NavItem.SHARE -> Screen.SHARE
                            NavItem.PROFILE -> Screen.PROFILE
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onGameClick = { game ->
                        when (game.id) {
                            "seven_up_down" -> currentScreen = Screen.GAME_SEVEN_UP_DOWN
                            "crush" -> currentScreen = Screen.GAME_CRUSH
                            "dragon_tiger" -> currentScreen = Screen.GAME_DRAGON_TIGER
                            "classic_dice" -> {
                                activeHtml5Title = "7 Up Down Classic"
                                activeHtml5Path = "games/seven_up_down/index.html"
                                currentScreen = Screen.GAME_HTML5
                            }
                            "mines" -> {
                                activeHtml5Title = "Fruit Slice / Mines"
                                activeHtml5Path = "games/fruit_slice/index.html"
                                currentScreen = Screen.GAME_HTML5
                            }
                            else -> currentScreen = Screen.GAME_SEVEN_UP_DOWN
                        }
                    },
                    onAddCashClick = {
                        selectedNav = NavItem.ADD_CASH
                        currentScreen = Screen.ADD_CASH
                    },
                    onWalletClick = {
                        selectedNav = NavItem.PROFILE
                        currentScreen = Screen.PROFILE
                    },
                    onProfileClick = {
                        selectedNav = NavItem.PROFILE
                        currentScreen = Screen.PROFILE
                    }
                )

                Screen.WALLET, Screen.PROFILE -> ProfileScreen(
                    onSettingsClick = { currentScreen = Screen.SETTINGS },
                    onHelpClick = { currentScreen = Screen.HELP },
                    onFairPlayClick = { currentScreen = Screen.FAIR_PLAY },
                    onContactClick = { currentScreen = Screen.HELP },
                    onLogoutClick = {
                        TokenManager.token = null
                        TokenManager.currentUser = null
                        currentScreen = Screen.LOGIN
                    },
                    onBackClick = {
                        selectedNav = NavItem.HOME
                        currentScreen = Screen.HOME
                    },
                    onAddCashClick = {
                        selectedNav = NavItem.ADD_CASH
                        currentScreen = Screen.ADD_CASH
                    },
                    onWithdrawClick = { currentScreen = Screen.WITHDRAW },
                    onTransactionsClick = { currentScreen = Screen.TRANSACTIONS }
                )

                Screen.ADD_CASH -> AddCashScreen(
                    onBackClick = {
                        selectedNav = NavItem.HOME
                        currentScreen = Screen.HOME
                    },
                    onDepositSuccess = {
                        selectedNav = NavItem.PROFILE
                        currentScreen = Screen.PROFILE
                    }
                )

                Screen.WITHDRAW -> WithdrawScreen(
                    onBackClick = { currentScreen = Screen.PROFILE },
                    onWithdrawSuccess = { currentScreen = Screen.TRANSACTIONS }
                )

                Screen.TRANSACTIONS -> TransactionsScreen(
                    onBackClick = { currentScreen = Screen.PROFILE }
                )

                Screen.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        selectedNav = NavItem.HOME
                        currentScreen = Screen.HOME
                    }
                )

                Screen.SHARE -> ShareScreen(
                    onBackClick = {
                        selectedNav = NavItem.HOME
                        currentScreen = Screen.HOME
                    }
                )

                Screen.SETTINGS -> SettingsScreen(
                    onBackClick = { currentScreen = Screen.PROFILE }
                )

                Screen.HELP -> HelpCentreScreen(
                    onBackClick = { currentScreen = Screen.PROFILE }
                )

                Screen.FAIR_PLAY -> FairPlayScreen(
                    onBackClick = { currentScreen = Screen.PROFILE }
                )

                Screen.GAME_SEVEN_UP_DOWN -> SevenUpDownGameScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )

                Screen.GAME_CRUSH -> CrushGameScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )

                Screen.GAME_DRAGON_TIGER -> DragonTigerGameScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )

                Screen.GAME_HTML5 -> Html5GameScreen(
                    gameTitle = activeHtml5Title,
                    gameAssetPath = activeHtml5Path,
                    onBackClick = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}
