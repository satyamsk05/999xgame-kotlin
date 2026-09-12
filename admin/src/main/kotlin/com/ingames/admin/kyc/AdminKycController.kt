package com.ingames.admin.kyc

class AdminKycController(private val service: AdminKycService) {
    fun handleRequest(): String = "kyc endpoint active"
}
