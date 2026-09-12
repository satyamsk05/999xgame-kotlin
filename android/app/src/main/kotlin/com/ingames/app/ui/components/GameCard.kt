package com.ingames.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ingames.models.GameInfo

@Composable
fun GameCard(
    game: GameInfo,
    onGameClick: (GameInfo) -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: androidx.compose.ui.unit.Dp = 140.dp
) {
    val imageSrc = if (game.imagePath.startsWith("file://") || game.imagePath.startsWith("http")) {
        game.imagePath
    } else {
        "file:///android_asset/${game.imagePath}"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF200038))
            .border(1.dp, Color(0xFF3A0868), RoundedCornerShape(20.dp))
            .clickable { onGameClick(game) }
    ) {
        // Full Image Cover with Clean Artwork
        AsyncImage(
            model = imageSrc,
            contentDescription = game.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
