package com.example.twsestockapp.ui.main.component

import java.math.BigDecimal

/**
 * Presentation direction of one stock's price change.
 *
 * This type intentionally contains no Compose Color dependency so the
 * direction rule can be tested as pure Kotlin.
 */
internal enum class StockChangeDirection {
    Positive,
    Negative,
    Neutral,
}

/**
 * Resolves the presentation direction for a stock price change.
 *
 * Rules:
 *
 * - value > 0  -> Positive
 * - value < 0  -> Negative
 * - value == 0 -> Neutral
 * - null       -> Neutral
 */
internal fun resolveStockChangeDirection(
    value: BigDecimal?,
): StockChangeDirection {

    if (value == null) {
        return StockChangeDirection.Neutral
    }

    return when {
        value > BigDecimal.ZERO ->
            StockChangeDirection.Positive

        value < BigDecimal.ZERO ->
            StockChangeDirection.Negative

        else ->
            StockChangeDirection.Neutral
    }
}
