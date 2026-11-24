package com.example.twsestockapp.ui.main.component

import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Resolves persistent Pager-field configuration into concrete visible
 * [StockPagerPage] definitions.
 *
 * Rules:
 *
 * - required valuation fields are always returned
 * - optional hidden fields do not become Pager pages
 * - effectively visible fields are ordered by absolute DisplayOrder
 * - unknown persisted keys are ignored
 *
 * Required visibility is a presentation rule. This resolver does not
 * modify persistent IsVisible or DisplayOrder values.
 */
internal fun resolveStockPagerPages(
    selections: List<StockFieldSelection>,
    availablePages: List<StockPagerPage> =
        stockPagerPages,
): List<StockPagerPage> {

    if (
        selections.isEmpty() ||
        availablePages.isEmpty()
    ) {
        return emptyList()
    }

    val pagesByKey =
        availablePages.associateBy { page ->
            page.key
        }

    return selections
        .asSequence()
        .filter { selection ->
            selection.isEffectivelyVisible()
        }
        .sortedBy { selection ->
            selection.DisplayOrder
        }
        .mapNotNull { selection ->
            pagesByKey[
                selection.FieldKey
            ]
        }
        .toList()
}
