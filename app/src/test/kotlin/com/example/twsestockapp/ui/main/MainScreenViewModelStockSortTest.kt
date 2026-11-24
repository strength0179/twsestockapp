package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.selection.StockSelection
import com.example.twsestockapp.test.MainDispatcherRule
import com.example.twsestockapp.ui.main.sort.StockSortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

/**
 * Tests presentation-only stock sorting exposed by MainScreenViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelStockSortTest {

    @get:Rule
    val mainDispatcherRule =
        MainDispatcherRule()

    @Test
    fun defaultSortPreservesPersistentSelectionOrder() = runTest {
        val fixture =
            fixture()

        startCollectingUiState(
            fixture.viewModel
        )

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            StockSortOption.Default,
            state.stockSortOption,
        )

        assertEquals(
            listOf(
                "A",
                "B",
                "C",
            ),
            state.visibleStocks
                .map { stock ->
                    stock.Code
                },
        )
    }

    @Test
    fun peRatioSortChangesOnlyVisiblePresentationOrder() = runTest {
        val fixture =
            fixture()

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel
            .selectStockSortOption(
                option =
                    StockSortOption.PeRatio,
            )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            StockSortOption.PeRatio,
            state.stockSortOption,
        )

        /*
         * PE:
         *
         * B = 30
         * C = 20
         * A = 10
         */
        assertEquals(
            listOf(
                "B",
                "C",
                "A",
            ),
            state.visibleStocks
                .map { stock ->
                    stock.Code
                },
        )

        /*
         * Complete stock collection remains untouched.
         */
        assertEquals(
            listOf(
                "A",
                "B",
                "C",
            ),
            state.allStocks
                .map { stock ->
                    stock.Code
                },
        )

        /*
         * Persistent selected-stock order also remains untouched.
         */
        assertEquals(
            listOf(
                "A",
                "B",
                "C",
            ),
            state.selections
                .map { selection ->
                    selection.Code
                },
        )

        assertEquals(
            listOf(
                1,
                2,
                3,
            ),
            state.selections
                .map { selection ->
                    selection.DisplayOrder
                },
        )
    }

    @Test
    fun changingSortOptionRecalculatesVisibleStocks() = runTest {
        val fixture =
            fixture()

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel
            .selectStockSortOption(
                option =
                    StockSortOption
                        .DividendYield,
            )

        runCurrent()

        var state =
            successState(
                fixture.viewModel
            )

        /*
         * Dividend yield:
         *
         * A = 5
         * C = 3
         * B = 1
         */
        assertEquals(
            listOf(
                "A",
                "C",
                "B",
            ),
            state.visibleStocks
                .map { stock ->
                    stock.Code
                },
        )

        fixture.viewModel
            .selectStockSortOption(
                option =
                    StockSortOption.PbRatio,
            )

        runCurrent()

        state =
            successState(
                fixture.viewModel
            )

        /*
         * PB:
         *
         * C = 9
         * B = 6
         * A = 2
         */
        assertEquals(
            listOf(
                "C",
                "B",
                "A",
            ),
            state.visibleStocks
                .map { stock ->
                    stock.Code
                },
        )
    }

    @Test
    fun selectingDefaultRestoresPersistentSelectionOrder() = runTest {
        val fixture =
            fixture()

        startCollectingUiState(
            fixture.viewModel
        )

        fixture.viewModel
            .selectStockSortOption(
                option =
                    StockSortOption.PeRatio,
            )

        runCurrent()

        fixture.viewModel
            .selectStockSortOption(
                option =
                    StockSortOption.Default,
            )

        runCurrent()

        val state =
            successState(
                fixture.viewModel
            )

        assertEquals(
            StockSortOption.Default,
            state.stockSortOption,
        )

        assertEquals(
            listOf(
                "A",
                "B",
                "C",
            ),
            state.visibleStocks
                .map { stock ->
                    stock.Code
                },
        )
    }

    private fun fixture():
        MainScreenViewModelFixture {

        val stocks =
            listOf(
                stock(
                    code =
                        "A",
                    peRatio =
                        "10",
                    dividendYield =
                        "5",
                    pbRatio =
                        "2",
                ),
                stock(
                    code =
                        "B",
                    peRatio =
                        "30",
                    dividendYield =
                        "1",
                    pbRatio =
                        "6",
                ),
                stock(
                    code =
                        "C",
                    peRatio =
                        "20",
                    dividendYield =
                        "3",
                    pbRatio =
                        "9",
                ),
            )

        return createMainScreenViewModelFixture(
            stocks =
                stocks,
            selections =
                listOf(
                    StockSelection(
                        Code =
                            "A",
                        DisplayOrder =
                            1,
                    ),
                    StockSelection(
                        Code =
                            "B",
                        DisplayOrder =
                            2,
                    ),
                    StockSelection(
                        Code =
                            "C",
                        DisplayOrder =
                            3,
                    ),
                ),
        )
    }

    private fun stock(
        code: String,
        peRatio: String,
        dividendYield: String,
        pbRatio: String,
    ): Stock {

        return Stock(
            Code =
                code,
            Name =
                code,
            valuation =
                BwibbuItem(
                    Code =
                        code,
                    Name =
                        code,
                    PEratio =
                        BigDecimal(
                            peRatio
                        ),
                    DividendYield =
                        BigDecimal(
                            dividendYield
                        ),
                    PBratio =
                        BigDecimal(
                            pbRatio
                        ),
                ),
        )
    }
}
