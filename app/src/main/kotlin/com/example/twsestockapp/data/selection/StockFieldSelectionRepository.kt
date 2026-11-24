package com.example.twsestockapp.data.selection

import kotlinx.coroutines.flow.Flow

/**
 * Contract for persistent Pager-field configuration.
 *
 * Every field remains persisted while hidden.
 *
 * [StockFieldSelection.DisplayOrder] is the absolute persistent
 * position across both visible and hidden fields.
 *
 * [StockFieldSelection.IsVisible] determines whether the field appears
 * in the Pager.
 *
 * Implementations must not expose Room, DAO, entity, or SQLite types.
 */
interface StockFieldSelectionRepository {

    /**
     * Observes every persisted field.
     *
     * Results must be ordered by absolute
     * [StockFieldSelection.DisplayOrder].
     */
    val selections:
        Flow<List<StockFieldSelection>>

    /**
     * Compatibility operation for adding or restoring a field.
     *
     * New application code should prefer [show] when operating on an
     * existing hidden field.
     */
    suspend fun select(
        fieldKey: String,
    )

    /**
     * Compatibility operation for hiding a field.
     *
     * New application code should prefer [hide].
     */
    suspend fun unselect(
        fieldKey: String,
    )

    /**
     * Hides a field without deleting its persistent record or directly
     * changing its absolute DisplayOrder.
     */
    suspend fun hide(
        fieldKey: String,
    )

    /**
     * Restores an existing hidden field at its persistent absolute
     * DisplayOrder.
     */
    suspend fun show(
        fieldKey: String,
    )

    /**
     * Moves a visible field before the previous visible field.
     *
     * Hidden fields remain part of the absolute-order movement.
     */
    suspend fun moveForward(
        fieldKey: String,
    )

    /**
     * Moves a visible field after the next visible field.
     *
     * Hidden fields remain part of the absolute-order movement.
     */
    suspend fun moveBackward(
        fieldKey: String,
    )

    /**
     * Replaces the complete field configuration.
     *
     * This operation is intended for initialization and controlled
     * configuration replacement, not normal hide/show interaction.
     */
    suspend fun replaceAll(
        fieldKeys: List<String>,
    )

    /**
     * Removes every persisted field.
     *
     * This operation is not part of normal field-editor behavior.
     */
    suspend fun clear()
}
