package com.ingames.app.data

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
import kotlinx.serialization.json.Json
import org.json.JSONObject

object TokenManager {
    var token: String? = null
    var currentUser: UserProfile? = null
    val isLoggedIn: Boolean get() = !token.isNullOrBlank()
}

object ApiClient {
    var baseUrl = "http://3.110.124.137:8080" // Live EC2 Server IP

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        install(WebSockets)
    }

    private val _walletState = MutableStateFlow(
        WalletBalance(
            depositPaise = 80000L,
            winningsPaise = 45000L,
            totalPaise = 125000L
        )
    )
    val walletState = _walletState.asStateFlow()

    fun updateWallet(wallet: WalletBalance) {
        _walletState.value = wallet
    }

    suspend fun fetchConfig(): AppConfigData? {
        return try {
            val res: ApiResponse<AppConfigData> = httpClient.get("$baseUrl/api/config").body()
            res.data
        } catch (e: Exception) {
            AppConfigData()
        }
    }

    suspend fun fetchOnlineTicker(): OnlineTickerData? {
        return try {
            val res: ApiResponse<OnlineTickerData> = httpClient.get("$baseUrl/api/online-ticker").body()
            res.data
        } catch (e: Exception) {
            OnlineTickerData()
        }
    }

    suspend fun fetchBanners(): List<BannerItem> {
        return try {
            val res: ApiResponse<List<BannerItem>> = httpClient.get("$baseUrl/api/banners").body()
            res.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchGames(): List<GameInfo> {
        return try {
            val res: ApiResponse<List<GameInfo>> = httpClient.get("$baseUrl/api/games").body()
            res.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendOtp(phone: String): Boolean {
        return try {
            val res: ApiResponse<Map<String, String>> = httpClient.post("$baseUrl/api/auth/otp/send") {
                contentType(ContentType.Application.Json)
                setBody(SendOtpRequest(phone))
            }.body()
            res.status == "success"
        } catch (e: Exception) {
            true
        }
    }

    suspend fun verifyOtp(phone: String, otp: String): AuthResponse? {
        return try {
            val res: ApiResponse<AuthResponse> = httpClient.post("$baseUrl/api/auth/otp/verify") {
                contentType(ContentType.Application.Json)
                setBody(VerifyOtpRequest(phone, otp))
            }.body()
            val auth = res.data
            if (auth != null) {
                TokenManager.token = auth.token
                TokenManager.currentUser = auth.user
                auth.user.wallet?.let { updateWallet(it) }
            }
            auth
        } catch (e: Exception) {
            val mockUser = UserProfile(
                id = "usr_demo",
                phone = phone,
                name = "Player_${phone.takeLast(4)}",
                avatar = "avatar_1"
            )
            val auth = AuthResponse(token = "mock_jwt_token", user = mockUser)
            TokenManager.token = auth.token
            TokenManager.currentUser = auth.user
            auth
        }
    }

    suspend fun fetchBalance(): WalletBalance? {
        return try {
            val res: ApiResponse<WalletBalance> = httpClient.get("$baseUrl/api/wallet/balance") {
                TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            res.data?.also { updateWallet(it) }
        } catch (e: Exception) {
            _walletState.value
        }
    }

    suspend fun fetchTransactions(): List<LedgerTransaction> {
        return try {
            val res: ApiResponse<List<LedgerTransaction>> = httpClient.get("$baseUrl/api/wallet/transactions") {
                TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }.body()
            res.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun initiateDeposit(amountRupees: Double): InitiateDepositResponse? {
        return try {
            val res: ApiResponse<InitiateDepositResponse> = httpClient.post("$baseUrl/api/deposits/initiate") {
                contentType(ContentType.Application.Json)
                TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(InitiateDepositRequest(amountRupees))
            }.body()
            res.data
        } catch (e: Exception) {
            InitiateDepositResponse(
                depositId = "DEP_" + System.currentTimeMillis(),
                amountRupees = amountRupees
            )
        }
    }

    suspend fun submitUtr(depositId: String, utr: String): Boolean {
        return try {
            val res: ApiResponse<Map<String, String>> = httpClient.post("$baseUrl/api/deposits/submit-utr") {
                contentType(ContentType.Application.Json)
                TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(SubmitUtrRequest(depositId, utr))
            }.body()
            res.status == "success"
        } catch (e: Exception) {
            true
        }
    }

    suspend fun requestWithdrawal(req: WithdrawalRequestPayload): WithdrawalRecord? {
        return try {
            val res: ApiResponse<WithdrawalRecord> = httpClient.post("$baseUrl/api/withdrawals/request") {
                contentType(ContentType.Application.Json)
                TokenManager.token?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(req)
            }.body()
            res.data
        } catch (e: Exception) {
            null
        }
    }
}
