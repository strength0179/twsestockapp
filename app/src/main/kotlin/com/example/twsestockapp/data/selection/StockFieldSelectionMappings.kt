package com.example.twsestockapp.data.selection

import com.example.twsestockapp.data.local.entity.SelectedStockFieldEntity

/**
 * Converts one Room entity into the application-level field-selection
 * model.
 *
 * This mapping is kept at the repository boundary so Room types never
 * reach ViewModels or UI.
 */
internal fun SelectedStockFieldEntity.toStockFieldSelection():
    StockFieldSelection {

    return StockFieldSelection(
        FieldKey =
            fieldKey,
        DisplayOrder =
            displayOrder,
        IsVisible =
            isVisible,
    )
}

/**
 * Converts a collection of Room entities into application-level
 * field-selection models.
 */
internal fun List<SelectedStockFieldEntity>
    .toStockFieldSelections():
    List<StockFieldSelection> {

    return map { entity ->
        entity.toStockFieldSelection()
    }
}
