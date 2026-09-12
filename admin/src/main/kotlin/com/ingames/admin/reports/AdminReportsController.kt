package com.ingames.admin.reports

class AdminReportsController(private val service: AdminReportsService) {
    fun handleRequest(): String = "reports endpoint active"
}
