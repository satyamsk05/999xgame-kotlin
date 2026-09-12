package com.ingames.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ingames.app.ui.theme.AppBackgroundBrush

/**
 * MobileDeviceFrame ensures optimal mobile aspect ratio rendering when previewed or run on large screens.
 */
@Composable
fun MobileDeviceFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF15001F)),
        contentAlignment = Alignment.Center
    ) {
        if (maxWidth > 600.dp) {
            // Desktop / Tablet mode: render inside a sleek mobile frame
            Box(
                modifier = Modifier
                    .width(420.dp)
                    .fillMaxHeight(0.96f)
                    .clip(RoundedCornerShape(36.dp))
                    .background(AppBackgroundBrush)
                    .border(2.dp, Color(0xFF3B1560), RoundedCornerShape(36.dp))
            ) {
                content()
            }
        } else {
            // Standard mobile device: full screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackgroundBrush)
            ) {
                content()
            }
        }
    }
}
