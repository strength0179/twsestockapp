package com.example.twsestockapp.data.selection

import com.example.twsestockapp.data.local.dao.SelectedStockFieldDao
import com.example.twsestockapp.data.local.entity.SelectedStockFieldEntity
import kotlinx.coroutines.flow.map

/**
 * Default production implementation of
 * [StockFieldSelectionRepository].
 *
 * Persistence is backed by [SelectedStockFieldDao].
 *
 * Observable-state ordering and duplicate suppression are inherited
 * from [BaseStockFieldSelectionRepository].
 */
class DefaultStockFieldSelectionRepository(
    private val selectedStockFieldDao:
        SelectedStockFieldDao,
) : BaseStockFieldSelectionRepository(
    sourceSelections =
        selectedStockFieldDao
            .observeSelections()
            .map { entities ->
                entities.toStockFieldSelections()
            },
) {

    /**
     * Compatibility operation.
     *
     * Existing fields become visible at their current absolute order.
     * Missing fields are appended by the DAO.
     */
    override suspend fun select(
        fieldKey: String,
    ) {
        selectedStockFieldDao.select(
            SelectedStockFieldEntity(
                fieldKey =
                    fieldKey,
                isVisible =
                    true,
            )
        )
    }

    /**
     * Compatibility operation.
     *
     * Unselect means hide rather than delete.
     */
    override suspend fun unselect(
        fieldKey: String,
    ) {
        hide(
            fieldKey =
                fieldKey,
        )
    }

    override suspend fun hide(
        fieldKey: String,
    ) {
        selectedStockFieldDao.hide(
            fieldKey =
                fieldKey,
        )
    }

    override suspend fun show(
        fieldKey: String,
    ) {
        selectedStockFieldDao.show(
            fieldKey =
                fieldKey,
        )
    }

    override suspend fun moveForward(
        fieldKey: String,
    ) {
        selectedStockFieldDao.moveForward(
            fieldKey =
                fieldKey,
        )
    }

    override suspend fun moveBackward(
        fieldKey: String,
    ) {
        selectedStockFieldDao.moveBackward(
            fieldKey =
                fieldKey,
        )
    }

    override suspend fun replaceAll(
        fieldKeys: List<String>,
    ) {
        selectedStockFieldDao.replaceAll(
            fieldKeys
                .distinct()
                .map { fieldKey ->
                    SelectedStockFieldEntity(
                        fieldKey =
                            fieldKey,
                        isVisible =
                            true,
                    )
                }
        )
    }

    override suspend fun clear() {
        selectedStockFieldDao.clear()
    }
}
