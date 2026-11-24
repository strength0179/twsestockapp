package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockSelection

/**
 * Displays one selected stock on the selected-stock Pager page.
 *
 * This is the dedicated ordering surface for selected stocks.
 *
 * Movement controls:
 *
 * - left arrow moves the stock toward the beginning
 * - right arrow moves the stock toward the end
 */
@Composable
internal fun SelectedStockSelectionItem(
    stock: Stock,
    selection: StockSelection,
    canMoveForward: Boolean,
    canMoveBackward: Boolean,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription =
                        "已選股票排序 " +
                            "${selection.DisplayOrder}，" +
                            stock.Code
                }
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Text(
            text =
                selection.DisplayOrder
                    .toString(),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )

        Spacer(
            modifier =
                Modifier.width(12.dp),
        )

        Text(
            text =
                "${stock.Code} ${stock.Name}",
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
        )

        SelectionMoveButtons(
            forwardDescription =
                "將 ${stock.Code} 往前移",
            backwardDescription =
                "將 ${stock.Code} 往後移",
            canMoveForward =
                canMoveForward,
            canMoveBackward =
                canMoveBackward,
            onMoveForward =
                onMoveForward,
            onMoveBackward =
                onMoveBackward,
        )
    }
}
