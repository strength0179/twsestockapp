package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.selection.StockFieldSelection
import com.example.twsestockapp.data.selection.StockSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure unit tests for [MainScreenStateFactory].
 *
 * These tests contain no Android, Room, ViewModel, or coroutine
 * dependencies.
 */
class MainScreenStateFactoryTest {

    @Test
    fun createSuccessOrdersStocksBySelectionDisplayOrder() {
        val state =
            MainScreenStateFactory.createSuccess(
                allStocks =
                    MAIN_SCREEN_FAKE_STOCKS,
                selections =
                    listOf(
                        StockSelection(
                            Code = "2330",
                            DisplayOrder = 3,
                        ),
                        StockSelection(
                            Code = "2454",
                            DisplayOrder = 1,
                        ),
                        StockSelection(
                            Code = "2317",
                            DisplayOrder = 2,
                        ),
                    ),
                fieldSelections =
                    emptyList(),
            )

        assertEquals(
            listOf(
                "2454",
                "2317",
                "2330",
            ),
            state.selections.map { selection ->
                selection.Code
            },
        )

        assertEquals(
            listOf(
                "2454",
                "2317",
                "2330",
            ),
            state.visibleStocks.map { stock ->
                stock.Code
            },
        )
    }

    @Test
    fun createSuccessOrdersFieldsByDisplayOrder() {
        val state =
            MainScreenStateFactory.createSuccess(
                allStocks =
                    MAIN_SCREEN_FAKE_STOCKS,
                selections =
                    selectionsOf(
                        "2330"
                    ),
                fieldSelections =
                    listOf(
                        StockFieldSelection(
                            FieldKey =
                                "peRatio",
                            DisplayOrder =
                                3,
                        ),
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
                    ),
            )

        assertEquals(
            listOf(
                "closingPrice",
                "tradeVolume",
                "peRatio",
            ),
            state.fieldSelections.map { selection ->
                selection.FieldKey
            },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.fieldSelections.map { selection ->
                selection.DisplayOrder
            },
        )
    }

    @Test
    fun createSuccessIgnoresUnknownStockCodeForVisibleStocks() {
        val state =
            MainScreenStateFactory.createSuccess(
                allStocks =
                    MAIN_SCREEN_FAKE_STOCKS,
                selections =
                    listOf(
                        StockSelection(
                            Code = "9999",
                            DisplayOrder = 1,
                        ),
                    ),
                fieldSelections =
                    emptyList(),
            )

        assertEquals(
            listOf(
                "9999"
            ),
            state.selections.map { selection ->
                selection.Code
            },
        )

        assertTrue(
            state.visibleStocks.isEmpty()
        )

        assertFalse(
            state.showSelectionPanel
        )
    }

    @Test
    fun createSuccessRequestsSelectionPanelWhenNoStocksSelected() {
        val state =
            MainScreenStateFactory.createSuccess(
                allStocks =
                    MAIN_SCREEN_FAKE_STOCKS,
                selections =
                    emptyList(),
                fieldSelections =
                    emptyList(),
            )

        assertTrue(
            state.selections.isEmpty()
        )

        assertTrue(
            state.visibleStocks.isEmpty()
        )

        assertTrue(
            state.showSelectionPanel
        )
    }
}
