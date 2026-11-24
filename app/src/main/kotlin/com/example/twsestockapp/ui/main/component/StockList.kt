package com.example.twsestockapp.ui.main.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Main selected-stock presentation.
 *
 * This entry point keeps MainScreen independent from Pager details.
 *
 * Persistent field selections are resolved here into concrete
 * StockPagerPage definitions before they reach StockPager.
 */
@Composable
internal fun StockList(
    stocks: List<Stock>,
    fieldSelections: List<StockFieldSelection>,
    modifier: Modifier = Modifier,
) {
    val pages =
        resolveStockPagerPages(
            selections =
                fieldSelections,
        )

    StockPager(
        stocks =
            stocks,
        pages =
            pages,
        modifier =
            modifier,
    )
}
