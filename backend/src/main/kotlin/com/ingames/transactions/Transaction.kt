package com.ingames.transactions

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val balanceAfter: Double,
    val referenceId: String,
    val timestamp: Long = System.currentTimeMillis()
)
