package com.ingames.transactions

class TransactionService {
    fun record(userId: String, type: TransactionType, amount: Double, balanceAfter: Double, refId: String): Transaction {
        return Transaction(id = "tx_${System.currentTimeMillis()}", userId = userId, type = type, amount = amount, balanceAfter = balanceAfter, referenceId = refId)
    }
}
