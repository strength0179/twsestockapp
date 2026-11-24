package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import com.example.twsestockapp.data.selection.StockSelectionRepository
import com.example.twsestockapp.ui.main.sort.StockSortOption
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Default production implementation of [MainScreenViewModel].
 *
 * UI-state lifecycle behavior is inherited from
 * [BaseMainScreenViewModel].
 *
 * Repository-backed commands are routed to their repositories.
 *
 * Stock sorting is presentation-only state and therefore remains inside
 * this ViewModel rather than being persisted through
 * StockSelectionRepository.
 */
class DefaultMainScreenViewModel(
    dataRepository:
        DataRepository,
    private val stockSelectionRepository:
        StockSelectionRepository,
    private val stockFieldSelectionRepository:
        StockFieldSelectionRepository,
    private val stockSortOptionState:
        MutableStateFlow<StockSortOption> =
        MutableStateFlow(
            StockSortOption.Default
        ),
) : BaseMainScreenViewModel(
    sourceUiState =
        createMainScreenUiStateFlow(
            dataRepository =
                dataRepository,
            stockSelectionRepository =
                stockSelectionRepository,
            stockFieldSelectionRepository =
                stockFieldSelectionRepository,
            stockSortOption =
                stockSortOptionState,
        ),
) {

    override fun selectStock(
        code: String,
    ) {
        launchCommand {
            stockSelectionRepository.select(
                code =
                    code,
            )
        }
    }

    override fun unselectStock(
        code: String,
    ) {
        launchCommand {
            stockSelectionRepository.unselect(
                code =
                    code,
            )
        }
    }

    override fun moveStockForward(
        code: String,
    ) {
        launchCommand {
            stockSelectionRepository.moveForward(
                code =
                    code,
            )
        }
    }

    override fun moveStockBackward(
        code: String,
    ) {
        launchCommand {
            stockSelectionRepository.moveBackward(
                code =
                    code,
            )
        }
    }

    override fun clearSelectedStocks() {
        launchCommand {
            stockSelectionRepository.clear()
        }
    }

    /**
     * Sorting is synchronous presentation state.
     *
     * Updating the StateFlow causes createMainScreenUiStateFlow to emit
     * a new Success state using the latest repository values.
     */
    override fun selectStockSortOption(
        option: StockSortOption,
    ) {
        stockSortOptionState.value =
            option
    }

    override fun showStockField(
        fieldKey: String,
    ) {
        launchCommand {
            stockFieldSelectionRepository.show(
                fieldKey =
                    fieldKey,
            )
        }
    }

    override fun hideStockField(
        fieldKey: String,
    ) {
        launchCommand {
            stockFieldSelectionRepository.hide(
                fieldKey =
                    fieldKey,
            )
        }
    }

    override fun moveStockFieldForward(
        fieldKey: String,
    ) {
        launchCommand {
            stockFieldSelectionRepository.moveForward(
                fieldKey =
                    fieldKey,
            )
        }
    }

    override fun moveStockFieldBackward(
        fieldKey: String,
    ) {
        launchCommand {
            stockFieldSelectionRepository.moveBackward(
                fieldKey =
                    fieldKey,
            )
        }
    }

    override fun replaceStockFields(
        fieldKeys: List<String>,
    ) {
        launchCommand {
            stockFieldSelectionRepository.replaceAll(
                fieldKeys =
                    fieldKeys,
            )
        }
    }
}
