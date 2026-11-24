package com.example.twsestockapp.ui.main.selection

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.ui.main.component.StockPagerPage
import com.example.twsestockapp.ui.main.component.isEffectivelyVisible
import com.example.twsestockapp.ui.main.component.isRequiredStockPagerField
import com.example.twsestockapp.ui.main.component.stockPagerPages

/**
 * Converts persistent Pager-field configuration into editor
 * presentation state.
 *
 * Persistence and presentation ordering are intentionally different.
 *
 * Required valuation fields:
 *
 * - peRatio
 * - dividendYield
 * - pbRatio
 *
 * Required fields are always presented as visible, even when an older
 * persistent row contains IsVisible=false.
 *
 * The factory never modifies persistent DisplayOrder or IsVisible
 * values.
 */
internal fun createStockFieldSelectionItemStates(
    selections: List<StockFieldSelection>,
    availablePages: List<StockPagerPage> =
        stockPagerPages,
): List<StockFieldSelectionItemState> {

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

    /*
     * DisplayOrder remains the authoritative persistent order.
     */
    val validOrderedSelections =
        selections
            .sortedBy { selection ->
                selection.DisplayOrder
            }
            .mapNotNull { selection ->
                val page =
                    pagesByKey[
                        selection.FieldKey
                    ]
                        ?: return@mapNotNull null

                ResolvedStockFieldSelection(
                    page =
                        page,
                    selection =
                        selection,
                )
            }

    /*
     * Required valuation fields are effectively visible even when the
     * persisted IsVisible value is false.
     */
    val visibleSelections =
        validOrderedSelections
            .filter { resolved ->
                resolved.selection
                    .isEffectivelyVisible()
            }

    val hiddenSelections =
        validOrderedSelections
            .filterNot { resolved ->
                resolved.selection
                    .isEffectivelyVisible()
            }

    val visibleCount =
        visibleSelections.size

    val visibleStates =
        visibleSelections
            .mapIndexed {
                    visibleIndex,
                    resolved ->

                val isRequired =
                    isRequiredStockPagerField(
                        fieldKey =
                            resolved.page.key,
                    )

                StockFieldSelectionItemState(
                    page =
                        resolved.page,
                    selection =
                        resolved.selection,
                    visibleDisplayOrder =
                        visibleIndex + 1,
                    /*
                     * This is effective presentation visibility, not a
                     * direct copy of persistent IsVisible.
                     */
                    isVisible =
                        true,
                    isRequired =
                        isRequired,
                    canShow =
                        false,
                    canHide =
                        !isRequired &&
                            visibleCount > 1,
                    canMoveForward =
                        visibleIndex > 0,
                    canMoveBackward =
                        visibleIndex <
                            visibleSelections.lastIndex,
                )
            }

    val hiddenStates =
        hiddenSelections
            .map { resolved ->
                StockFieldSelectionItemState(
                    page =
                        resolved.page,
                    selection =
                        resolved.selection,
                    visibleDisplayOrder =
                        null,
                    isVisible =
                        false,
                    isRequired =
                        false,
                    canShow =
                        true,
                    canHide =
                        false,
                    canMoveForward =
                        false,
                    canMoveBackward =
                        false,
                )
            }

    /*
     * Editor layout:
     *
     * all effectively visible rows first,
     * followed by optional hidden rows.
     *
     * Each group preserves persistent absolute order.
     */
    return visibleStates +
        hiddenStates
}

/**
 * Intermediate pairing of one persistent selection with its current
 * Pager-page definition.
 */
private data class ResolvedStockFieldSelection(
    val page: StockPagerPage,
    val selection: StockFieldSelection,
)
