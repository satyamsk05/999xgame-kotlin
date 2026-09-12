package com.ingames.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class StoryBubble(
    val id: String,
    val title: String,
    val subtitle: String,
    val avatarPath: String,
    val badgeText: String? = null,
    val isFire: Boolean = false
)

@Composable
fun PromoStoryRow(
    onStoryClick: (StoryBubble) -> Unit = {}
) {
    val stories = listOf(
        StoryBubble("1", "Rank 1 lao", "30K le jao", "file:///android_asset/Avatar/avatar_1.png", "🔥", true),
        StoryBubble("2", "WINNERS", "PRO-TIP", "file:///android_asset/Avatar/avatar_8.png", null, false),
        StoryBubble("3", "HAPPY", "WINNERS", "file:///android_asset/Avatar/avatar_9.png", null, false)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stories.forEach { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStoryClick(story) }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ring
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                if (story.isFire) {
                                    Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFFF4757), Color(0xFF9C27B0), Color(0xFFFFD700)))
                                } else {
                                    Brush.sweepGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF8B5CF6)))
                                }
                            )
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF130222)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (story.isFire) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Rank 1 lao", color = Color(0xFFA28CB8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "30K", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    Text(text = "le jao", color = Color(0xFFA28CB8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                AsyncImage(
                                    model = story.avatarPath,
                                    contentDescription = story.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }

                    // Optional Fire Badge
                    if (story.isFire) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = (2).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF4757)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (!story.isFire) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (story.id == "2") Color(0xFFEC4899) else Color(0xFF6C5CE7)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${story.title} ${story.subtitle}",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChampionsLeagueBanner(
    onPlayClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Main Deposit Bonus Card (Exact Image 1 Match)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF200038))
                .border(1.dp, Color(0xFF4A1578), RoundedCornerShape(20.dp))
                .clickable { onPlayClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Deposit Info & Action Button
                Column(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Small DEPOSIT Tag Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF3B125B))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DEPOSIT",
                            color = Color(0xFFC084FC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "DEPOSIT BONUS\n180% BONUS",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 18.sp
                    )

                    Text(
                        text = "DEPOSIT -> GET BONUS",
                        color = Color(0xFFE9D5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // DEPOSIT NOW Button (Vibrant Green)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF08C98B))
                            .clickable { onPlayClick() }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "DEPOSIT NOW",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Right side: Wallet Graphic Avatar / Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D5C3A)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "file:///android_asset/images/7updown.png",
                        contentDescription = "Deposit Bonus",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
