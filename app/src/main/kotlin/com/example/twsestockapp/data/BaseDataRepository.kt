package com.example.twsestockapp.data

import com.example.twsestockapp.data.json.ParsedStockJson
import com.example.twsestockapp.data.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Base implementation of [DataRepository].
 *
 * This class owns the repository-wide loading pipeline:
 *
 * 1. load one ParsedStockJson snapshot
 * 2. merge the three TWSE datasets
 * 3. emit application-level Stock models
 *
 * Concrete implementations decide how ParsedStockJson is obtained.
 *
 * The exposed Flow remains cold. Every collection invokes
 * [loadParsedStockJson] again.
 */
abstract class BaseDataRepository(
    private val loadParsedStockJson:
        suspend () -> ParsedStockJson,
) : DataRepository {

    final override val data:
        Flow<List<Stock>> =
        flow {
            val parsedStockJson =
                loadParsedStockJson()

            emit(
                mergeStocks(
                    dayItems =
                        parsedStockJson.dayItems,
                    averageItems =
                        parsedStockJson.averageItems,
                    valuationItems =
                        parsedStockJson
                            .valuationItems,
                )
            )
        }
}
