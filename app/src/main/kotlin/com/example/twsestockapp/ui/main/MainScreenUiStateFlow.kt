package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import com.example.twsestockapp.data.selection.StockSelectionRepository
import com.example.twsestockapp.ui.main.sort.StockSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

/**
 * Creates the combined MainScreen presentation-state stream.
 *
 * Sources:
 *
 * - stock data
 * - persistent stock selection
 * - persistent field selection
 * - current stock sorting option
 *
 * Whenever one source changes, a new presentation state is created from
 * the latest values of all four sources.
 */
internal fun createMainScreenUiStateFlow(
    dataRepository: DataRepository,
    stockSelectionRepository:
        StockSelectionRepository,
    stockFieldSelectionRepository:
        StockFieldSelectionRepository,
    stockSortOption:
        Flow<StockSortOption>,
): Flow<MainScreenUiState> {

    val successStates:
        Flow<MainScreenUiState> =
        combine(
            dataRepository.data,
            stockSelectionRepository.selections,
            stockFieldSelectionRepository.selections,
            stockSortOption,
        ) {
                allStocks,
                selections,
                fieldSelections,
                sortOption ->

            MainScreenStateFactory.createSuccess(
                allStocks =
                    allStocks,
                selections =
                    selections,
                fieldSelections =
                    fieldSelections,
                stockSortOption =
                    sortOption,
            )
        }

    return successStates.catch { throwable ->
        emit(
            MainScreenUiState.Error(
                throwable =
                    throwable,
            )
        )
    }
}
