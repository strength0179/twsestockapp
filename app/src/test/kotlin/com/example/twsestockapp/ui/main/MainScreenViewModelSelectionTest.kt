package com.example.twsestockapp.ui.main

import com.example.twsestockapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests selecting and unselecting stocks through MainScreenViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelSelectionTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun selectStockAppendsStockToEnd() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330"
                    )
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.selectStock(
            code = "2454"
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            listOf(
                "2330",
                "2454"
            ),
            state.selections.map { selection ->
                selection.Code
            }
        )

        assertEquals(
            listOf(
                1,
                2
            ),
            state.selections.map { selection ->
                selection.DisplayOrder
            }
        )
    }

    @Test
    fun unselectingFinalStockRequestsSelectionPanel() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    selectionsOf(
                        "2330"
                    )
            )

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel.unselectStock(
            code = "2330"
        )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertTrue(
            state.selections.isEmpty()
        )

        assertTrue(
            state.visibleStocks.isEmpty()
        )

        assertTrue(
            state.showSelectionPanel
        )
    }
}
