package com.ingames.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*
import com.ingames.models.CrushState
import com.ingames.models.RoundStatus
import kotlinx.coroutines.delay

@Composable
fun CrushGameScreen(onBackClick: () -> Unit) {
    val wallet by ApiClient.walletState.collectAsState()
    var currentMultiplier by remember { mutableStateOf(1.00) }
    var isFlying by remember { mutableStateOf(false) }
    var hasCrashed by remember { mutableStateOf(false) }
    var betAmount by remember { mutableStateOf(100L) } // ₹100
    var hasBet by remember { mutableStateOf(false) }
    var hasCashedOut by remember { mutableStateOf(false) }
    var cashedMultiplier by remember { mutableStateOf(0.0) }
    var recentHistory by remember { mutableStateOf(listOf(2.45, 1.12, 5.80, 1.00, 3.20, 12.4, 1.88)) }

    // Simulation loop
    LaunchedEffect(Unit) {
        while (true) {
            // BETTING (6s)
            isFlying = false
            hasCrashed = false
            hasCashedOut = false
            currentMultiplier = 1.00

            delay(6000)

            // FLYING
            isFlying = true
            val crashPoint = listOf(1.45, 2.10, 1.12, 3.50, 6.20, 1.80, 15.0).random()
            val start = System.currentTimeMillis()

            while (true) {
                val elapsed = (System.currentTimeMillis() - start) / 1000.0
                val mult = Math.floor(Math.exp(0.12 * elapsed) * 100.0) / 100.0
                currentMultiplier = mult

                if (currentMultiplier >= crashPoint) {
                    currentMultiplier = crashPoint
                    break
                }
                delay(50)
            }

            // CRASHED
            isFlying = false
            hasCrashed = true
            recentHistory = listOf(crashPoint) + recentHistory.take(8)
            hasBet = false

            delay(3000)
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
        // Top Bar
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
                    Text(text = "Crush / Crash", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Provably Fair 100%", color = GoldSecondary, fontSize = 11.sp)
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

        // Recent Multipliers
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(recentHistory) { mult ->
                val color = if (mult >= 2.0) TextGreen else if (mult >= 1.5) GoldPrimary else TextRed
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.2f))
                        .border(1.dp, color, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "%.2fx".format(mult), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Curve Graph Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF140224))
                .border(1.5.dp, CardBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val progress = ((currentMultiplier - 1.0) / 5.0).toFloat().coerceIn(0f, 1f)

                val path = Path().apply {
                    moveTo(30f, h - 30f)
                    quadraticBezierTo(
                        w * 0.4f, h - 30f,
                        30f + progress * (w - 80f), (h - 30f) - progress * (h - 80f)
                    )
                }
                drawPath(
                    path = path,
                    color = if (hasCrashed) TextRed else GoldPrimary,
                    style = Stroke(width = 6f)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%.2fx".format(currentMultiplier),
                    color = if (hasCrashed) TextRed else if (isFlying) TextGreen else GoldPrimary,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black
                )
                if (hasCrashed) {
                    Text(text = "CRASHED", color = TextRed, fontSize = 16.sp, fontWeight = FontWeight.Black)
                } else if (!isFlying) {
                    Text(text = "NEXT ROUND STARTING...", color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else if (hasCashedOut) {
                    Text(text = "CASHED OUT @ %.2fx".format(cashedMultiplier), color = TextGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Bet & Cashout Actions
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (isFlying && hasBet && !hasCashedOut) {
                // Instant Cashout Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreenButtonBrush)
                        .clickable {
                            hasCashedOut = true
                            cashedMultiplier = currentMultiplier
                            val winPaise = (betAmount * 100 * currentMultiplier).toLong()
                            ApiClient.updateWallet(
                                wallet.copy(
                                    winningsPaise = wallet.winningsPaise + winPaise,
                                    totalPaise = wallet.totalPaise + winPaise
                                )
                            )
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CASHOUT ₹%.2f".format(betAmount * currentMultiplier),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                // Place Bet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(50L, 100L, 500L, 1000L).forEach { amt ->
                        val isSelected = betAmount == amt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else CardBackground)
                                .border(1.dp, if (isSelected) GoldPrimary else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { betAmount = amt }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₹$amt",
                                color = if (isSelected) GoldPrimary else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (hasBet) Brush.linearGradient(listOf(Color(0xFF3B1560), Color(0xFF3B1560))) else GreenButtonBrush)
                        .clickable(enabled = !hasBet && !isFlying) {
                            val paise = betAmount * 100L
                            if (wallet.totalPaise >= paise) {
                                ApiClient.updateWallet(wallet.copy(totalPaise = wallet.totalPaise - paise))
                                hasBet = true
                            }
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasBet) "BET PLACED (₹$betAmount)" else "PLACE BET (₹$betAmount)",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
