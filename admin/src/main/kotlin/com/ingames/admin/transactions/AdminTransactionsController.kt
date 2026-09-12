package com.ingames.admin.transactions

class AdminTransactionsController(private val service: AdminTransactionsService) {
    fun handleRequest(): String = "transactions endpoint active"
}
