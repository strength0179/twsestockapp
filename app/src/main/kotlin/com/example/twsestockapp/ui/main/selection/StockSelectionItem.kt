package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
 * Displays one stock inside the complete stock list.
 *
 * The main list always preserves the supplied stock order.
 *
 * Selection representation:
 *
 * - empty box means not selected
 * - numbered box means selected
 * - clicking an empty box selects the stock
 * - clicking a numbered box unselects the stock
 *
 * Reordering belongs exclusively to StockSelectionDrawer.
 */
@Composable
internal fun StockSelectionItem(
    stock: Stock,
    selection: StockSelection?,
    onSelect: () -> Unit,
    onUnselect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected =
        selection != null

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription =
                        "股票清單項目 ${stock.Code}"
                }
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        SelectionOrderBox(
            displayOrder =
                selection?.DisplayOrder,
            stockCode =
                stock.Code,
            onClick = {
                if (isSelected) {
                    onUnselect()
                } else {
                    onSelect()
                }
            },
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
    }
}

/**
 * Selection-order control for one stock in the complete stock list.
 */
@Composable
private fun SelectionOrderBox(
    displayOrder: Int?,
    stockCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description =
        if (displayOrder == null) {
            "$stockCode 尚未選取"
        } else {
            "$stockCode 排序 $displayOrder，點擊取消選取"
        }

    Box(
        modifier =
            modifier
                .size(44.dp)
                .border(
                    width = 1.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .outline,
                    shape =
                        MaterialTheme
                            .shapes
                            .small,
                )
                .clickable(
                    onClick =
                        onClick,
                )
                .semantics {
                    contentDescription =
                        description
                },
        contentAlignment =
            Alignment.Center,
    ) {
        Text(
            text =
                displayOrder
                    ?.toString()
                    ?: "",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )
    }
}
