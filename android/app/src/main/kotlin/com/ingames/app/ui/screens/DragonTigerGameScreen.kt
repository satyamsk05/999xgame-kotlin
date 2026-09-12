package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*
import com.ingames.models.Card
import com.ingames.models.DragonTigerBetArea
import com.ingames.models.DragonTigerState
import com.ingames.models.RoundStatus
import kotlinx.coroutines.delay

@Composable
fun DragonTigerGameScreen(onBackClick: () -> Unit) {
    val wallet by ApiClient.walletState.collectAsState()
    var selectedChip by remember { mutableStateOf(100) }
    var gameState by remember {
        mutableStateOf(
            DragonTigerState(
                roundId = "dt_1001",
                roundNumber = 1001,
                status = RoundStatus.BETTING_OPEN,
                serverSeedHash = "seed_hash_dt",
                remainingMs = 15000,
                recentOutcomes = listOf(
                    DragonTigerBetArea.DRAGON,
                    DragonTigerBetArea.TIGER,
                    DragonTigerBetArea.DRAGON,
                    DragonTigerBetArea.TIE
                )
            )
        )
    }

    var placedBets by remember { mutableStateOf<Map<DragonTigerBetArea, Long>>(emptyMap()) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    // Simulation loop
    LaunchedEffect(Unit) {
        while (true) {
            // BETTING (12s)
            gameState = gameState.copy(status = RoundStatus.BETTING_OPEN, dragonCard = null, tigerCard = null)
            for (sec in 12 downTo 1) {
                gameState = gameState.copy(remainingMs = sec * 1000L)
                delay(1000)
            }

            // DEALING (2s)
            gameState = gameState.copy(status = RoundStatus.BETTING_CLOSED, remainingMs = 2000)
            delay(2000)

            // RESULT
            val dVal = (1..13).random()
            val tVal = (1..13).random()
            val dCard = Card(rank = ranks[dVal - 1], suit = "HEARTS", value = dVal)
            val tCard = Card(rank = ranks[tVal - 1], suit = "SPADES", value = tVal)

            val win = when {
                dVal > tVal -> DragonTigerBetArea.DRAGON
                tVal > dVal -> DragonTigerBetArea.TIGER
                else -> DragonTigerBetArea.TIE
            }

            gameState = gameState.copy(
                status = RoundStatus.RESULT,
                dragonCard = dCard,
                tigerCard = tCard,
                winningArea = win,
                recentOutcomes = listOf(win) + gameState.recentOutcomes.take(10)
            )

            val bet = placedBets[win] ?: 0L
            if (bet > 0) {
                val mult = if (win == DragonTigerBetArea.TIE) 8.0 else 2.0
                val payout = (bet * mult).toLong()
                toastMessage = "WON +₹%.2f".format(payout / 100.0)
                ApiClient.updateWallet(
                    wallet.copy(
                        winningsPaise = wallet.winningsPaise + payout,
                        totalPaise = wallet.totalPaise + payout
                    )
                )
            }
            placedBets = emptyMap()

            delay(4000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Dragon Tiger", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Round #${gameState.roundNumber}", color = GoldSecondary, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "₹%.2f".format(wallet.totalRupees),
                    color = GoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Recent Outcomes
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(gameState.recentOutcomes) { area ->
                val (txt, color) = when (area) {
                    DragonTigerBetArea.DRAGON -> Pair("D", Color(0xFFE91E63))
                    DragonTigerBetArea.TIGER -> Pair("T", Color(0xFFFF9800))
                    DragonTigerBetArea.TIE -> Pair("Tie", Color(0xFF00E676))
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.25f))
                        .border(1.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = txt, color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Card Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF5A108A), Color(0xFF1E0433))
                    )
                )
                .border(1.5.dp, CardBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (gameState.status == RoundStatus.BETTING_OPEN) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${gameState.remainingMs / 1000}s",
                        color = GoldPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(text = "PLACE YOUR BETS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardView(title = "DRAGON", card = gameState.dragonCard, color = Color(0xFFE91E63))
                    Text(text = "VS", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    CardView(title = "TIGER", card = gameState.tigerCard, color = Color(0xFFFF9800))
                }
            }
        }

        if (toastMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GreenButtonBrush)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = toastMessage!!, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Betting Zones: Dragon (2x), Tie (8x), Tiger (2x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DTZone(
                title = "DRAGON",
                payout = "2X",
                color = Color(0xFFE91E63),
                placedAmount = placedBets[DragonTigerBetArea.DRAGON] ?: 0L,
                modifier = Modifier.weight(1f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (DragonTigerBetArea.DRAGON to (placedBets[DragonTigerBetArea.DRAGON] ?: 0L) + paise)
                    }
                }
            )

            DTZone(
                title = "TIE",
                payout = "8X",
                color = Color(0xFF00E676),
                placedAmount = placedBets[DragonTigerBetArea.TIE] ?: 0L,
                modifier = Modifier.weight(0.8f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (DragonTigerBetArea.TIE to (placedBets[DragonTigerBetArea.TIE] ?: 0L) + paise)
                    }
                }
            )

            DTZone(
                title = "TIGER",
                payout = "2X",
                color = Color(0xFFFF9800),
                placedAmount = placedBets[DragonTigerBetArea.TIGER] ?: 0L,
                modifier = Modifier.weight(1f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (DragonTigerBetArea.TIGER to (placedBets[DragonTigerBetArea.TIGER] ?: 0L) + paise)
                    }
                }
            )
        }

        // Chip Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf(50, 100, 500, 1000, 5000).forEach { chip ->
                val isSelected = selectedChip == chip
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoldPrimary else CardBackground)
                        .border(2.dp, if (isSelected) Color.White else GoldSecondary.copy(alpha = 0.4f), CircleShape)
                        .clickable { selectedChip = chip },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹$chip",
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CardView(title: String, card: Card?, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(2.dp, color, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = card?.rank ?: "?",
                color = Color(0xFF1F0130),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun DTZone(
    title: String,
    payout: String,
    color: Color,
    placedAmount: Long,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.5.dp, color, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = payout, color = GoldSecondary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)

            if (placedAmount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenButtonStart)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "₹%.0f".format(placedAmount / 100.0),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
