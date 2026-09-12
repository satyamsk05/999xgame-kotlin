package com.ingames.admin.dashboard

class AdminDashboardController(private val service: AdminDashboardService) {
    fun handleRequest(): String = "dashboard endpoint active"
}
