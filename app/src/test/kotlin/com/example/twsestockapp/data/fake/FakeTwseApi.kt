package com.example.twsestockapp.data.fake

import com.example.twsestockapp.data.remote.TwseApi
import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Test-only implementation of TwseApi.
 *
 * Returns predefined data without performing actual network requests.
 * This allows DataRepository behavior to be tested independently from
 * Retrofit, Gson, network availability, and the real TWSE service.
 */
class FakeTwseApi(
    private val dayItems: List<StockDayItem> = emptyList(),
    private val averageItems: List<StockDayAvgItem> = emptyList(),
    private val valuationItems: List<BwibbuItem> = emptyList()
) : TwseApi {

    override suspend fun getStockDayAll(): List<StockDayItem> {
        return dayItems
    }

    override suspend fun getStockDayAllRaw(): ResponseBody {
        return emptyJsonResponseBody()
    }

    override suspend fun getStockDayAvgAll(): List<StockDayAvgItem> {
        return averageItems
    }

    override suspend fun getStockDayAvgAllRaw(): ResponseBody {
        return emptyJsonResponseBody()
    }

    override suspend fun getBwibbuAll(): List<BwibbuItem> {
        return valuationItems
    }

    override suspend fun getBwibbuAllRaw(): ResponseBody {
        return emptyJsonResponseBody()
    }

    private fun emptyJsonResponseBody(): ResponseBody {
        return EMPTY_JSON.toResponseBody(JSON_MEDIA_TYPE)
    }

    private companion object {
        const val EMPTY_JSON = "[]"

        val JSON_MEDIA_TYPE =
            "application/json; charset=utf-8".toMediaType()
    }
}
