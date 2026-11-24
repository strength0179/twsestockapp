package com.example.twsestockapp.data

import com.example.twsestockapp.data.fake.FakeTwseApi
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import java.math.BigDecimal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultDataRepositoryTest {

    @Test
    fun sameCodeFromThreeApisIsMergedIntoOneStock() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2330",
                    name = "Day Name",
                )
            ),
            averageItems = listOf(
                createAverageItem(
                    code = "2330",
                    name = "Average Name",
                )
            ),
            valuationItems = listOf(
                createValuationItem(
                    code = "2330",
                    name = "Valuation Name",
                )
            ),
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            1,
            result.size,
        )

        val stock =
            result.first()

        assertEquals(
            "2330",
            stock.Code,
        )

        assertEquals(
            "Day Name",
            stock.Name,
        )

        assertNotNull(stock.day)
        assertNotNull(stock.average)
        assertNotNull(stock.valuation)

        assertTrue(stock.hasDayData)
        assertTrue(stock.hasAverageData)
        assertTrue(stock.hasValuationData)
    }

    @Test
    fun missingApiDataRemainsNull() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2330",
                    name = "Day Name",
                )
            )
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            1,
            result.size,
        )

        val stock =
            result.first()

        assertNotNull(stock.day)
        assertNull(stock.average)
        assertNull(stock.valuation)

        assertTrue(stock.hasDayData)
        assertFalse(stock.hasAverageData)
        assertFalse(stock.hasValuationData)
    }

    @Test
    fun differentCodesFromDifferentApisProduceFullOuterJoin() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2330",
                    name = "Day Stock",
                )
            ),
            averageItems = listOf(
                createAverageItem(
                    code = "2454",
                    name = "Average Stock",
                )
            ),
            valuationItems = listOf(
                createValuationItem(
                    code = "2317",
                    name = "Valuation Stock",
                )
            ),
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            3,
            result.size,
        )

        assertEquals(
            listOf(
                "2330",
                "2454",
                "2317",
            ),
            result.map { stock ->
                stock.Code
            },
        )

        assertEquals(
            "Day Stock",
            result[0].Name,
        )

        assertEquals(
            "Average Stock",
            result[1].Name,
        )

        assertEquals(
            "Valuation Stock",
            result[2].Name,
        )
    }

    @Test
    fun stockNameUsesDefinedSourcePriority() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2330",
                    name = "Day Name",
                )
            ),
            averageItems = listOf(
                createAverageItem(
                    code = "2330",
                    name = "Average Name",
                )
            ),
            valuationItems = listOf(
                createValuationItem(
                    code = "2330",
                    name = "Valuation Name",
                )
            ),
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            "Day Name",
            result.first().Name,
        )
    }

    @Test
    fun averageNameIsUsedWhenDayNameIsBlank() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2330",
                    name = "",
                )
            ),
            averageItems = listOf(
                createAverageItem(
                    code = "2330",
                    name = "Average Name",
                )
            ),
            valuationItems = listOf(
                createValuationItem(
                    code = "2330",
                    name = "Valuation Name",
                )
            ),
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            "Average Name",
            result.first().Name,
        )
    }

    @Test
    fun emptyApiResponsesEmitEmptyStockList() = runTest {
        val api =
            FakeTwseApi()

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertTrue(
            result.isEmpty()
        )
    }

    @Test
    fun resultingStocksPreserveSourceOrder() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "2454",
                    name = "Stock A",
                ),
                createDayItem(
                    code = "2330",
                    name = "Stock B",
                ),
                createDayItem(
                    code = "2317",
                    name = "Stock C",
                ),
            )
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            listOf(
                "2454",
                "2330",
                "2317",
            ),
            result.map { stock ->
                stock.Code
            },
        )
    }

    @Test
    fun existingDayOrderIsPreservedBeforeAdditionalApiCodes() = runTest {
        val api = FakeTwseApi(
            dayItems = listOf(
                createDayItem(
                    code = "3000",
                    name = "Day One",
                ),
                createDayItem(
                    code = "1000",
                    name = "Day Two",
                ),
            ),
            averageItems = listOf(
                createAverageItem(
                    code = "2000",
                    name = "Average Only",
                )
            ),
            valuationItems = listOf(
                createValuationItem(
                    code = "0500",
                    name = "Valuation Only",
                )
            ),
        )

        val repository =
            DefaultDataRepository(api)

        val result =
            repository.data.first()

        assertEquals(
            listOf(
                "3000",
                "1000",
                "2000",
                "0500",
            ),
            result.map { stock ->
                stock.Code
            },
        )
    }

    private fun createDayItem(
        code: String,
        name: String,
    ): StockDayItem {
        return StockDayItem(
            Code = code,
            Name = name,
            TradeVolume = "1000",
            TradeValue = "100000",
            OpeningPrice = BigDecimal("100.00"),
            HighestPrice = BigDecimal("110.00"),
            LowestPrice = BigDecimal("95.00"),
            ClosingPrice = BigDecimal("105.00"),
            Change = BigDecimal("5.00"),
            Transaction = "100",
        )
    }

    private fun createAverageItem(
        code: String,
        name: String,
    ): StockDayAvgItem {
        return StockDayAvgItem(
            Code = code,
            Name = name,
            ClosingPrice = BigDecimal("105.00"),
            MonthlyAveragePrice = BigDecimal("102.50"),
        )
    }

    private fun createValuationItem(
        code: String,
        name: String,
    ): BwibbuItem {
        return BwibbuItem(
            Code = code,
            Name = name,
            PEratio = BigDecimal("20.50"),
            DividendYield = BigDecimal("2.50"),
            PBratio = BigDecimal("5.00"),
        )
    }
}
