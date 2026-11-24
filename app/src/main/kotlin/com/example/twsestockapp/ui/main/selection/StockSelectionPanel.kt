package com.example.twsestockapp.ui.main.selection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockSelection
import kotlinx.coroutines.launch

/**
 * Two-page stock-selection editor.
 *
 * Each page contains:
 *
 * - one fixed-height navigation title
 * - one independently scrollable stock list
 *
 * Page 0:
 *
 * - complete stock catalog
 * - preserves the supplied stock order
 * - handles select / unselect
 * - selected-stock page is located to the right
 *
 * Page 1:
 *
 * - selected stocks ordered by DisplayOrder
 * - handles selected-stock ordering
 * - complete stock catalog is located to the left
 *
 * Both title rows use the same fixed height and are outside their
 * respective LazyColumn, so list scrolling never moves a title away.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StockSelectionPanel(
    stocks: List<Stock>,
    selections: List<StockSelection>,
    onSelect: (String) -> Unit,
    onUnselect: (String) -> Unit,
    onMoveForward: (String) -> Unit,
    onMoveBackward: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState =
        rememberPagerState(
            pageCount = {
                STOCK_SELECTION_PAGE_COUNT
            },
        )

    val coroutineScope =
        rememberCoroutineScope()

    val stocksByCode =
        stocks.associateBy { stock ->
            stock.Code
        }

    val selectionsByCode =
        selections.associateBy { selection ->
            selection.Code
        }

    val selectedEntries =
        selections
            .sortedBy { selection ->
                selection.DisplayOrder
            }
            .mapNotNull { selection ->
                val stock =
                    stocksByCode[
                        selection.Code
                    ]
                        ?: return@mapNotNull null

                SelectedStockEntry(
                    stock =
                        stock,
                    selection =
                        selection,
                )
            }

    HorizontalPager(
        state =
            pagerState,
        modifier =
            modifier.fillMaxSize(),
    ) { page ->
        when (page) {

            STOCK_CATALOG_PAGE -> {
                StockCatalogPage(
                    stocks =
                        stocks,
                    selectionsByCode =
                        selectionsByCode,
                    selectedCount =
                        selectedEntries.size,
                    onSelect =
                        onSelect,
                    onUnselect =
                        onUnselect,
                    onOpenSelectedStocks = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(
                                page =
                                    SELECTED_STOCKS_PAGE,
                            )
                        }
                    },
                )
            }

            SELECTED_STOCKS_PAGE -> {
                SelectedStocksPage(
                    selectedEntries =
                        selectedEntries,
                    selectedCount =
                        selectedEntries.size,
                    onMoveForward =
                        onMoveForward,
                    onMoveBackward =
                        onMoveBackward,
                    onContinueAdding = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(
                                page =
                                    STOCK_CATALOG_PAGE,
                            )
                        }
                    },
                )
            }
        }
    }
}

/**
 * Complete stock-catalog page.
 *
 * The title remains fixed while only the catalog LazyColumn scrolls.
 */
@Composable
private fun StockCatalogPage(
    stocks: List<Stock>,
    selectionsByCode:
        Map<String, StockSelection>,
    selectedCount: Int,
    onSelect: (String) -> Unit,
    onUnselect: (String) -> Unit,
    onOpenSelectedStocks: () -> Unit,
) {
    Column(
        modifier =
            Modifier.fillMaxSize(),
    ) {
        StockCatalogPageTitle(
            selectedCount =
                selectedCount,
            onOpenSelectedStocks =
                onOpenSelectedStocks,
        )

        HorizontalDivider()

        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag(
                        STOCK_CATALOG_LIST_TAG
                    ),
        ) {
            items(
                items =
                    stocks,
                key = { stock ->
                    stock.Code
                },
            ) { stock ->
                val selection =
                    selectionsByCode[
                        stock.Code
                    ]

                StockSelectionItem(
                    stock =
                        stock,
                    selection =
                        selection,
                    onSelect = {
                        onSelect(
                            stock.Code
                        )
                    },
                    onUnselect = {
                        onUnselect(
                            stock.Code
                        )
                    },
                )
            }
        }
    }
}

