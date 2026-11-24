package com.example.twsestockapp.data.local

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.twsestockapp.data.local.dao.SelectedStockDao
import com.example.twsestockapp.data.local.database.TWSEStockDatabase
import com.example.twsestockapp.data.local.entity.SelectedStockEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for the ordered selected-stock DAO.
 *
 * Each test uses an isolated in-memory Room database.
 */
@RunWith(AndroidJUnit4::class)
class SelectedStockDaoTest {

    private lateinit var database:
        TWSEStockDatabase

    private lateinit var dao:
        SelectedStockDao

    @Before
    fun createDatabase() {
        val context =
            ApplicationProvider
                .getApplicationContext<Context>()

        database =
            Room
                .inMemoryDatabaseBuilder<TWSEStockDatabase>(
                    context
                )
                .setDriver(
                    BundledSQLiteDriver()
                )
                .build()

        dao =
            database.selectedStockDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    /**
     * A new database contains no selected stocks.
     */
    @Test
    fun databaseInitiallyContainsNoSelections() = runTest {
        val selections =
            dao.observeSelections().first()

        assertTrue(
            selections.isEmpty()
        )

        assertEquals(
            0,
            dao.count()
        )
    }

    /**
     * Newly selected stocks are appended in selection order.
     */
    @Test
    fun selectingStocksAppendsThemInOrder() = runTest {
        select("2454")
        select("2330")
        select("2317")

        assertSelections(
            "2454" to 1,
            "2330" to 2,
            "2317" to 3
        )
    }

    /**
     * Selecting an existing code does not add or reorder it.
     */
    @Test
    fun duplicateSelectionDoesNotCreateOrReorderRows() = runTest {
        select("2330")
        select("2454")
        select("2330")

        assertSelections(
            "2330" to 1,
            "2454" to 2
        )

        assertEquals(
            2,
            dao.count()
        )
    }

    /**
     * Removing a stock compacts all later order values.
     */
    @Test
    fun unselectRemovesStockAndCompactsOrders() = runTest {
        select("2330")
        select("2454")
        select("2317")
        select("2603")

        dao.unselect(
            code = "2454"
        )

        assertSelections(
            "2330" to 1,
            "2317" to 2,
            "2603" to 3
        )
    }

    /**
     * Moving forward exchanges the stock with the previous position.
     *
     * The first stock cannot move further forward.
     */
    @Test
    fun moveForwardSwapsWithPreviousStock() = runTest {
        select("2330")
        select("2454")
        select("2317")

        dao.moveForward(
            code = "2317"
        )

        assertSelections(
            "2330" to 1,
            "2317" to 2,
            "2454" to 3
        )

        dao.moveForward(
            code = "2330"
        )

        assertSelections(
            "2330" to 1,
            "2317" to 2,
            "2454" to 3
        )
    }

    /**
     * Moving backward exchanges the stock with the next position.
     *
     * The final stock cannot move further backward.
     */
    @Test
    fun moveBackwardSwapsWithNextStock() = runTest {
        select("2330")
        select("2454")
        select("2317")

        dao.moveBackward(
            code = "2330"
        )

        assertSelections(
            "2454" to 1,
            "2330" to 2,
            "2317" to 3
        )

        dao.moveBackward(
            code = "2317"
        )

        assertSelections(
            "2454" to 1,
            "2330" to 2,
            "2317" to 3
        )
    }

    /**
     * Selects one stock. The DAO assigns the actual display order.
     */
    private suspend fun select(
        code: String
    ) {
        dao.select(
            SelectedStockEntity(
                code = code
            )
        )
    }

    /**
     * Validates both stock-code order and persisted order values.
     */
    private suspend fun assertSelections(
        vararg expected:
            Pair<String, Int>
    ) {
        val actual =
            dao.observeSelections()
                .first()
                .map { selection ->
                    selection.code to
                        selection.displayOrder
                }

        assertEquals(
            expected.toList(),
            actual
        )
    }
}
