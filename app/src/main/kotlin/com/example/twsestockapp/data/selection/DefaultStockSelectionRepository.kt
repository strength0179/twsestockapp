package com.example.twsestockapp.data.selection

import com.example.twsestockapp.data.local.dao.SelectedStockDao
import com.example.twsestockapp.data.local.entity.SelectedStockEntity
import kotlinx.coroutines.flow.map

/**
 * Default production implementation of [StockSelectionRepository].
 *
 * Persistence is backed by [SelectedStockDao].
 *
 * Observable-state ordering and duplicate suppression are inherited
 * from [BaseStockSelectionRepository].
 */
class DefaultStockSelectionRepository(
    private val selectedStockDao:
        SelectedStockDao,
) : BaseStockSelectionRepository(
    sourceSelections =
        selectedStockDao
            .observeSelections()
            .map { entities ->
                entities.toStockSelections()
            },
) {

    override suspend fun select(
        code: String,
    ) {
        selectedStockDao.select(
            SelectedStockEntity(
                code =
                    code,
            )
        )
    }

    override suspend fun unselect(
        code: String,
    ) {
        selectedStockDao.unselect(
            code =
                code,
        )
    }

    override suspend fun moveForward(
        code: String,
    ) {
        selectedStockDao.moveForward(
            code =
                code,
        )
    }

    override suspend fun moveBackward(
        code: String,
    ) {
        selectedStockDao.moveBackward(
            code =
                code,
        )
    }

    override suspend fun clear() {
        selectedStockDao.clear()
    }
}
