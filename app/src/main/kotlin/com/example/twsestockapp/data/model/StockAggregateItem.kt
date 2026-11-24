package com.example.twsestockapp.data.model

import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockBaseItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem

/**
 * Contract for an application-level stock object that aggregates
 * stock information from multiple TWSE API data sources.
 *
 * Implementations represent one logical stock identified by [Code].
 *
 * Each source item is nullable because the same stock is not assumed
 * to exist in every API response.
 *
 * Consumers should depend on this interface when they need access to
 * the original API source objects, rather than depending on a concrete
 * [Stock] implementation.
 *
 * The aggregate currently consists of:
 *
 * - [StockDayItem] for daily trading information.
 * - [StockDayAvgItem] for closing-price and monthly-average information.
 * - [BwibbuItem] for valuation information.
 *
 * This interface describes data composition only. Rules that select
 * or derive a canonical value from multiple sources belong to the
 * corresponding capability interface or implementation.
 */
interface StockAggregateItem : StockBaseItem {

    val day: StockDayItem?

    val average: StockDayAvgItem?

    val valuation: BwibbuItem?

    val hasDayData: Boolean
        get() = day != null

    val hasAverageData: Boolean
        get() = average != null

    val hasValuationData: Boolean
        get() = valuation != null
}