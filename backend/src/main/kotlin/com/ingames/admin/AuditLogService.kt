package com.ingames.admin

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.AdminAuditLogEntry
import java.util.UUID

object AuditLogService {

    fun log(adminId: String?, action: String, target: String?, detailsJson: String? = null) {
        val id = "audit_" + UUID.randomUUID().toString().take(12)
        val now = java.time.Instant.now().toString()

        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement(
                        "INSERT INTO audit_logs (id, admin_id, action, target, details, created_at) VALUES (?, ?, ?, ?, ?::jsonb, NOW())"
                    )
                    stmt.setString(1, id)
                    stmt.setString(2, adminId)
                    stmt.setString(3, action)
                    stmt.setString(4, target)
                    stmt.setString(5, detailsJson ?: "{}")
                    stmt.executeUpdate()
                }
            } catch (e: Exception) {
                logMemory(id, adminId, action, target, detailsJson, now)
            }
        } else {
            logMemory(id, adminId, action, target, detailsJson, now)
        }
    }

    private fun logMemory(id: String, adminId: String?, action: String, target: String?, detailsJson: String?, now: String) {
        MemoryDataStore.auditLogs.add(
            AdminAuditLogEntry(
                id = id,
                adminId = adminId,
                action = action,
                target = target,
                detailsJson = detailsJson,
                createdAt = now
            )
        )
    }

    fun getLogs(limit: Int = 50): List<AdminAuditLogEntry> {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement("SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ?")
                    stmt.setInt(1, limit)
                    val rs = stmt.executeQuery()
                    val list = mutableListOf<AdminAuditLogEntry>()
                    while (rs.next()) {
                        list.add(
                            AdminAuditLogEntry(
                                id = rs.getString("id"),
                                adminId = rs.getString("admin_id"),
                                action = rs.getString("action"),
                                target = rs.getString("target"),
                                detailsJson = rs.getString("details"),
                                createdAt = rs.getString("created_at") ?: ""
                            )
                        )
                    }
                    list
                }
            } catch (e: Exception) {
                MemoryDataStore.auditLogs.takeLast(limit).reversed()
            }
        } else {
            return MemoryDataStore.auditLogs.takeLast(limit).reversed()
        }
    }
}
