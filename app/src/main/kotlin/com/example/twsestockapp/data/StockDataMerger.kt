package com.example.twsestockapp.data

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem

/**
 * Combines the three TWSE datasets into application-level stock models.
 *
 * This is a full outer join by stock code:
 *
 * - a code appearing in any source is included
 * - matching source items are placed in the same Stock
 * - missing source items remain null
 *
 * Stock-name priority:
 *
 * 1. StockDayItem.Name
 * 2. StockDayAvgItem.Name
 * 3. BwibbuItem.Name
 *
 * No additional stock-code sorting is applied.
 */
internal fun mergeStocks(
    dayItems: List<StockDayItem>,
    averageItems: List<StockDayAvgItem>,
    valuationItems: List<BwibbuItem>,
): List<Stock> {
    val dayByCode =
        dayItems.associateBy { item ->
            item.Code
        }

    val averageByCode =
        averageItems.associateBy { item ->
            item.Code
        }

    val valuationByCode =
        valuationItems.associateBy { item ->
            item.Code
        }

    /*
     * LinkedHashSet preserves source insertion order:
     *
     * 1. STOCK_DAY_ALL codes
     * 2. additional STOCK_DAY_AVG_ALL codes
     * 3. additional BWIBBU_ALL codes
     */
    val allCodes =
        linkedSetOf<String>().apply {
            addAll(
                dayByCode.keys
            )

            addAll(
                averageByCode.keys
            )

            addAll(
                valuationByCode.keys
            )
        }

    return allCodes.map { code ->
        val day =
            dayByCode[
                code
            ]

        val average =
            averageByCode[
                code
            ]

        val valuation =
            valuationByCode[
                code
            ]

        Stock(
            Code =
                code,
            Name =
                firstNonBlankName(
                    day?.Name,
                    average?.Name,
                    valuation?.Name,
                ),
            day =
                day,
            average =
                average,
            valuation =
                valuation,
        )
    }
}

/**
 * Returns the first available non-blank stock name.
 */
private fun firstNonBlankName(
    vararg names: String?,
): String {
    return names
        .firstOrNull { name ->
            !name.isNullOrBlank()
        }
        .orEmpty()
}
