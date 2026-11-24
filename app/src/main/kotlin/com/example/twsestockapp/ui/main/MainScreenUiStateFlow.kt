package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import com.example.twsestockapp.data.selection.StockSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

/**
 * Creates the combined MainScreen presentation-state stream.
 *
 * The explicit Flow<MainScreenUiState> return type allows the stream to
 * emit both Success and Error states.
 */
internal fun createMainScreenUiStateFlow(
    dataRepository: DataRepository,
    stockSelectionRepository:
        StockSelectionRepository,
    stockFieldSelectionRepository:
        StockFieldSelectionRepository,
): Flow<MainScreenUiState> {

    val successStates:
        Flow<MainScreenUiState> =
        combine(
            dataRepository.data,
            stockSelectionRepository.selections,
            stockFieldSelectionRepository.selections,
        ) {
                allStocks,
                selections,
                fieldSelections ->

            MainScreenStateFactory.createSuccess(
                allStocks =
                    allStocks,
                selections =
                    selections,
                fieldSelections =
                    fieldSelections,
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
