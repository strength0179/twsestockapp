package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/**
 * Tests ordering behavior exposed by MainScreenViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelOrderingTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun visibleStocksFollowSelectionDisplayOrder() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    listOf(
                        StockSelection(
                            Code = "2330",
                            DisplayOrder = 3
                        ),
                        StockSelection(
                            Code = "2454",
                            DisplayOrder = 1
                        ),
                        StockSelection(
                            Code = "2317",
                            DisplayOrder = 2
                        )
                    )
            )

        startCollectingUiState(
            fixture.viewModel
        )

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            listOf(
                "2454",
                "2317",
                "2330"
            ),
            state.visibleStocks.map { stock ->
                stock.Code
            }
        )

        assertEquals(
            listOf(
                1,
                2,
                3
            ),
            state.selections.map { selection ->
                selection.DisplayOrder
            }
        )

        assertFalse(
            state.showSelectionPanel
        )
    }

    @Test
    fun moveStockForwardSwapsWithPreviousSelection() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                        "2454",
                        "2317"
                    )
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.moveStockForward(
            code = "2317"
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            listOf(
                "2330",
                "2317",
                "2454"
            ),
            state.selections.map { selection ->
                selection.Code
            }
        )

        assertEquals(
            listOf(
                "2330",
                "2317",
                "2454"
            ),
            state.visibleStocks.map { stock ->
                stock.Code
            }
        )
    }

    @Test
    fun moveStockBackwardSwapsWithNextSelection() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330",
                        "2454",
                        "2317"
                    )
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.moveStockBackward(
            code = "2330"
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            listOf(
                "2454",
                "2330",
                "2317"
            ),
            state.selections.map { selection ->
                selection.Code
            }
        )

        assertEquals(
            listOf(
                "2454",
                "2330",
                "2317"
            ),
            state.visibleStocks.map { stock ->
                stock.Code
            }
        )
    }
}
