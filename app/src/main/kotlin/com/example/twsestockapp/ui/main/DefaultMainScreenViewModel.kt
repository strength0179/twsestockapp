package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import com.example.twsestockapp.data.selection.StockSelectionRepository

/**
 * Default production implementation of [MainScreenViewModel].
 *
 * UI-state lifecycle behavior is inherited from
 * [BaseMainScreenViewModel].
 *
 * This implementation routes UI commands to the appropriate
 * repository.
 */
class DefaultMainScreenViewModel(
    dataRepository: DataRepository,
    private val stockSelectionRepository:
        StockSelectionRepository,
    private val stockFieldSelectionRepository:
        StockFieldSelectionRepository,
) : BaseMainScreenViewModel(
    sourceUiState =
        createMainScreenUiStateFlow(
            dataRepository =
                dataRepository,
            stockSelectionRepository =
                stockSelectionRepository,
            stockFieldSelectionRepository =
                stockFieldSelectionRepository,
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
