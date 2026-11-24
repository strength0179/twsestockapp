package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock

/**
 * A stock list displayed inside one Pager page.
 *
 * All Pager pages use the same stocks and therefore retain the same
 * presentation order supplied by MainScreen state.
 *
 * Tapping a stock row opens its valuation dialog.
 */
@Composable
internal fun StockPagerList(
    stocks: List<Stock>,
    page: StockPagerPage,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    var selectedStockCode by
        rememberSaveable {
            mutableStateOf<String?>(
                null
            )
        }

    val selectedStock =
        selectedStockCode
            ?.let { code ->
                stocks.firstOrNull { stock ->
                    stock.Code ==
                        code
                }
            }

    LazyColumn(
        state =
            listState,
        modifier =
            modifier,
    ) {
        items(
            items =
                stocks,
            key = { stock ->
                stock.Code
            },
        ) { stock ->
            val valueColor =
                resolveStockPagerValueColor(
                    stock =
                        stock,
                    page =
                        page,
                )

            StockPagerRow(
                stock =
                    stock,
                value =
                    page.valueText(
                        stock
                    ),
                valueColor =
                    valueColor,
                valueColorDescription =
                    resolveStockPagerValueColorDescription(
                        stock =
                            stock,
                        page =
                            page,
                    ),
                onClick = {
                    selectedStockCode =
                        stock.Code
                },
            )
        }
    }

    if (selectedStock != null) {
        StockValuationDialog(
            stock =
                selectedStock,
            onDismissRequest = {
                selectedStockCode =
                    null
            },
        )
    }
}

/**
 * Resolves the displayed value color for one stock on one Pager page.
 *
 * Only the price-change page has value-dependent color semantics.
 *
 * Other pages retain their existing page title/value color.
 */
private fun resolveStockPagerValueColor(
    stock: Stock,
    page: StockPagerPage,
): Color {

    if (
        page.key !=
        StockPagerFieldKey.CHANGE
    ) {
        return page.titleColor
    }

    return when (
        resolveStockChangeDirection(
            value =
                stock.day?.Change,
        )
    ) {
        StockChangeDirection.Positive ->
            StockValueColors.Positive

        StockChangeDirection.Negative ->
            StockValueColors.Negative

        StockChangeDirection.Neutral ->
            page.titleColor
    }
}

/**
 * Exposes semantic change-color state for accessibility and UI tests.
 */
private fun resolveStockPagerValueColorDescription(
    stock: Stock,
    page: StockPagerPage,
): String? {

    if (
        page.key !=
        StockPagerFieldKey.CHANGE
    ) {
        return null
    }

    return when (
        resolveStockChangeDirection(
            value =
                stock.day?.Change,
        )
    ) {
        StockChangeDirection.Positive ->
            "${stock.Code} 漲跌為正，紅字"

        StockChangeDirection.Negative ->
            "${stock.Code} 漲跌為負，綠字"

        StockChangeDirection.Neutral ->
            "${stock.Code} 漲跌為中性"
    }
}

/**
 * One clickable stock row inside a Pager page.
 */
@Composable
private fun StockPagerRow(
    stock: Stock,
    value: String,
    valueColor: Color,
    valueColorDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick,
                )
                .semantics {
                    contentDescription =
                        "開啟 ${stock.Code} 股票估值"
                },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp,
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Text(
                    text =
                        stock.Code,
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )

                Text(
                    text =
                        stock.Name,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis,
                )
            }

            Text(
                text =
                    value,
                modifier =
                    Modifier
                        .weight(0.8f)
                        .then(
                            if (
                                valueColorDescription !=
                                null
                            ) {
                                Modifier.semantics {
                                    contentDescription =
                                        valueColorDescription
                                }
                            } else {
                                Modifier
                            }
                        ),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                color =
                    valueColor,
                fontWeight =
                    FontWeight.SemiBold,
                textAlign =
                    TextAlign.End,
                maxLines =
                    1,
            )
        }

        HorizontalDivider(
            color =
                MaterialTheme
                    .colorScheme
                    .outlineVariant,
        )
    }
}
