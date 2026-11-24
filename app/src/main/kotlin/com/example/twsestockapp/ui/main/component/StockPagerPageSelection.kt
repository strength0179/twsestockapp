package com.example.twsestockapp.ui.main.component

import com.example.twsestockapp.data.selection.StockFieldSelection

/**
 * Resolves persistent Pager-field configuration into concrete visible
 * [StockPagerPage] definitions.
 *
 * Rules:
 *
 * - hidden fields do not become Pager pages
 * - visible fields are ordered by absolute DisplayOrder
 * - unknown persisted keys are ignored
 *
 * Hidden fields retain their absolute DisplayOrder in persistence, but
 * that order is not renumbered here.
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
            selection.IsVisible
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
