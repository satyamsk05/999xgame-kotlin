package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.ui.theme.*

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "App Settings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SettingSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Game audio & win sounds",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
                SettingSwitchRow(
                    title = "Vibration / Haptics",
                    subtitle = "Touch feedback on bet placement",
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
                SettingSwitchRow(
                    title = "Push Notifications",
                    subtitle = "Match alerts & bonus announcements",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "APPLICATION INFO", color = GoldSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "InGames 999x Native Android (Kotlin)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Version: 1.0.0 (Build 100)", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenButtonStart,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF19052E)
            )
        )
    }
}
