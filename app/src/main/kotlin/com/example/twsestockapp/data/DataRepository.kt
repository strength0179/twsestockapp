package com.example.twsestockapp.data

import com.example.twsestockapp.data.model.Stock
import kotlinx.coroutines.flow.Flow

/**
 * Contract for retrieving aggregated TWSE stock data.
 *
 * Consumers receive only application-level [Stock] models.
 *
 * Remote response models, JSON documents, cache files, parsers, and
 * synchronization details remain private to the data layer.
 */
interface DataRepository {

    /**
     * Emits aggregated TWSE stock data.
     *
     * Each collection begins a new loading operation.
     */
    val data:
        Flow<List<Stock>>
}
