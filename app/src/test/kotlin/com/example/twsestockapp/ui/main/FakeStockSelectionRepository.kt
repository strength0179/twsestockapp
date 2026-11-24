package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.data.selection.StockSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Observable in-memory implementation of
 * [StockSelectionRepository].
 *
 * Used by MainScreenViewModel unit tests.
 *
 * Ordering rules mirror the production repository contract:
 *
 * - selecting a new stock appends it to the end
 * - removing a stock compacts the remaining order
 * - moving forward swaps with the previous item
 * - moving backward swaps with the next item
 * - order values remain continuous and one-based
 */
internal class FakeStockSelectionRepository(
    initialSelections: List<StockSelection>
) : StockSelectionRepository {

    private val selectionState =
        MutableStateFlow(
            normalizeInitialSelections(
                initialSelections
            )
        )

    override val selections:
        Flow<List<StockSelection>> =
        selectionState

    /**
     * Appends a new stock to the end of the current selection order.
     *
     * Selecting an already-selected stock does nothing.
     */
    override suspend fun select(
        code: String
    ) {
        if (
            selectionState.value.any { selection ->
                selection.Code == code
            }
        ) {
            return
        }

        selectionState.value =
            reindexPreservingOrder(
                selectionState.value +
                    StockSelection(
                        Code = code,
                        DisplayOrder =
                            selectionState.value.size + 1
                    )
            )
    }

    /**
     * Removes one selected stock and compacts the remaining order.
     */
    override suspend fun unselect(
        code: String
    ) {
        selectionState.value =
            reindexPreservingOrder(
                selectionState.value.filterNot { selection ->
                    selection.Code == code
                }
            )
    }

    /**
     * Moves one selected stock toward the beginning.
     *
     * The first stock cannot move further forward.
     */
    override suspend fun moveForward(
        code: String
    ) {
        val ordered =
            selectionState.value
                .toMutableList()

        val index =
            ordered.indexOfFirst { selection ->
                selection.Code == code
            }

        if (index <= 0) {
            return
        }

        val previous =
            ordered[index - 1]

        ordered[index - 1] =
            ordered[index]

        ordered[index] =
            previous

        selectionState.value =
            reindexPreservingOrder(
                ordered
            )
    }

    /**
     * Moves one selected stock toward the end.
     *
     * The final stock cannot move further backward.
     */
    override suspend fun moveBackward(
        code: String
    ) {
        val ordered =
            selectionState.value
                .toMutableList()

        val index =
            ordered.indexOfFirst { selection ->
                selection.Code == code
            }

        if (
            index < 0 ||
            index >= ordered.lastIndex
        ) {
            return
        }

        val next =
            ordered[index + 1]

        ordered[index + 1] =
            ordered[index]

        ordered[index] =
            next

        selectionState.value =
            reindexPreservingOrder(
                ordered
            )
    }

    /**
     * Removes every selected stock.
     */
    override suspend fun clear() {
        selectionState.value =
            emptyList()
    }

    companion object {

        /**
         * Normalizes externally supplied starting data.
         *
         * Initial selections may be provided in arbitrary list order,
         * therefore DisplayOrder determines their initial ordering.
         */
        private fun normalizeInitialSelections(
            selections: List<StockSelection>
        ): List<StockSelection> {

            return reindexPreservingOrder(
                selections.sortedBy { selection ->
                    selection.DisplayOrder
                }
            )
        }

        /**
         * Assigns continuous one-based DisplayOrder values while
         * preserving the current list order.
         *
         * This function intentionally does not sort.
         *
         * Sorting here would undo a moveForward or moveBackward after
         * the list positions have already been exchanged.
         */
        private fun reindexPreservingOrder(
            selections: List<StockSelection>
        ): List<StockSelection> {

            return selections.mapIndexed { index, selection ->
                selection.copy(
                    DisplayOrder = index + 1
                )
            }
        }
    }
}
