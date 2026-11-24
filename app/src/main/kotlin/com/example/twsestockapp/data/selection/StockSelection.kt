package com.example.twsestockapp.data.selection

/**
 * Application-level representation of one selected stock.
 *
 * This model is independent from Room and represents the selection
 * information required by the rest of the application.
 *
 * @property Code Stock code.
 * @property DisplayOrder One-based position in the user's selected-stock list.
 */
data class StockSelection(
    val Code: String,
    val DisplayOrder: Int
)
