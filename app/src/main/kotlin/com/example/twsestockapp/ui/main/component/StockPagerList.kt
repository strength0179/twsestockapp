package com.example.twsestockapp.ui.main.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.twsestockapp.data.model.Stock

/**
 * Displays the stocks belonging to one Pager page.
 *
 * Responsibilities:
 *
 * - render the ordered stock collection
 * - retain the selected stock code used by the valuation dialog
 * - coordinate row clicks with StockValuationDialog
 *
 * Row rendering and field-specific color presentation belong to
 * dedicated components.
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

    val neutralTextColor =
        MaterialTheme
            .colorScheme
            .onSurface

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
            val presentation =
                resolveStockPagerRowPresentation(
                    stock =
                        stock,
                    page =
                        page,
                    neutralTextColor =
                        neutralTextColor,
                )

            StockPagerRow(
                stock =
                    stock,
                value =
                    page.valueText(
                        stock
                    ),
                presentation =
                    presentation,
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
