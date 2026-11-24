package com.example.twsestockapp.ui.main.selection

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.ui.main.component.StockPagerPage
import com.example.twsestockapp.ui.main.component.stockPagerPages

/**
 * Converts persistent Pager-field configuration into editor
 * presentation state.
 *
 * Persistence and presentation ordering are intentionally different.
 *
 * Example persistent state:
 *
 * A absolute=1 visible
 * B absolute=2 hidden
 * C absolute=3 visible
 * D absolute=4 visible
 *
 * Editor state:
 *
 * A visibleOrder=1
 * C visibleOrder=2
 * D visibleOrder=3
 * B visibleOrder=null
 *
 * The factory never modifies persistent DisplayOrder values.
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
     * DisplayOrder remains the authoritative absolute persistent order.
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

    val visibleSelections =
        validOrderedSelections
            .filter { resolved ->
                resolved.selection.IsVisible
            }

    val hiddenSelections =
        validOrderedSelections
            .filterNot { resolved ->
                resolved.selection.IsVisible
            }

    val visibleCount =
        visibleSelections.size

    val visibleStates =
        visibleSelections
            .mapIndexed {
                    visibleIndex,
                    resolved ->

                StockFieldSelectionItemState(
                    page =
                        resolved.page,
                    selection =
                        resolved.selection,

                    /*
                     * Presentation-only order.
                     *
                     * This intentionally differs from the persistent
                     * DisplayOrder when hidden fields exist.
                     */
                    visibleDisplayOrder =
                        visibleIndex + 1,

                    isVisible =
                        true,

                    canShow =
                        false,

                    /*
                     * Never allow the final visible Pager page to be
                     * hidden.
                     */
                    canHide =
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

                    /*
                     * Hidden rows are rendered as x rather than a
                     * numeric visible order.
                     */
                    visibleDisplayOrder =
                        null,

                    isVisible =
                        false,

                    canShow =
                        true,

                    canHide =
                        false,

                    /*
                     * Hidden fields retain their absolute database
                     * position but are not directly reorderable through
                     * the editor.
                     */
                    canMoveForward =
                        false,

                    canMoveBackward =
                        false,
                )
            }

    /*
     * Editor layout:
     *
     * all visible rows first,
     * followed by all hidden rows.
     *
     * Each group preserves absolute persistent order.
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
