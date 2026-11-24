package com.example.twsestockapp.data.selection

import com.example.twsestockapp.data.local.entity.SelectedStockEntity

/**
 * Converts one persistence entity into the application-level
 * stock-selection model.
 *
 * Room-specific types stop at this mapping boundary.
 */
internal fun SelectedStockEntity.toStockSelection():
    StockSelection {

    return StockSelection(
        Code =
            code,
        DisplayOrder =
            displayOrder,
    )
}

/**
 * Converts persistence entities into application-level stock-selection
 * models.
 */
internal fun List<SelectedStockEntity>
    .toStockSelections():
    List<StockSelection> {

    return map { entity ->
        entity.toStockSelection()
    }
}
