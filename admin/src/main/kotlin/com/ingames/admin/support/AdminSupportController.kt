package com.ingames.admin.support

class AdminSupportController(private val service: AdminSupportService) {
    fun handleRequest(): String = "support endpoint active"
}
