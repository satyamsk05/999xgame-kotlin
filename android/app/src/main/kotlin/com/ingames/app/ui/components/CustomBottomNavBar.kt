package com.ingames.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import androidx.compose.foundation.border
import com.ingames.app.ui.theme.RushBackground
import com.ingames.app.ui.theme.RushDeepPurple
import com.ingames.app.ui.theme.RushPurple
import com.ingames.app.ui.theme.RushInactiveIcon

import androidx.compose.ui.graphics.Brush

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

enum class NavItem(val title: String, val svgPath: String) {
    HOME("Home", "nav_icon/home.svg"),
    SHARE("Share", "nav_icon/share.svg"),
    ADD_CASH("Add Cash", "nav_icon/addmoney.svg"),
    PROFILE("Profile", "nav_icon/profile.svg"),
    WALLET("Wallet", "nav_icon/wallet.svg")
}

@Composable
fun CustomBottomNavBar(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(NavItem.HOME, NavItem.SHARE, NavItem.ADD_CASH, NavItem.PROFILE)
    val context = LocalContext.current
    val svgImageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "pillSlideIndex"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF57197B), Color(0xFF15001F))
                )
            )
            .padding(vertical = 2.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val tabWidth = maxWidth / items.size

            // 1. Sliding Active White Pill Indicator across tabs (70dp x 60dp)
            Box(
                modifier = Modifier
                    .offset(x = tabWidth * animatedIndex)
                    .width(tabWidth)
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                )
            }

            // 2. Interactive Navigation Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }

                    val animatedTint by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.50f),
                        animationSpec = tween(200),
                        label = "tintColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onItemSelected(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = "file:///android_asset/${item.svgPath}",
                                imageLoader = svgImageLoader,
                                contentDescription = item.title,
                                colorFilter = ColorFilter.tint(animatedTint),
                                modifier = Modifier.size(30.dp)
                            )

                            Spacer(modifier = Modifier.height(0.5.dp))

                            Text(
                                text = item.title,
                                color = animatedTint,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.W700 else FontWeight.W500
                            )
                        }
                    }
                }
            }
        }
    }
}
