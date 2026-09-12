package com.ingames.admin

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.WithdrawalRecord

object AdminWithdrawalService {

    fun getPendingWithdrawals(): List<WithdrawalRecord> {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement(
                        "SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY created_at ASC"
                    )
                    val rs = stmt.executeQuery()
                    val list = mutableListOf<WithdrawalRecord>()
                    while (rs.next()) {
                        val amountPaise = rs.getLong("amount")
                        val amountRupees = amountPaise / 100.0
                        list.add(
                            WithdrawalRecord(
                                id = rs.getString("id"),
                                withdrawalId = rs.getString("withdrawal_id") ?: rs.getString("id"),
                                grossAmountRupees = amountRupees,
                                tdsDeductedRupees = 0.0,
                                netAmountRupees = amountRupees,
                                payoutMethod = rs.getString("payout_method") ?: "UPI",
                                status = rs.getString("status"),
                                createdAt = rs.getString("created_at") ?: ""
                            )
                        )
                    }
                    if (list.isNotEmpty()) list else getMemoryPendingWithdrawals()
                }
            } catch (e: Exception) {
                getMemoryPendingWithdrawals()
            }
        } else {
            return getMemoryPendingWithdrawals()
        }
    }

    private fun getMemoryPendingWithdrawals(): List<WithdrawalRecord> {
        return MemoryDataStore.withdrawals.values
            .filter { it["status"] == "PENDING" }
            .map {
                val paise = (it["amount_paise"] as? Long)
                    ?: ((it["gross_amount_rupees"] as? Double)?.let { d -> (d * 100).toLong() })
                    ?: 0L
                val rupees = paise / 100.0
                WithdrawalRecord(
                    id = it["id"] as String,
                    withdrawalId = it["withdrawal_id"] as? String ?: (it["id"] as String),
                    grossAmountRupees = rupees,
                    tdsDeductedRupees = 0.0,
                    netAmountRupees = rupees,
                    payoutMethod = it["payout_method"] as? String ?: "UPI",
                    status = it["status"] as String,
                    createdAt = it["created_at"] as? String ?: ""
                )
            }
    }

    fun approveWithdrawal(adminId: String, withdrawalId: String, note: String? = null): Boolean {
        AuditLogService.log(adminId, "APPROVE_WITHDRAWAL", withdrawalId, "note: $note")
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement(
                        "UPDATE withdrawals SET status = 'SUCCESS', admin_id = ?, admin_note = ?, completed_at = NOW(), updated_at = NOW() WHERE withdrawal_id = ? OR id = ?"
                    )
                    stmt.setString(1, adminId)
                    stmt.setString(2, note ?: "Approved by admin")
                    stmt.setString(3, withdrawalId)
                    stmt.setString(4, withdrawalId)
                    val updated = stmt.executeUpdate() > 0
                    if (updated) true else approveMemory(withdrawalId)
                }
            } catch (e: Exception) {
                approveMemory(withdrawalId)
            }
        } else {
            return approveMemory(withdrawalId)
        }
    }

    private fun approveMemory(withdrawalId: String): Boolean {
        val record = MemoryDataStore.withdrawals[withdrawalId] ?: return false
        record["status"] = "SUCCESS"
        return true
    }

    fun rejectWithdrawal(adminId: String, withdrawalId: String, reason: String? = null): Boolean {
        AuditLogService.log(adminId, "REJECT_WITHDRAWAL", withdrawalId, "reason: $reason")
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val select = conn.prepareStatement("SELECT user_id, amount FROM withdrawals WHERE withdrawal_id = ? OR id = ?")
                    select.setString(1, withdrawalId)
                    select.setString(2, withdrawalId)
                    val rs = select.executeQuery()
                    if (rs.next()) {
                        val userId = rs.getString("user_id")
                        val amountPaise = rs.getLong("amount")

                        val refund = conn.prepareStatement(
                            "UPDATE wallets SET reserved_balance = GREATEST(0, reserved_balance - ?), deposit_balance = deposit_balance + ?, updated_at = NOW() WHERE user_id = ?"
                        )
                        refund.setLong(1, amountPaise)
                        refund.setLong(2, amountPaise)
                        refund.setString(3, userId)
                        refund.executeUpdate()

                        val update = conn.prepareStatement(
                            "UPDATE withdrawals SET status = 'REJECTED', admin_id = ?, admin_note = ?, rejected_at = NOW(), updated_at = NOW() WHERE withdrawal_id = ? OR id = ?"
                        )
                        update.setString(1, adminId)
                        update.setString(2, reason ?: "Rejected by admin")
                        update.setString(3, withdrawalId)
                        update.setString(4, withdrawalId)
                        update.executeUpdate() > 0
                    } else {
                        rejectMemory(withdrawalId)
                    }
                }
            } catch (e: Exception) {
                rejectMemory(withdrawalId)
            }
        } else {
            return rejectMemory(withdrawalId)
        }
    }

    private fun rejectMemory(withdrawalId: String): Boolean {
        val record = MemoryDataStore.withdrawals[withdrawalId] ?: return false
        val userId = record["user_id"] as? String ?: return false
        val amountPaise = (record["amount_paise"] as? Long)
            ?: ((record["gross_amount_rupees"] as? Double)?.let { d -> (d * 100).toLong() })
            ?: 0L

        val wallet = MemoryDataStore.wallets[userId]
        if (wallet != null) {
            val res = wallet["reserved"] ?: 0L
            val dep = wallet["deposit"] ?: 0L
            wallet["reserved"] = (res - amountPaise).coerceAtLeast(0L)
            wallet["deposit"] = dep + amountPaise
        }

        record["status"] = "REJECTED"
        return true
    }
}
