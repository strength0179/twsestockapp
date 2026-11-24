package com.example.twsestockapp.ui.main.component

import androidx.compose.ui.graphics.Color

/**
 * Semantic colors used by stock-value presentation.
 *
 * Taiwan-market convention:
 *
 * - positive change -> red
 * - negative change -> green
 */
internal object StockValueColors {

    /**
     * Positive stock-price movement.
     */
    val Positive =
        Color(
            0xFFC62828
        )

    /**
     * Negative stock-price movement.
     */
    val Negative =
        Color(
            0xFF2E7D32
        )
}
