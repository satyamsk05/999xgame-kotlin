package com.ingames.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.data.TokenManager
import com.ingames.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var isWaitingForWhatsApp by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var verificationToken by remember { mutableStateOf<String?>(null) }
    var verificationUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(verificationToken) {
        val token = verificationToken ?: return@LaunchedEffect
        while (true) {
            delay(2000)
            val status = ApiClient.checkLogginStatus(token)
            when (status?.status?.uppercase()) {
                "VERIFIED" -> {
                    val auth = ApiClient.verifyLoggin(token)
                    if (auth?.success == true && !auth.token.isNullOrBlank()) {
                        TokenManager.setSession(auth.token, auth.user)
                        auth.user?.wallet?.let { ApiClient.updateWallet(it) }
                        isWaitingForWhatsApp = false
                        isLoading = false
                        verificationToken = null
                        onLoginSuccess()
                        break
                    } else {
                        errorMessage = auth?.message ?: "WhatsApp verification failed"
                        isWaitingForWhatsApp = false
                        isLoading = false
                        verificationToken = null
                        break
                    }
                }
                "EXPIRED", "ALREADY_CONSUMED" -> {
                    errorMessage = status.message ?: "Verification expired. Please try again."
                    isWaitingForWhatsApp = false
                    isLoading = false
                    verificationToken = null
                    break
                }
            }
        }
    }

    fun startWhatsAppLogin() {
        scope.launch {
            isLoading = true
            isWaitingForWhatsApp = false
            errorMessage = null

            val data = ApiClient.createLogginToken()
            if (data?.success == true && !data.token.isNullOrBlank() && !data.verificationUrl.isNullOrBlank()) {
                verificationToken = data.token
                verificationUrl = data.verificationUrl
                isWaitingForWhatsApp = true
                isLoading = false
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.verificationUrl)))
                } catch (_: Exception) {
                    errorMessage = "WhatsApp verification link could not be opened"
                    isWaitingForWhatsApp = false
                    verificationToken = null
                }
            } else {
                isLoading = false
                errorMessage = data?.message ?: "Unable to start WhatsApp login. Please try again."
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "999x InGames",
                color = GoldPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isWaitingForWhatsApp)
                    "WhatsApp par khulne wale message ko Send karke verification complete karein"
                else
                    "WhatsApp se securely login karein",
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (isWaitingForWhatsApp) {
                CircularProgressIndicator(
                    color = GoldPrimary,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Waiting for WhatsApp verification...",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "WhatsApp message send karne ke baad app automatically login ho jayega.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                if (!verificationUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GreenButtonBrush)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(verificationUrl)))
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OPEN WHATSAPP AGAIN", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            } else {
                Text(
                    text = "No password. No OTP typing.\nVerify your WhatsApp number in one tap.",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GreenButtonBrush)
                        .clickable(enabled = !isLoading) { startWhatsAppLogin() }
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = "CONTINUE WITH WHATSAPP",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = TextRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
