package com.example.twsestockapp.data.local.json

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FileStockJsonCacheTest {

    private lateinit var cacheDirectory: File
    private lateinit var cache: StockJsonCache

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        cacheDirectory = File(
            context.filesDir,
            "stock-json",
        )

        cacheDirectory.deleteRecursively()

        cache = FileStockJsonCache(context)
    }

    @After
    fun tearDown() {
        cacheDirectory.deleteRecursively()
    }

    @Test
    fun save_createsExpectedFile() = runBlocking {
        val date = LocalDate.of(2026, 11, 22)
        val json = """[{"Code":"00401A"}]"""

        cache.save(
            date = date,
            type = StockJsonType.STOCK_DAY_ALL,
            json = json,
        )

        val expectedFile = File(
            cacheDirectory,
            "2026-11-22/stock_day_all.json",
        )

        assertTrue(expectedFile.isFile)

        assertTrue(
            cache.exists(
                date = date,
                type = StockJsonType.STOCK_DAY_ALL,
            )
        )
    }

    @Test
    fun saveAndRead_preservesOriginalJsonExactly() = runBlocking {
        val date = LocalDate.of(2026, 11, 22)

        val json = """
            [
              {
                "Code": "00401A",
                "Name": "主動摩根台灣鑫收"
              },
              {
                "Code": "00406A",
                "Name": "主動中信台灣收益"
              }
            ]
        """.trimIndent()

        cache.save(
            date = date,
            type = StockJsonType.STOCK_DAY_ALL,
            json = json,
        )

        val result = cache.read(
            date = date,
            type = StockJsonType.STOCK_DAY_ALL,
        )

        assertEquals(
            json,
            result,
        )
    }

    @Test
    fun read_returnsNullWhenFileDoesNotExist() = runBlocking {
        val date = LocalDate.of(2026, 11, 22)

        val result = cache.read(
            date = date,
            type = StockJsonType.STOCK_DAY_ALL,
        )

        assertNull(result)

        assertFalse(
            cache.exists(
                date = date,
                type = StockJsonType.STOCK_DAY_ALL,
            )
        )
    }

    @Test
    fun save_threeTypesDoNotOverwriteEachOther() = runBlocking {
        val date = LocalDate.of(2026, 11, 22)

        val stockDayJson =
            """{"source":"STOCK_DAY_ALL"}"""

        val stockDayAvgJson =
            """{"source":"STOCK_DAY_AVG_ALL"}"""

        val bwibbuJson =
            """{"source":"BWIBBU_ALL"}"""

        cache.save(
            date = date,
            type = StockJsonType.STOCK_DAY_ALL,
            json = stockDayJson,
        )

        cache.save(
            date = date,
            type = StockJsonType.STOCK_DAY_AVG_ALL,
            json = stockDayAvgJson,
        )

        cache.save(
            date = date,
            type = StockJsonType.BWIBBU_ALL,
            json = bwibbuJson,
        )

        assertEquals(
            stockDayJson,
            cache.read(
                date = date,
                type = StockJsonType.STOCK_DAY_ALL,
            ),
        )

        assertEquals(
            stockDayAvgJson,
            cache.read(
                date = date,
                type = StockJsonType.STOCK_DAY_AVG_ALL,
            ),
        )

        assertEquals(
            bwibbuJson,
            cache.read(
                date = date,
                type = StockJsonType.BWIBBU_ALL,
            ),
        )
    }

    @Test
    fun latestDate_returnsNewestDateForSpecifiedType() = runBlocking {
        cache.save(
            date = LocalDate.of(2026, 11, 20),
            type = StockJsonType.STOCK_DAY_ALL,
            json = """{"date":"20"}""",
        )

        cache.save(
            date = LocalDate.of(2026, 11, 22),
            type = StockJsonType.STOCK_DAY_ALL,
            json = """{"date":"22"}""",
        )

        cache.save(
            date = LocalDate.of(2026, 11, 21),
            type = StockJsonType.STOCK_DAY_ALL,
            json = """{"date":"21"}""",
        )

        assertEquals(
            LocalDate.of(2026, 11, 22),
            cache.latestDate(
                StockJsonType.STOCK_DAY_ALL,
            ),
        )
    }

    @Test
    fun latestDate_isIndependentForEachType() = runBlocking {
        cache.save(
            date = LocalDate.of(2026, 11, 22),
            type = StockJsonType.STOCK_DAY_ALL,
            json = """{"day":"22"}""",
        )

        cache.save(
            date = LocalDate.of(2026, 11, 20),
            type = StockJsonType.BWIBBU_ALL,
            json = """{"bwibbu":"20"}""",
        )

        assertEquals(
            LocalDate.of(2026, 11, 22),
            cache.latestDate(
                StockJsonType.STOCK_DAY_ALL,
            ),
        )

        assertEquals(
            LocalDate.of(2026, 11, 20),
            cache.latestDate(
                StockJsonType.BWIBBU_ALL,
            ),
        )

        assertNull(
            cache.latestDate(
                StockJsonType.STOCK_DAY_AVG_ALL,
            )
        )
    }

    @Test
    fun readLatest_returnsNewestJsonForSpecifiedType() = runBlocking {
        val oldJson = """{"version":"old"}"""
        val newJson = """{"version":"new"}"""

        cache.save(
            date = LocalDate.of(2026, 11, 20),
            type = StockJsonType.STOCK_DAY_ALL,
            json = oldJson,
        )

        cache.save(
            date = LocalDate.of(2026, 11, 22),
            type = StockJsonType.STOCK_DAY_ALL,
            json = newJson,
        )

        cache.save(
            date = LocalDate.of(2026, 11, 23),
            type = StockJsonType.BWIBBU_ALL,
            json = """{"other":"newer-date"}""",
        )

        val result = cache.readLatest(
            StockJsonType.STOCK_DAY_ALL,
        )

        assertEquals(
            newJson,
            result,
        )
    }

    @Test
    fun latestDate_ignoresInvalidDirectoriesAndMissingType() = runBlocking {
        cache.save(
            date = LocalDate.of(2026, 11, 21),
            type = StockJsonType.STOCK_DAY_ALL,
            json = """{"valid":true}""",
        )

        File(
            cacheDirectory,
            "invalid-date",
        ).mkdirs()

        val newerDirectory = File(
            cacheDirectory,
            "2026-11-23",
        )

        newerDirectory.mkdirs()

        File(
            newerDirectory,
            StockJsonType.BWIBBU_ALL.fileName,
        ).writeText(
            """{"different-type":true}"""
        )

        assertEquals(
            LocalDate.of(2026, 11, 21),
            cache.latestDate(
                StockJsonType.STOCK_DAY_ALL,
            ),
        )
    }
}
