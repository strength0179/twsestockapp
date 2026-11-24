package com.example.twsestockapp.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Persistent record representing one selected stock.
 *
 * The existence of a row means that the stock is selected.
 *
 * [displayOrder] determines the stock position in the user's
 * selected-stock list. Order values start from 1.
 *
 * A default value of 0 is provided only to keep construction compatible
 * with callers that do not assign the order themselves. The DAO replaces
 * that value with the correct next order when selecting a stock.
 *
 * @property code Unique stock code.
 * @property displayOrder One-based display position.
 */
@Entity(
    tableName = "selected_stocks"
)
data class SelectedStockEntity(

    @PrimaryKey
    val code: String,

    val displayOrder: Int = 0
)
