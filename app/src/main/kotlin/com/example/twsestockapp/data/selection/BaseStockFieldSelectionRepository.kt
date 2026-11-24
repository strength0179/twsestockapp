package com.example.twsestockapp.data.selection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Base implementation for [StockFieldSelectionRepository].
 *
 * This class owns repository-wide output invariants that must remain
 * consistent across production, in-memory, cached, or future
 * implementations:
 *
 * - selections are ordered by absolute DisplayOrder
 * - equal consecutive selection states are suppressed
 *
 * Storage-specific commands remain the responsibility of concrete
 * implementations.
 */
abstract class BaseStockFieldSelectionRepository(
    sourceSelections:
        Flow<List<StockFieldSelection>>,
) : StockFieldSelectionRepository {

    /**
     * Canonical observable repository state.
     *
     * Sorting here prevents consumers from depending on the ordering
     * behavior of a particular DAO or storage implementation.
     */
    final override val selections:
        Flow<List<StockFieldSelection>> =
        sourceSelections
            .map { selections ->
                selections.sortedBy { selection ->
                    selection.DisplayOrder
                }
            }
            .distinctUntilChanged()
}
