package com.ingames.wallet

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.*
import java.util.UUID

object FinancialService {

    fun getWalletBalance(userId: String): WalletBalance {
        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                return DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement(
                        "SELECT deposit_balance, winnings_balance, bonus_balance, reserved_balance FROM wallets WHERE user_id = ?"
                    )
                    stmt.setString(1, userId)
                    val rs = stmt.executeQuery()
                    if (rs.next()) {
                        val dep = rs.getLong("deposit_balance")
                        val win = rs.getLong("winnings_balance")
                        val bon = rs.getLong("bonus_balance")
                        val res = rs.getLong("reserved_balance")
                        WalletBalance(
                            depositPaise = dep,
                            winningsPaise = win,
                            bonusPaise = bon,
                            reservedPaise = res,
                            totalPaise = dep + win + bon
                        )
                    } else {
                        getMemoryWalletBalance(userId)
                    }
                }
            } catch (e: Exception) {
                return getMemoryWalletBalance(userId)
            }
        } else {
            return getMemoryWalletBalance(userId)
        }
    }

    private fun getMemoryWalletBalance(userId: String): WalletBalance {
        val wallet = MemoryDataStore.wallets.getOrPut(userId) {
            mutableMapOf("deposit" to 80000L, "winnings" to 45000L, "bonus" to 0L, "reserved" to 0L)
        }
        val dep = wallet["deposit"] ?: 0L
        val win = wallet["winnings"] ?: 0L
        val bon = wallet["bonus"] ?: 0L
        val res = wallet["reserved"] ?: 0L
        return WalletBalance(
            depositPaise = dep,
            winningsPaise = win,
            bonusPaise = bon,
            reservedPaise = res,
            totalPaise = dep + win + bon
        )
    }

    @Synchronized
    fun debitForBet(
        userId: String,
        amountPaise: Long,
        gameId: String,
        roundId: String,
        idempotencyKey: String
    ): Result<WalletBalance> {
        if (amountPaise <= 0) return Result.failure(IllegalArgumentException("Bet amount must be positive"))

        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                return DatabaseFactory.withConnection { conn ->
                    conn.autoCommit = false
                    val checkLedger = conn.prepareStatement("SELECT id FROM wallet_ledger WHERE idempotency_key = ?")
                    checkLedger.setString(1, idempotencyKey)
                    val rsCheck = checkLedger.executeQuery()
                    if (rsCheck.next()) {
                        conn.rollback()
                        conn.autoCommit = true
                        return@withConnection Result.success(getWalletBalance(userId))
                    }

                    val lockStmt = conn.prepareStatement(
                        "SELECT deposit_balance, winnings_balance, bonus_balance, reserved_balance FROM wallets WHERE user_id = ? FOR UPDATE"
                    )
                    lockStmt.setString(1, userId)
                    val rsWallet = lockStmt.executeQuery()
                    if (!rsWallet.next()) {
                        conn.rollback()
                        conn.autoCommit = true
                        return@withConnection debitForBetMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
                    }

                    var dep = rsWallet.getLong("deposit_balance")
                    var win = rsWallet.getLong("winnings_balance")
                    var bon = rsWallet.getLong("bonus_balance")
                    val res = rsWallet.getLong("reserved_balance")
                    val totalAvailable = dep + win + bon

                    if (totalAvailable < amountPaise) {
                        conn.rollback()
                        conn.autoCommit = true
                        return@withConnection Result.failure(IllegalStateException("INSUFFICIENT_FUNDS"))
                    }

                    var remainingToDeduct = amountPaise
                    val fromDep = minOf(dep, remainingToDeduct)
                    dep -= fromDep
                    remainingToDeduct -= fromDep

                    val fromWin = minOf(win, remainingToDeduct)
                    win -= fromWin
                    remainingToDeduct -= fromWin

                    val fromBon = minOf(bon, remainingToDeduct)
                    bon -= fromBon
                    remainingToDeduct -= fromBon

                    val updateStmt = conn.prepareStatement(
                        "UPDATE wallets SET deposit_balance = ?, winnings_balance = ?, bonus_balance = ?, updated_at = NOW() WHERE user_id = ?"
                    )
                    updateStmt.setLong(1, dep)
                    updateStmt.setLong(2, win)
                    updateStmt.setLong(3, bon)
                    updateStmt.setString(4, userId)
                    updateStmt.executeUpdate()

                    val ledgerStmt = conn.prepareStatement(
                        "INSERT INTO wallet_ledger (id, user_id, type, amount, balance_after, reference_type, reference_id, idempotency_key, description, created_at) VALUES (?, ?, 'GAME_BET', ?, ?, 'GAME_ROUND', ?, ?, ?, NOW())"
                    )
                    ledgerStmt.setString(1, UUID.randomUUID().toString())
                    ledgerStmt.setString(2, userId)
                    ledgerStmt.setLong(3, -amountPaise)
                    ledgerStmt.setLong(4, dep + win + bon)
                    ledgerStmt.setString(5, roundId)
                    ledgerStmt.setString(6, idempotencyKey)
                    ledgerStmt.setString(7, "Wager on $gameId (Round #$roundId)")
                    ledgerStmt.executeUpdate()

                    conn.commit()
                    conn.autoCommit = true
                    Result.success(
                        WalletBalance(
                            depositPaise = dep,
                            winningsPaise = win,
                            bonusPaise = bon,
                            reservedPaise = res,
                            totalPaise = dep + win + bon
                        )
                    )
                }
            } catch (e: Exception) {
                return debitForBetMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
            }
        } else {
            return debitForBetMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
        }
    }

    private fun debitForBetMemory(
        userId: String,
        amountPaise: Long,
        gameId: String,
        roundId: String,
        idempotencyKey: String
    ): Result<WalletBalance> {
        val wallet = MemoryDataStore.wallets.getOrPut(userId) {
            mutableMapOf("deposit" to 80000L, "winnings" to 45000L, "bonus" to 0L, "reserved" to 0L)
        }
        if (MemoryDataStore.ledger.containsKey(idempotencyKey)) {
            return Result.success(getWalletBalance(userId))
        }

        var dep = wallet["deposit"] ?: 0L
        var win = wallet["winnings"] ?: 0L
        var bon = wallet["bonus"] ?: 0L
        val res = wallet["reserved"] ?: 0L
        val total = dep + win + bon

        if (total < amountPaise) {
            return Result.failure(IllegalStateException("INSUFFICIENT_FUNDS"))
        }

        var rem = amountPaise
        val fromDep = minOf(dep, rem)
        dep -= fromDep
        rem -= fromDep

        val fromWin = minOf(win, rem)
        win -= fromWin
        rem -= fromWin

        val fromBon = minOf(bon, rem)
        bon -= fromBon

        wallet["deposit"] = dep
        wallet["winnings"] = win
        wallet["bonus"] = bon

        val newTotal = dep + win + bon
        val ledgerId = "led_" + UUID.randomUUID().toString().take(8)
        MemoryDataStore.ledger[idempotencyKey] = mutableMapOf(
            "id" to ledgerId,
            "user_id" to userId,
            "type" to TransactionType.GAME_BET,
            "amount" to -amountPaise,
            "balance_after" to newTotal,
            "reference_type" to "GAME_ROUND",
            "reference_id" to roundId,
            "idempotency_key" to idempotencyKey,
            "description" to "Wager on $gameId (Round #$roundId)",
            "created_at" to java.time.Instant.now().toString()
        )

        return Result.success(
            WalletBalance(
                depositPaise = dep,
                winningsPaise = win,
                bonusPaise = bon,
                reservedPaise = res,
                totalPaise = newTotal
            )
        )
    }

    @Synchronized
    fun creditWinnings(
        userId: String,
        amountPaise: Long,
        gameId: String,
        roundId: String,
        idempotencyKey: String
    ): Result<WalletBalance> {
        if (amountPaise <= 0) return Result.success(getWalletBalance(userId))

        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                return DatabaseFactory.withConnection { conn ->
                    conn.autoCommit = false
                    val lockStmt = conn.prepareStatement(
                        "SELECT deposit_balance, winnings_balance, bonus_balance, reserved_balance FROM wallets WHERE user_id = ? FOR UPDATE"
                    )
                    lockStmt.setString(1, userId)
                    val rs = lockStmt.executeQuery()
                    if (!rs.next()) {
                        conn.rollback()
                        conn.autoCommit = true
                        return@withConnection creditWinningsMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
                    }

                    val dep = rs.getLong("deposit_balance")
                    var win = rs.getLong("winnings_balance") + amountPaise
                    val bon = rs.getLong("bonus_balance")
                    val res = rs.getLong("reserved_balance")

                    val updateStmt = conn.prepareStatement(
                        "UPDATE wallets SET winnings_balance = ?, updated_at = NOW() WHERE user_id = ?"
                    )
                    updateStmt.setLong(1, win)
                    updateStmt.setString(2, userId)
                    updateStmt.executeUpdate()

                    val ledgerStmt = conn.prepareStatement(
                        "INSERT INTO wallet_ledger (id, user_id, type, amount, balance_after, reference_type, reference_id, idempotency_key, description, created_at) VALUES (?, ?, 'GAME_WIN', ?, ?, 'GAME_ROUND', ?, ?, ?, NOW())"
                    )
                    ledgerStmt.setString(1, UUID.randomUUID().toString())
                    ledgerStmt.setString(2, userId)
                    ledgerStmt.setLong(3, amountPaise)
                    ledgerStmt.setLong(4, dep + win + bon)
                    ledgerStmt.setString(5, roundId)
                    ledgerStmt.setString(6, idempotencyKey)
                    ledgerStmt.setString(7, "Won on $gameId (Round #$roundId)")
                    ledgerStmt.executeUpdate()

                    conn.commit()
                    conn.autoCommit = true
                    Result.success(
                        WalletBalance(
                            depositPaise = dep,
                            winningsPaise = win,
                            bonusPaise = bon,
                            reservedPaise = res,
                            totalPaise = dep + win + bon
                        )
                    )
                }
            } catch (e: Exception) {
                return creditWinningsMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
            }
        } else {
            return creditWinningsMemory(userId, amountPaise, gameId, roundId, idempotencyKey)
        }
    }

    private fun creditWinningsMemory(
        userId: String,
        amountPaise: Long,
        gameId: String,
        roundId: String,
        idempotencyKey: String
    ): Result<WalletBalance> {
        val wallet = MemoryDataStore.wallets.getOrPut(userId) {
            mutableMapOf("deposit" to 80000L, "winnings" to 45000L, "bonus" to 0L, "reserved" to 0L)
        }
        val dep = wallet["deposit"] ?: 0L
        val win = (wallet["winnings"] ?: 0L) + amountPaise
        val bon = wallet["bonus"] ?: 0L
        val res = wallet["reserved"] ?: 0L
        wallet["winnings"] = win

        val newTotal = dep + win + bon
        val ledgerId = "led_" + UUID.randomUUID().toString().take(8)
        MemoryDataStore.ledger[idempotencyKey] = mutableMapOf(
            "id" to ledgerId,
            "user_id" to userId,
            "type" to TransactionType.GAME_WIN,
            "amount" to amountPaise,
            "balance_after" to newTotal,
            "reference_type" to "GAME_ROUND",
            "reference_id" to roundId,
            "idempotency_key" to idempotencyKey,
            "description" to "Won on $gameId (Round #$roundId)",
            "created_at" to java.time.Instant.now().toString()
        )

        return Result.success(
            WalletBalance(
                depositPaise = dep,
                winningsPaise = win,
                bonusPaise = bon,
                reservedPaise = res,
                totalPaise = newTotal
            )
        )
    }

    // --- DEPOSITS ---

    fun initiateDeposit(userId: String, amountRupees: Double, paymentMethod: String): InitiateDepositResponse {
        val depositId = "DEP_" + System.currentTimeMillis() + "_" + (1000..9999).random()
        val record = mutableMapOf<String, Any?>(
            "id" to UUID.randomUUID().toString(),
            "deposit_id" to depositId,
            "user_id" to userId,
            "amount_rupees" to amountRupees,
            "payment_method" to paymentMethod,
            "status" to "PENDING",
            "created_at" to java.time.Instant.now().toString()
        )
        MemoryDataStore.deposits[depositId] = record
        return InitiateDepositResponse(
            depositId = depositId,
            amountRupees = amountRupees,
            upiId = "ingames@upi",
            qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=upi://pay?pa=ingames@upi&pn=InGames&am=$amountRupees&tr=$depositId",
            expirySeconds = 900
        )
    }

    fun submitUtr(userId: String, depositId: String, utr: String): Boolean {
        val record = MemoryDataStore.deposits[depositId] ?: return false
        record["utr"] = utr
        record["status"] = "VERIFYING"
        return true
    }

    // --- WITHDRAWALS ---

    @Synchronized
    fun requestWithdrawal(userId: String, req: WithdrawalRequestPayload): Result<WithdrawalRecord> {
        val amountPaise = (req.amountRupees * 100).toLong()
        if (amountPaise < 10000) { // Min withdrawal ₹100
            return Result.failure(IllegalArgumentException("Minimum withdrawal is ₹100"))
        }

        val wallet = getWalletBalance(userId)
        val availablePaise = wallet.winningsPaise + wallet.depositPaise
        if (availablePaise < amountPaise) {
            return Result.failure(IllegalStateException("INSUFFICIENT_FUNDS"))
        }

        val wMap = MemoryDataStore.wallets[userId] ?: return Result.failure(IllegalStateException("Wallet error"))
        var rem = amountPaise
        val win = wMap["winnings"] ?: 0L
        val fromWin = minOf(win, rem)
        wMap["winnings"] = win - fromWin
        rem -= fromWin

        if (rem > 0) {
            val dep = wMap["deposit"] ?: 0L
            val fromDep = minOf(dep, rem)
            wMap["deposit"] = dep - fromDep
            rem -= fromDep
        }

        wMap["reserved"] = (wMap["reserved"] ?: 0L) + amountPaise

        val tdsPaise = if (amountPaise > 1000000) (amountPaise * 0.30).toLong() else 0L
        val netPaise = amountPaise - tdsPaise
        val withdrawalId = "WTH_" + System.currentTimeMillis() + "_" + (1000..9999).random()

        val riskProfile = com.ingames.admin.RiskScoringService.getRiskProfile(userId)
        val initialStatus = if (riskProfile.riskLevel == com.ingames.admin.RiskLevel.CRITICAL) "RISK_LOCKED" else "PENDING"

        val record = WithdrawalRecord(
            id = UUID.randomUUID().toString(),
            withdrawalId = withdrawalId,
            grossAmountRupees = req.amountRupees,
            tdsDeductedRupees = tdsPaise / 100.0,
            netAmountRupees = netPaise / 100.0,
            payoutMethod = req.payoutMethod,
            status = initialStatus,
            createdAt = java.time.Instant.now().toString()
        )

        MemoryDataStore.withdrawals[withdrawalId] = mutableMapOf(
            "id" to record.id,
            "withdrawal_id" to record.withdrawalId,
            "user_id" to userId,
            "gross_rupees" to record.grossAmountRupees,
            "tds_rupees" to record.tdsDeductedRupees,
            "net_rupees" to record.netAmountRupees,
            "method" to req.payoutMethod,
            "upi_id" to req.upiId,
            "bank_account" to req.bankAccountNumber,
            "status" to "PENDING",
            "created_at" to record.createdAt
        )

        return Result.success(record)
    }

    fun getTransactions(userId: String): List<LedgerTransaction> {
        return MemoryDataStore.ledger.values
            .filter { it["user_id"] == userId }
            .map {
                LedgerTransaction(
                    id = it["id"] as String,
                    userId = it["user_id"] as String,
                    type = it["type"] as TransactionType,
                    amountPaise = it["amount"] as Long,
                    balanceAfterPaise = it["balance_after"] as Long,
                    referenceType = it["reference_type"] as? String,
                    referenceId = it["reference_id"] as? String,
                    description = it["description"] as? String ?: "",
                    createdAt = it["created_at"] as String
                )
            }
            .sortedByDescending { it.createdAt }
    }
}
