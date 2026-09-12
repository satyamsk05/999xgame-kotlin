package com.ingames.users

import com.ingames.database.DatabaseFactory
import com.ingames.database.MemoryDataStore
import com.ingames.models.UserProfile
import com.ingames.models.WalletBalance
import java.util.UUID

object UserRepository {

    fun getOrCreateUserByPhone(phone: String): UserProfile {
        if (DatabaseFactory.isConnectedToPostgres) {
            try {
                return DatabaseFactory.withConnection { conn ->
                    val select = conn.prepareStatement("SELECT * FROM users WHERE phone = ?")
                    select.setString(1, phone)
                    val rs = select.executeQuery()
                    if (rs.next()) {
                        val userId = rs.getString("id")
                        val name = (try { rs.getString("name") } catch (e: Exception) { null })
                            ?: (try { rs.getString("username") } catch (e: Exception) { null })
                            ?: "Player"
                        val avatar = (try { rs.getString("avatar") } catch (e: Exception) { null })
                            ?: (try { rs.getString("avatar_path") } catch (e: Exception) { null })
                            ?: "avatar_1"
                        val role = (try { rs.getString("role") } catch (e: Exception) { null }) ?: "USER"
                        val isBlocked = try { rs.getBoolean("is_blocked") } catch (e: Exception) { false }
                        UserProfile(
                            id = userId,
                            phone = phone,
                            name = name,
                            avatar = avatar,
                            role = role,
                            isBlocked = isBlocked
                        )
                    } else {
                        val newId = UUID.randomUUID().toString()
                        val insertUser = conn.prepareStatement(
                            "INSERT INTO users (id, phone, username, name, avatar, role, created_at) VALUES (?, ?, ?, ?, ?, 'USER', NOW())"
                        )
                        insertUser.setString(1, newId)
                        insertUser.setString(2, phone)
                        insertUser.setString(3, "Player_${phone.takeLast(4)}")
                        insertUser.setString(4, "Player_${phone.takeLast(4)}")
                        insertUser.setString(5, "avatar_1")
                        insertUser.executeUpdate()

                        val insertWallet = conn.prepareStatement(
                            "INSERT INTO wallets (id, user_id, deposit_balance, winnings_balance, bonus_balance, reserved_balance, updated_at) VALUES (?, ?, 0, 0, 0, 0, NOW())"
                        )
                        insertWallet.setString(1, UUID.randomUUID().toString())
                        insertWallet.setString(2, newId)
                        insertWallet.executeUpdate()

                        UserProfile(
                            id = newId,
                            phone = phone,
                            name = "Player_${phone.takeLast(4)}",
                            avatar = "avatar_1"
                        )
                    }
                }
            } catch (e: Exception) {
                return getMemoryUserByPhone(phone)
            }
        } else {
            return getMemoryUserByPhone(phone)
        }
    }

    private fun getMemoryUserByPhone(phone: String): UserProfile {
        val existing = MemoryDataStore.users.values.find { it["phone"] == phone }
        if (existing != null) {
            val userId = existing["id"] as String
            val wallet = MemoryDataStore.wallets.getOrPut(userId) {
                mutableMapOf("deposit" to 80000L, "winnings" to 45000L, "bonus" to 0L, "reserved" to 0L)
            }
            return UserProfile(
                id = userId,
                phone = phone,
                name = existing["name"] as? String ?: "Player",
                avatar = existing["avatar"] as? String ?: "avatar_1",
                role = existing["role"] as? String ?: "USER",
                isBlocked = existing["is_blocked"] as? Boolean ?: false,
                wallet = WalletBalance(
                    depositPaise = wallet["deposit"] ?: 0L,
                    winningsPaise = wallet["winnings"] ?: 0L,
                    bonusPaise = wallet["bonus"] ?: 0L,
                    reservedPaise = wallet["reserved"] ?: 0L,
                    totalPaise = (wallet["deposit"] ?: 0L) + (wallet["winnings"] ?: 0L) + (wallet["bonus"] ?: 0L)
                )
            )
        } else {
            val newId = "usr_" + UUID.randomUUID().toString().take(8)
            val userMap = mutableMapOf<String, Any?>(
                "id" to newId,
                "phone" to phone,
                "name" to "Player_${phone.takeLast(4)}",
                "avatar" to "avatar_1",
                "role" to "USER",
                "is_blocked" to false
            )
            MemoryDataStore.users[newId] = userMap
            MemoryDataStore.wallets[newId] = mutableMapOf(
                "deposit" to 80000L,
                "winnings" to 45000L,
                "bonus" to 0L,
                "reserved" to 0L
            )
            return UserProfile(
                id = newId,
                phone = phone,
                name = "Player_${phone.takeLast(4)}",
                avatar = "avatar_1",
                wallet = WalletBalance(depositPaise = 80000L, winningsPaise = 45000L, totalPaise = 125000L)
            )
        }
    }

