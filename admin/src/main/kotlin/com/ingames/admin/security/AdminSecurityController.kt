package com.ingames.admin.security

class AdminSecurityController(private val service: AdminSecurityService) {
    fun handleRequest(): String = "security endpoint active"
}
