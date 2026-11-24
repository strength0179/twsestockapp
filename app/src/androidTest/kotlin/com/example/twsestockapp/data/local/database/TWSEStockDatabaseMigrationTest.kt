package com.example.twsestockapp.data.local.database

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented tests for [TWSEStockDatabase.MIGRATION_2_3].
 *
 * These tests create a version-2-style SQLite database, execute the
 * production migration directly, and inspect the resulting schema and
 * data through SQLiteConnection.
 *
 * Verified behavior:
 *
 * - selected_stock_fields is created
 * - all eight default Pager fields are inserted
 * - default field order is correct
 * - existing selected_stocks data remains unchanged
 */
@RunWith(AndroidJUnit4::class)
class TWSEStockDatabaseMigrationTest {

    private lateinit var context:
        Context

    private lateinit var databaseFile:
        File

    @Before
    fun prepareDatabaseFile() {
        context =
            ApplicationProvider
                .getApplicationContext()

        databaseFile =
            context.getDatabasePath(
                TEST_DATABASE_NAME
            )

        context.deleteDatabase(
            TEST_DATABASE_NAME
        )

        databaseFile.parentFile
            ?.mkdirs()
    }

    @After
    fun deleteDatabaseFile() {
        context.deleteDatabase(
            TEST_DATABASE_NAME
        )
    }

    /**
     * Migration creates selected_stock_fields and installs all current
     * Pager fields in their original default order.
     */
    @Test
    fun migration2To3CreatesDefaultOrderedFieldSelections() = runTest {
        val connection =
            BundledSQLiteDriver()
                .open(
                    databaseFile.absolutePath
                )

        try {
            createVersion2Schema(
                connection =
                    connection,
            )

            TWSEStockDatabase
                .MIGRATION_2_3
                .migrate(
                    connection =
                        connection,
                )

            val fields =
                mutableListOf<
                    Pair<String, Long>
                >()

            connection.prepare(
                """
                SELECT fieldKey, displayOrder
                FROM selected_stock_fields
                ORDER BY displayOrder ASC
                """.trimIndent()
            ).use { statement ->
                while (
                    statement.step()
                ) {
                    fields +=
                        statement.getText(0) to
                            statement.getLong(1)
                }
            }

            assertEquals(
                listOf(
                    "closingPrice" to 1L,
                    "change" to 2L,
                    "changePercentage" to 3L,
                    "tradeVolume" to 4L,
                    "monthlyAveragePrice" to 5L,
                    "peRatio" to 6L,
                    "dividendYield" to 7L,
                    "pbRatio" to 8L,
                ),
                fields,
            )
        } finally {
            connection.close()
        }
    }

    /**
     * Adding the Pager-field table must not modify existing ordered
     * stock selections stored by database version 2.
     */
    @Test
    fun migration2To3PreservesExistingSelectedStocks() = runTest {
        val connection =
            BundledSQLiteDriver()
                .open(
                    databaseFile.absolutePath
                )

        try {
            createVersion2Schema(
                connection =
                    connection,
            )

            connection.execSQL(
                """
                INSERT INTO selected_stocks
                    (code, displayOrder)
                VALUES
                    ('2454', 1),
                    ('2330', 2),
                    ('2317', 3)
                """.trimIndent()
            )

            TWSEStockDatabase
                .MIGRATION_2_3
                .migrate(
                    connection =
                        connection,
                )

            val selectedStocks =
                mutableListOf<
                    Pair<String, Long>
                >()

            connection.prepare(
                """
                SELECT code, displayOrder
                FROM selected_stocks
                ORDER BY displayOrder ASC
                """.trimIndent()
            ).use { statement ->
                while (
                    statement.step()
                ) {
                    selectedStocks +=
                        statement.getText(0) to
                            statement.getLong(1)
                }
            }

            assertEquals(
                listOf(
                    "2454" to 1L,
                    "2330" to 2L,
                    "2317" to 3L,
                ),
                selectedStocks,
            )
        } finally {
            connection.close()
        }
    }

    /**
     * The migrated table has the expected primary key and required
     * columns.
     */
    @Test
    fun migration2To3CreatesExpectedFieldTableSchema() = runTest {
        val connection =
            BundledSQLiteDriver()
                .open(
                    databaseFile.absolutePath
                )

        try {
            createVersion2Schema(
                connection =
                    connection,
            )

            TWSEStockDatabase
                .MIGRATION_2_3
                .migrate(
                    connection =
                        connection,
                )

            val columns =
                mutableListOf<
                    MigrationColumn
                >()

            connection.prepare(
                """
                PRAGMA table_info(
                    selected_stock_fields
                )
                """.trimIndent()
            ).use { statement ->
                while (
                    statement.step()
                ) {
                    /*
                     * PRAGMA table_info columns:
                     *
                     * 0 cid
                     * 1 name
                     * 2 type
                     * 3 notnull
                     * 4 default value
                     * 5 primary-key position
                     */
                    columns +=
                        MigrationColumn(
                            name =
                                statement
                                    .getText(1),
                            type =
                                statement
                                    .getText(2),
                            isNotNull =
                                statement
                                    .getLong(3) == 1L,
                            primaryKeyPosition =
                                statement
                                    .getLong(5),
                        )
                }
            }

            assertEquals(
                listOf(
                    MigrationColumn(
                        name =
                            "fieldKey",
                        type =
                            "TEXT",
                        isNotNull =
                            true,
                        primaryKeyPosition =
                            1L,
                    ),
                    MigrationColumn(
                        name =
                            "displayOrder",
                        type =
                            "INTEGER",
                        isNotNull =
                            true,
                        primaryKeyPosition =
                            0L,
                    ),
                ),
                columns,
            )

            assertTrue(
                columns.any { column ->
                    column.name ==
                        "fieldKey" &&
                        column.primaryKeyPosition ==
                        1L
                }
            )
        } finally {
            connection.close()
        }
    }

    /**
     * Creates the database schema that existed before version 3.
     *
     * Version 2 contains ordered selected stocks but does not yet
     * contain selected_stock_fields.
     */
    private fun createVersion2Schema(
        connection:
            androidx.sqlite.SQLiteConnection,
    ) {
        connection.execSQL(
            """
            CREATE TABLE selected_stocks (
                code TEXT NOT NULL,
                displayOrder INTEGER NOT NULL,
                PRIMARY KEY(code)
            )
            """.trimIndent()
        )

        connection.execSQL(
            """
            PRAGMA user_version = 2
            """.trimIndent()
        )
    }

    private data class MigrationColumn(
        val name: String,
        val type: String,
        val isNotNull: Boolean,
        val primaryKeyPosition: Long,
    )

    companion object {

        private const val TEST_DATABASE_NAME =
            "twse_stock_migration_2_3_test.db"
    }
}
