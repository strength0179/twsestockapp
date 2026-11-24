package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock

/**
 * A stock list displayed inside one pager page.
 *
 * All pager pages use the same stocks and therefore retain the same
 * user-defined display order.
 */
@Composable
internal fun StockPagerList(
    stocks: List<Stock>,
    page: StockPagerPage,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(
            items = stocks,
            key = { stock ->
                stock.Code
            },
        ) { stock ->
            StockPagerRow(
                stock = stock,
                value = page.valueText(stock),
                valueColor = page.titleColor,
            )
        }
    }
}

@Composable
private fun StockPagerRow(
    stock: Stock,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp,
                ),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stock.Code,
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                )

                Text(
                    text = stock.Name,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = value,
                modifier = Modifier.weight(0.8f),
                style =
                    MaterialTheme.typography
                        .titleMedium,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }

        HorizontalDivider(
            color =
                MaterialTheme.colorScheme
                    .outlineVariant,
        )
    }
}
