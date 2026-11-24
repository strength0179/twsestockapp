package com.example.twsestockapp.ui.main.sort

import com.example.twsestockapp.data.model.Stock
import com.example.twsestockapp.data.remote.model.BwibbuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.math.BigDecimal

/**
 * Pure JVM tests for [resolveStockSort].
 */
class StockSortResolverTest {

    @Test
    fun defaultPreservesOriginalStockOrder() {
        val stocks =
            listOf(
                stock(
                    code = "A",
                    peRatio = "10",
                ),
                stock(
                    code = "B",
                    peRatio = "30",
                ),
                stock(
                    code = "C",
                    peRatio = "20",
                ),
            )

        val result =
            resolveStockSort(
                stocks =
                    stocks,
                option =
                    StockSortOption.Default,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "A",
                    "B",
                    "C",
                ),
        )

        /*
         * Default does not needlessly allocate a different list.
         */
        assertSame(
            stocks,
            result,
        )
    }

    @Test
    fun peRatioUsesDescendingOrder() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "A",
                            peRatio = "10",
                        ),
                        stock(
                            code = "B",
                            peRatio = "30",
                        ),
                        stock(
                            code = "C",
                            peRatio = "20",
                        ),
                    ),
                option =
                    StockSortOption.PeRatio,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "B",
                    "C",
                    "A",
                ),
        )
    }

    @Test
    fun dividendYieldUsesDescendingOrder() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "A",
                            dividendYield = "1.5",
                        ),
                        stock(
                            code = "B",
                            dividendYield = "5.4",
                        ),
                        stock(
                            code = "C",
                            dividendYield = "3.2",
                        ),
                    ),
                option =
                    StockSortOption.DividendYield,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "B",
                    "C",
                    "A",
                ),
        )
    }

    @Test
    fun pbRatioUsesDescendingOrder() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "A",
                            pbRatio = "2.1",
                        ),
                        stock(
                            code = "B",
                            pbRatio = "6.9",
                        ),
                        stock(
                            code = "C",
                            pbRatio = "4.3",
                        ),
                    ),
                option =
                    StockSortOption.PbRatio,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "B",
                    "C",
                    "A",
                ),
        )
    }

    @Test
    fun nullValuationValuesArePlacedLast() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "A",
                            peRatio = "10",
                        ),
                        stock(
                            code = "NO_VALUE",
                        ),
                        stock(
                            code = "B",
                            peRatio = "30",
                        ),
                    ),
                option =
                    StockSortOption.PeRatio,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "B",
                    "A",
                    "NO_VALUE",
                ),
        )
    }

    @Test
    fun equalValuesPreserveOriginalRelativeOrder() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "FIRST",
                            dividendYield = "3.5",
                        ),
                        stock(
                            code = "SECOND",
                            dividendYield = "3.5",
                        ),
                        stock(
                            code = "THIRD",
                            dividendYield = "3.5",
                        ),
                    ),
                option =
                    StockSortOption.DividendYield,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "FIRST",
                    "SECOND",
                    "THIRD",
                ),
        )
    }

    @Test
    fun multipleNullValuesPreserveOriginalRelativeOrder() {
        val result =
            resolveStockSort(
                stocks =
                    listOf(
                        stock(
                            code = "NULL_FIRST",
                        ),
                        stock(
                            code = "VALUE",
                            pbRatio = "5",
                        ),
                        stock(
                            code = "NULL_SECOND",
                        ),
                    ),
                option =
                    StockSortOption.PbRatio,
            )

        assertCodes(
            actual =
                result,
            expected =
                listOf(
                    "VALUE",
                    "NULL_FIRST",
                    "NULL_SECOND",
                ),
        )
    }

    @Test
    fun sortingDoesNotModifyInputList() {
        val stocks =
            listOf(
                stock(
                    code = "A",
                    peRatio = "10",
                ),
                stock(
                    code = "B",
                    peRatio = "30",
                ),
            )

        resolveStockSort(
            stocks =
                stocks,
            option =
                StockSortOption.PeRatio,
        )

        assertCodes(
            actual =
                stocks,
            expected =
                listOf(
                    "A",
                    "B",
                ),
        )
    }

    private fun stock(
        code: String,
        peRatio: String? = null,
        dividendYield: String? = null,
        pbRatio: String? = null,
    ): Stock {

        val hasValuation =
            peRatio != null ||
                dividendYield != null ||
                pbRatio != null

        return Stock(
            Code =
                code,
            Name =
                code,
            valuation =
                if (hasValuation) {
                    BwibbuItem(
                        Code =
                            code,
                        Name =
                            code,
                        PEratio =
                            peRatio
                                ?.let(
                                    ::BigDecimal
                                ),
                        DividendYield =
                            dividendYield
                                ?.let(
                                    ::BigDecimal
                                ),
                        PBratio =
                            pbRatio
                                ?.let(
                                    ::BigDecimal
                                ),
                    )
                } else {
                    null
                },
        )
    }

    private fun assertCodes(
        actual: List<Stock>,
        expected: List<String>,
    ) {
        assertEquals(
            expected,
            actual.map { stock ->
                stock.Code
            },
        )
    }
}
