package com.ingames.app.data

import android.content.Context
import com.ingames.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object TokenManager {
    private const val PREFS = "ingames_auth"
    private const val TOKEN_KEY = "token"

    private var prefs: android.content.SharedPreferences? = null
    var token: String? = null
        private set
    var currentUser: UserProfile? = null
        private set

    val isLoggedIn: Boolean get() = !token.isNullOrBlank()

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        token = prefs?.getString(TOKEN_KEY, null)
    }

    fun setSession(authToken: String, user: UserProfile?) {
        token = authToken
        currentUser = user
        prefs?.edit()?.putString(TOKEN_KEY, authToken)?.apply()
    }

    fun clearSession() {
        token = null
        currentUser = null
        prefs?.edit()?.remove(TOKEN_KEY)?.apply()
    }
}

@Serializable
data class LogginTokenData(
    val success: Boolean = false,
    val token: String? = null,
    val verificationUrl: String? = null,
    val expiresAt: Long? = null,
    val message: String? = null
)

@Serializable
data class LogginStatusData(
    val success: Boolean = false,
    val status: String = "PENDING",
    val verifiedPhone: String? = null,
    val message: String? = null
)

@Serializable
data class LogginVerifyData(
    val success: Boolean = false,
    val status: String = "PENDING",
    val token: String? = null,
    val isNewUser: Boolean = false,
    val user: UserProfile? = null,
    val message: String? = null
)

object ApiClient {
    var baseUrl = "http://3.110.124.137:8080"

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(jsonConfig) }
        install(WebSockets)
    }

    private val _walletState = MutableStateFlow(
        WalletBalance(depositPaise = 0L, winningsPaise = 0L, totalPaise = 0L)
    )
    val walletState = _walletState.asStateFlow()

    fun updateWallet(wallet: WalletBalance) { _walletState.value = wallet }

    suspend fun createLogginToken(): LogginTokenData? {
        return try {
            val res: ApiResponse<LogginTokenData> = httpClient.post("$baseUrl/api/auth/loggin/create-token").body()
            if (res.status == "success") res.data else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun checkLogginStatus(token: String): LogginStatusData? {
        return try {
            val res: ApiResponse<LogginStatusData> = httpClient.get("$baseUrl/api/auth/loggin/status/$token").body()
            if (res.status == "success") res.data else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun verifyLoggin(token: String): LogginVerifyData? {
        return try {
            val res: ApiResponse<LogginVerifyData> = httpClient.post("$baseUrl/api/auth/loggin/verify") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("token" to token))
            }.body()
            if (res.status == "success") res.data else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun restoreSession(): Boolean {
        val authToken = TokenManager.token ?: return false
        return try {
            val res: ApiResponse<UserProfile> = httpClient.get("$baseUrl/api/user/profile") {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }.body()
            val user = res.data
            if (res.status == "success" && user != null) {
                TokenManager.setSession(authToken, user)
                user.wallet?.let { updateWallet(it) }
                true
            } else {
                TokenManager.clearSession()
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchConfig(): AppConfigData? = try {
        val res: ApiResponse<AppConfigData> = httpClient.get("$baseUrl/api/config").body(); res.data
    } catch (_: Exception) { AppConfigData() }

    suspend fun fetchOnlineTicker(): OnlineTickerData? = try {
        val res: ApiResponse<OnlineTickerData> = httpClient.get("$baseUrl/api/online-ticker").body(); res.data
    } catch (_: Exception) { OnlineTickerData() }

    suspend fun fetchBanners(): List<BannerItem> = try {
        val res: ApiResponse<List<BannerItem>> = httpClient.get("$baseUrl/api/banners").body(); res.data ?: emptyList()
    } catch (_: Exception) { emptyList() }

    suspend fun fetchGames(): List<GameInfo> = try {
        val res: ApiResponse<List<GameInfo>> = httpClient.get("$baseUrl/api/games").body(); res.data ?: emptyList()
    } catch (_: Exception) { emptyList() }

    suspend fun fetchBalance(): WalletBalance? = try {
        val res: ApiResponse<WalletBalance> = httpClient.get("$baseUrl/api/wallet/balance") {
            TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
        res.data?.also { updateWallet(it) }
    } catch (_: Exception) { null }

    suspend fun fetchTransactions(): List<LedgerTransaction> = try {
        val res: ApiResponse<List<LedgerTransaction>> = httpClient.get("$baseUrl/api/wallet/transactions") {
            TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
        res.data ?: emptyList()
    } catch (_: Exception) { emptyList() }

    suspend fun initiateDeposit(amountRupees: Double): InitiateDepositResponse? = try {
        val res: ApiResponse<InitiateDepositResponse> = httpClient.post("$baseUrl/api/deposits/initiate") {
            contentType(ContentType.Application.Json)
            TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            setBody(InitiateDepositRequest(amountRupees))
        }.body()
        res.data
    } catch (_: Exception) { null }

    suspend fun submitUtr(depositId: String, utr: String): Boolean = try {
        val res: ApiResponse<Map<String, String>> = httpClient.post("$baseUrl/api/deposits/submit-utr") {
            contentType(ContentType.Application.Json)
            TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            setBody(SubmitUtrRequest(depositId, utr))
        }.body()
        res.status == "success"
    } catch (_: Exception) { false }

    suspend fun requestWithdrawal(req: WithdrawalRequestPayload): WithdrawalRecord? = try {
        val res: ApiResponse<WithdrawalRecord> = httpClient.post("$baseUrl/api/withdrawals/request") {
            contentType(ContentType.Application.Json)
            TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            setBody(req)
        }.body()
        res.data
    } catch (_: Exception) { null }
}
