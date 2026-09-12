package com.ingames.deposits

import kotlinx.serialization.Serializable

@Serializable
data class Deposit(
    val id: String,
    val userId: String,
    val amount: Double,
    val gateway: String,
    val transactionRef: String,
    val status: DepositStatus = DepositStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
