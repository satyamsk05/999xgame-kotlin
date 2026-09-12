package com.ingames.admin

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore

object AdminGameConfigService {

    fun updateGameConfig(
        adminId: String,
        gameId: String,
        minStakePaise: Long? = null,
        maxStakePaise: Long? = null,
        isEnabled: Boolean? = null
    ): Boolean {
        val details = "minStake: $minStakePaise, maxStake: $maxStakePaise, isEnabled: $isEnabled"
        AuditLogService.log(adminId, "UPDATE_GAME_CONFIG", gameId, details)

        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val statusStr = if (isEnabled == true) "LIVE" else if (isEnabled == false) "MAINTENANCE" else null
                    var sql = "UPDATE games SET "
                    val updates = mutableListOf<String>()
                    if (minStakePaise != null) updates.add("min_stake = $minStakePaise")
                    if (maxStakePaise != null) updates.add("max_stake = $maxStakePaise")
                    if (statusStr != null) updates.add("status = '$statusStr'")
                    if (updates.isEmpty()) return@withConnection true

                    sql += updates.joinToString(", ") + " WHERE id = ?"
                    val stmt = conn.prepareStatement(sql)
                    stmt.setString(1, gameId)
                    stmt.executeUpdate() > 0
                }
            } catch (e: Exception) {
                updateMemory(gameId, minStakePaise, maxStakePaise, isEnabled)
            }
        } else {
            return updateMemory(gameId, minStakePaise, maxStakePaise, isEnabled)
        }
    }

    private fun updateMemory(
        gameId: String,
        minStakePaise: Long?,
        maxStakePaise: Long?,
        isEnabled: Boolean?
    ): Boolean {
        val game = MemoryDataStore.games[gameId] ?: return false
        if (minStakePaise != null) game["min_stake"] = minStakePaise
        if (maxStakePaise != null) game["max_stake"] = maxStakePaise
        if (isEnabled != null) game["status"] = if (isEnabled) "LIVE" else "MAINTENANCE"
        return true
    }
}
