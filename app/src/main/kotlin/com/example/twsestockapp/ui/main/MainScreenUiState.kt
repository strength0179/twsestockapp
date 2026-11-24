package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection

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
        val allStocks: List<Stock>,
        val visibleStocks: List<Stock>,
        val selections: List<StockSelection>,
        val showSelectionPanel: Boolean,
        val fieldSelections:
            List<StockFieldSelection> =
            emptyList(),
    ) : MainScreenUiState
}
