package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockSelection

/**
 * Complete stock collection shared by MainScreenViewModel tests.
 */
internal val MAIN_SCREEN_FAKE_STOCKS: List<Stock> =
    listOf(
        Stock(
            Code = "2317",
            Name = "鴻海"
        ),
        Stock(
            Code = "2330",
            Name = "台積電"
        ),
        Stock(
            Code = "2454",
            Name = "聯發科"
        )
    )

/**
 * Creates continuous one-based ordered selections.
 *
 * Example:
 *
 * selectionsOf(
 *     "2330",
 *     "2454",
 *     "2317"
 * )
 *
 * produces:
 *
 * 1 -> 2330
 * 2 -> 2454
 * 3 -> 2317
 */
internal fun selectionsOf(
    vararg codes: String
): List<StockSelection> {

    return codes.mapIndexed { index, code ->
        StockSelection(
            Code = code,
            DisplayOrder = index + 1
        )
    }
}
