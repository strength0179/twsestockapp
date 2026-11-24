package com.example.twsestockapp.data.json

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StockJsonParserTest {

    private val parser =
        StockJsonParser()

    @Test
    fun parseStockDayAll_parsesItems() {
        val json = """
            [
              {
                "Code": "2330",
                "Name": "台積電"
              },
              {
                "Code": "2317",
                "Name": "鴻海"
              }
            ]
        """.trimIndent()

        val result =
            parser.parseStockDayAll(json)

        assertEquals(
            2,
            result.size,
        )

        assertEquals(
            "2330",
            result[0].Code,
        )

        assertEquals(
            "台積電",
            result[0].Name,
        )

        assertEquals(
            "2317",
            result[1].Code,
        )

        assertEquals(
            "鴻海",
            result[1].Name,
        )
    }

    @Test
    fun parseStockDayAvgAll_parsesItems() {
        val json = """
            [
              {
                "Code": "2330",
                "Name": "台積電"
              },
              {
                "Code": "2317",
                "Name": "鴻海"
              }
            ]
        """.trimIndent()

        val result =
            parser.parseStockDayAvgAll(json)

        assertEquals(
            2,
            result.size,
        )

        assertEquals(
            "2330",
            result[0].Code,
        )

        assertEquals(
            "台積電",
            result[0].Name,
        )

        assertEquals(
            "2317",
            result[1].Code,
        )

        assertEquals(
            "鴻海",
            result[1].Name,
        )
    }

    @Test
    fun parseBwibbuAll_parsesItems() {
        val json = """
            [
              {
                "Code": "2330",
                "Name": "台積電"
              },
              {
                "Code": "2317",
                "Name": "鴻海"
              }
            ]
        """.trimIndent()

        val result =
            parser.parseBwibbuAll(json)

        assertEquals(
            2,
            result.size,
        )

        assertEquals(
            "2330",
            result[0].Code,
        )

        assertEquals(
            "台積電",
            result[0].Name,
        )

        assertEquals(
            "2317",
            result[1].Code,
        )

        assertEquals(
            "鴻海",
            result[1].Name,
        )
    }

    @Test
    fun parseStockDayAll_usesBigDecimalTypeAdapter() {
        val json = """
            [
              {
                "Code": "2330",
                "Name": "台積電",
                "TradeVolume": "1,234,567",
                "TradeValue": "1,000,000",
                "OpeningPrice": "1,234.56",
                "HighestPrice": "1,250.00",
                "LowestPrice": "1,200.00",
                "ClosingPrice": "1,240.50",
                "Change": "12.50",
                "Transaction": "12345"
              }
            ]
        """.trimIndent()

        val result =
            parser.parseStockDayAll(json)

        assertEquals(
            1,
            result.size,
        )

        assertEquals(
            BigDecimal("1234.56"),
            result[0].OpeningPrice,
        )

        assertEquals(
            BigDecimal("1250.00"),
            result[0].HighestPrice,
        )

        assertEquals(
            BigDecimal("1200.00"),
            result[0].LowestPrice,
        )

        assertEquals(
            BigDecimal("1240.50"),
            result[0].ClosingPrice,
        )

        assertEquals(
            BigDecimal("12.50"),
            result[0].Change,
        )

        assertEquals(
            "1,234,567",
            result[0].TradeVolume,
        )
    }

    @Test
    fun parseStockDayAll_emptyArrayReturnsEmptyList() {
        val result =
            parser.parseStockDayAll("[]")

        assertTrue(
            result.isEmpty()
        )
    }

    @Test
    fun parseStockDayAvgAll_emptyArrayReturnsEmptyList() {
        val result =
            parser.parseStockDayAvgAll("[]")

        assertTrue(
            result.isEmpty()
        )
    }

    @Test
    fun parseBwibbuAll_emptyArrayReturnsEmptyList() {
        val result =
            parser.parseBwibbuAll("[]")

        assertTrue(
            result.isEmpty()
        )
    }
}
