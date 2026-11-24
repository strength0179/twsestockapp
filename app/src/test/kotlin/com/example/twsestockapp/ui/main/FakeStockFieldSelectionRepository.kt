package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Observable in-memory implementation of
 * [StockFieldSelectionRepository] for ViewModel tests.
 *
 * The fake follows the same ordering rules as the production DAO:
 *
 * - every field remains stored when hidden
 * - DisplayOrder is the absolute persistent order
 * - IsVisible controls Pager visibility
 * - hide/show do not directly change DisplayOrder
 * - visible fields move relative to other visible fields
 * - hidden fields between moved visible fields participate in the
 *   absolute-order shift
 */
internal class FakeStockFieldSelectionRepository(
    initialSelections:
        List<StockFieldSelection>,
) : StockFieldSelectionRepository {

    private val selectionState =
        MutableStateFlow(
            normalizeInitialSelections(
                initialSelections
            )
        )

    override val selections:
        Flow<List<StockFieldSelection>> =
        selectionState

    /**
     * Compatibility operation.
     *
     * Existing fields are restored to visible at their existing
     * absolute position.
     *
     * Missing fields are appended to the end.
     */
    override suspend fun select(
        fieldKey: String,
    ) {
        val existingIndex =
            selectionState.value
                .indexOfFirst { selection ->
                    selection.FieldKey ==
                        fieldKey
                }

        if (existingIndex >= 0) {
            show(
                fieldKey =
                    fieldKey,
            )

            return
        }

        val nextOrder =
            (selectionState.value
                .maxOfOrNull { selection ->
                    selection.DisplayOrder
                } ?: 0) + 1

        selectionState.value =
            (
                selectionState.value +
                    StockFieldSelection(
                        FieldKey =
                            fieldKey,
                        DisplayOrder =
                            nextOrder,
                        IsVisible =
                            true,
                    )
            )
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
    }

    /**
     * Compatibility operation.
     *
     * Unselect now means hide.
     */
    override suspend fun unselect(
        fieldKey: String,
    ) {
        hide(
            fieldKey =
                fieldKey,
        )
    }

    /**
     * Hides one field while preserving its absolute DisplayOrder.
     *
     * The final visible field cannot be hidden.
     */
    override suspend fun hide(
        fieldKey: String,
    ) {
        val current =
            selectionState.value
                .firstOrNull { selection ->
                    selection.FieldKey ==
                        fieldKey
                }
                ?: return

        if (!current.IsVisible) {
            return
        }

        val visibleCount =
            selectionState.value
                .count { selection ->
                    selection.IsVisible
                }

        if (visibleCount <= 1) {
            return
        }

        selectionState.value =
            selectionState.value
                .map { selection ->
                    if (
                        selection.FieldKey ==
                        fieldKey
                    ) {
                        selection.copy(
                            IsVisible =
                                false,
                        )
                    } else {
                        selection
                    }
                }
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
    }

    /**
     * Restores one hidden field without changing its absolute order.
     */
    override suspend fun show(
        fieldKey: String,
    ) {
        selectionState.value =
            selectionState.value
                .map { selection ->
                    if (
                        selection.FieldKey ==
                        fieldKey
                    ) {
                        selection.copy(
                            IsVisible =
                                true,
                        )
                    } else {
                        selection
                    }
                }
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
    }

    /**
     * Moves a visible field before the previous visible field.
     *
     * Example:
     *
     * A1 B2(hidden) C3 D4
     *
     * moveForward(C)
     *
     * C1 A2 B3(hidden) D4
     */
    override suspend fun moveForward(
        fieldKey: String,
    ) {
        val ordered =
            selectionState.value
                .sortedBy { selection ->
                    selection.DisplayOrder
                }

        val current =
            ordered.firstOrNull { selection ->
                selection.FieldKey ==
                    fieldKey
            }
                ?: return

        if (!current.IsVisible) {
            return
        }

        val previousVisible =
            ordered
                .filter { selection ->
                    selection.IsVisible &&
                        selection.DisplayOrder <
                        current.DisplayOrder
                }
                .maxByOrNull { selection ->
                    selection.DisplayOrder
                }
                ?: return

        val targetOrder =
            previousVisible.DisplayOrder

        val currentOrder =
            current.DisplayOrder

        selectionState.value =
            ordered
                .map { selection ->
                    when {
                        selection.FieldKey ==
                            current.FieldKey -> {

                            selection.copy(
                                DisplayOrder =
                                    targetOrder,
                            )
                        }

                        selection.DisplayOrder >=
                            targetOrder &&
                            selection.DisplayOrder <
                            currentOrder -> {

                            selection.copy(
                                DisplayOrder =
                                    selection.DisplayOrder +
                                        1,
                            )
                        }

                        else ->
                            selection
                    }
                }
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
    }

    /**
     * Moves a visible field after the next visible field.
     *
     * Example:
     *
     * A1 B2(hidden) C3 D4
     *
     * moveBackward(A)
     *
     * B1(hidden) C2 A3 D4
     */
    override suspend fun moveBackward(
        fieldKey: String,
    ) {
        val ordered =
            selectionState.value
                .sortedBy { selection ->
                    selection.DisplayOrder
                }

        val current =
            ordered.firstOrNull { selection ->
                selection.FieldKey ==
                    fieldKey
            }
                ?: return

        if (!current.IsVisible) {
            return
        }

        val nextVisible =
            ordered
                .filter { selection ->
                    selection.IsVisible &&
                        selection.DisplayOrder >
                        current.DisplayOrder
                }
                .minByOrNull { selection ->
                    selection.DisplayOrder
                }
                ?: return

        val currentOrder =
            current.DisplayOrder

        val targetOrder =
            nextVisible.DisplayOrder

        selectionState.value =
            ordered
                .map { selection ->
                    when {
                        selection.FieldKey ==
                            current.FieldKey -> {

                            selection.copy(
                                DisplayOrder =
                                    targetOrder,
                            )
                        }

                        selection.DisplayOrder >
                            currentOrder &&
                            selection.DisplayOrder <=
                            targetOrder -> {

                            selection.copy(
                                DisplayOrder =
                                    selection.DisplayOrder -
                                        1,
                            )
                        }

                        else ->
                            selection
                    }
                }
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
    }

    /**
     * Replaces the complete test configuration.
     *
     * Every supplied key starts visible, matching the production
     * repository compatibility operation.
     */
    override suspend fun replaceAll(
        fieldKeys: List<String>,
    ) {
        selectionState.value =
            fieldKeys
                .distinct()
                .mapIndexed {
                        index,
                        fieldKey ->

                    StockFieldSelection(
                        FieldKey =
                            fieldKey,
                        DisplayOrder =
                            index + 1,
                        IsVisible =
                            true,
                    )
                }
    }

    override suspend fun clear() {
        selectionState.value =
            emptyList()
    }

    companion object {

        /**
         * Test configuration uses the supplied DisplayOrder as its
         * authoritative absolute order.
         *
         * Unlike the previous fake, this function must not re-number
         * selections because hidden fields retain their positions.
         */
        private fun normalizeInitialSelections(
            selections:
                List<StockFieldSelection>,
        ): List<StockFieldSelection> {

            return selections
                .sortedBy { selection ->
                    selection.DisplayOrder
                }
        }
    }
}
