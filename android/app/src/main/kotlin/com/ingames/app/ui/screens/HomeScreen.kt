package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ingames.app.data.ApiClient
import com.ingames.app.data.TokenManager
import com.ingames.app.ui.components.ChampionsLeagueBanner
import com.ingames.app.ui.components.GameCard
import com.ingames.app.ui.components.OnlineTicker
import com.ingames.app.ui.components.ShimmerCardSkeleton
import com.ingames.app.ui.components.TopHeader
import com.ingames.models.GameInfo
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.Brush
import com.ingames.app.ui.theme.AppBackgroundBrush

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(
    onGameClick: (GameInfo) -> Unit,
    onAddCashClick: () -> Unit,
    onWalletClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val wallet by ApiClient.walletState.collectAsState()
    var games by remember { mutableStateOf<List<GameInfo>>(emptyList()) }
    val scope = rememberCoroutineScope()

    val user = TokenManager.currentUser
    val username = user?.name ?: "satyamog"

    val defaultGames = listOf(
        GameInfo(
            id = "seven_up_down",
            title = "7 Up Down",
            description = "Predict 2 dice total (2-6, 7, 8-12)",
            imagePath = "images/7updown.png",
            accentColorHex = "#FF5722",
            route = "/game/seven_up_down",
            activePlayers = 890
        ),
        GameInfo(
            id = "crush",
            title = "Crush / Double",
            description = "Cashout before the crash!",
            imagePath = "images/double.png",
            accentColorHex = "#E91E63",
            route = "/game/crush",
            activePlayers = 1240
        ),
        GameInfo(
            id = "dragon_tiger",
            title = "Dragon Tiger",
            description = "High card duel with fast payouts",
            imagePath = "images/dtgame.png",
            accentColorHex = "#FFC107",
            route = "/game/dragon_tiger",
            activePlayers = 650
        ),
        GameInfo(
            id = "mines",
            title = "Mines",
            description = "Uncover gems and dodge bombs",
            imagePath = "images/mines.png",
            accentColorHex = "#9C27B0",
            route = "/game/html5/fruit_slice",
            activePlayers = 420
        ),
        GameInfo(
            id = "classic_dice",
            title = "Classic Dice",
            description = "Roll dice & win instant rewards",
            imagePath = "images/classic_dice.png",
            accentColorHex = "#4CAF50",
            route = "/game/html5/seven_up_down",
            activePlayers = 380
        )
    )

    LaunchedEffect(Unit) {
        scope.launch {
            val g = ApiClient.fetchGames()
            if (g.isNotEmpty()) games = g
        }
    }

    val displayGames = if (games.isNotEmpty()) games else defaultGames

    // Top 2 Games: Classic Dice & Double (Crush)
    val classicDice = displayGames.find { it.id == "classic_dice" } ?: displayGames.getOrNull(0)
    val doubleGame = displayGames.find { it.id == "crush" } ?: displayGames.getOrNull(1)
    val top2Games = listOfNotNull(classicDice, doubleGame)

    // Next Games (7 Up Down, Dragon Tiger, Mines)
    val nextGames = displayGames.filter { it.id !in listOf("classic_dice", "crush") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF15001F))
            .statusBarsPadding()
    ) {
        // 1. FIXED TOP SECTION: Profile & Wallet
        TopHeader(
            username = username,
            balanceRupees = wallet.totalRupees,
            avatarPath = "file:///android_asset/Avatar/avatar_1.png",
            onProfileClick = onProfileClick,
            onAddCashClick = onAddCashClick,
            onWalletClick = onWalletClick
        )

        // SCROLLABLE CONTENT AREA (Includes Online Ticker & Game Cards)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ONLINE PLAYERS TICKER (Scrollable with screen)
            item {
                OnlineTicker(onlineCount = 89206)
            }

            // PROMOTION BANNER
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    ChampionsLeagueBanner(
                        onPlayClick = {
                            displayGames.firstOrNull()?.let { onGameClick(it) }
                        }
                    )
                }
            }

            // TOP 2 FEATURED GAMES (Horizontal Swipable Row with 260dp Square Cards - Exact Image 1 Match)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(top2Games) { game ->
                        GameCard(
                            game = game,
                            onGameClick = onGameClick,
                            modifier = Modifier.width(260.dp),
                            cardHeight = 260.dp
                        )
                    }
                }
            }

            // NEXT 2 GAMES ROWS (Side-by-Side 2 Columns)
            if (nextGames.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        nextGames.chunked(2).forEach { rowGames ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                for (game in rowGames) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        GameCard(
                                            game = game,
                                            onGameClick = onGameClick,
                                            cardHeight = 150.dp
                                        )
                                    }
                                }
                                repeat(2 - rowGames.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
