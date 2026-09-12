package com.ingames.wallet

import com.ingames.models.*

data class InitiateDepositPayload(val amountRupees: Double, val paymentMethod: String)
data class SubmitUtrPayload(val depositId: String, val utr: String)

object WalletController {
    fun getBalance(userId: String): WalletBalance {
        return FinancialService.getWalletBalance(userId)
    }

    fun getTransactions(userId: String): List<LedgerTransaction> {
        return FinancialService.getTransactions(userId)
    }

    fun initiateDeposit(userId: String, payload: InitiateDepositPayload): InitiateDepositResponse {
        return FinancialService.initiateDeposit(userId, payload.amountRupees, payload.paymentMethod)
    }

    fun submitUtr(userId: String, payload: SubmitUtrPayload): Boolean {
        return FinancialService.submitUtr(userId, payload.depositId, payload.utr)
    }

    fun requestWithdrawal(userId: String, payload: WithdrawalRequestPayload): Result<WithdrawalRecord> {
        return FinancialService.requestWithdrawal(userId, payload)
    }
}
