package com.example.twsestockapp.data.sync

import com.example.twsestockapp.data.local.json.StockJsonType
import java.time.LocalDate

/**
 * Contract for synchronizing raw TWSE JSON datasets.
 *
 * Implementations decide how datasets are downloaded and cached.
 *
 * Consumers receive a complete result for every [StockJsonType].
 */
interface StockJsonSynchronizer {

    /**
     * Synchronizes all supported stock JSON datasets for the current
     * synchronization date.
     */
    suspend fun synchronize():
        StockJsonSyncResult
}

/**
 * Complete synchronization result for one date.
 */
data class StockJsonSyncResult(
    val date: LocalDate,
    val items:
        Map<
            StockJsonType,
            StockJsonSyncItemResult,
        >,
) {

    /**
     * True when at least one downloaded dataset differs from its
     * previously cached version or has no previous cached version.
     */
    val hasChanges:
        Boolean
        get() =
            items.values.any { result ->
                result.status ==
                    StockJsonSyncStatus
                        .DOWNLOADED_CHANGED
            }

    /**
     * Returns the synchronization result for one dataset.
     */
    operator fun get(
        type: StockJsonType,
    ): StockJsonSyncItemResult {

        return requireNotNull(
            items[
                type
            ]
        ) {
            "Missing synchronization result for $type"
        }
    }
}

/**
 * Synchronization result for one TWSE JSON dataset.
 */
data class StockJsonSyncItemResult(
    val type: StockJsonType,
    val json: String,
    val status: StockJsonSyncStatus,
)

/**
 * Describes where synchronized JSON came from and whether a download
 * changed the previous cached content.
 */
enum class StockJsonSyncStatus {

    /**
     * Today's cached JSON was used without downloading.
     */
    FROM_CACHE,

    /**
     * JSON was downloaded but matched the previous cached JSON.
     */
    DOWNLOADED_UNCHANGED,

    /**
     * JSON was downloaded and differed from the previous JSON, or no
     * previous cached JSON existed.
     */
    DOWNLOADED_CHANGED,
}
