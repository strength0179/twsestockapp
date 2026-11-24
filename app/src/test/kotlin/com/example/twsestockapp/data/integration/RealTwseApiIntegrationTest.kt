package com.example.twsestockapp.data.integration

import com.example.twsestockapp.data.DefaultDataRepository
import com.example.twsestockapp.data.remote.TwseApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * Real-network integration tests for the TWSE data pipeline.
 *
 * Unlike normal unit tests, these tests call the real TWSE OpenAPI.
 *
 * They verify the complete production data path:
 *
 * TWSE OpenAPI
 * -> Retrofit
 * -> Gson
 * -> BigDecimalTypeAdapter
 * -> TwseApi
 * -> DefaultDataRepository
 * -> Flow<List<Stock>>
 *
 * These tests are disabled by default because their result depends on
 * external network and service availability.
 *
 * To enable them, set environment variable:
 *
 * RUN_TWSE_INTEGRATION=true
 */
class RealTwseApiIntegrationTest {

    /**
     * Tests the complete repository pipeline against the real TWSE API.
     *
     * Expected behavior:
     *
     * - The repository emits a List<Stock>.
     * - The list is not empty.
     * - Returned stocks contain non-blank stock codes.
     * - At least one Stock contains data from one of the three APIs.
     */
    @Test
    fun realTwseRepositoryReturnsStocks() = runTest {

        requireIntegrationTestEnabled()

        val repository = DefaultDataRepository(
            api = TwseApiClient.api
        )

        val stocks = repository.data.first()

        assertFalse(
            "TWSE returned no aggregated stocks",
            stocks.isEmpty()
        )

        assertTrue(
            "At least one stock contains a blank Code",
            stocks.all { stock ->
                stock.Code.isNotBlank()
            }
        )

        assertTrue(
            "No stock contains API source data",
            stocks.any { stock ->
                stock.hasDayData ||
                    stock.hasAverageData ||
                    stock.hasValuationData
            }
        )
    }

    /**
     * Calls STOCK_DAY_ALL directly through the production TwseApi.
     */
    @Test
    fun realStockDayApiReturnsData() = runTest {

        requireIntegrationTestEnabled()

        val result =
            TwseApiClient.api.getStockDayAll()

        assertFalse(
            "STOCK_DAY_ALL returned an empty list",
            result.isEmpty()
        )

        val firstItem = result.first()

        assertTrue(
            "STOCK_DAY_ALL returned a blank Code",
            firstItem.Code.isNotBlank()
        )

        assertTrue(
            "STOCK_DAY_ALL returned a blank Name",
            firstItem.Name.isNotBlank()
        )
    }

    /**
     * Calls STOCK_DAY_AVG_ALL directly through the production TwseApi.
     */
    @Test
    fun realStockDayAvgApiReturnsData() = runTest {

        requireIntegrationTestEnabled()

        val result =
            TwseApiClient.api.getStockDayAvgAll()

        assertFalse(
            "STOCK_DAY_AVG_ALL returned an empty list",
            result.isEmpty()
        )

        val firstItem = result.first()

        assertTrue(firstItem.Code.isNotBlank())
        assertTrue(firstItem.Name.isNotBlank())
    }

    /**
     * Calls BWIBBU_ALL directly through the production TwseApi.
     */
    @Test
    fun realBwibbuApiReturnsData() = runTest {

        requireIntegrationTestEnabled()

        val result =
            TwseApiClient.api.getBwibbuAll()

        assertFalse(
            "BWIBBU_ALL returned an empty list",
            result.isEmpty()
        )

        val firstItem = result.first()

        assertTrue(firstItem.Code.isNotBlank())
        assertTrue(firstItem.Name.isNotBlank())
    }

    /**
     * Enables real-network tests only when explicitly requested.
     *
     * Normal unit-test runs therefore remain deterministic and do not
     * depend on TWSE or Internet availability.
     */
    private fun requireIntegrationTestEnabled() {

        val enabled =
            System.getenv("RUN_TWSE_INTEGRATION")
                ?.equals(
                    other = "true",
                    ignoreCase = true
                )
                ?: false

        assumeTrue(
            "Real TWSE integration tests are disabled. " +
                "Set RUN_TWSE_INTEGRATION=true to enable them.",
            enabled
        )
    }
}