package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests MainScreenViewModel state construction.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelStateTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun uiStateInitiallyLoading() = runTest {
        val fixture =
            createMainScreenViewModelFixture()

        assertEquals(
            MainScreenUiState.Loading,
            fixture.viewModel.uiState.value
        )
    }

    @Test
    fun emptySelectionRequestsSelectionPanel() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections = emptyList()
            )

        startCollectingUiState(
            fixture.viewModel
        )

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            MAIN_SCREEN_FAKE_STOCKS,
            state.allStocks
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

    @Test
    fun unknownSelectionDoesNotCreateSyntheticStock() = runTest {
        val fixture =
            createMainScreenViewModelFixture(
                selections =
                    listOf(
                        StockSelection(
                            Code = "9999",
                            DisplayOrder = 1
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
            listOf("9999"),
            state.selections.map { selection ->
                selection.Code
            }
        )

        assertTrue(
            state.visibleStocks.isEmpty()
        )

        assertFalse(
            state.showSelectionPanel
        )
    }
}
