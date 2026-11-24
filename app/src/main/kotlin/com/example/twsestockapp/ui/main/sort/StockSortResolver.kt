package com.example.twsestockapp.ui.main.sort

import com.example.twsestockapp.data.model.Stock
import java.math.BigDecimal

/**
 * Resolves the visible stock order for one [StockSortOption].
 *
 * Rules:
 *
 * - Default preserves the supplied order exactly
 * - valuation sorts are descending
 * - stocks without the selected valuation value are placed last
 * - equal values preserve their original relative order
 *
 * The resolver never mutates the supplied list.
 */
internal fun resolveStockSort(
    stocks: List<Stock>,
    option: StockSortOption,
): List<Stock> {

    if (
        option ==
        StockSortOption.Default
    ) {
        return stocks
    }

    return stocks
        .withIndex()
        .sortedWith(
            Comparator { left, right ->
                compareStocks(
                    left =
                        left.value,
                    right =
                        right.value,
                    leftOriginalIndex =
                        left.index,
                    rightOriginalIndex =
                        right.index,
                    option =
                        option,
                )
            }
        )
        .map { indexedStock ->
            indexedStock.value
        }
}

/**
 * Compares two stocks according to the selected valuation option.
 *
 * Null values are always placed after non-null values.
 */
private fun compareStocks(
    left: Stock,
    right: Stock,
    leftOriginalIndex: Int,
    rightOriginalIndex: Int,
    option: StockSortOption,
): Int {

    val leftValue =
        left.sortValue(
            option =
                option,
        )

    val rightValue =
        right.sortValue(
            option =
                option,
        )

    return when {
        leftValue == null &&
            rightValue == null ->
            leftOriginalIndex
                .compareTo(
                    rightOriginalIndex
                )

        leftValue == null ->
            1

        rightValue == null ->
            -1

        else -> {
            /*
             * Descending:
             *
             * compare right against left rather than left against
             * right.
             */
            val valueComparison =
                rightValue.compareTo(
                    leftValue
                )

            if (valueComparison != 0) {
                valueComparison
            } else {
                /*
                 * Equal valuation values preserve the incoming order.
                 */
                leftOriginalIndex
                    .compareTo(
                        rightOriginalIndex
                    )
            }
        }
    }
}

/**
 * Returns the numeric value associated with one sorting option.
 */
private fun Stock.sortValue(
    option: StockSortOption,
): BigDecimal? {

    return when (option) {
        StockSortOption.Default ->
            null

        StockSortOption.PeRatio ->
            valuation?.PEratio

        StockSortOption.DividendYield ->
            valuation?.DividendYield

        StockSortOption.PbRatio ->
            valuation?.PBratio
    }
}
