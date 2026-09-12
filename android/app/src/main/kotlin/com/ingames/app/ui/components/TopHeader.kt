package com.ingames.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import com.ingames.app.ui.theme.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ColorFilter
import coil.ImageLoader
import coil.decode.SvgDecoder

@Composable
fun TopHeader(
    username: String = "satyamog",
    balanceRupees: Double = 0.0,
    avatarPath: String = "file:///android_asset/Avatar/avatar_1.png",
    onProfileClick: () -> Unit,
    onAddCashClick: () -> Unit,
    onWalletClick: () -> Unit
) {
    val context = LocalContext.current
    val svgImageLoader = androidx.compose.runtime.remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-16).dp)
            .padding(horizontal = 14.dp, vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Avatar + Username + Profile Button (Exact Reference Match)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            // Circular Avatar with Gold Glow Border (60dp - 10% larger)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF200038))
                    .border(2.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = avatarPath,
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = username,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Profile Pill Button (Corner Radius 8dp, Height reduced by 20%)
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A0E4E))
                        .border(1.dp, Color(0xFF4C1D95), RoundedCornerShape(8.dp))
                        .clickable { onProfileClick() }
                        .padding(horizontal = 8.dp, vertical = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Profile",
                            color = Color(0xFFFFD700),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "▸",
                            color = Color(0xFFFFD700),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Right: Vibrant Green Wallet Pill with SVG Wallet Icon & Bold Balance (Flutter Parity)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF00B57F), Color(0xFF009A69))
                    )
                )
                .clickable {
                    onWalletClick()
                    onAddCashClick()
                }
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            AsyncImage(
                model = "file:///android_asset/nav_icon/wallet.svg",
                imageLoader = svgImageLoader,
                contentDescription = "Wallet Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "₹%.0f".format(balanceRupees),
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(18.dp)
                    .background(Color.White.copy(alpha = 0.35f))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "+",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
