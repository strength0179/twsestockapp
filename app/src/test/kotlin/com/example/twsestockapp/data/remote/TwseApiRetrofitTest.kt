package com.example.twsestockapp.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class TwseApiRetrofitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: TwseApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = TwseApiClient.createApi(
            mockWebServer.url("/").toString()
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun stockDayAvgResponseUsesCustomBigDecimalAdapter() = runTest {
        val responseBody = """
            [
              {
                "Code": "2330",
                "Name": "台積電",
                "ClosingPrice": "1,234.50",
                "MonthlyAveragePrice": "1,200.25"
              },
              {
                "Code": "9999",
                "Name": "測試股票",
                "ClosingPrice": "--",
                "MonthlyAveragePrice": ""
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(
                    "Content-Type",
                    "application/json; charset=utf-8"
                )
                .setBody(responseBody)
        )

        val result = api.getStockDayAvgAll()

        assertEquals(2, result.size)

        val firstItem = result[0]

        assertEquals("2330", firstItem.Code)
        assertEquals("台積電", firstItem.Name)

        assertEquals(
            BigDecimal("1234.50"),
            firstItem.ClosingPrice
        )

        assertEquals(
            BigDecimal("1200.25"),
            firstItem.MonthlyAveragePrice
        )

        val secondItem = result[1]

        assertEquals("9999", secondItem.Code)
        assertNull(secondItem.ClosingPrice)
        assertNull(secondItem.MonthlyAveragePrice)
    }

    @Test
    fun stockDayAvgApiUsesExpectedRequestPathAndMethod() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("[]")
        )

        api.getStockDayAvgAll()

        val request = mockWebServer.takeRequest()

        assertEquals(
            "/exchangeReport/STOCK_DAY_AVG_ALL",
            request.path
        )

        assertEquals(
            "GET",
            request.method
        )
    }
}