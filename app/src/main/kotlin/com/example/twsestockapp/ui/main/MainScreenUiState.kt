package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.ui.main.sort.StockSortOption

/**
 * Complete presentation state for the main stock screen.
 */
sealed interface MainScreenUiState {

    data object Loading :
        MainScreenUiState

    data class Error(
        val throwable: Throwable,
    ) : MainScreenUiState

    data class Success(
        /**
         * Complete stock collection in its original application order.
         *
         * Sorting selected from the main-screen Filter must not modify
         * this collection.
         */
        val allStocks:
            List<Stock>,

        /**
         * Stocks currently shown by the main-screen Pager.
         *
         * This collection follows persistent stock selection first and
         * then applies [stockSortOption].
         */
        val visibleStocks:
            List<Stock>,

        /**
         * Persistent selected-stock order.
         *
         * Filter sorting must never modify DisplayOrder.
         */
        val selections:
            List<StockSelection>,

        val showSelectionPanel:
            Boolean,

        val fieldSelections:
            List<StockFieldSelection> =
            emptyList(),

        /**
         * Current main-screen stock sorting option.
         */
        val stockSortOption:
            StockSortOption =
            StockSortOption.Default,
    ) : MainScreenUiState
}
