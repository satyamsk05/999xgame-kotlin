package com.ingames.deposits

class DepositService {
    fun createDepositOrder(userId: String, amount: Double, gateway: String): Deposit {
        return Deposit(id = "d_${System.currentTimeMillis()}", userId = userId, amount = amount, gateway = gateway, transactionRef = "ref_${System.currentTimeMillis()}")
    }
}
