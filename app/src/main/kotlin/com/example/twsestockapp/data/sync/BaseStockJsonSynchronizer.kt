package com.example.twsestockapp.data.sync

import com.example.twsestockapp.data.local.json.StockJsonCache
import com.example.twsestockapp.data.local.json.StockJsonType
import java.time.LocalDate

/**
 * Base implementation of [StockJsonSynchronizer].
 *
 * This class owns the synchronization policy shared by concrete
 * implementations:
 *
 * 1. use today's cached JSON when available
 * 2. otherwise read the latest previous JSON
 * 3. download the current JSON
 * 4. save the downloaded JSON under today's date
 * 5. compare previous and downloaded content
 * 6. report cache/change status
 *
 * Concrete implementations only define how one dataset is downloaded.
 */
abstract class BaseStockJsonSynchronizer(
    private val cache:
        StockJsonCache,
    private val currentDate:
        () -> LocalDate,
) : StockJsonSynchronizer {

    /**
     * Synchronizes all current [StockJsonType] entries.
     */
    final override suspend fun synchronize():
        StockJsonSyncResult {

        val date =
            currentDate()

        val results =
            StockJsonType
                .entries
                .associateWith { type ->
                    synchronizeType(
                        date =
                            date,
                        type =
                            type,
                    )
                }

        return StockJsonSyncResult(
            date =
                date,
            items =
                results,
        )
    }

    /**
     * Downloads raw JSON for one dataset.
     *
     * Cache handling, persistence, and status calculation remain owned
     * by this base class.
     */
    protected abstract suspend fun download(
        type: StockJsonType,
    ): String

    /**
     * Synchronizes one dataset.
     */
    private suspend fun synchronizeType(
        date: LocalDate,
        type: StockJsonType,
    ): StockJsonSyncItemResult {

        val cachedToday =
            cache.read(
                date =
                    date,
                type =
                    type,
            )

        if (cachedToday != null) {
            return StockJsonSyncItemResult(
                type =
                    type,
                json =
                    cachedToday,
                status =
                    StockJsonSyncStatus
                        .FROM_CACHE,
            )
        }

        val previousJson =
            cache.readLatest(
                type =
                    type,
            )

        val downloadedJson =
            download(
                type =
                    type,
            )

        cache.save(
            date =
                date,
            type =
                type,
            json =
                downloadedJson,
        )

        val status =
            when {
                previousJson == null ->
                    StockJsonSyncStatus
                        .DOWNLOADED_CHANGED

                previousJson ==
                    downloadedJson ->
                    StockJsonSyncStatus
                        .DOWNLOADED_UNCHANGED

                else ->
                    StockJsonSyncStatus
                        .DOWNLOADED_CHANGED
            }

        return StockJsonSyncItemResult(
            type =
                type,
            json =
                downloadedJson,
            status =
                status,
        )
    }
}
