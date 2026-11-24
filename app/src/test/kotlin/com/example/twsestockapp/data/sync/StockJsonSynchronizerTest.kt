package com.example.twsestockapp.data.sync

import com.example.twsestockapp.data.local.json.StockJsonCache
import com.example.twsestockapp.data.local.json.StockJsonType
import com.example.twsestockapp.data.remote.TwseApi
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StockJsonSynchronizerTest {

    private val today =
        LocalDate.of(2026, 11, 22)

    @Test
    fun synchronize_whenTodayFilesExist_usesCacheWithoutCallingApi() =
        runBlocking {
            val cache = FakeStockJsonCache().apply {
                put(
                    date = today,
                    type = StockJsonType.STOCK_DAY_ALL,
                    json = STOCK_DAY_JSON,
                )

                put(
                    date = today,
                    type = StockJsonType.STOCK_DAY_AVG_ALL,
                    json = STOCK_DAY_AVG_JSON,
                )

                put(
                    date = today,
                    type = StockJsonType.BWIBBU_ALL,
                    json = BWIBBU_JSON,
                )
            }

            val api = FakeTwseApi()

            val synchronizer = createSynchronizer(
                api = api,
                cache = cache,
            )

            val result = synchronizer.synchronize()

            assertEquals(
                StockJsonSyncStatus.FROM_CACHE,
                result[StockJsonType.STOCK_DAY_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.FROM_CACHE,
                result[StockJsonType.STOCK_DAY_AVG_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.FROM_CACHE,
                result[StockJsonType.BWIBBU_ALL].status,
            )

            assertEquals(
                0,
                api.totalRawCallCount,
            )

            assertFalse(result.hasChanges)
        }

    @Test
    fun synchronize_whenNoCache_downloadsAndSavesAllTypes() =
        runBlocking {
            val cache = FakeStockJsonCache()

            val api = FakeTwseApi(
                stockDayJson = STOCK_DAY_JSON,
                stockDayAvgJson = STOCK_DAY_AVG_JSON,
                bwibbuJson = BWIBBU_JSON,
            )

            val synchronizer = createSynchronizer(
                api = api,
                cache = cache,
            )

            val result = synchronizer.synchronize()

            assertEquals(
                3,
                api.totalRawCallCount,
            )

            assertEquals(
                STOCK_DAY_JSON,
                cache.read(
                    date = today,
                    type = StockJsonType.STOCK_DAY_ALL,
                ),
            )

            assertEquals(
                STOCK_DAY_AVG_JSON,
                cache.read(
                    date = today,
                    type = StockJsonType.STOCK_DAY_AVG_ALL,
                ),
            )

            assertEquals(
                BWIBBU_JSON,
                cache.read(
                    date = today,
                    type = StockJsonType.BWIBBU_ALL,
                ),
            )

            assertEquals(
                StockJsonSyncStatus.DOWNLOADED_CHANGED,
                result[StockJsonType.STOCK_DAY_ALL].status,
            )

            assertTrue(result.hasChanges)
        }

    @Test
    fun synchronize_whenDownloadedJsonMatchesLatest_marksUnchanged() =
        runBlocking {
            val yesterday =
                LocalDate.of(2026, 11, 21)

            val cache = FakeStockJsonCache().apply {
                put(
                    date = yesterday,
                    type = StockJsonType.STOCK_DAY_ALL,
                    json = STOCK_DAY_JSON,
                )

                put(
                    date = yesterday,
                    type = StockJsonType.STOCK_DAY_AVG_ALL,
                    json = STOCK_DAY_AVG_JSON,
                )

                put(
                    date = yesterday,
                    type = StockJsonType.BWIBBU_ALL,
                    json = BWIBBU_JSON,
                )
            }

            val api = FakeTwseApi(
                stockDayJson = STOCK_DAY_JSON,
                stockDayAvgJson = STOCK_DAY_AVG_JSON,
                bwibbuJson = BWIBBU_JSON,
            )

            val result = createSynchronizer(
                api = api,
                cache = cache,
            ).synchronize()

            StockJsonType.entries.forEach { type ->
                assertEquals(
                    StockJsonSyncStatus.DOWNLOADED_UNCHANGED,
                    result[type].status,
                )

                assertTrue(
                    cache.exists(
                        date = today,
                        type = type,
                    )
                )
            }

            assertEquals(
                3,
                api.totalRawCallCount,
            )

            assertFalse(result.hasChanges)
        }

    @Test
    fun synchronize_whenDownloadedJsonDiffers_marksChanged() =
        runBlocking {
            val yesterday =
                LocalDate.of(2026, 11, 21)

            val cache = FakeStockJsonCache().apply {
                put(
                    date = yesterday,
                    type = StockJsonType.STOCK_DAY_ALL,
                    json = """[{"old":"stock-day"}]""",
                )

                put(
                    date = yesterday,
                    type = StockJsonType.STOCK_DAY_AVG_ALL,
                    json = STOCK_DAY_AVG_JSON,
                )

                put(
                    date = yesterday,
                    type = StockJsonType.BWIBBU_ALL,
                    json = BWIBBU_JSON,
                )
            }

            val api = FakeTwseApi(
                stockDayJson = STOCK_DAY_JSON,
                stockDayAvgJson = STOCK_DAY_AVG_JSON,
                bwibbuJson = BWIBBU_JSON,
            )

            val result = createSynchronizer(
                api = api,
                cache = cache,
            ).synchronize()

            assertEquals(
                StockJsonSyncStatus.DOWNLOADED_CHANGED,
                result[StockJsonType.STOCK_DAY_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.DOWNLOADED_UNCHANGED,
                result[StockJsonType.STOCK_DAY_AVG_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.DOWNLOADED_UNCHANGED,
                result[StockJsonType.BWIBBU_ALL].status,
            )

            assertEquals(
                STOCK_DAY_JSON,
                result[StockJsonType.STOCK_DAY_ALL].json,
            )

            assertTrue(result.hasChanges)
        }

    @Test
    fun synchronize_whenOnlyOneTodayFileIsMissing_downloadsOnlyMissingType() =
        runBlocking {
            val yesterday =
                LocalDate.of(2026, 11, 21)

            val cache = FakeStockJsonCache().apply {
                put(
                    date = today,
                    type = StockJsonType.STOCK_DAY_ALL,
                    json = STOCK_DAY_JSON,
                )

                put(
                    date = today,
                    type = StockJsonType.BWIBBU_ALL,
                    json = BWIBBU_JSON,
                )

                put(
                    date = yesterday,
                    type = StockJsonType.STOCK_DAY_AVG_ALL,
                    json = STOCK_DAY_AVG_JSON,
                )
            }

            val api = FakeTwseApi(
                stockDayJson = """unused""",
                stockDayAvgJson = STOCK_DAY_AVG_JSON,
                bwibbuJson = """unused""",
            )

            val result = createSynchronizer(
                api = api,
                cache = cache,
            ).synchronize()

            assertEquals(
                0,
                api.stockDayRawCallCount,
            )

            assertEquals(
                1,
                api.stockDayAvgRawCallCount,
            )

            assertEquals(
                0,
                api.bwibbuRawCallCount,
            )

            assertEquals(
                StockJsonSyncStatus.FROM_CACHE,
                result[StockJsonType.STOCK_DAY_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.DOWNLOADED_UNCHANGED,
                result[StockJsonType.STOCK_DAY_AVG_ALL].status,
            )

            assertEquals(
                StockJsonSyncStatus.FROM_CACHE,
                result[StockJsonType.BWIBBU_ALL].status,
            )

            assertFalse(result.hasChanges)
        }

    @Test
    fun synchronize_returnsInjectedCurrentDate() =
        runBlocking {
            val cache = FakeStockJsonCache()
            val api = FakeTwseApi()

            val result = createSynchronizer(
                api = api,
                cache = cache,
            ).synchronize()

            assertEquals(
                today,
                result.date,
            )
        }

    private fun createSynchronizer(
        api: TwseApi,
        cache: StockJsonCache,
    ): StockJsonSynchronizer {
        return DefaultStockJsonSynchronizer(
            api = api,
            cache = cache,
            currentDate = { today },
        )
    }

    private companion object {
        const val STOCK_DAY_JSON =
            """[{"Code":"00401A","Name":"主動摩根台灣鑫收"}]"""

        const val STOCK_DAY_AVG_JSON =
            """[{"Code":"00401A","ClosingPrice":"10.00"}]"""

        const val BWIBBU_JSON =
            """[{"Code":"00401A","PEratio":"12.34"}]"""
    }
}

private class FakeStockJsonCache : StockJsonCache {

    private val values =
        mutableMapOf<Pair<LocalDate, StockJsonType>, String>()

    fun put(
        date: LocalDate,
        type: StockJsonType,
        json: String,
    ) {
        values[date to type] = json
    }

    override suspend fun save(
        date: LocalDate,
        type: StockJsonType,
        json: String,
    ) {
        values[date to type] = json
    }

    override suspend fun exists(
        date: LocalDate,
        type: StockJsonType,
    ): Boolean {
        return values.containsKey(date to type)
    }

    override suspend fun read(
        date: LocalDate,
        type: StockJsonType,
    ): String? {
        return values[date to type]
    }

    override suspend fun readLatest(
        type: StockJsonType,
    ): String? {
        val latestDate = latestDate(type)
            ?: return null

        return values[latestDate to type]
    }

    override suspend fun latestDate(
        type: StockJsonType,
    ): LocalDate? {
        return values.keys
            .asSequence()
            .filter { (_, savedType) ->
                savedType == type
            }
            .map { (date, _) ->
                date
            }
            .maxOrNull()
    }
}

private class FakeTwseApi(
    private val stockDayJson: String = """[]""",
    private val stockDayAvgJson: String = """[]""",
    private val bwibbuJson: String = """[]""",
) : TwseApi {

    var stockDayRawCallCount: Int = 0
        private set

    var stockDayAvgRawCallCount: Int = 0
        private set

    var bwibbuRawCallCount: Int = 0
        private set

    val totalRawCallCount: Int
        get() =
            stockDayRawCallCount +
                stockDayAvgRawCallCount +
                bwibbuRawCallCount

    override suspend fun getStockDayAll(): List<StockDayItem> {
        return emptyList()
    }

    override suspend fun getStockDayAllRaw(): ResponseBody {
        stockDayRawCallCount += 1
        return responseBody(stockDayJson)
    }

    override suspend fun getStockDayAvgAll(): List<StockDayAvgItem> {
        return emptyList()
    }

    override suspend fun getStockDayAvgAllRaw(): ResponseBody {
        stockDayAvgRawCallCount += 1
        return responseBody(stockDayAvgJson)
    }

    override suspend fun getBwibbuAll(): List<BwibbuItem> {
        return emptyList()
    }

    override suspend fun getBwibbuAllRaw(): ResponseBody {
        bwibbuRawCallCount += 1
        return responseBody(bwibbuJson)
    }

    private fun responseBody(
        json: String,
    ): ResponseBody {
        return json.toResponseBody(
            "application/json; charset=utf-8".toMediaType()
        )
    }
}

