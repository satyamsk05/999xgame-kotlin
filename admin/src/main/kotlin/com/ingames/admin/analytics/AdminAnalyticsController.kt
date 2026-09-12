package com.ingames.admin.analytics

class AdminAnalyticsController(private val service: AdminAnalyticsService) {
    fun handleRequest(): String = "analytics endpoint active"
}
