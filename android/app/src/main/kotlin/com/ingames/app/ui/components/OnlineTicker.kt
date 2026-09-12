package com.ingames.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun OnlineTicker(
    onlineCount: Int = 85000,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF15001F),
                            0.2f to Color(0xFF220138),
                            0.5f to Color(0xFF2B044A),
                            0.8f to Color(0xFF220138),
                            1.0f to Color(0xFF15001F)
                        )
                    )
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3 Overlapping Avatars
            Row(
                horizontalArrangement = Arrangement.spacedBy((-8).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(
                    imagePath = "file:///android_asset/Avatar/avatar_2.png",
                    borderColor = Color(0xFFFFD700)
                )
                AvatarCircle(
                    imagePath = "file:///android_asset/Avatar/avatar_3.png",
                    borderColor = Color(0xFFF83050)
                )
                AvatarCircle(
                    imagePath = "file:///android_asset/Avatar/avatar_7.png",
                    borderColor = Color(0xFF38BDF8)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Formatted Text: "85,000 online"
            Text(
                text = "%,d online".format(onlineCount),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Pulsing Green Indicator Dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(dotScale)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676))
            )
        }
    }
}

@Composable
private fun AvatarCircle(imagePath: String, borderColor: Color) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Color(0xFF2A0E4E))
            .border(1.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imagePath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}
