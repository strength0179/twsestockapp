package com.example.twsestockapp.data.selection

import kotlinx.coroutines.flow.Flow

/**
 * Contract for persistent ordered stock selections.
 *
 * A [StockSelection] represents:
 *
 * - which stock is selected
 * - where that stock appears in the selected-stock list
 *
 * Implementations must not expose Room, DAO, entity, or SQLite types.
 */
interface StockSelectionRepository {

    /**
     * Observes persistent stock selections.
     *
     * Results must be ordered by [StockSelection.DisplayOrder].
     */
    val selections:
        Flow<List<StockSelection>>

    /**
     * Adds [code] to the persistent stock-selection list.
     *
     * A newly selected stock is appended to the end.
     */
    suspend fun select(
        code: String,
    )

    /**
     * Removes [code] from the persistent stock-selection list.
     *
     * Remaining selections are compacted by the persistence layer.
     */
    suspend fun unselect(
        code: String,
    )

    /**
     * Moves [code] one position toward the beginning.
     */
    suspend fun moveForward(
        code: String,
    )

    /**
     * Moves [code] one position toward the end.
     */
    suspend fun moveBackward(
        code: String,
    )

    /**
     * Removes every persistent stock selection.
     */
    suspend fun clear()
}
