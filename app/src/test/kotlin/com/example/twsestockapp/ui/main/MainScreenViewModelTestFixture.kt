package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

/**
 * Objects required by one DefaultMainScreenViewModel unit test.
 */
internal data class MainScreenViewModelFixture(
    val viewModel:
        DefaultMainScreenViewModel,
)

/**
 * Creates a DefaultMainScreenViewModel backed by deterministic test
 * repositories.
 */
internal fun createMainScreenViewModelFixture(
    stocks: List<Stock> =
        MAIN_SCREEN_FAKE_STOCKS,
    selections: List<StockSelection> =
        emptyList(),
    fieldSelections:
        List<StockFieldSelection> =
        emptyList(),
): MainScreenViewModelFixture {

    return MainScreenViewModelFixture(
        viewModel =
            DefaultMainScreenViewModel(
                dataRepository =
                    FakeDataRepository(
                        stocks =
                            stocks,
                    ),
                stockSelectionRepository =
                    FakeStockSelectionRepository(
                        initialSelections =
                            selections,
                    ),
                stockFieldSelectionRepository =
                    FakeStockFieldSelectionRepository(
                        initialSelections =
                            fieldSelections,
                    ),
            )
    )
}

/**
 * Starts observing uiState.
 *
 * BaseMainScreenViewModel uses SharingStarted.WhileSubscribed, so tests
 * need an active subscriber before upstream Flows become active.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun TestScope.startCollectingUiState(
    viewModel:
        MainScreenViewModel,
) {
    backgroundScope.launch {
        viewModel.uiState.collect()
    }

    runCurrent()
}

/**
 * Processes scheduled coroutine work and returns the current successful
 * UI state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun TestScope.successState(
    viewModel:
        MainScreenViewModel,
): MainScreenUiState.Success {

    runCurrent()

    val current =
        viewModel.uiState.value

    check(
        current is
            MainScreenUiState.Success
    ) {
        "Expected MainScreenUiState.Success but was $current"
    }

    return current
}
