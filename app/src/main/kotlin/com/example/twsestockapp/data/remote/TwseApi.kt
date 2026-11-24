package com.example.twsestockapp.data.remote

import com.example.twsestockapp.data.remote.model.BwibbuItem
import com.example.twsestockapp.data.remote.model.StockDayAvgItem
import com.example.twsestockapp.data.remote.model.StockDayItem
import okhttp3.ResponseBody
import retrofit2.http.GET

interface TwseApi {

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<StockDayItem>

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAllRaw(): ResponseBody

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgAll(): List<StockDayAvgItem>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgAllRaw(): ResponseBody

    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbuAll(): List<BwibbuItem>

    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbuAllRaw(): ResponseBody
}
