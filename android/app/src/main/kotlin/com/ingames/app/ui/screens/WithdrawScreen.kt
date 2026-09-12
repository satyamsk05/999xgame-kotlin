package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*
import com.ingames.models.WithdrawalRequestPayload
import kotlinx.coroutines.launch

@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit,
    onWithdrawSuccess: () -> Unit
) {
    val wallet by ApiClient.walletState.collectAsState()
    var amountText by remember { mutableStateOf("500") }
    var payoutMethod by remember { mutableStateOf("UPI") }
    var upiId by remember { mutableStateOf("player@apl") }
    var bankAccount by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }
    var accountHolder by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val amountDouble = amountText.toDoubleOrNull() ?: 0.0
    val tdsDeduction = if (amountDouble > 10000.0) amountDouble * 0.30 else 0.0
    val netAmount = (amountDouble - tdsDeduction).coerceAtLeast(0.0)

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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Withdraw Winnings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Winnings Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "WITHDRAWABLE WINNINGS", color = GoldSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹%.2f".format(wallet.winningsRupees),
                            color = TextGreen,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Min ₹100",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ENTER AMOUNT (₹)",
                color = GoldSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PAYOUT METHOD",
                color = GoldSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("UPI", "BANK").forEach { method ->
                    val isSelected = payoutMethod == method
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else CardBackground)
                            .border(1.dp, if (isSelected) GoldPrimary else CardBorder, RoundedCornerShape(12.dp))
                            .clickable { payoutMethod = method }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (method == "UPI") "UPI ID" else "Bank Transfer",
                            color = if (isSelected) GoldPrimary else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            if (payoutMethod == "UPI") {
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Enter UPI ID (e.g. name@upi)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder
                    )
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = accountHolder,
                        onValueChange = { accountHolder = it },
                        label = { Text("Account Holder Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        label = { Text("Bank Account Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ifscCode,
                        onValueChange = { ifscCode = it },
                        label = { Text("IFSC Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Payout Summary Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Gross Withdrawal", color = TextMuted, fontSize = 13.sp)
                    Text(text = "₹%.2f".format(amountDouble), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TDS Tax (0%)", color = TextMuted, fontSize = 13.sp)
                    Text(text = "₹%.2f".format(tdsDeduction), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = CardBorder)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Net Amount Credited", color = GoldSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "₹%.2f".format(netAmount), color = TextGreen, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        if (errorMessage != null) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMessage!!, color = TextRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GreenButtonBrush)
                    .clickable(enabled = !isLoading) {
                        if (amountDouble < 100.0) {
                            errorMessage = "Minimum withdrawal amount is ₹100.00"
                            return@clickable
                        }
                        if (amountDouble > wallet.winningsRupees) {
                            errorMessage = "Withdrawal amount exceeds withdrawable winnings"
                            return@clickable
                        }
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val res = ApiClient.requestWithdrawal(
                                WithdrawalRequestPayload(
                                    amountRupees = amountDouble,
                                    payoutMethod = payoutMethod,
                                    upiId = upiId,
                                    bankAccountNumber = bankAccount,
                                    ifscCode = ifscCode,
                                    accountHolderName = accountHolder
                                )
                            )
                            isLoading = false
                            if (res != null) {
                                ApiClient.fetchBalance()
                                onWithdrawSuccess()
                            } else {
                                errorMessage = "Failed to submit withdrawal request"
                            }
                        }
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = "SUBMIT WITHDRAWAL REQUEST",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
