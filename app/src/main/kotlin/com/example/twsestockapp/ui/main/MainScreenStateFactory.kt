package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection

/**
 * Creates successful MainScreen presentation state.
 *
 * This factory owns the mapping and ordering rules used when repository
 * data becomes UI state.
 */
internal object MainScreenStateFactory {

    /**
     * Creates a successful UI state from stock data and both persistent
     * selection collections.
     */
    fun createSuccess(
        allStocks: List<Stock>,
        selections: List<StockSelection>,
        fieldSelections: List<StockFieldSelection>,
    ): MainScreenUiState.Success {

        val stocksByCode =
            allStocks.associateBy { stock ->
                stock.Code
            }

        val orderedSelections =
            selections.sortedBy { selection ->
                selection.DisplayOrder
            }

        val visibleStocks =
            orderedSelections.mapNotNull { selection ->
                stocksByCode[
                    selection.Code
                ]
            }

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
            showSelectionPanel =
                orderedSelections.isEmpty(),
        )
    }
}
