package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ingames.app.data.ApiClient
import com.ingames.app.ui.theme.*
import kotlinx.coroutines.launch

data class CashOffer(val amount: Int, val cashback: Int)

@Composable
fun AddCashScreen(
    onBackClick: () -> Unit,
    onDepositSuccess: () -> Unit
) {
    val wallet by ApiClient.walletState.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var showUtrDialog by remember { mutableStateOf(false) }
    var currentDepositId by remember { mutableStateOf("") }
    var utrInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val offers = listOf(
        CashOffer(200, 25),
        CashOffer(500, 75),
        CashOffer(50, 4),
        CashOffer(100, 10)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            // Header: Add Cash + Total Balance
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
                        text = "Add Cash",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Balance",
                        color = Color(0xFFA594C6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${"%.1f".format(wallet.totalRupees)}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        item {
            // Enter Amount Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF16062C))
                    .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Column {
                    // Purple Input Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF5E1386))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (amountText.isEmpty()) {
                            Text(
                                text = "Enter Amount",
                                color = Color(0xFFBCA6DC),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        BasicTextField(
                            value = amountText,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() } && input.length <= 6) {
                                    amountText = input
                                }
                            },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(Color.White),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cashback text with % icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select offers to get ",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Cashback",
                            color = Color(0xFF00E676),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Offers ✨",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 2x2 Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (row in offers.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (offer in row) {
                            val isSelected = amountText == offer.amount.toString()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF16062C))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00E676) else Color(0xFF3B1865),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { amountText = offer.amount.toString() }
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${offer.amount}",
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color(0xFF8A7CA8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "₹${offer.cashback} Cashback",
                                        color = Color(0xFF00E676),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Payment Webpage Info Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF16062C))
                    .border(1.dp, Color(0xFF3B1865), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "UPI payment, QR and UTR verification will open on the payment webpage.",
                        color = Color(0xFFB0A2C9),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            val parsedAmount = amountText.toIntOrNull() ?: 0

            // Bottom CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF00E676))
                    .clickable(enabled = !isLoading) {
                        if (parsedAmount > 0) {
                            scope.launch {
                                isLoading = true
                                val dep = ApiClient.initiateDeposit(parsedAmount.toDouble())
                                isLoading = false
                                if (dep != null) {
                                    currentDepositId = dep.depositId
                                    showUtrDialog = true
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = if (parsedAmount > 0) "DEPOSIT ₹$parsedAmount" else "ENTER AMOUNT TO DEPOSIT",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    if (showUtrDialog) {
        AlertDialog(
            onDismissRequest = { showUtrDialog = false },
            title = {
                Text(text = "Submit 12-Digit UTR", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Order: $currentDepositId\nAmount: ₹$amountText\n\nEnter the 12-digit UTR from your UPI payment app:",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = utrInput,
                        onValueChange = { if (it.length <= 12) utrInput = it },
                        placeholder = { Text("Enter 12-digit UTR") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF3B1865)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            if (utrInput.isNotBlank()) {
                                ApiClient.submitUtr(currentDepositId, utrInput)
                                showUtrDialog = false
                                onDepositSuccess()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text("Submit UTR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUtrDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = Color(0xFF1E0A3C)
        )
    }
}
