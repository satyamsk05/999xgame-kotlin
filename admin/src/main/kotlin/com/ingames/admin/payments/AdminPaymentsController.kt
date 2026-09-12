package com.ingames.admin.payments

class AdminPaymentsController(private val service: AdminPaymentsService) {
    fun handleRequest(): String = "payments endpoint active"
}
