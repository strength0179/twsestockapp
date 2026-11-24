package com.example.twsestockapp.data.local.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.twsestockapp.data.local.dao.SelectedStockDao
import com.example.twsestockapp.data.local.dao.SelectedStockFieldDao
import com.example.twsestockapp.data.local.entity.SelectedStockEntity
import com.example.twsestockapp.data.local.entity.SelectedStockFieldEntity

/**
 * Local application database.
 *
 * Version history:
 *
 * Version 1
 * - selected_stocks(code)
 *
 * Version 2
 * - selected_stocks adds displayOrder
 *
 * Version 3
 * - selected_stock_fields(fieldKey, displayOrder)
 *
 * Version 4
 * - selected_stock_fields adds isVisible
 * - hidden fields remain persisted and retain absolute order
 */
@Database(
    entities = [
        SelectedStockEntity::class,
        SelectedStockFieldEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class TWSEStockDatabase : RoomDatabase() {

    abstract fun selectedStockDao():
        SelectedStockDao

    abstract fun selectedStockFieldDao():
        SelectedStockFieldDao

    companion object {

        val MIGRATION_1_2 =
            object : Migration(
                startVersion = 1,
                endVersion = 2,
            ) {
                override suspend fun migrate(
                    connection: SQLiteConnection,
                ) {
                    connection.execSQL(
                        """
                        ALTER TABLE selected_stocks
                        ADD COLUMN displayOrder INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )

                    connection.execSQL(
                        """
                        UPDATE selected_stocks
                        SET displayOrder = (
                            SELECT COUNT(*)
                            FROM selected_stocks AS ordered
                            WHERE ordered.code <= selected_stocks.code
                        )
                        """.trimIndent()
                    )
                }
            }

        val MIGRATION_2_3 =
            object : Migration(
                startVersion = 2,
                endVersion = 3,
            ) {
                override suspend fun migrate(
                    connection: SQLiteConnection,
                ) {
                    connection.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS selected_stock_fields (
                            fieldKey TEXT NOT NULL,
                            displayOrder INTEGER NOT NULL,
                            PRIMARY KEY(fieldKey)
                        )
                        """.trimIndent()
                    )

                    connection.execSQL(
                        """
                        INSERT OR IGNORE INTO selected_stock_fields
                            (fieldKey, displayOrder)
                        VALUES
                            ('closingPrice', 1),
                            ('change', 2),
                            ('changePercentage', 3),
                            ('tradeVolume', 4),
                            ('monthlyAveragePrice', 5),
                            ('peRatio', 6),
                            ('dividendYield', 7),
                            ('pbRatio', 8)
                        """.trimIndent()
                    )
                }
            }

        /**
         * Version 3 -> 4
         *
         * Adds persistent visibility without changing existing absolute
         * field order.
         *
         * Existing fields default to visible.
         */
        val MIGRATION_3_4 =
            object : Migration(
                startVersion = 3,
                endVersion = 4,
            ) {
                override suspend fun migrate(
                    connection: SQLiteConnection,
                ) {
                    connection.execSQL(
                        """
                        ALTER TABLE selected_stock_fields
                        ADD COLUMN isVisible INTEGER NOT NULL DEFAULT 1
                        """.trimIndent()
                    )
                }
            }
    }
}
