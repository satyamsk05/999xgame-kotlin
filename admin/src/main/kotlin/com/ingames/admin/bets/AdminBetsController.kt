package com.ingames.admin.bets

class AdminBetsController(private val service: AdminBetsService) {
    fun handleRequest(): String = "bets endpoint active"
}
