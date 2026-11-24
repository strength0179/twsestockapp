package com.example.twsestockapp.data.local.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL

/**
 * Provides the process-wide production [TWSEStockDatabase].
 */
object TWSEStockDatabaseProvider {

    private const val DATABASE_NAME =
        "twse_stock.db"

    @Volatile
    private var database:
        TWSEStockDatabase? = null

    fun getDatabase(
        context: Context,
    ): TWSEStockDatabase {
        return database
            ?: synchronized(this) {
                database
                    ?: createDatabase(
                        context =
                            context.applicationContext,
                    ).also { createdDatabase ->
                        database =
                            createdDatabase
                    }
            }
    }

    private fun createDatabase(
        context: Context,
    ): TWSEStockDatabase {
        return Room
            .databaseBuilder<TWSEStockDatabase>(
                context =
                    context,
                name =
                    DATABASE_NAME,
            )
            .setDriver(
                BundledSQLiteDriver()
            )
            .addMigrations(
                TWSEStockDatabase.MIGRATION_1_2,
                TWSEStockDatabase.MIGRATION_2_3,
                TWSEStockDatabase.MIGRATION_3_4,
            )
            .addCallback(
                createDatabaseCallback()
            )
            .build()
    }

    /**
     * Creates all eight visible fields for a completely new database.
     */
    private fun createDatabaseCallback():
        RoomDatabase.Callback {

        return object :
            RoomDatabase.Callback() {

            override suspend fun onCreate(
                connection:
                    SQLiteConnection,
            ) {
                connection.execSQL(
                    """
                    INSERT OR IGNORE INTO selected_stock_fields
                        (fieldKey, displayOrder, isVisible)
                    VALUES
                        ('closingPrice', 1, 1),
                        ('change', 2, 1),
                        ('changePercentage', 3, 1),
                        ('tradeVolume', 4, 1),
                        ('monthlyAveragePrice', 5, 1),
                        ('peRatio', 6, 1),
                        ('dividendYield', 7, 1),
                        ('pbRatio', 8, 1)
                    """.trimIndent()
                )
            }
        }
    }
}
