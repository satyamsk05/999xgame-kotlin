package com.ingames.admin.notifications

class AdminNotificationsController(private val service: AdminNotificationsService) {
    fun handleRequest(): String = "notifications endpoint active"
}
