package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection

/**
 * Stock data shared by MainScreen instrumented tests.
 */
internal val MAIN_SCREEN_TEST_STOCKS: List<Stock> =
    listOf(
        Stock(
            Code = "2317",
            Name = "鴻海",
        ),
        Stock(
            Code = "2330",
            Name = "台積電",
        ),
        Stock(
            Code = "2454",
            Name = "聯發科",
        ),
        Stock(
            Code = "2603",
            Name = "長榮",
        ),
    )

/**
 * Pager-field configuration shared by MainScreen tests.
 *
 * Order:
 *
 * 1. closingPrice
 * 2. tradeVolume
 * 3. peRatio
 */
internal val MAIN_SCREEN_TEST_FIELD_SELECTIONS:
    List<StockFieldSelection> =
    listOf(
        StockFieldSelection(
            FieldKey = "closingPrice",
            DisplayOrder = 1,
        ),
        StockFieldSelection(
            FieldKey = "tradeVolume",
            DisplayOrder = 2,
        ),
        StockFieldSelection(
            FieldKey = "peRatio",
            DisplayOrder = 3,
        ),
    )

/**
 * Standard successful state used by MainScreen tests.
 *
 * Selected stock order:
 *
 * 1. 2454
 * 2. 2330
 */
internal val MAIN_SCREEN_TEST_STATE:
    MainScreenUiState.Success =
    MainScreenUiState.Success(
        allStocks =
            MAIN_SCREEN_TEST_STOCKS,
        visibleStocks =
            listOf(
                MAIN_SCREEN_TEST_STOCKS.first { stock ->
                    stock.Code == "2454"
                },
                MAIN_SCREEN_TEST_STOCKS.first { stock ->
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
            MAIN_SCREEN_TEST_FIELD_SELECTIONS,
        showSelectionPanel =
            false,
    )

