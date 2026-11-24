package com.example.twsestockapp.data.model

import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem

/**
 * Default implementation of [StockAggregateItem].
 *
 * Stock is the primary application data object used after the
 * individual TWSE API responses have been combined.
 */
data class Stock(
    override val Code: String,
    override val Name: String,

    override val day: StockDayItem? = null,
    override val average: StockDayAvgItem? = null,
    override val valuation: BwibbuItem? = null
) : StockAggregateItem