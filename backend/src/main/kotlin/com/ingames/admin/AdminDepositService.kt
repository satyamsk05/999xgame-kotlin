package com.ingames.admin

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.DepositRecord

object AdminDepositService {

    fun getPendingDeposits(): List<DepositRecord> {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement(
                        "SELECT * FROM deposits WHERE status IN ('PENDING', 'UTR_SUBMITTED', 'VERIFYING') ORDER BY created_at ASC"
                    )
                    val rs = stmt.executeQuery()
                    val list = mutableListOf<DepositRecord>()
                    while (rs.next()) {
                        val amountPaise = rs.getLong("amount")
                        val amountRupees = amountPaise / 100.0
                        list.add(
                            DepositRecord(
                                id = rs.getString("id"),
                                depositId = rs.getString("deposit_id") ?: rs.getString("id"),
                                amountRupees = amountRupees,
                                status = rs.getString("status"),
                                utr = rs.getString("utr"),
                                createdAt = rs.getString("created_at") ?: ""
                            )
                        )
                    }
                    if (list.isNotEmpty()) list else getMemoryPendingDeposits()
                }
            } catch (e: Exception) {
                getMemoryPendingDeposits()
            }
        } else {
            return getMemoryPendingDeposits()
        }
    }

    private fun getMemoryPendingDeposits(): List<DepositRecord> {
        return MemoryDataStore.deposits.values
            .filter { it["status"] == "PENDING" || it["status"] == "UTR_SUBMITTED" || it["status"] == "VERIFYING" }
            .map {
                val paise = (it["amount_paise"] as? Long)
                    ?: ((it["amount_rupees"] as? Double)?.let { d -> (d * 100).toLong() })
                    ?: 0L
                DepositRecord(
                    id = it["id"] as String,
                    depositId = it["deposit_id"] as? String ?: (it["id"] as String),
                    amountRupees = paise / 100.0,
                    status = it["status"] as String,
                    utr = it["utr"] as? String,
                    createdAt = it["created_at"] as? String ?: ""
                )
            }
    }

    fun approveDeposit(adminId: String, depositId: String, note: String? = null): Boolean {
        AuditLogService.log(adminId, "APPROVE_DEPOSIT", depositId, "note: $note")
        val memSuccess = approveMemory(depositId)

        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                DatabaseFactory.withConnection { conn ->
                    val select = conn.prepareStatement("SELECT user_id, amount FROM deposits WHERE deposit_id = ? OR id = ?")
                    select.setString(1, depositId)
                    select.setString(2, depositId)
                    val rs = select.executeQuery()
                    if (rs.next()) {
                        val userId = rs.getString("user_id")
                        val amountPaise = rs.getLong("amount")

                        val credit = conn.prepareStatement(
                            "UPDATE wallets SET deposit_balance = deposit_balance + ?, updated_at = NOW() WHERE user_id = ?"
                        )
                        credit.setLong(1, amountPaise)
                        credit.setString(2, userId)
                        credit.executeUpdate()

                        val update = conn.prepareStatement(
                            "UPDATE deposits SET status = 'CONFIRMED', admin_id = ?, admin_note = ?, confirmed_at = NOW(), updated_at = NOW() WHERE deposit_id = ? OR id = ?"
                        )
                        update.setString(1, adminId)
                        update.setString(2, note ?: "Approved by admin")
                        update.setString(3, depositId)
                        update.setString(4, depositId)
                        update.executeUpdate()
                    } else {
                        val memRecord = MemoryDataStore.deposits[depositId]
                        val userId = memRecord?.get("user_id") as? String
                        val amountPaise = (memRecord?.get("amount_paise") as? Long)
                            ?: ((memRecord?.get("amount_rupees") as? Double)?.let { d -> (d * 100).toLong() })
                            ?: 0L
                        if (userId != null && amountPaise > 0L) {
                            val credit = conn.prepareStatement(
                                "UPDATE wallets SET deposit_balance = deposit_balance + ?, updated_at = NOW() WHERE user_id = ?"
                            )
                            credit.setLong(1, amountPaise)
                            credit.setString(2, userId)
                            credit.executeUpdate()
                        }
                    }
                    Unit
                }
            } catch (e: Exception) {
                // Ignore DB error
            }
        }
        return memSuccess
    }

    private fun approveMemory(depositId: String): Boolean {
        val record = MemoryDataStore.deposits[depositId] ?: return false
        val userId = record["user_id"] as? String ?: return false
        val amountPaise = (record["amount_paise"] as? Long)
            ?: ((record["amount_rupees"] as? Double)?.let { d -> (d * 100).toLong() })
            ?: 0L

        val wallet = MemoryDataStore.wallets.getOrPut(userId) {
            mutableMapOf("deposit" to 0L, "winnings" to 0L, "bonus" to 0L, "reserved" to 0L)
        }
        wallet["deposit"] = (wallet["deposit"] ?: 0L) + amountPaise
        record["status"] = "CONFIRMED"
        return true
    }

    fun rejectDeposit(adminId: String, depositId: String, reason: String? = null): Boolean {
        AuditLogService.log(adminId, "REJECT_DEPOSIT", depositId, "reason: $reason")
        val memSuccess = rejectMemory(depositId)

        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                DatabaseFactory.withConnection { conn ->
                    val update = conn.prepareStatement(
                        "UPDATE deposits SET status = 'REJECTED', admin_id = ?, admin_note = ?, rejected_at = NOW(), updated_at = NOW() WHERE deposit_id = ? OR id = ?"
                    )
                    update.setString(1, adminId)
                    update.setString(2, reason ?: "Rejected by admin")
                    update.setString(3, depositId)
                    update.setString(4, depositId)
                    update.executeUpdate()
                }
            } catch (e: Exception) {
                // Ignore DB error
            }
        }
        return memSuccess
    }

    private fun rejectMemory(depositId: String): Boolean {
        val record = MemoryDataStore.deposits[depositId] ?: return false
        record["status"] = "REJECTED"
        return true
    }
}
