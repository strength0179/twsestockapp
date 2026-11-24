package com.example.twsestockapp

import android.content.Context
import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.DefaultDataRepository
import com.example.twsestockapp.data.json.StockJsonParser
import com.example.twsestockapp.data.local.database.TWSEStockDatabaseProvider
import com.example.twsestockapp.data.local.json.FileStockJsonCache
import com.example.twsestockapp.data.local.json.StockJsonCache
import com.example.twsestockapp.data.remote.TwseApiClient
import com.example.twsestockapp.data.selection.DefaultStockFieldSelectionRepository
import com.example.twsestockapp.data.selection.DefaultStockSelectionRepository
import com.example.twsestockapp.data.selection.StockFieldSelectionRepository
import com.example.twsestockapp.data.selection.StockSelectionRepository
import com.example.twsestockapp.data.sync.DefaultStockJsonSynchronizer

/**
 * Application dependency container.
 *
 * This container defines how production implementations are assembled.
 *
 * UI and ViewModel code depend on repository interfaces rather than
 * directly constructing databases, DAOs, Retrofit clients, JSON caches,
 * parsers, or synchronization components.
 */
class AppContainer(
    context: Context,
) {

    private val applicationContext =
        context.applicationContext

    /**
     * Shared production Room database.
     *
     * Both persistent user-stock selection and Pager-field selection
     * use this database instance.
     */
    private val database by lazy {
        TWSEStockDatabaseProvider.getDatabase(
            context =
                applicationContext,
        )
    }

    /**
     * Stores the raw TWSE JSON datasets in application-private storage.
     */
    private val stockJsonCache:
        StockJsonCache by lazy {

        FileStockJsonCache(
            context =
                applicationContext,
        )
    }

    /**
     * Retrieves today's raw JSON from cache when available.
     *
     * Missing datasets are downloaded from TWSE and stored in the
     * daily JSON cache.
     */
    private val stockJsonSynchronizer by lazy {
        DefaultStockJsonSynchronizer(
            api =
                TwseApiClient.api,
            cache =
                stockJsonCache,
        )
    }

    /**
     * Converts the raw cached TWSE JSON into the typed remote models
     * used by the data repository.
     */
    private val stockJsonParser by lazy {
        StockJsonParser()
    }

    /**
     * Repository providing current TWSE stock data.
     *
     * Production data flows through:
     *
     * TWSE raw API / daily JSON cache
     * -> StockJsonSynchronizer
     * -> StockJsonParser
     * -> DefaultDataRepository
     */
    val dataRepository:
        DataRepository by lazy {

        DefaultDataRepository(
            synchronizer =
                stockJsonSynchronizer,
            parser =
                stockJsonParser,
        )
    }

    /**
     * Repository providing persistent user stock selections and
     * user-defined stock display order.
     *
     * Persistence chain:
     *
     * selected_stocks
     * -> SelectedStockDao
     * -> DefaultStockSelectionRepository
     */
    val stockSelectionRepository:
        StockSelectionRepository by lazy {

        DefaultStockSelectionRepository(
            selectedStockDao =
                database.selectedStockDao(),
        )
    }

    /**
     * Repository providing persistent Pager-field selections and
     * user-defined Pager-field display order.
     *
     * Persistence chain:
     *
     * selected_stock_fields
     * -> SelectedStockFieldDao
     * -> DefaultStockFieldSelectionRepository
     */
    val stockFieldSelectionRepository:
        StockFieldSelectionRepository by lazy {

        DefaultStockFieldSelectionRepository(
            selectedStockFieldDao =
                database.selectedStockFieldDao(),
        )
    }
}

