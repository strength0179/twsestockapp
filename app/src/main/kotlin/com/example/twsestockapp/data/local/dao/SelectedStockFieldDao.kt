package com.example.twsestockapp.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.twsestockapp.data.local.entity.SelectedStockFieldEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for persistent stock Pager fields.
 *
 * Every field remains stored even when hidden.
 *
 * displayOrder is the absolute order across visible and hidden fields.
 * isVisible controls whether the field is shown by the Pager.
 */
@Dao
abstract class SelectedStockFieldDao {

    /**
     * Observes every persisted field in absolute database order.
     */
    @Query(
        """
        SELECT *
        FROM selected_stock_fields
        ORDER BY displayOrder ASC
        """
    )
    abstract fun observeSelections():
        Flow<List<SelectedStockFieldEntity>>

    /**
     * Compatibility operation used by the current repository.
     *
     * Existing fields become visible without changing order. A missing
     * field is appended to the absolute order.
     */
    @Transaction
    open suspend fun select(
        field: SelectedStockFieldEntity,
    ) {
        val existing =
            findByFieldKey(
                fieldKey =
                    field.fieldKey,
            )

        if (existing != null) {
            show(
                fieldKey =
                    field.fieldKey,
            )

            return
        }

        val nextOrder =
            (findMaximumOrder() ?: 0) + 1

        insertInternal(
            SelectedStockFieldEntity(
                fieldKey =
                    field.fieldKey,
                displayOrder =
                    nextOrder,
                isVisible =
                    true,
            )
        )
    }

    /**
     * Compatibility operation used by the current repository.
     *
     * Unselect now means hide. The row and absolute order remain.
     */
    @Transaction
    open suspend fun unselect(
        fieldKey: String,
    ) {
        hide(
            fieldKey =
                fieldKey,
        )
    }

    /**
     * Hides one field without deleting it or changing absolute order.
     *
     * At least one field remains visible.
     */
    @Transaction
    open suspend fun hide(
        fieldKey: String,
    ) {
        val current =
            findByFieldKey(
                fieldKey =
                    fieldKey,
            )
                ?: return

        if (!current.isVisible) {
            return
        }

        if (countVisible() <= 1) {
            return
        }

        updateVisibility(
            fieldKey =
                fieldKey,
            isVisible =
                false,
        )
    }

    /**
     * Restores one hidden field at its current absolute order.
     */
    @Transaction
    open suspend fun show(
        fieldKey: String,
    ) {
        val current =
            findByFieldKey(
                fieldKey =
                    fieldKey,
            )
                ?: return

        if (current.isVisible) {
            return
        }

        updateVisibility(
            fieldKey =
                fieldKey,
            isVisible =
                true,
        )
    }

    /**
     * Moves a visible field before the previous visible field.
     *
     * Hidden fields between the two visible positions participate in
     * the absolute-order shift.
     *
     * Example:
     *
     * A1, B2 hidden, C3, D4
     *
     * moveForward(C)
     *
     * C1, A2, B3 hidden, D4
     */
    @Transaction
    open suspend fun moveForward(
        fieldKey: String,
    ) {
        val current =
            findByFieldKey(
                fieldKey =
                    fieldKey,
            )
                ?: return

        if (!current.isVisible) {
            return
        }

        val previousVisible =
            findPreviousVisible(
                displayOrder =
                    current.displayOrder,
            )
                ?: return

        shiftOrdersForward(
            targetOrder =
                previousVisible.displayOrder,
            currentOrder =
                current.displayOrder,
        )

        updateOrder(
            fieldKey =
                current.fieldKey,
            displayOrder =
                previousVisible.displayOrder,
        )
    }

    /**
     * Moves a visible field after the next visible field.
     *
     * Hidden fields between the two visible positions participate in
     * the absolute-order shift.
     */
    @Transaction
    open suspend fun moveBackward(
        fieldKey: String,
    ) {
        val current =
            findByFieldKey(
                fieldKey =
                    fieldKey,
            )
                ?: return

        if (!current.isVisible) {
            return
        }

        val nextVisible =
            findNextVisible(
                displayOrder =
                    current.displayOrder,
            )
                ?: return

        shiftOrdersBackward(
            currentOrder =
                current.displayOrder,
            targetOrder =
                nextVisible.displayOrder,
        )

        updateOrder(
            fieldKey =
                current.fieldKey,
            displayOrder =
                nextVisible.displayOrder,
        )
    }

