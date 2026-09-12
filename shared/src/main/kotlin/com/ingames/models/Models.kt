package com.ingames.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: String = "success",
    val data: T? = null,
    val code: String? = null,
    val message: String? = null,
    val requestId: String? = null
)

@Serializable
data class AppConfigData(
    val onlineUsers: Int = 1250,
    val maintenanceMode: Boolean = false,
    val minimumAppVersion: String = "1.0.0"
)

@Serializable
data class OnlineTickerData(
    val totalOnline: Int = 1250,
    val label: String = "online",
    val formattedText: String = "1,250 online",
    val ringColors: List<String> = listOf("#FFC107", "#FF9800", "#4FC3F7"),
    val avatars: List<String> = listOf(
        "avatars/avatar_1.png",
        "avatars/avatar_2.png",
        "avatars/avatar_3.png",
        "avatars/avatar_7.png",
        "avatars/avatar_8.png",
        "avatars/avatar_9.png"
    ),
    val isLive: Boolean = true
)

@Serializable
data class BannerItem(
    val id: String = "",
    val tag: String = "DEPOSIT",
    val title: String = "100% Welcome Bonus",
    val subtitle: String = "DEPOSIT -> GET BONUS",
    val buttonText: String = "DEPOSIT NOW",
    val imageUrl: String = "banners/deposit_banner.png",
    val targetScreen: String = "/add-cash"
)

// --- AUTH & USER ---

@Serializable
data class SendOtpRequest(
    val phone: String
)

@Serializable
data class VerifyOtpRequest(
    val phone: String,
    val otp: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String? = null,
    val user: UserProfile
)

@Serializable
data class UserProfile(
    val id: String,
    val phone: String,
    val name: String = "Player",
    val avatar: String = "avatar_1",
    val role: String = "USER",
    val isBlocked: Boolean = false,
    val wallet: WalletBalance? = null,
    val createdAt: String? = null
)

@Serializable
data class UserStats(
    val totalGamesPlayed: Int = 0,
    val totalWonAmountPaise: Long = 0,
    val referralCount: Int = 0,
    val referralEarningsPaise: Long = 0
)

@Serializable
data class UpdateAvatarRequest(
    val avatar: String
)

// --- WALLET & MONEY (Integer Paise: 100 paise = 1 INR) ---

@Serializable
data class WalletBalance(
    val depositPaise: Long = 0,
    val winningsPaise: Long = 0,
    val bonusPaise: Long = 0,
    val reservedPaise: Long = 0,
    val totalPaise: Long = 0
) {
    val depositRupees: Double get() = depositPaise / 100.0
    val winningsRupees: Double get() = winningsPaise / 100.0
    val bonusRupees: Double get() = bonusPaise / 100.0
    val totalRupees: Double get() = totalPaise / 100.0
}

@Serializable
enum class TransactionType {
    DEPOSIT,
    DEPOSIT_BONUS,
    WITHDRAWAL_REQUEST,
    WITHDRAWAL_SUCCESS,
    WITHDRAWAL_REJECT,
    GAME_BET,
    GAME_WIN,
    REFERRAL_REWARD,
    ADMIN_ADJUSTMENT
}

@Serializable
data class LedgerTransaction(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val amountPaise: Long,
    val balanceAfterPaise: Long,
    val referenceType: String? = null,
    val referenceId: String? = null,
    val description: String = "",
    val createdAt: String
)

@Serializable
data class InitiateDepositRequest(
    val amountRupees: Double,
    val paymentMethod: String = "UPI_MANUAL"
)

@Serializable
data class InitiateDepositResponse(
    val depositId: String,
    val amountRupees: Double,
    val upiId: String = "pay@upi",
    val qrCodeUrl: String? = null,
    val expirySeconds: Int = 900
)

@Serializable
data class SubmitUtrRequest(
    val depositId: String,
    val utr: String
)

@Serializable
data class DepositRecord(
    val id: String,
    val depositId: String,
    val amountRupees: Double,
    val status: String,
    val utr: String? = null,
    val createdAt: String
)

