package com.example.twsestockapp.ui.main.selection

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.ui.main.component.StockPagerPage

/**
 * Presentation state for one row in the Pager-field editor.
 *
 * [selection] contains the persistent absolute order.
 *
 * [visibleDisplayOrder] is presentation-only:
 *
 * - visible field -> 1, 2, 3...
 * - hidden field  -> null
 *
 * visibleDisplayOrder must never be written back to persistence.
 */
internal data class StockFieldSelectionItemState(
    val page: StockPagerPage,
    val selection: StockFieldSelection,
    val visibleDisplayOrder: Int?,
    val isVisible: Boolean,
    val canShow: Boolean,
    val canHide: Boolean,
    val canMoveForward: Boolean,
    val canMoveBackward: Boolean,
)
