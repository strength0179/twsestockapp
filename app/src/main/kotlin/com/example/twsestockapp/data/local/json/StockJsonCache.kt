package com.example.twsestockapp.data.local.json

import java.time.LocalDate

interface StockJsonCache {

    suspend fun save(
        date: LocalDate,
        type: StockJsonType,
        json: String,
    )

    suspend fun exists(
        date: LocalDate,
        type: StockJsonType,
    ): Boolean

    suspend fun read(
        date: LocalDate,
        type: StockJsonType,
    ): String?

    suspend fun readLatest(
        type: StockJsonType,
    ): String?

    suspend fun latestDate(
        type: StockJsonType,
    ): LocalDate?
}