/**
 * Selected-stock ordering page.
 *
 * The title remains fixed while only the selected-stock LazyColumn
 * scrolls.
 */
@Composable
private fun SelectedStocksPage(
    selectedEntries:
        List<SelectedStockEntry>,
    selectedCount: Int,
    onMoveForward: (String) -> Unit,
    onMoveBackward: (String) -> Unit,
    onContinueAdding: () -> Unit,
) {
    Column(
        modifier =
            Modifier.fillMaxSize(),
    ) {
        SelectedStocksPageTitle(
            selectedCount =
                selectedCount,
            onContinueAdding =
                onContinueAdding,
        )

        HorizontalDivider()

        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag(
                        SELECTED_STOCKS_LIST_TAG
                    ),
        ) {
            items(
                items =
                    selectedEntries,
                key = { entry ->
                    entry.stock.Code
                },
            ) { entry ->
                val index =
                    selectedEntries
                        .indexOfFirst { candidate ->
                            candidate.stock.Code ==
                                entry.stock.Code
                        }

                SelectedStockSelectionItem(
                    stock =
                        entry.stock,
                    selection =
                        entry.selection,
                    canMoveForward =
                        index > 0,
                    canMoveBackward =
                        index >= 0 &&
                            index <
                                selectedEntries.lastIndex,
                    onMoveForward = {
                        onMoveForward(
                            entry.stock.Code
                        )
                    },
                    onMoveBackward = {
                        onMoveBackward(
                            entry.stock.Code
                        )
                    },
                )
            }
        }
    }
}

/**
 * Fixed title for the complete stock catalog.
 *
 * The selected-stock page is located to the right.
 */
@Composable
private fun StockCatalogPageTitle(
    selectedCount: Int,
    onOpenSelectedStocks: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    STOCK_SELECTION_TITLE_HEIGHT
                )
                .clickable(
                    onClick =
                        onOpenSelectedStocks,
                )
                .semantics {
                    contentDescription =
                        "查看已選股票"
                }
                .padding(
                    horizontal = 16.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Text(
            text =
                "已選股票 $selectedCount",
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )

        Text(
            text =
                "→",
            style =
                MaterialTheme
                    .typography
                    .titleLarge,
        )
    }
}

/**
 * Fixed title for the selected-stock ordering page.
 *
 * The complete stock catalog is located to the left.
 */
@Composable
private fun SelectedStocksPageTitle(
    selectedCount: Int,
    onContinueAdding: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    STOCK_SELECTION_TITLE_HEIGHT
                )
                .clickable(
                    onClick =
                        onContinueAdding,
                )
                .semantics {
                    contentDescription =
                        "繼續新增股票"
                }
                .padding(
                    horizontal = 16.dp,
                ),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Text(
            text =
                "←",
            style =
                MaterialTheme
                    .typography
                    .titleLarge,
        )

        Text(
            text =
                "繼續新增",
            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 12.dp,
                    ),
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
        )

        Text(
            text =
                "已選股票 $selectedCount",
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
        )
    }
}

private data class SelectedStockEntry(
    val stock: Stock,
    val selection: StockSelection,
)

/**
 * Both Pager-page titles intentionally share the same height.
 */
private val STOCK_SELECTION_TITLE_HEIGHT =
    56.dp

internal const val STOCK_CATALOG_LIST_TAG =
    "stock-catalog-list"

internal const val SELECTED_STOCKS_LIST_TAG =
    "selected-stocks-list"

private const val STOCK_CATALOG_PAGE =
    0

private const val SELECTED_STOCKS_PAGE =
    1

private const val STOCK_SELECTION_PAGE_COUNT =
    2
