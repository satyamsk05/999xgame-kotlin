package com.ingames.games.common

import kotlinx.serialization.Serializable

@Serializable
enum class Currency {
    INR,
    USD,
    EUR
}

@Serializable
data class Money(
    val amount: Double,
    val currency: Currency = Currency.INR
) {
    operator fun plus(other: Money): Money = Money(amount + other.amount, currency)
    operator fun minus(other: Money): Money = Money(amount - other.amount, currency)
}
