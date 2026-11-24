package com.example.twsestockapp.data.sync

import com.example.twsestockapp.data.local.json.StockJsonCache
import com.example.twsestockapp.data.local.json.StockJsonType
import com.example.twsestockapp.data.remote.TwseApi
import java.time.LocalDate

/**
 * Default production implementation of [StockJsonSynchronizer].
 *
 * Shared synchronization policy is inherited from
 * [BaseStockJsonSynchronizer].
 *
 * This implementation maps each [StockJsonType] to its TWSE API
 * endpoint and converts the response body into raw JSON.
 */
class DefaultStockJsonSynchronizer(
    private val api:
        TwseApi,
    cache:
        StockJsonCache,
    currentDate:
        () -> LocalDate =
        LocalDate::now,
) : BaseStockJsonSynchronizer(
    cache =
        cache,
    currentDate =
        currentDate,
) {

    /**
     * Downloads one raw TWSE JSON dataset.
     */
    override suspend fun download(
        type: StockJsonType,
    ): String {

        val responseBody =
            when (type) {
                StockJsonType
                    .STOCK_DAY_ALL ->
                    api.getStockDayAllRaw()

                StockJsonType
                    .STOCK_DAY_AVG_ALL ->
                    api.getStockDayAvgAllRaw()

                StockJsonType
                    .BWIBBU_ALL ->
                    api.getBwibbuAllRaw()
            }

        return responseBody.use { body ->
            body.string()
        }
    }
}
