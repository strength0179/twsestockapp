package com.example.twsestockapp.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Persistent representation of one stock Pager field.
 *
 * Rows are not deleted when fields are hidden.
 *
 * [displayOrder] is the absolute persistent position and includes both
 * visible and hidden fields.
 *
 * [isVisible] determines whether the field currently appears in the
 * Pager.
 */
@Entity(
    tableName = "selected_stock_fields",
)
data class SelectedStockFieldEntity(

    @PrimaryKey
    val fieldKey: String,

    val displayOrder: Int = 0,

    @ColumnInfo(
        defaultValue = "1",
    )
    val isVisible: Boolean = true,
)