    /**
     * Replaces the complete table.
     *
     * This remains available for initialization and deterministic test
     * setup. Normal user hide/show operations must not call this.
     */
    @Transaction
    open suspend fun replaceAll(
        fields: List<SelectedStockFieldEntity>,
    ) {
        clear()

        fields.forEachIndexed { index, field ->
            insertInternal(
                field.copy(
                    displayOrder =
                        index + 1,
                )
            )
        }
    }

    @Query(
        """
        DELETE FROM selected_stock_fields
        """
    )
    abstract suspend fun clear()

    /**
     * Number of all persisted fields, including hidden fields.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM selected_stock_fields
        """
    )
    abstract suspend fun count(): Int

    /**
     * Number of visible Pager fields.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM selected_stock_fields
        WHERE isVisible = 1
        """
    )
    abstract suspend fun countVisible(): Int

    @Insert(
        onConflict =
            OnConflictStrategy.IGNORE,
    )
    protected abstract suspend fun insertInternal(
        field: SelectedStockFieldEntity,
    )

    @Query(
        """
        SELECT *
        FROM selected_stock_fields
        WHERE fieldKey = :fieldKey
        LIMIT 1
        """
    )
    protected abstract suspend fun findByFieldKey(
        fieldKey: String,
    ): SelectedStockFieldEntity?

    @Query(
        """
        SELECT *
        FROM selected_stock_fields
        WHERE isVisible = 1
          AND displayOrder < :displayOrder
        ORDER BY displayOrder DESC
        LIMIT 1
        """
    )
    protected abstract suspend fun findPreviousVisible(
        displayOrder: Int,
    ): SelectedStockFieldEntity?

    @Query(
        """
        SELECT *
        FROM selected_stock_fields
        WHERE isVisible = 1
          AND displayOrder > :displayOrder
        ORDER BY displayOrder ASC
        LIMIT 1
        """
    )
    protected abstract suspend fun findNextVisible(
        displayOrder: Int,
    ): SelectedStockFieldEntity?

    @Query(
        """
        SELECT MAX(displayOrder)
        FROM selected_stock_fields
        """
    )
    protected abstract suspend fun findMaximumOrder():
        Int?

    /**
     * Makes room when moving an item toward the beginning.
     *
     * The moving row itself is excluded by displayOrder < currentOrder.
     */
    @Query(
        """
        UPDATE selected_stock_fields
        SET displayOrder = displayOrder + 1
        WHERE displayOrder >= :targetOrder
          AND displayOrder < :currentOrder
        """
    )
    protected abstract suspend fun shiftOrdersForward(
        targetOrder: Int,
        currentOrder: Int,
    )

    /**
     * Closes the gap when moving an item toward the end.
     *
     * The moving row itself is excluded by displayOrder > currentOrder.
     */
    @Query(
        """
        UPDATE selected_stock_fields
        SET displayOrder = displayOrder - 1
        WHERE displayOrder > :currentOrder
          AND displayOrder <= :targetOrder
        """
    )
    protected abstract suspend fun shiftOrdersBackward(
        currentOrder: Int,
        targetOrder: Int,
    )

    @Query(
        """
        UPDATE selected_stock_fields
        SET displayOrder = :displayOrder
        WHERE fieldKey = :fieldKey
        """
    )
    protected abstract suspend fun updateOrder(
        fieldKey: String,
        displayOrder: Int,
    )

    @Query(
        """
        UPDATE selected_stock_fields
        SET isVisible = :isVisible
        WHERE fieldKey = :fieldKey
        """
    )
    protected abstract suspend fun updateVisibility(
        fieldKey: String,
        isVisible: Boolean,
    )
}
