package com.example.twsestockapp.ui.main.component

import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Returns true when the field is one of the mandatory valuation Pager
 * fields.
 *
 * Required valuation fields:
 *
 * - PE ratio
 * - dividend yield
 * - PB ratio
 */
internal fun isRequiredStockPagerField(
    fieldKey: String,
): Boolean {

    return fieldKey ==
        StockPagerFieldKey.PE_RATIO ||
        fieldKey ==
        StockPagerFieldKey.DIVIDEND_YIELD ||
        fieldKey ==
        StockPagerFieldKey.PB_RATIO
}

/**
 * Resolves the effective presentation visibility of one persistent
 * field selection.
 *
 * Required valuation fields are always visible, even when an older
 * persistent row contains IsVisible=false.
 *
 * This function does not modify persistence.
 */
internal fun StockFieldSelection.isEffectivelyVisible():
    Boolean {

    return IsVisible ||
        isRequiredStockPagerField(
            fieldKey =
                FieldKey,
        )
}

/**
 * Returns true when at least one Pager field is effectively visible.
 *
 * Required valuation fields count as visible regardless of their
 * persisted IsVisible value.
 */
internal fun hasEffectivelyVisibleStockPagerField(
    selections: List<StockFieldSelection>,
): Boolean {

    return selections.any { selection ->
        selection.isEffectivelyVisible()
    }
}
