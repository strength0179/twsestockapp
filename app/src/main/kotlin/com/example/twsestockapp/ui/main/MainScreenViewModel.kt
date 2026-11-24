package com.example.twsestockapp.ui.main

import com.example.twsestockapp.ui.main.sort.StockSortOption
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract exposed by the main-screen ViewModel.
 *
 * UI components depend on this interface rather than a concrete
 * lifecycle or repository implementation.
 */
interface MainScreenViewModel {

    val uiState:
        StateFlow<MainScreenUiState>

    fun selectStock(
        code: String,
    )

    fun unselectStock(
        code: String,
    )

    fun moveStockForward(
        code: String,
    )

    fun moveStockBackward(
        code: String,
    )

    fun clearSelectedStocks()

    /**
     * Changes the presentation sorting used by the main stock Pager.
     *
     * This operation does not modify persistent StockSelection order.
     */
    fun selectStockSortOption(
        option: StockSortOption,
    )

    /**
     * Restores an existing hidden Pager field at its persistent
     * absolute DisplayOrder.
     */
    fun showStockField(
        fieldKey: String,
    )

    /**
     * Hides a Pager field while preserving its persistent row and
     * absolute DisplayOrder.
     */
    fun hideStockField(
        fieldKey: String,
    )

    /**
     * Moves a visible field before the previous visible field.
     */
    fun moveStockFieldForward(
        fieldKey: String,
    )

    /**
     * Moves a visible field after the next visible field.
     */
    fun moveStockFieldBackward(
        fieldKey: String,
    )

    /**
     * Replaces the complete field configuration.
     */
    fun replaceStockFields(
        fieldKeys: List<String>,
    )
}
