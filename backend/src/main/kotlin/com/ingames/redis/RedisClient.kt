package com.ingames.redis

@Suppress("UNUSED_PARAMETER")
class RedisClient {
    fun connect() {}
    fun get(key: String): String? = null
    fun set(key: String, value: String, ttlSeconds: Long = 3600) {}
}
