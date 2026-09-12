package com.ingames.admin

import com.ingames.database.MemoryDataStore
import kotlinx.serialization.Serializable

@Serializable
data class PlatformAnalyticsSummary(
    val totalDepositPaise: Long,
    val totalWithdrawalPaise: Long,
    val grossGamingRevenuePaise: Long, // Total Bets - Total Wins
    val netGamingRevenuePaise: Long,   // GGR - Bonuses
    val activeUsersCount: Int,
    val pendingWithdrawalsCount: Int,
    val riskLockedWithdrawalsCount: Int
)

object AdminAnalyticsService {

    fun getSummary(): PlatformAnalyticsSummary {
        var depPaise = 0L
        MemoryDataStore.deposits.values.forEach { dep ->
            if (dep["status"] == "CONFIRMED") {
                val paise = (dep["amount_paise"] as? Long)
                    ?: ((dep["amount_rupees"] as? Double)?.let { d -> (d * 100).toLong() })
                    ?: 0L
                depPaise += paise
            }
        }

        var wthPaise = 0L
        var pendingWth = 0
        var lockedWth = 0
        MemoryDataStore.withdrawals.values.forEach { w ->
            val status = w["status"] as? String
            val gross = ((w["gross_rupees"] as? Double) ?: 0.0) * 100
            if (status == "APPROVED" || status == "CONFIRMED") {
                wthPaise += gross.toLong()
            } else if (status == "PENDING") {
                pendingWth++
            } else if (status == "RISK_LOCKED") {
                lockedWth++
            }
        }

        var betsPaise = 0L
        var winsPaise = 0L
        MemoryDataStore.ledger.values.forEach { tx ->
            val type = tx["type"]?.toString()
            val amt = (tx["amount"] as? Long) ?: 0L
            if (type == "GAME_BET") {
                betsPaise += Math.abs(amt)
            } else if (type == "GAME_WIN") {
                winsPaise += amt
            }
        }

        val ggr = betsPaise - winsPaise
        val ngr = (ggr * 0.9).toLong() // Net after platform tax / promo deduction
        val activeUsers = MemoryDataStore.wallets.keys.size

        return PlatformAnalyticsSummary(
            totalDepositPaise = depPaise,
            totalWithdrawalPaise = wthPaise,
            grossGamingRevenuePaise = ggr,
            netGamingRevenuePaise = ngr,
            activeUsersCount = activeUsers,
            pendingWithdrawalsCount = pendingWth,
            riskLockedWithdrawalsCount = lockedWth
        )
    }
}
