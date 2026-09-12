package com.ingames

import com.ingames.database.MemoryDataStore
import com.ingames.wallet.FinancialService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FinancialServiceTest {

    private val testUserId = "test_user_fin_1"

    @BeforeEach
    fun setup() {
        MemoryDataStore.wallets[testUserId] = mutableMapOf(
            "deposit" to 50000L,   // ₹500
            "winnings" to 20000L,  // ₹200
            "bonus" to 0L,
            "reserved" to 0L
        )
    }

    @Test
    fun `test initial wallet balance retrieval`() {
        val bal = FinancialService.getWalletBalance(testUserId)
        assertEquals(50000L, bal.depositPaise)
        assertEquals(20000L, bal.winningsPaise)
        assertEquals(70000L, bal.totalPaise)
        assertEquals(700.0, bal.totalRupees)
    }

    @Test
    fun `test atomic bet debit deducts deposit first`() {
        val idempotency = UUID.randomUUID().toString()
        val res = FinancialService.debitForBet(
            userId = testUserId,
            amountPaise = 10000L, // ₹100
            gameId = "seven_up_down",
            roundId = "round_123",
            idempotencyKey = idempotency
        )

        assertTrue(res.isSuccess)
        val bal = res.getOrThrow()
        assertEquals(40000L, bal.depositPaise) // 500 - 100 = 400
        assertEquals(20000L, bal.winningsPaise)
        assertEquals(60000L, bal.totalPaise)

        // Repeat with same idempotency key -> no double deduction
        val repeatRes = FinancialService.debitForBet(
            userId = testUserId,
            amountPaise = 10000L,
            gameId = "seven_up_down",
            roundId = "round_123",
            idempotencyKey = idempotency
        )
        assertTrue(repeatRes.isSuccess)
        assertEquals(60000L, repeatRes.getOrThrow().totalPaise)
    }

    @Test
    fun `test debit fails when insufficient funds`() {
        val res = FinancialService.debitForBet(
            userId = testUserId,
            amountPaise = 100000L, // ₹1000 (only ₹700 available)
            gameId = "seven_up_down",
            roundId = "round_124",
            idempotencyKey = UUID.randomUUID().toString()
        )
        assertTrue(res.isFailure)
        assertEquals("INSUFFICIENT_FUNDS", res.exceptionOrNull()?.message)
    }

    @Test
    fun `test credit winnings increases winnings balance and records ledger`() {
        val idempotency = UUID.randomUUID().toString()
        val res = FinancialService.creditWinnings(
            userId = testUserId,
            amountPaise = 20000L, // ₹200
            gameId = "seven_up_down",
            roundId = "round_125",
            idempotencyKey = idempotency
        )
        assertTrue(res.isSuccess)
        val bal = res.getOrThrow()
        assertEquals(50000L, bal.depositPaise)
        assertEquals(40000L, bal.winningsPaise) // 200 + 200 = 400
        assertEquals(90000L, bal.totalPaise)
    }

    @Test
    fun `test simultaneous dual financial requests result in exactly one debit`() {
        val idempotencyKey = "same_financial_request_123"
        val executor = Executors.newFixedThreadPool(2)
        val startLatch = java.util.concurrent.CountDownLatch(1)

        val task1 = java.util.concurrent.Callable {
            startLatch.await()
            FinancialService.debitForBet(
                userId = testUserId,
                amountPaise = 10000L, // ₹100
                gameId = "seven_up_down",
                roundId = "rnd_simultaneous",
                idempotencyKey = idempotencyKey
            )
        }

        val task2 = java.util.concurrent.Callable {
            startLatch.await()
            FinancialService.debitForBet(
                userId = testUserId,
                amountPaise = 10000L, // ₹100
                gameId = "seven_up_down",
                roundId = "rnd_simultaneous",
                idempotencyKey = idempotencyKey
            )
        }

        val future1 = executor.submit(task1)
        val future2 = executor.submit(task2)
        startLatch.countDown()

        val res1 = future1.get(5, TimeUnit.SECONDS)
        val res2 = future2.get(5, TimeUnit.SECONDS)

        assertTrue(res1.isSuccess)
        assertTrue(res2.isSuccess)

        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)

        val finalBal = FinancialService.getWalletBalance(testUserId)
        // Original 70,000 - 10,000 = 60,000 paise exactly once!
        assertEquals(60000L, finalBal.totalPaise)
    }
}
