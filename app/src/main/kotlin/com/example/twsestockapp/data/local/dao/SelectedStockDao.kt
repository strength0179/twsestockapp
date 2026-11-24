package com.example.twsestockapp.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.twsestockapp.data.local.entity.SelectedStockEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Database access object for the ordered selected-stock list.
 *
 * Selection is represented by row existence.
 *
 * Display order starts at 1 and remains continuous:
 *
 * 1, 2, 3, 4...
 *
 * Selecting a new stock appends it to the end.
 * Removing a stock compacts all later positions.
 * Moving a stock exchanges its order with the adjacent stock.
 */
@Dao
abstract class SelectedStockDao {

    /**
     * Observes complete selection records in display order.
     */
    @Query(
        """
        SELECT *
        FROM selected_stocks
        ORDER BY displayOrder ASC
        """
    )
    abstract fun observeSelections():
        Flow<List<SelectedStockEntity>>

    /**
     * Compatibility projection for current repository callers.
     *
     * Codes are emitted in user-defined display order.
     */
    fun observeSelectedCodes(): Flow<List<String>> {
        return observeSelections()
            .map { selections ->
                selections.map { selection ->
                    selection.code
                }
            }
    }

    /**
     * Selects a stock and appends it to the end.
     *
     * Re-selecting an existing code does nothing.
     *
     * The order supplied by [stock] is intentionally ignored because
     * order ownership belongs to this DAO.
     */
    @Transaction
    open suspend fun select(
        stock: SelectedStockEntity
    ) {
        if (findByCode(stock.code) != null) {
            return
        }

        val nextOrder =
            (findMaximumOrder() ?: 0) + 1

        insertInternal(
            SelectedStockEntity(
                code = stock.code,
                displayOrder = nextOrder
            )
        )
    }

    /**
     * Removes one stock and compacts all later order values.
     */
    @Transaction
    open suspend fun unselect(
        code: String
    ) {
        val selectedStock =
            findByCode(code)
                ?: return

        deleteInternal(
            code = code
        )

        decrementOrdersAfter(
            displayOrder =
                selectedStock.displayOrder
        )
    }

    /**
     * Removes every selected stock.
     */
    @Query(
        """
        DELETE FROM selected_stocks
        """
    )
    abstract suspend fun clear()

    /**
     * Moves a stock one position toward the beginning.
     *
     * For example:
     *
     * 3 + -> exchanges order 3 with order 2.
     *
     * The first item cannot move further forward.
     */
    @Transaction
    open suspend fun moveForward(
        code: String
    ) {
        val current =
            findByCode(code)
                ?: return

        if (current.displayOrder <= 1) {
            return
        }

        val previous =
            findByOrder(
                displayOrder =
                    current.displayOrder - 1
            ) ?: return

        updateOrder(
            code = previous.code,
            displayOrder =
                current.displayOrder
        )

        updateOrder(
            code = current.code,
            displayOrder =
                previous.displayOrder
        )
    }

    /**
     * Moves a stock one position toward the end.
     *
     * For example:
     *
     * 3 - -> exchanges order 3 with order 4.
     *
     * The final item cannot move further backward.
     */
    @Transaction
    open suspend fun moveBackward(
        code: String
    ) {
        val current =
            findByCode(code)
                ?: return

        val next =
            findByOrder(
                displayOrder =
                    current.displayOrder + 1
            ) ?: return

        updateOrder(
            code = next.code,
            displayOrder =
                current.displayOrder
        )

        updateOrder(
            code = current.code,
            displayOrder =
                next.displayOrder
        )
    }

    /**
     * Returns the current number of selected stocks.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM selected_stocks
        """
    )
    abstract suspend fun count(): Int

    @Insert(
        onConflict = OnConflictStrategy.IGNORE
    )
    protected abstract suspend fun insertInternal(
        stock: SelectedStockEntity
    )

    @Query(
        """
        SELECT *
        FROM selected_stocks
        WHERE code = :code
        LIMIT 1
        """
    )
    protected abstract suspend fun findByCode(
        code: String
    ): SelectedStockEntity?

    @Query(
        """
        SELECT *
        FROM selected_stocks
        WHERE displayOrder = :displayOrder
        LIMIT 1
        """
    )
    protected abstract suspend fun findByOrder(
        displayOrder: Int
    ): SelectedStockEntity?

    @Query(
        """
        SELECT MAX(displayOrder)
        FROM selected_stocks
        """
    )
    protected abstract suspend fun findMaximumOrder(): Int?

    @Query(
        """
        DELETE FROM selected_stocks
        WHERE code = :code
        """
    )
    protected abstract suspend fun deleteInternal(
        code: String
    )

    @Query(
        """
        UPDATE selected_stocks
        SET displayOrder = displayOrder - 1
        WHERE displayOrder > :displayOrder
        """
    )
    protected abstract suspend fun decrementOrdersAfter(
        displayOrder: Int
    )

    @Query(
        """
        UPDATE selected_stocks
        SET displayOrder = :displayOrder
        WHERE code = :code
        """
    )
    protected abstract suspend fun updateOrder(
        code: String,
        displayOrder: Int
    )
}
