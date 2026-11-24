package com.example.twsestockapp.data

import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import java.math.BigDecimal

/**
 * Pure JVM tests for [mergeStocks].
 *
 * These tests isolate stock-data aggregation rules from repository
 * loading, caching, synchronization, networking, and Flow behavior.
 */
class StockDataMergerTest {

    /**
     * Items with the same stock code from all three data sources must
     * become one Stock containing all three source models.
     */
    @Test
    fun sameCodeFromThreeSourcesMergesIntoOneStock() {
        val day =
            dayItem(
                code = "2330",
                name = "台積電",
            )

        val average =
            averageItem(
                code = "2330",
                name = "台積電",
            )

        val valuation =
            valuationItem(
                code = "2330",
                name = "台積電",
            )

        val stocks =
            mergeStocks(
                dayItems =
                    listOf(
                        day
                    ),
                averageItems =
                    listOf(
                        average
                    ),
                valuationItems =
                    listOf(
                        valuation
                    ),
            )

        assertEquals(
            1,
            stocks.size,
        )

        val stock =
            stocks.single()

        assertEquals(
            "2330",
            stock.Code,
        )

        assertEquals(
            "台積電",
            stock.Name,
        )

        assertSame(
            day,
            stock.day,
        )

        assertSame(
            average,
            stock.average,
        )

        assertSame(
            valuation,
            stock.valuation,
        )
    }

    /**
     * mergeStocks performs a full outer join by stock code.
     *
     * Codes appearing in only one source must still appear in the
     * resulting Stock collection.
     */
    @Test
    fun codesExistingInOnlyOneSourceArePreserved() {
        val stocks =
            mergeStocks(
                dayItems =
                    listOf(
                        dayItem(
                            code = "DAY_ONLY",
                            name = "Day",
                        ),
                    ),
                averageItems =
                    listOf(
                        averageItem(
                            code = "AVERAGE_ONLY",
                            name = "Average",
                        ),
                    ),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "VALUATION_ONLY",
                            name = "Valuation",
                        ),
                    ),
            )

        assertEquals(
            listOf(
                "DAY_ONLY",
                "AVERAGE_ONLY",
                "VALUATION_ONLY",
            ),
            stocks.map { stock ->
                stock.Code
            },
        )
    }

    /**
     * Stock names use this priority:
     *
     * 1. day
     * 2. average
     * 3. valuation
     */
    @Test
    fun namePrefersDayThenAverageThenValuation() {
        val allSources =
            mergeStocks(
                dayItems =
                    listOf(
                        dayItem(
                            code = "A",
                            name = "Day Name",
                        ),
                    ),
                averageItems =
                    listOf(
                        averageItem(
                            code = "A",
                            name = "Average Name",
                        ),
                    ),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "A",
                            name = "Valuation Name",
                        ),
                    ),
            )

        assertEquals(
            "Day Name",
            allSources.single().Name,
        )

        val withoutDay =
            mergeStocks(
                dayItems =
                    emptyList(),
                averageItems =
                    listOf(
                        averageItem(
                            code = "B",
                            name = "Average Name",
                        ),
                    ),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "B",
                            name = "Valuation Name",
                        ),
                    ),
            )

        assertEquals(
            "Average Name",
            withoutDay.single().Name,
        )

        val valuationOnly =
            mergeStocks(
                dayItems =
                    emptyList(),
                averageItems =
                    emptyList(),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "C",
                            name = "Valuation Name",
                        ),
                    ),
            )

        assertEquals(
            "Valuation Name",
            valuationOnly.single().Name,
        )
    }

    /**
     * Blank names do not block fallback to a lower-priority source.
     */
    @Test
    fun blankNamesFallBackToNextSource() {
        val stocks =
            mergeStocks(
                dayItems =
                    listOf(
                        dayItem(
                            code = "2330",
                            name = "   ",
                        ),
                    ),
                averageItems =
                    listOf(
                        averageItem(
                            code = "2330",
                            name = "",
                        ),
                    ),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "2330",
                            name = "Fallback Name",
                        ),
                    ),
            )

        assertEquals(
            "Fallback Name",
            stocks.single().Name,
        )
    }

    /**
     * New stock codes preserve source insertion priority:
     *
     * 1. day codes
     * 2. additional average codes
     * 3. additional valuation codes
     *
     * Duplicate codes from later sources do not create extra Stocks.
     */
    @Test
    fun outputPreservesDatasetInsertionPriority() {
        val stocks =
            mergeStocks(
                dayItems =
                    listOf(
                        dayItem(
                            code = "A",
                            name = "A",
                        ),
                        dayItem(
                            code = "B",
                            name = "B",
                        ),
                    ),
                averageItems =
                    listOf(
                        averageItem(
                            code = "B",
                            name = "B",
                        ),
                        averageItem(
                            code = "C",
                            name = "C",
                        ),
                    ),
                valuationItems =
                    listOf(
                        valuationItem(
                            code = "C",
                            name = "C",
                        ),
                        valuationItem(
                            code = "D",
                            name = "D",
                        ),
                    ),
            )

        assertEquals(
            listOf(
                "A",
                "B",
                "C",
                "D",
            ),
            stocks.map { stock ->
                stock.Code
            },
        )
    }

    /**
     * Missing source datasets remain null on the merged Stock rather
     * than creating placeholder source models.
     */
    @Test
    fun missingDatasetLeavesCorrespondingModelNull() {
        val day =
            dayItem(
                code = "2330",
                name = "台積電",
            )

        val stocks =
            mergeStocks(
                dayItems =
                    listOf(
                        day
                    ),
                averageItems =
                    emptyList(),
                valuationItems =
                    emptyList(),
            )

        val stock =
            stocks.single()

        assertSame(
            day,
            stock.day,
        )

        assertNull(
            stock.average
        )

        assertNull(
            stock.valuation
        )
    }

    private fun dayItem(
        code: String,
        name: String,
    ): StockDayItem {

        return StockDayItem(
            Code =
                code,
            Name =
                name,
            TradeVolume =
                "1000",
            TradeValue =
                "100000",
            OpeningPrice =
                BigDecimal("100"),
            HighestPrice =
                BigDecimal("105"),
            LowestPrice =
                BigDecimal("95"),
            ClosingPrice =
                BigDecimal("102"),
            Change =
                BigDecimal("2"),
            Transaction =
                "100",
        )
    }

    private fun averageItem(
        code: String,
        name: String,
    ): StockDayAvgItem {

        return StockDayAvgItem(
            Code =
                code,
            Name =
                name,
            ClosingPrice =
                BigDecimal("102"),
            MonthlyAveragePrice =
                BigDecimal("99"),
        )
    }

    private fun valuationItem(
        code: String,
        name: String,
    ): BwibbuItem {

        return BwibbuItem(
            Code =
                code,
            Name =
                name,
            PEratio =
                BigDecimal("20"),
            DividendYield =
                BigDecimal("2.5"),
            PBratio =
                BigDecimal("5"),
        )
    }
}
