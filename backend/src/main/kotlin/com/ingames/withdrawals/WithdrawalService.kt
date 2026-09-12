package com.ingames.withdrawals

class WithdrawalService {
    fun requestWithdrawal(userId: String, amount: Double, method: String, details: String): Withdrawal {
        return Withdrawal(id = "w_${System.currentTimeMillis()}", userId = userId, amount = amount, payoutMethod = method, accountDetails = details)
    }
}
