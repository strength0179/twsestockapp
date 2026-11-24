package com.example.twsestockapp.data

import com.example.twsestockapp.data.json.ParsedStockJson
import com.example.twsestockapp.data.json.StockJsonParser
import com.example.twsestockapp.data.remote.TwseApi
import com.example.twsestockapp.data.remote.TwseApiClient
import com.example.twsestockapp.data.sync.StockJsonSynchronizer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Default production implementation of [DataRepository].
 *
 * Production wiring uses [StockJsonSynchronizer] and *
 * 1. synchronize today's JSON datasets
 * 2. parse synchronized JSON
 * 3. let [BaseDataRepository] merge and emit Stock models
 *
 * The [TwseApi] constructor remains available for current unit tests and
 * transitional typed-API callers.
 */
class DefaultDataRepository private constructor(
    loadParsedStockJson:
        suspend () -> ParsedStockJson,
) : BaseDataRepository(
    loadParsedStockJson =
        loadParsedStockJson,
) {

    /**
     * Production constructor.
     */
    constructor(
        synchronizer: StockJsonSynchronizer,
        parser: StockJsonParser,
    ) : this(
        loadParsedStockJson = {
            parser.parse(
                synchronizer.synchronize()
            )
        }
    )

    /**
     * Compatibility constructor for typed-API tests and transitional
     * callers.
     *
     * Production wiring should prefer the synchronizer/parser
     * constructor.
     */
    constructor(
        api: TwseApi =
            TwseApiClient.api,
    ) : this(
        loadParsedStockJson = {
            loadDirectlyFromApi(
                api =
                    api,
            )
        }
    )

    private companion object {

        /**
         * Loads all typed TWSE datasets concurrently.
         */
        suspend fun loadDirectlyFromApi(
            api: TwseApi,
        ): ParsedStockJson =
            coroutineScope {
                val dayDeferred =
                    async {
                        api.getStockDayAll()
                    }

                val averageDeferred =
                    async {
                        api.getStockDayAvgAll()
                    }

                val valuationDeferred =
                    async {
                        api.getBwibbuAll()
                    }

                ParsedStockJson(
                    dayItems =
                        dayDeferred.await(),
                    averageItems =
                        averageDeferred.await(),
                    valuationItems =
                        valuationDeferred.await(),
                )
            }
    }
}
