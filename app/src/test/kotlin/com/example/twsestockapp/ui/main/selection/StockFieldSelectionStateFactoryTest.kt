package com.example.twsestockapp.ui.main.selection

import com.example.twsestockapp.data.selection.StockFieldSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM tests for Pager-field editor state conversion.
 *
 * Required valuation fields:
 *
 * - peRatio
 * - dividendYield
 * - pbRatio
 *
 * Required fields are effectively visible even when their persisted
 * IsVisible value is false.
 */
class StockFieldSelectionStateFactoryTest {

    @Test
    fun hiddenOptionalFieldsMoveToBottomAndVisibleOrderIsRenumbered() {
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
    fun hiddenOptionalFieldHasExpectedState() {
        val hidden =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )
                .last()

        assertEquals(
            "change",
            hidden.page.key,
        )

        assertFalse(
            hidden.isVisible
        )

        assertFalse(
            hidden.isRequired
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

        assertFalse(
            hidden.canMoveForward
        )

        assertFalse(
            hidden.canMoveBackward
        )
    }

    @Test
    fun visibleMovementUsesEffectiveVisibleOrder() {
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
    fun finalOptionalVisibleFieldCannotBeHidden() {
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
            visible.isRequired
        )

        assertFalse(
            visible.canHide
        )
    }

    @Test
    fun optionalFieldsCanBeHiddenWhenMultipleFieldsAreEffectivelyVisible() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    sampleSelections(),
            )

        val closingPrice =
            states.first { state ->
                state.page.key ==
                    "closingPrice"
            }

        val tradeVolume =
            states.first { state ->
                state.page.key ==
                    "tradeVolume"
            }

        assertTrue(
            closingPrice.canHide
        )

        assertTrue(
            tradeVolume.canHide
        )
    }

    @Test
    fun allThreeValuationFieldsAreRequired() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    requiredValuationSelections(
                        persistedVisible =
                            true,
                    ),
            )

        assertEquals(
            listOf(
                "peRatio",
                "dividendYield",
                "pbRatio",
            ),
            states.map { state ->
                state.page.key
            },
        )

        assertTrue(
            states.all { state ->
                state.isRequired
            }
        )

        assertTrue(
            states.all { state ->
                state.isVisible
            }
        )

        assertTrue(
            states.none { state ->
                state.canHide
            }
        )
    }

    @Test
    fun persistedHiddenRequiredFieldsAreStillEffectivelyVisible() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    requiredValuationSelections(
                        persistedVisible =
                            false,
                    ),
            )

        assertEquals(
            3,
            states.size,
        )

        assertTrue(
            states.all { state ->
                state.isRequired
            }
        )

        assertTrue(
            states.all { state ->
                state.isVisible
            }
        )
    }

    @Test
    fun persistedHiddenRequiredFieldsReceiveCompactVisibleOrder() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    requiredValuationSelections(
                        persistedVisible =
                            false,
                    ),
            )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            states.map { state ->
                state.visibleDisplayOrder
            },
        )
    }

    @Test
    fun persistedHiddenRequiredFieldsCannotShowOrHide() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    requiredValuationSelections(
                        persistedVisible =
                            false,
                    ),
            )

        assertTrue(
            states.none { state ->
                state.canShow
            }
        )

        assertTrue(
            states.none { state ->
                state.canHide
            }
        )
    }

    @Test
    fun persistedHiddenRequiredFieldsRemainReorderable() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    requiredValuationSelections(
                        persistedVisible =
                            false,
                    ),
            )

        val peRatio =
            states[0]

        val dividendYield =
            states[1]

        val pbRatio =
            states[2]

        assertFalse(
            peRatio.canMoveForward
        )

        assertTrue(
            peRatio.canMoveBackward
        )

        assertTrue(
            dividendYield.canMoveForward
        )

        assertTrue(
            dividendYield.canMoveBackward
        )

        assertTrue(
            pbRatio.canMoveForward
        )

        assertFalse(
            pbRatio.canMoveBackward
        )
    }

    @Test
    fun effectiveVisibilityDoesNotModifyPersistentIsVisible() {
        val selections =
            requiredValuationSelections(
                persistedVisible =
                    false,
            )

        val states =
            createStockFieldSelectionItemStates(
                selections =
                    selections,
            )

        assertTrue(
            states.all { state ->
                state.isVisible
            }
        )

        assertTrue(
            selections.none { selection ->
                selection.IsVisible
            }
        )

        assertTrue(
            states.none { state ->
                state.selection.IsVisible
            }
        )
    }

    @Test
    fun optionalPersistedHiddenFieldsRemainHiddenBesideRequiredFields() {
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
                                false,
                        ),
                        field(
                            fieldKey =
                                "peRatio",
                            absoluteOrder =
                                2,
                            isVisible =
                                false,
                        ),
                        field(
                            fieldKey =
                                "change",
                            absoluteOrder =
                                3,
                            isVisible =
                                false,
                        ),
                        field(
                            fieldKey =
                                "dividendYield",
                            absoluteOrder =
                                4,
                            isVisible =
                                false,
                        ),
                        field(
                            fieldKey =
                                "pbRatio",
                            absoluteOrder =
                                5,
                            isVisible =
                                false,
                        ),
                    ),
            )

        val visibleKeys =
            states
                .filter { state ->
                    state.isVisible
                }
                .map { state ->
                    state.page.key
                }

        val hiddenKeys =
            states
                .filterNot { state ->
                    state.isVisible
                }
                .map { state ->
                    state.page.key
                }

        assertEquals(
            listOf(
                "peRatio",
                "dividendYield",
                "pbRatio",
            ),
            visibleKeys,
        )

        assertEquals(
            listOf(
                "closingPrice",
                "change",
            ),
            hiddenKeys,
        )
    }

    @Test
    fun requiredFieldsKeepPersistentAbsoluteOrderWhenForcedVisible() {
        val states =
            createStockFieldSelectionItemStates(
                selections =
                    listOf(
                        field(
                            fieldKey =
                                "pbRatio",
                            absoluteOrder =
                                3,
                            isVisible =
                                false,
                        ),
                        field(
                            fieldKey =
                                "peRatio",
                            absoluteOrder =
                                1,
                            isVisible =
                                false,
                        ),
                        field(
                            fieldKey =
                                "dividendYield",
                            absoluteOrder =
                                2,
                            isVisible =
                                false,
                        ),
                    ),
            )

        assertEquals(
            listOf(
                "peRatio",
                "dividendYield",
                "pbRatio",
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
            ),
            states.map { state ->
                state.selection.DisplayOrder
            },
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

    private fun requiredValuationSelections(
        persistedVisible: Boolean,
    ): List<StockFieldSelection> {

        return listOf(
            field(
                fieldKey =
                    "peRatio",
                absoluteOrder =
                    1,
                isVisible =
                    persistedVisible,
            ),
            field(
                fieldKey =
                    "dividendYield",
                absoluteOrder =
                    2,
                isVisible =
                    persistedVisible,
            ),
            field(
                fieldKey =
                    "pbRatio",
                absoluteOrder =
                    3,
                isVisible =
                    persistedVisible,
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
