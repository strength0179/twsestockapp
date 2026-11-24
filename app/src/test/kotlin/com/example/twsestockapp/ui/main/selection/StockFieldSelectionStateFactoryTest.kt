package com.example.twsestockapp.ui.main.selection

import com.example.twsestockapp.data.selection.StockFieldSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM tests for Pager-field editor state conversion.
 */
class StockFieldSelectionStateFactoryTest {

    /**
     * Persistent:
     *
     * closingPrice 1 visible
     * change       2 hidden
     * tradeVolume  3 visible
     * peRatio      4 visible
     *
     * Editor:
     *
     * closingPrice 1
     * tradeVolume  2
     * peRatio      3
     * change       x
     */
    @Test
    fun hiddenFieldsMoveToBottomAndVisibleOrderIsRenumbered() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )

        assertEquals(
            listOf(
                "closingPrice",
                "tradeVolume",
                "peRatio",
                "change",
            ),
            states.map { state ->
                state.page.key
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
                null,
            ),
            states.map { state ->
                state.visibleDisplayOrder
            },
        )
    }

    /**
     * Presentation order does not modify absolute persistent order.
     */
    @Test
    fun persistentAbsoluteOrdersRemainUnchanged() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )

        assertEquals(
            listOf(
                1,
                3,
                4,
                2,
            ),
            states.map { state ->
                state.selection.DisplayOrder
            },
        )
    }

    @Test
    fun hiddenFieldHasNoVisibleDisplayOrder() {
        val hidden =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )
                .last()

        assertFalse(
            hidden.isVisible
        )

        assertNull(
            hidden.visibleDisplayOrder
        )

        assertTrue(
            hidden.canShow
        )

        assertFalse(
            hidden.canHide
        )
    }

    @Test
    fun hiddenFieldMovementIsDisabled() {
        val hidden =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )
                .last()

        assertFalse(
            hidden.canMoveForward
        )

        assertFalse(
            hidden.canMoveBackward
        )
    }

    @Test
    fun visibleMovementUsesVisibleOrderNotAbsoluteOrder() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )

        val first =
            states[0]

        val middle =
            states[1]

        val lastVisible =
            states[2]

        assertFalse(
            first.canMoveForward
        )

        assertTrue(
            first.canMoveBackward
        )

        assertTrue(
            middle.canMoveForward
        )

        assertTrue(
            middle.canMoveBackward
        )

        assertTrue(
            lastVisible.canMoveForward
        )

        assertFalse(
            lastVisible.canMoveBackward
        )
    }

    @Test
    fun finalVisibleFieldCannotBeHidden() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    listOf(
                        field(
                            fieldKey =
                                "closingPrice",
                            absoluteOrder =
                                1,
                            isVisible =
                                true,
                        ),
                        field(
                            fieldKey =
                                "change",
                            absoluteOrder =
                                2,
                            isVisible =
                                false,
                        ),
                    ),
            )

        val visible =
            states.first()

        assertTrue(
            visible.isVisible
        )

        assertFalse(
            visible.canHide
        )
    }

    @Test
    fun visibleFieldsCanBeHiddenWhenMoreThanOneIsVisible() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )

        val visibleStates =
            states.filter { state ->
                state.isVisible
            }

        assertTrue(
            visibleStates.all { state ->
                state.canHide
            }
        )
    }

    @Test
    fun unknownPersistedFieldIsIgnored() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    listOf(
                        field(
                            fieldKey =
                                "closingPrice",
                            absoluteOrder =
                                1,
                        ),
                        field(
                            fieldKey =
                                "unknownField",
                            absoluteOrder =
                                2,
                        ),
                        field(
                            fieldKey =
                                "tradeVolume",
                            absoluteOrder =
                                3,
                        ),
                    ),
            )

        assertEquals(
            listOf(
                "closingPrice",
                "tradeVolume",
            ),
            states.map { state ->
                state.page.key
            },
        )

        /*
         * UI visible order is compact even though the persistent
         * absolute order contains a gap caused by the unknown row.
         */
        assertEquals(
            listOf(
                1,
                2,
            ),
            states.map { state ->
                state.visibleDisplayOrder
            },
        )
    }

    private fun sampleSelections():
        List<StockFieldSelection> {

        return listOf(
            field(
                fieldKey =
                    "closingPrice",
                absoluteOrder =
                    1,
                isVisible =
                    true,
            ),
            field(
                fieldKey =
                    "change",
                absoluteOrder =
                    2,
                isVisible =
                    false,
            ),
            field(
                fieldKey =
                    "tradeVolume",
                absoluteOrder =
                    3,
                isVisible =
                    true,
            ),
            field(
                fieldKey =
                    "peRatio",
                absoluteOrder =
                    4,
                isVisible =
                    true,
            ),
        )
    }

    private fun field(
        fieldKey: String,
        absoluteOrder: Int,
        isVisible: Boolean = true,
    ): StockFieldSelection {

        return StockFieldSelection(
            FieldKey =
                fieldKey,
            DisplayOrder =
                absoluteOrder,
            IsVisible =
                isVisible,
        )
    }
}
