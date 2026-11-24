package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests Pager-field visibility commands exposed by
 * [MainScreenViewModel].
 *
 * New visibility model:
 *
 * - fields remain in the collection when hidden
 * - DisplayOrder is the absolute persistent order
 * - IsVisible determines whether a field appears in the Pager
 * - restoring a field does not directly change DisplayOrder
 * - at least one field remains visible
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelFieldSelectionTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()


    /**
     * Selecting an already-visible field does not duplicate or reorder
     * it.
     */
    @Test
    fun selectingExistingVisibleFieldDoesNotDuplicateIt() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                    ),
                fieldSelections =
                    listOf(
                        fieldSelection(
                            fieldKey =
                                "closingPrice",
                            displayOrder =
                                1,
                        ),
                        fieldSelection(
                            fieldKey =
                                "tradeVolume",
                            displayOrder =
                                2,
                        ),
                    ),
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.showStockField(
            fieldKey =
                "closingPrice",
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            2,
            state.fieldSelections.size,
        )

        assertFieldState(
            actual =
                state.fieldSelections,
            expected =
                listOf(
                    expectedField(
                        "closingPrice",
                        1,
                        true,
                    ),
                    expectedField(
                        "tradeVolume",
                        2,
                        true,
                    ),
                ),
        )
    }

    /**
     * Unselect now means hide.
     *
     * The field remains persisted and its absolute DisplayOrder is not
     * compacted.
     *
     * A1 B2 C3
     *
     * hide B
     *
     * A1 B2(hidden) C3
     */
    @Test
    fun hideStockFieldHidesFieldWithoutChangingAbsoluteOrder() =
        runTest {

            val fixture =
                createMainScreenViewModelFixture(
                    selections =
                        selectionsOf(
                            "2330",
                        ),
                    fieldSelections =
                        fieldSelectionsOf(
                            "closingPrice",
                            "change",
                            "tradeVolume",
                        ),
                )

            startCollectingUiState(
                fixture.viewModel
            )

            fixture.viewModel.hideStockField(
                fieldKey =
                    "change",
            )

            runCurrent()

            val state =
                successState(
                    fixture.viewModel
                )

            assertFieldState(
                actual =
                    state.fieldSelections,
                expected =
                    listOf(
                        expectedField(
                            "closingPrice",
                            1,
                            true,
                        ),
                        expectedField(
                            "change",
                            2,
                            false,
                        ),
                        expectedField(
                            "tradeVolume",
                            3,
                            true,
                        ),
                    ),
            )
        }

    /**
     * Selecting a hidden existing field restores it at the same
     * absolute DisplayOrder.
     *
     * This tests the compatibility select operation, which maps to show
     * when the field already exists.
     */
    @Test
    fun showStockFieldRestoresHiddenFieldAtSameAbsoluteOrder() =
        runTest {

            val fixture =
                createMainScreenViewModelFixture(
                    selections =
                        selectionsOf(
                            "2330",
                        ),
                    fieldSelections =
                        listOf(
                            fieldSelection(
                                fieldKey =
                                    "closingPrice",
                                displayOrder =
                                    1,
                                isVisible =
                                    true,
                            ),
                            fieldSelection(
                                fieldKey =
                                    "change",
                                displayOrder =
                                    2,
                                isVisible =
                                    false,
                            ),
                            fieldSelection(
                                fieldKey =
                                    "tradeVolume",
                                displayOrder =
                                    3,
                                isVisible =
                                    true,
                            ),
                        ),
                )

            startCollectingUiState(
                fixture.viewModel
            )

            fixture.viewModel.showStockField(
                fieldKey =
                    "change",
            )

            runCurrent()

            val state =
                successState(
                    fixture.viewModel
                )

            assertFieldState(
                actual =
                    state.fieldSelections,
                expected =
                    listOf(
                        expectedField(
                            "closingPrice",
                            1,
                            true,
                        ),
                        expectedField(
                            "change",
                            2,
                            true,
                        ),
                        expectedField(
                            "tradeVolume",
                            3,
                            true,
                        ),
                    ),
            )
        }

    /**
     * The final visible field cannot be hidden.
     */
    @Test
    fun unselectingOnlyVisibleFieldKeepsItVisible() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                    ),
                fieldSelections =
                    listOf(
                        fieldSelection(
                            fieldKey =
                                "closingPrice",
                            displayOrder =
                                1,
                            isVisible =
                                true,
                        ),
                    ),
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.hideStockField(
            fieldKey =
                "closingPrice",
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            1,
            state.fieldSelections.size,
        )

        assertTrue(
            state.fieldSelections
                .single()
                .IsVisible
        )

        assertEquals(
            1,
            state.fieldSelections
                .single()
                .DisplayOrder
        )
    }

    /**
     * A hidden field remains represented in MainScreenUiState.
     *
     * ViewModel state therefore contains complete persistent field
     * configuration, not only Pager-visible fields.
     */
    @Test
    fun hiddenFieldRemainsInUiState() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                    ),
                fieldSelections =
                    fieldSelectionsOf(
                        "closingPrice",
                        "tradeVolume",
                    ),
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.hideStockField(
            fieldKey =
                "tradeVolume",
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        val hidden =
            state.fieldSelections
                .first { selection ->
                    selection.FieldKey ==
                        "tradeVolume"
                }

        assertFalse(
            hidden.IsVisible
        )

        assertEquals(
            2,
            hidden.DisplayOrder
        )
    }

    private fun fieldSelection(
        fieldKey: String,
        displayOrder: Int,
        isVisible: Boolean = true,
    ): StockFieldSelection {

        return StockFieldSelection(
            FieldKey =
                fieldKey,
            DisplayOrder =
                displayOrder,
            IsVisible =
                isVisible,
        )
    }

    private fun expectedField(
        fieldKey: String,
        displayOrder: Int,
        isVisible: Boolean,
    ): ExpectedField {

        return ExpectedField(
            fieldKey =
                fieldKey,
            displayOrder =
                displayOrder,
            isVisible =
                isVisible,
        )
    }

    private fun assertFieldState(
        actual:
            List<StockFieldSelection>,
        expected:
            List<ExpectedField>,
    ) {
        assertEquals(
            expected,
            actual.map { selection ->
                ExpectedField(
                    fieldKey =
                        selection.FieldKey,
                    displayOrder =
                        selection.DisplayOrder,
                    isVisible =
                        selection.IsVisible,
                )
            },
        )
    }

    private data class ExpectedField(
        val fieldKey: String,
        val displayOrder: Int,
        val isVisible: Boolean,
    )
}


