package com.ingames.admin.settings

class AdminSettingsController(private val service: AdminSettingsService) {
    fun handleRequest(): String = "settings endpoint active"
}
