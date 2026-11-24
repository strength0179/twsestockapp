package com.example.twsestockapp.data.json

import com.example.twsestockapp.data.remote.TwseApiClient
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import com.example.twsestockapp.data.sync.StockJsonSyncResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Parses the raw JSON returned by StockJsonSynchronizer into the
 * typed TWSE response models already used by the application.
 *
 * The parser uses the same Gson configuration as TwseApiClient so
 * cached JSON and direct Retrofit responses follow the same numeric
 * parsing rules, including BigDecimalTypeAdapter.
 */
class StockJsonParser(
    private val gson: Gson = TwseApiClient.createGson(),
) {

    /**
     * Parses all three JSON datasets contained in a synchronization result.
     */
    fun parse(
        syncResult: StockJsonSyncResult,
    ): ParsedStockJson {
        return ParsedStockJson(
            dayItems = parseStockDayAll(
                syncResult[
                    com.example.twsestockapp.data.local.json.StockJsonType.STOCK_DAY_ALL
                ].json
            ),
            averageItems = parseStockDayAvgAll(
                syncResult[
                    com.example.twsestockapp.data.local.json.StockJsonType.STOCK_DAY_AVG_ALL
                ].json
            ),
            valuationItems = parseBwibbuAll(
                syncResult[
                    com.example.twsestockapp.data.local.json.StockJsonType.BWIBBU_ALL
                ].json
            ),
        )
    }

    /**
     * Parses STOCK_DAY_ALL raw JSON.
     */
    fun parseStockDayAll(
        json: String,
    ): List<StockDayItem> {
        return gson.fromJson(
            json,
            object : TypeToken<List<StockDayItem>>() {}.type,
        )
    }

    /**
     * Parses STOCK_DAY_AVG_ALL raw JSON.
     */
    fun parseStockDayAvgAll(
        json: String,
    ): List<StockDayAvgItem> {
        return gson.fromJson(
            json,
            object : TypeToken<List<StockDayAvgItem>>() {}.type,
        )
    }

    /**
     * Parses BWIBBU_ALL raw JSON.
     */
    fun parseBwibbuAll(
        json: String,
    ): List<BwibbuItem> {
        return gson.fromJson(
            json,
            object : TypeToken<List<BwibbuItem>>() {}.type,
        )
    }
}

/**
 * Typed representation of the three TWSE JSON datasets.
 *
 * This matches the three collections currently consumed by
 * DefaultDataRepository.mergeStocks().
 */
data class ParsedStockJson(
    val dayItems: List<StockDayItem>,
    val averageItems: List<StockDayAvgItem>,
    val valuationItems: List<BwibbuItem>,
)

