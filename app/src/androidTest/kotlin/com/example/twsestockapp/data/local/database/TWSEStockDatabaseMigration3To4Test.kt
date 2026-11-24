package com.example.twsestockapp.data.local.database

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Tests database migration from version 3 to version 4.
 */
@RunWith(AndroidJUnit4::class)
class TWSEStockDatabaseMigration3To4Test {

    private lateinit var context:
        Context

    private lateinit var databaseFile:
        File

    @Before
    fun prepareDatabase() {
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
    fun deleteDatabase() {
        context.deleteDatabase(
            TEST_DATABASE_NAME
        )
    }

    @Test
    fun migrationAddsVisibleColumnAndPreservesOrder() = runTest {
        val connection =
            BundledSQLiteDriver()
                .open(
                    databaseFile.absolutePath
                )

        try {
            connection.execSQL(
                """
                CREATE TABLE selected_stock_fields (
                    fieldKey TEXT NOT NULL,
                    displayOrder INTEGER NOT NULL,
                    PRIMARY KEY(fieldKey)
                )
                """.trimIndent()
            )

            connection.execSQL(
                """
                INSERT INTO selected_stock_fields
                    (fieldKey, displayOrder)
                VALUES
                    ('A', 1),
                    ('B', 2),
                    ('C', 3),
                    ('D', 4)
                """.trimIndent()
            )

            TWSEStockDatabase
                .MIGRATION_3_4
                .migrate(
                    connection =
                        connection,
                )

            val rows =
                mutableListOf<
                    Triple<String, Long, Long>
                >()

            connection.prepare(
                """
                SELECT fieldKey, displayOrder, isVisible
                FROM selected_stock_fields
                ORDER BY displayOrder ASC
                """.trimIndent()
            ).use { statement ->
                while (statement.step()) {
                    rows +=
                        Triple(
                            statement.getText(0),
                            statement.getLong(1),
                            statement.getLong(2),
                        )
                }
            }

            assertEquals(
                listOf(
                    Triple("A", 1L, 1L),
                    Triple("B", 2L, 1L),
                    Triple("C", 3L, 1L),
                    Triple("D", 4L, 1L),
                ),
                rows,
            )
        } finally {
            connection.close()
        }
    }

    companion object {

        private const val TEST_DATABASE_NAME =
            "twse_stock_migration_3_4_test.db"
    }
}
