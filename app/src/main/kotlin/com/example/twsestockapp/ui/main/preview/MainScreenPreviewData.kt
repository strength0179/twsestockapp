package com.example.twsestockapp.ui.main.preview

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.ui.main.MainScreenUiState

/**
 * Fixed stock data shared by MainScreen Compose Previews.
 */
internal val PREVIEW_STOCKS: List<Stock> =
    listOf(
        Stock(
            Code = "2330",
            Name = "台積電",
        ),
        Stock(
            Code = "2454",
            Name = "聯發科",
        ),
        Stock(
            Code = "2317",
            Name = "鴻海",
        ),
        Stock(
            Code = "2603",
            Name = "長榮",
        ),
    )

/**
 * Pager fields enabled in Preview state.
 *
 * Order:
 *
 * 1. closingPrice
 * 2. tradeVolume
 * 3. peRatio
 */
internal val PREVIEW_FIELD_SELECTIONS:
    List<StockFieldSelection> =
    listOf(
        StockFieldSelection(
            FieldKey =
                "closingPrice",
            DisplayOrder =
                1,
        ),
        StockFieldSelection(
            FieldKey =
                "tradeVolume",
            DisplayOrder =
                2,
        ),
        StockFieldSelection(
            FieldKey =
                "peRatio",
            DisplayOrder =
                3,
        ),
    )

/**
 * Shared successful state for standard MainScreen Previews.
 */
internal val PREVIEW_MAIN_SCREEN_STATE:
    MainScreenUiState.Success =
    MainScreenUiState.Success(
        allStocks =
            PREVIEW_STOCKS,
        visibleStocks =
            listOf(
                PREVIEW_STOCKS.first { stock ->
                    stock.Code == "2454"
                },
                PREVIEW_STOCKS.first { stock ->
                    stock.Code == "2330"
                },
            ),
        selections =
            listOf(
                StockSelection(
                    Code = "2454",
                    DisplayOrder = 1,
                ),
                StockSelection(
                    Code = "2330",
                    DisplayOrder = 2,
                ),
            ),
        fieldSelections =
            PREVIEW_FIELD_SELECTIONS,
        showSelectionPanel =
            false,
    )

/**
 * State representing a new user without selected stocks.
 */
internal val PREVIEW_EMPTY_STOCK_SELECTION_STATE:
    MainScreenUiState.Success =
    MainScreenUiState.Success(
        allStocks =
            PREVIEW_STOCKS,
        visibleStocks =
            emptyList(),
        selections =
            emptyList(),
        fieldSelections =
            PREVIEW_FIELD_SELECTIONS,
        showSelectionPanel =
            true,
    )
