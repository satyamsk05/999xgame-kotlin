@file:Suppress("UNUSED_PARAMETER")
package com.ingames.redis

class RedisClient {
    fun connect() {}
    fun get(key: String): String? = null
    fun set(key: String, value: String, ttlSeconds: Long = 3600) {}
}
