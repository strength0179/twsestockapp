package com.example.twsestockapp.data.selection

/**
 * Application-level representation of one stock Pager field.
 *
 * Every known field remains persisted even when it is hidden.
 *
 * [DisplayOrder] is the field's absolute persistent position. Hiding a
 * field does not remove it or change this position.
 *
 * [IsVisible] determines whether the field is currently shown by the
 * Pager.
 *
 * Example:
 *
 * Database:
 *
 * A order 1 visible
 * B order 2 hidden
 * C order 3 visible
 *
 * Editor display:
 *
 * A 1
 * C 2
 * B x
 */
data class StockFieldSelection(
    val FieldKey: String,
    val DisplayOrder: Int,
    val IsVisible: Boolean = true,
)
