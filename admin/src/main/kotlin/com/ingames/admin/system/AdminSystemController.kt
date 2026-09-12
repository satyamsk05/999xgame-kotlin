package com.ingames.admin.system

class AdminSystemController(private val service: AdminSystemService) {
    fun handleRequest(): String = "system endpoint active"
}
