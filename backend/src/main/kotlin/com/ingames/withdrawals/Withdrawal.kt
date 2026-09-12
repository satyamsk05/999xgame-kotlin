package com.ingames.withdrawals

import kotlinx.serialization.Serializable

@Serializable
data class Withdrawal(
    val id: String,
    val userId: String,
    val amount: Double,
    val payoutMethod: String,
    val accountDetails: String,
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
