package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Creates continuous one-based Pager-field selections from field keys.
 *
 * Example:
 *
 * fieldSelectionsOf(
 *     "closingPrice",
 *     "tradeVolume",
 *     "peRatio",
 * )
 *
 * produces:
 *
 * closingPrice -> 1
 * tradeVolume  -> 2
 * peRatio      -> 3
 */
internal fun fieldSelectionsOf(
    vararg fieldKeys: String,
): List<StockFieldSelection> {

    return fieldKeys.mapIndexed {
            index,
            fieldKey ->

        StockFieldSelection(
            FieldKey =
                fieldKey,
            DisplayOrder =
                index + 1,
        )
    }
}
