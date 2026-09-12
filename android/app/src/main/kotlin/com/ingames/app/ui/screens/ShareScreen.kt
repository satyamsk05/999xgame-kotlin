package com.ingames.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import com.ingames.app.ui.theme.AppBackground

data class ReferralRecord(
    val name: String,
    val date: String,
    val amount: String,
    val avatarPath: String
)

@Composable
fun ShareScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val svgImageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    val referrals = listOf(
        ReferralRecord("Dh animation", "09 Dec", "₹15", "Avatar/avatar_1.png"),
        ReferralRecord("Harshthakur", "08 Dec", "₹15", "Avatar/avatar_2.png"),
        ReferralRecord("RAHUL", "07 Dec", "₹15", "Avatar/avatar_3.png")
    )

    fun shareApp(isWhatsApp: Boolean) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey! Play exciting games on InGames 999x and earn real cash! Use my referral code: INGAMES999 to get ₹50 free bonus! Download now: https://ingames999.com"
            )
            if (isWhatsApp) {
                setPackage("com.whatsapp")
            }
        }
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // Fallback generic share
            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey! Play exciting games on InGames 999x and earn real cash! Code: INGAMES999"
                )
            }
            context.startActivity(Intent.createChooser(genericIntent, "Share InGames"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Header: Refer & Earn + Language button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Refer & Earn",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF280C48))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "अ  口",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                // Your Earnings Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Earnings",
                            color = Color(0xFFA594C6),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹30",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF280C48))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💰", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "₹",
                                    color = Color.Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            item {
                // 1 Referral = ₹1,000 Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF16062C))
                        .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(22.dp))
                        .padding(horizontal = 12.dp, vertical = 18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "1 Referral = ₹1,000",
                            color = Color(0xFF00E676),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step 1: Phone (signs up)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "₹15",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "signs up",
                                    color = Color(0xFFB0A2C9),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                AsyncImage(
                                    model = "file:///android_asset/IMG_20260904_223402.png",
                                    contentDescription = "Signs up",
                                    modifier = Modifier.size(62.dp)
                                )
                            }

                            Text(
                                text = "+",
                                color = Color(0xFF8A7CA8),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Step 2: Money bag (adds cash)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "₹55",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "adds cash",
                                        color = Color(0xFFB0A2C9),
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF7A6B94),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                AsyncImage(
                                    model = "file:///android_asset/refercoin.png",
                                    contentDescription = "Adds cash",
                                    modifier = Modifier.size(62.dp)
                                )
                            }

                            Text(
                                text = "+",
                                color = Color(0xFF8A7CA8),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Step 3: Controller (play games)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "₹930",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "play games",
                                        color = Color(0xFFB0A2C9),
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF7A6B94),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                AsyncImage(
                                    model = "file:///android_asset/IMG_20260904_223443.png",
                                    contentDescription = "Play games",
                                    modifier = Modifier.size(62.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))

                // Referrals History Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF16062C))
                        .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column {
                        referrals.forEachIndexed { index, referral ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = "file:///android_asset/${referral.avatarPath}",
                                        contentDescription = referral.name,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF35115E))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = referral.name,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = referral.date,
                                            color = Color(0xFF8A7CA8),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Text(
                                    text = referral.amount,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (index < referrals.size - 1) {
                                HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                            }
                        }

                        HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // View all referrals button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* View all */ }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View all referrals",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Fixed Share Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Share Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF6C5CE7))
                    .clickable { shareApp(isWhatsApp = false) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Share on WhatsApp Button
            Box(
                modifier = Modifier
                    .weight(1.4f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF00E676))
                    .clickable { shareApp(isWhatsApp = true) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "file:///android_asset/whatsapp-svgrepo-com.svg",
                        imageLoader = svgImageLoader,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share on Whatsapp",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
