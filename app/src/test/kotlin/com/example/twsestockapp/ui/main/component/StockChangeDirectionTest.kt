package com.example.twsestockapp.ui.main.component

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

/**
 * Pure JVM tests for stock price-change direction resolution.
 */
class StockChangeDirectionTest {

    @Test
    fun positiveValueResolvesPositive() {
        assertEquals(
            StockChangeDirection.Positive,
            resolveStockChangeDirection(
                value =
                    BigDecimal(
                        "1.25"
                    ),
            ),
        )
    }

    @Test
    fun negativeValueResolvesNegative() {
        assertEquals(
            StockChangeDirection.Negative,
            resolveStockChangeDirection(
                value =
                    BigDecimal(
                        "-1.25"
                    ),
            ),
        )
    }

    @Test
    fun zeroResolvesNeutral() {
        assertEquals(
            StockChangeDirection.Neutral,
            resolveStockChangeDirection(
                value =
                    BigDecimal.ZERO,
            ),
        )
    }

    @Test
    fun scaledZeroResolvesNeutral() {
        assertEquals(
            StockChangeDirection.Neutral,
            resolveStockChangeDirection(
                value =
                    BigDecimal(
                        "0.000"
                    ),
            ),
        )
    }

    @Test
    fun nullResolvesNeutral() {
        assertEquals(
            StockChangeDirection.Neutral,
            resolveStockChangeDirection(
                value =
                    null,
            ),
        )
    }
}
