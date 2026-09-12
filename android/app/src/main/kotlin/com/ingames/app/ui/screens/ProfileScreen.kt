package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFairPlayClick: () -> Unit,
    onContactClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    onAddCashClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
    onTransactionsClick: () -> Unit = {}
) {
    val wallet by ApiClient.walletState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            // Header Row: Wallet title + Support and Settings buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wallet",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Support button
                    Column(
                        modifier = Modifier
                            .size(width = 64.dp, height = 54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF280C48))
                            .clickable { onHelpClick() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = "Support",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Support",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Settings button
                    Column(
                        modifier = Modifier
                            .size(width = 64.dp, height = 54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF280C48))
                            .clickable { onSettingsClick() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            // Total Balance & All Transactions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        color = Color(0xFFA594C6),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${wallet.totalRupees.toInt()}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF280C48))
                        .clickable { onTransactionsClick() }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "All Transactions",
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

        item {
            // Main Balance Breakdown Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF16062C))
                    .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Column {
                    // Deposit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Deposit",
                                    color = Color(0xFFB0A2C9),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFF7A6B94),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${wallet.depositRupees.toInt()}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF00E676))
                                .clickable { onAddCashClick() }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ ADD CASH",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Winnings Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Winnings",
                                    color = Color(0xFFB0A2C9),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFF7A6B94),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${wallet.winningsRupees.toInt()}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF6C5CE7))
                                .clickable { onWithdrawClick() }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SyncAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "WITHDRAW",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Rush Rewards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rush Rewards",
                                    color = Color(0xFFB0A2C9),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFF7A6B94),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${wallet.bonusRupees.toInt()}",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3B1464))
                                .clickable { onBackClick() }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PLAY TO USE",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // BEST DEAL Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Card Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF16062C))
                        .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(22.dp))
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹500",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+ ₹75 Cashback",
                                color = Color(0xFF00E676),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF00E676))
                                .clickable { onAddCashClick() }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ ADD",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // BEST DEAL Overlapping Badge
                Box(
                    modifier = Modifier
                        .offset(x = 14.dp, y = (-10).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF4757))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "BEST DEAL",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1B0B33))
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFairPlayClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Fair Play Policy", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF7A6B94))
                }
                HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onContactClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ContactSupport, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Contact Us", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF7A6B94))
                }
                HorizontalDivider(color = Color(0xFF2A104E), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogoutClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFFF4757), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Logout", color = Color(0xFFFF4757), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF7A6B94))
                }
            }
        }
    }
}
