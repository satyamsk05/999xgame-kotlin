package com.ingames.admin.cms

class AdminCmsController(private val service: AdminCmsService) {
    fun handleRequest(): String = "cms endpoint active"
}