    fun getUserById(userId: String): UserProfile? {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val select = conn.prepareStatement("SELECT * FROM users WHERE id = ?")
                    select.setString(1, userId)
                    val rs = select.executeQuery()
                    if (rs.next()) {
                        val name = (try { rs.getString("name") } catch (e: Exception) { null })
                            ?: (try { rs.getString("username") } catch (e: Exception) { null })
                            ?: "Player"
                        val avatar = (try { rs.getString("avatar") } catch (e: Exception) { null })
                            ?: (try { rs.getString("avatar_path") } catch (e: Exception) { null })
                            ?: "avatar_1"
                        val role = (try { rs.getString("role") } catch (e: Exception) { null }) ?: "USER"
                        val isBlocked = try { rs.getBoolean("is_blocked") } catch (e: Exception) { false }
                        UserProfile(
                            id = rs.getString("id"),
                            phone = rs.getString("phone"),
                            name = name,
                            avatar = avatar,
                            role = role,
                            isBlocked = isBlocked
                        )
                    } else null
                }
            } catch (e: Exception) {
                getMemoryUserById(userId)
            }
        } else {
            return getMemoryUserById(userId)
        }
    }

    private fun getMemoryUserById(userId: String): UserProfile? {
        val userMap = MemoryDataStore.users[userId] ?: return null
        val wallet = MemoryDataStore.wallets[userId] ?: mutableMapOf()
        val dep = wallet["deposit"] ?: 0L
        val win = wallet["winnings"] ?: 0L
        val bon = wallet["bonus"] ?: 0L
        val res = wallet["reserved"] ?: 0L
        return UserProfile(
            id = userId,
            phone = userMap["phone"] as String,
            name = userMap["name"] as? String ?: "Player",
            avatar = userMap["avatar"] as? String ?: "avatar_1",
            role = userMap["role"] as? String ?: "USER",
            isBlocked = userMap["is_blocked"] as? Boolean ?: false,
            wallet = WalletBalance(
                depositPaise = dep,
                winningsPaise = win,
                bonusPaise = bon,
                reservedPaise = res,
                totalPaise = dep + win + bon
            )
        )
    }

    fun updateAvatar(userId: String, avatar: String): Boolean {
        if (DatabaseFactory.isConnectedToPostgres) {
            return try {
                DatabaseFactory.withConnection { conn ->
                    val stmt = conn.prepareStatement("UPDATE users SET avatar = ? WHERE id = ?")
                    stmt.setString(1, avatar)
                    stmt.setString(2, userId)
                    stmt.executeUpdate() > 0
                }
            } catch (e: Exception) {
                val user = MemoryDataStore.users[userId] ?: return false
                user["avatar"] = avatar
                true
            }
        } else {
            val user = MemoryDataStore.users[userId] ?: return false
            user["avatar"] = avatar
            return true
        }
    }
}
