package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockSelection

/**
 * Displays one selected stock on the selected-stock Pager page.
 *
 * This is the dedicated ordering and removal surface for selected
 * stocks.
 *
 * Controls:
 *
 * - "+" moves the stock toward the beginning
 * - "-" moves the stock toward the end
 * - "×" removes the stock from the selected-stock collection
 *
 * The final selected stock cannot be removed.
 */
@Composable
internal fun SelectedStockSelectionItem(
    stock: Stock,
    selection: StockSelection,
    canMoveForward: Boolean,
    canMoveBackward: Boolean,
    canRemove: Boolean,
    onMoveForward: () -> Unit,
    onMoveBackward: () -> Unit,
    onRemove: () -> Unit,
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
            maxLines =
                1,
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

        Spacer(
            modifier =
                Modifier.width(6.dp),
        )

        SelectedStockRemoveButton(
            stockCode =
                stock.Code,
            enabled =
                canRemove,
            onClick =
                onRemove,
        )
    }
}

/**
 * Removes one stock from the selected-stock collection.
 *
 * The button remains visible but is disabled when this is the final
 * selected stock.
 *
 * Disabled semantics are exposed explicitly so accessibility services
 * and Compose UI tests observe the same state as the Material button.
 */
@Composable
private fun SelectedStockRemoveButton(
    stockCode: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick =
            onClick,
        enabled =
            enabled,
        modifier =
            modifier
                .size(40.dp)
                .semantics {
                    contentDescription =
                        "移除自選股票 $stockCode"

                    if (!enabled) {
                        disabled()
                    }
                },
        shape =
            CircleShape,
        colors =
            IconButtonDefaults
                .filledTonalIconButtonColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .errorContainer,
                    contentColor =
                        MaterialTheme
                            .colorScheme
                            .onErrorContainer,
                ),
    ) {
        Text(
            text =
                "×",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )
    }
}
