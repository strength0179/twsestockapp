package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.ui.main.sort.StockSortOption
import com.example.twsestockapp.ui.main.sort.resolveStockSort

/**
 * Creates successful MainScreen presentation state.
 *
 * This factory owns the mapping and ordering rules used when repository
 * data becomes UI state.
 *
 * Main-screen Filter sorting only affects visibleStocks. It must never
 * modify:
 *
 * - allStocks
 * - StockSelection.DisplayOrder
 * - StockFieldSelection.DisplayOrder
 */
internal object MainScreenStateFactory {

    /**
     * Creates a successful UI state from stock data, both persistent
     * selection collections, and the current stock sorting option.
     */
    fun createSuccess(
        allStocks: List<Stock>,
        selections: List<StockSelection>,
        fieldSelections:
            List<StockFieldSelection>,
        stockSortOption:
            StockSortOption =
            StockSortOption.Default,
    ): MainScreenUiState.Success {

        val stocksByCode =
            allStocks.associateBy { stock ->
                stock.Code
            }

        val orderedSelections =
            selections.sortedBy { selection ->
                selection.DisplayOrder
            }

        /*
         * Persistent selected-stock ordering is resolved first.
         *
         * The Filter may reorder this presentation collection, but it
         * does not change StockSelection.DisplayOrder.
         */
        val selectedStocks =
            orderedSelections.mapNotNull { selection ->
                stocksByCode[
                    selection.Code
                ]
            }

        val visibleStocks =
            resolveStockSort(
                stocks =
                    selectedStocks,
                option =
                    stockSortOption,
            )

        val orderedFieldSelections =
            fieldSelections.sortedBy { selection ->
                selection.DisplayOrder
            }

        return MainScreenUiState.Success(
            allStocks =
                allStocks,
            visibleStocks =
                visibleStocks,
            selections =
                orderedSelections,
            fieldSelections =
                orderedFieldSelections,
            stockSortOption =
                stockSortOption,
            showSelectionPanel =
                orderedSelections.isEmpty(),
        )
    }
}
