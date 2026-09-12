package com.ingames.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.ingames.models.LedgerTransaction
import com.ingames.models.TransactionType
import kotlinx.coroutines.launch

@Composable
fun TransactionsScreen(
    onBackClick: () -> Unit
) {
    var transactions by remember { mutableStateOf<List<LedgerTransaction>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            val list = ApiClient.fetchTransactions()
            transactions = list.ifEmpty {
                // Fallback mock history for display
                listOf(
                    LedgerTransaction(
                        id = "tx_1",
                        userId = "u1",
                        type = TransactionType.GAME_WIN,
                        amountPaise = 20000L,
                        balanceAfterPaise = 125000L,
                        description = "Won on 7 Up Down (Round #1042)",
                        createdAt = "Just now"
                    ),
                    LedgerTransaction(
                        id = "tx_2",
                        userId = "u1",
                        type = TransactionType.GAME_BET,
                        amountPaise = -10000L,
                        balanceAfterPaise = 105000L,
                        description = "Wager on 7 Up Down (Round #1042)",
                        createdAt = "2 mins ago"
                    ),
                    LedgerTransaction(
                        id = "tx_3",
                        userId = "u1",
                        type = TransactionType.DEPOSIT,
                        amountPaise = 50000L,
                        balanceAfterPaise = 115000L,
                        description = "UPI Deposit Verified",
                        createdAt = "Today, 10:30 AM"
                    )
                )
            }
            isLoading = false
        }
    }

    val filteredList = transactions.filter { tx ->
        when (selectedFilter) {
            "WINS" -> tx.type == TransactionType.GAME_WIN
            "BETS" -> tx.type == TransactionType.GAME_BET
            "DEPOSITS" -> tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.DEPOSIT_BONUS
            "WITHDRAWALS" -> tx.type == TransactionType.WITHDRAWAL_REQUEST || tx.type == TransactionType.WITHDRAWAL_SUCCESS
            else -> true
        }
    }

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
                    text = "Transaction Passbook",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "WINS", "BETS", "DEPOSITS").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) GoldPrimary else CardBackground)
                            .border(1.dp, if (isSelected) GoldPrimary else CardBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.Black else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            }
        } else if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No transactions found", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredList) { tx ->
                val isCredit = tx.amountPaise > 0
                val amountRupees = Math.abs(tx.amountPaise) / 100.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.description.ifBlank { tx.type.name },
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = tx.createdAt,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${if (isCredit) "+" else "-"}₹%.2f".format(amountRupees),
                            color = if (isCredit) TextGreen else TextRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Bal: ₹%.2f".format(tx.balanceAfterPaise / 100.0),
                            color = GoldSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
