package com.ingames.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// App Background (#15001F)
val BackgroundStart = Color(0xFF15001F)
val BackgroundEnd = Color(0xFF15001F)
val AppBackground = Color(0xFF15001F)

val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF15001F), Color(0xFF15001F))
)

// Primary Palette Tokens
val RushBackground = Color(0xFF15001F)
val RushDeepPurple = Color(0xFF200038)
val RushPurple = Color(0xFF57197B)
val RushPurpleAccent = Color(0xFF6808B8)
val RushPrimaryText = Color(0xFFFFFFFF)
val RushSecondaryText = Color(0xA6FFFFFF)
val RushInactiveIcon = Color(0x8CFFFFFF)
val RushActionAccent = Color(0xFFF83050)
val RushLightSurface = Color(0xFFF8F8F8)

// Accent Colors
val GoldPrimary = Color(0xFFF59E0B)
val GoldSecondary = Color(0xFFFDCB58)
val GoldAccent = Color(0xFFFFD700)

// Action Emerald Green
val GreenButtonStart = Color(0xFF00E676)
val GreenButtonEnd = Color(0xFF00C853)

val GreenButtonBrush = Brush.horizontalGradient(
    colors = listOf(GreenButtonStart, GreenButtonEnd)
)

// Cards & Surfaces
val CardBackground = Color(0xFF200038)
val CardBorder = Color(0xFF3A0868)
val CardGloss = Color(0x1AFFFFFF)

// Navigation Bar Gradient Background (#57197B to #15001F)
val BottomNavStart = Color(0xFF57197B)
val BottomNavMid = Color(0xFF350B50)
val BottomNavEnd = Color(0xFF15001F)

val BottomNavBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF57197B), Color(0xFF15001F))
)

// Nav Tab Colors
val NavActiveBackground = Color(0xFF500098) // Active subtle container (#500098)
val NavInactiveText = Color(0x8CFFFFFF) // lower-opacity white/gray

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xA6FFFFFF)
val TextMuted = Color(0x8CFFFFFF)
val TextGreen = Color(0xFF00E676)
val TextRed = Color(0xFFF83050)
