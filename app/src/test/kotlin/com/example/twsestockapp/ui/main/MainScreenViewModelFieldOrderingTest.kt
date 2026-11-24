package com.example.twsestockapp.ui.main

import com.example.twsestockapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests Pager-field ordering commands exposed by
 * [MainScreenViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelFieldOrderingTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun moveStockFieldForwardSwapsWithPreviousField() = runTest {
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
                        "peRatio",
                    ),
            )

        startCollectingUiState(
            fixture.viewModel,
        )

        fixture.viewModel.moveStockFieldForward(
            fieldKey =
                "peRatio",
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel,
            )

        assertEquals(
            listOf(
                "closingPrice",
                "peRatio",
                "tradeVolume",
            ),
            state.fieldSelections.map { selection ->
                selection.FieldKey
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.fieldSelections.map { selection ->
                selection.DisplayOrder
            },
        )
    }

    @Test
    fun moveStockFieldBackwardSwapsWithNextField() = runTest {
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
                        "peRatio",
                    ),
            )

        startCollectingUiState(
            fixture.viewModel,
        )

        fixture.viewModel.moveStockFieldBackward(
            fieldKey =
                "closingPrice",
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel,
            )

        assertEquals(
            listOf(
                "tradeVolume",
                "closingPrice",
                "peRatio",
            ),
            state.fieldSelections.map { selection ->
                selection.FieldKey
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.fieldSelections.map { selection ->
                selection.DisplayOrder
            },
        )
    }

    @Test
    fun replaceStockFieldsUsesSuppliedOrder() = runTest {
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
            fixture.viewModel,
        )

        fixture.viewModel.replaceStockFields(
            fieldKeys =
                listOf(
                    "dividendYield",
                    "peRatio",
                    "closingPrice",
                ),
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel,
            )

        assertEquals(
            listOf(
                "dividendYield",
                "peRatio",
                "closingPrice",
            ),
            state.fieldSelections.map { selection ->
                selection.FieldKey
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.fieldSelections.map { selection ->
                selection.DisplayOrder
            },
        )
    }

    @Test
    fun replaceStockFieldsRemovesDuplicateKeys() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                    ),
                fieldSelections =
                    emptyList(),
            )

        startCollectingUiState(
            fixture.viewModel,
        )

        fixture.viewModel.replaceStockFields(
            fieldKeys =
                listOf(
                    "closingPrice",
                    "tradeVolume",
                    "closingPrice",
                    "peRatio",
                ),
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel,
            )

        assertEquals(
            listOf(
                "closingPrice",
                "tradeVolume",
                "peRatio",
            ),
            state.fieldSelections.map { selection ->
                selection.FieldKey
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.fieldSelections.map { selection ->
                selection.DisplayOrder
            },
        )
    }
}
