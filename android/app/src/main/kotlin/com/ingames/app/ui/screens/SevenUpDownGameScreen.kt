package com.ingames.app.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*
import com.ingames.models.RoundStatus
import com.ingames.models.SevenUpDownBetArea
import com.ingames.models.SevenUpDownState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SevenUpDownGameScreen(
    onBackClick: () -> Unit
) {
    val wallet by ApiClient.walletState.collectAsState()
    var selectedChip by remember { mutableStateOf(50) } // ₹50
    var gameState by remember {
        mutableStateOf(
            SevenUpDownState(
                roundId = "7ud_1001",
                roundNumber = 1001,
                status = RoundStatus.BETTING_OPEN,
                serverSeedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                remainingMs = 12000,
                recentOutcomes = listOf(4, 7, 10, 8, 3, 11, 7, 5)
            )
        )
    }

    var placedBets by remember { mutableStateOf<Map<SevenUpDownBetArea, Long>>(emptyMap()) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Simulation loop for responsive gameplay
    LaunchedEffect(Unit) {
        while (true) {
            // BETTING OPEN (12s)
            gameState = gameState.copy(
                status = RoundStatus.BETTING_OPEN,
                dice = null,
                diceTotal = null,
                winningArea = null
            )
            for (sec in 12 downTo 1) {
                gameState = gameState.copy(remainingMs = sec * 1000L)
                delay(1000)
            }

            // ROLLING (2s)
            gameState = gameState.copy(status = RoundStatus.BETTING_CLOSED, remainingMs = 2000)
            delay(2000)

            // RESULT
            val d1 = (1..6).random()
            val d2 = (1..6).random()
            val tot = d1 + d2
            val winArea = when {
                tot < 7 -> SevenUpDownBetArea.DOWN
                tot == 7 -> SevenUpDownBetArea.SEVEN
                else -> SevenUpDownBetArea.UP
            }

            gameState = gameState.copy(
                status = RoundStatus.RESULT,
                dice = listOf(d1, d2),
                diceTotal = tot,
                winningArea = winArea,
                recentOutcomes = listOf(tot) + gameState.recentOutcomes.take(10)
            )

            // Calculate payout
            val betOnWin = placedBets[winArea] ?: 0L
            if (betOnWin > 0) {
                val mult = if (winArea == SevenUpDownBetArea.SEVEN) 5.0 else 2.0
                val winAmt = (betOnWin * mult).toLong()
                toastMessage = "WIN! +₹%.2f".format(winAmt / 100.0)
                ApiClient.updateWallet(
                    wallet.copy(
                        winningsPaise = wallet.winningsPaise + winAmt,
                        totalPaise = wallet.totalPaise + winAmt
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
                    Text(text = "7 Up Down", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Round #${gameState.roundNumber}", color = GoldSecondary, fontSize = 11.sp)
                }
            }

            // Balance
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

        // Recent Outcomes Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(gameState.recentOutcomes) { score ->
                val color = when {
                    score < 7 -> Color(0xFF2196F3)
                    score == 7 -> GoldPrimary
                    else -> Color(0xFFE91E63)
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.25f))
                        .border(1.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$score", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Center Stage: Dice Arena & Timer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF551287), Color(0xFF1E0433))
                    )
                )
                .border(1.5.dp, GoldSecondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (gameState.status == RoundStatus.BETTING_OPEN) {
                    Text(
                        text = "${(gameState.remainingMs / 1000)}s",
                        color = GoldPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "PLACE YOUR BETS",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    // Show Rolling / Revealed Dice
                    val d = gameState.dice ?: listOf(3, 4)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DieFace(value = d[0])
                        DieFace(value = d[1])
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TOTAL: ${gameState.diceTotal ?: (d[0] + d[1])}",
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
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

        // 3 Betting Areas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BettingZone(
                title = "2 - 6",
                subtitle = "7 DOWN",
                payout = "2X",
                color = Color(0xFF2196F3),
                placedAmount = placedBets[SevenUpDownBetArea.DOWN] ?: 0L,
                modifier = Modifier.weight(1f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (SevenUpDownBetArea.DOWN to (placedBets[SevenUpDownBetArea.DOWN] ?: 0L) + paise)
                    }
                }
            )

            BettingZone(
                title = "7",
                subtitle = "LUCKY 7",
                payout = "5X",
                color = GoldPrimary,
                placedAmount = placedBets[SevenUpDownBetArea.SEVEN] ?: 0L,
                modifier = Modifier.weight(1f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (SevenUpDownBetArea.SEVEN to (placedBets[SevenUpDownBetArea.SEVEN] ?: 0L) + paise)
                    }
                }
            )

            BettingZone(
                title = "8 - 12",
                subtitle = "7 UP",
                payout = "2X",
                color = Color(0xFFE91E63),
                placedAmount = placedBets[SevenUpDownBetArea.UP] ?: 0L,
                modifier = Modifier.weight(1f),
                onClick = {
                    val paise = selectedChip * 100L
                    if (wallet.totalPaise >= paise && gameState.status == RoundStatus.BETTING_OPEN) {
                        ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                        placedBets = placedBets + (SevenUpDownBetArea.UP to (placedBets[SevenUpDownBetArea.UP] ?: 0L) + paise)
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
            listOf(10, 50, 100, 500, 1000).forEach { chip ->
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
private fun DieFace(value: Int) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            color = Color(0xFF1F0130),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun BettingZone(
    title: String,
    subtitle: String,
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
            Text(text = title, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(text = subtitle, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = payout, color = GoldSecondary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)

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