@Serializable
data class WithdrawalRequestPayload(
    val amountRupees: Double,
    val payoutMethod: String = "UPI", // "UPI" or "BANK"
    val upiId: String? = null,
    val bankAccountNumber: String? = null,
    val ifscCode: String? = null,
    val accountHolderName: String? = null
)

@Serializable
data class WithdrawalRecord(
    val id: String,
    val withdrawalId: String,
    val grossAmountRupees: Double,
    val tdsDeductedRupees: Double,
    val netAmountRupees: Double,
    val payoutMethod: String,
    val status: String, // PENDING, APPROVED, REJECTED
    val createdAt: String
)

// --- GAMES & PROVABLY FAIR ---

@Serializable
enum class RoundStatus {
    CREATED,
    BETTING_OPEN,
    BETTING_CLOSED,
    RESULT,
    SETTLING,
    SETTLED
}

@Serializable
data class GameInfo(
    val id: String,
    val title: String,
    val description: String = "",
    val imagePath: String,
    val accentColorHex: String = "#FFD700",
    val route: String,
    val activePlayers: Int = 120,
    val isEnabled: Boolean = true
)

@Serializable
data class ProvablyFairData(
    val serverSeedHash: String,
    val serverSeed: String? = null, // Only revealed after settlement
    val roundNumber: Long
)

// 1. Seven Up Down
@Serializable
enum class SevenUpDownBetArea {
    DOWN,    // 2 to 6 (Pays 2x)
    SEVEN,   // Exactly 7 (Pays 5x)
    UP       // 8 to 12 (Pays 2x)
}

@Serializable
data class SevenUpDownState(
    val roundId: String,
    val roundNumber: Long,
    val status: RoundStatus,
    val serverSeedHash: String,
    val remainingMs: Long = 0,
    val dice: List<Int>? = null,
    val diceTotal: Int? = null,
    val winningArea: SevenUpDownBetArea? = null,
    val recentOutcomes: List<Int> = emptyList(),
    val totalBetsCount: Int = 0
)

@Serializable
data class SevenUpDownBetRequest(
    val roundId: String,
    val area: SevenUpDownBetArea,
    val amountPaise: Long,
    val idempotencyKey: String
)

// 2. Crush / Crash
@Serializable
data class CrushState(
    val roundId: String,
    val roundNumber: Long,
    val status: RoundStatus,
    val serverSeedHash: String,
    val currentMultiplier: Double = 1.00,
    val crashPoint: Double? = null, // Only revealed once crashed
    val remainingMs: Long = 0,
    val recentCrashPoints: List<Double> = emptyList()
)

@Serializable
data class CrushBetRequest(
    val roundId: String,
    val amountPaise: Long,
    val autoCashoutMultiplier: Double? = null,
    val idempotencyKey: String
)

@Serializable
data class CrushCashoutRequest(
    val roundId: String,
    val idempotencyKey: String
)

// 3. Dragon Tiger
@Serializable
enum class DragonTigerBetArea {
    DRAGON,  // Pays 1.95x / 2.0x
    TIGER,   // Pays 1.95x / 2.0x
    TIE      // Pays 8.0x
}

@Serializable
data class Card(
    val rank: String, // "A", "2".."10", "J", "Q", "K"
    val suit: String, // "SPADES", "HEARTS", "DIAMONDS", "CLUBS"
    val value: Int    // 1 to 13
)

@Serializable
data class DragonTigerState(
    val roundId: String,
    val roundNumber: Long,
    val status: RoundStatus,
    val serverSeedHash: String,
    val remainingMs: Long = 0,
    val dragonCard: Card? = null,
    val tigerCard: Card? = null,
    val winningArea: DragonTigerBetArea? = null,
    val recentOutcomes: List<DragonTigerBetArea> = emptyList()
)

@Serializable
data class DragonTigerBetRequest(
    val roundId: String,
    val area: DragonTigerBetArea,
    val amountPaise: Long,
    val idempotencyKey: String
)

// --- WEBSOCKET ENVELOPE ---

@Serializable
data class WsMessage(
    val action: String? = null,
    val event: String? = null,
    val gameId: String? = null,
    val room: String? = null,
    val data: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
