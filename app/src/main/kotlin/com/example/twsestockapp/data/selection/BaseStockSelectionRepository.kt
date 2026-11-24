package com.example.twsestockapp.data.selection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Base implementation for [StockSelectionRepository].
 *
 * This class owns repository-wide output invariants shared by concrete
 * implementations:
 *
 * - selections are ordered by DisplayOrder
 * - equal consecutive selection states are suppressed
 *
 * Persistence-specific commands remain the responsibility of concrete
 * implementations.
 */
abstract class BaseStockSelectionRepository(
    sourceSelections:
        Flow<List<StockSelection>>,
) : StockSelectionRepository {

    /**
     * Canonical observable repository state.
     *
     * Sorting here prevents consumers from depending on ordering
     * behavior provided by one particular storage implementation.
     */
    final override val selections:
        Flow<List<StockSelection>> =
        sourceSelections
            .map { selections ->
                selections.sortedBy { selection ->
                    selection.DisplayOrder
                }
            }
            .distinctUntilChanged()
}
