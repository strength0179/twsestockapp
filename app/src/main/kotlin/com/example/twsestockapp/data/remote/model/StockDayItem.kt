package com.example.twsestockapp.data.remote.model

import java.math.BigDecimal

data class StockDayItem(
    override val Code: String,
    override val Name: String,
    val TradeVolume: String,
    val TradeValue: String,
    val OpeningPrice: BigDecimal?,
    val HighestPrice: BigDecimal?,
    val LowestPrice: BigDecimal?,
    override val ClosingPrice: BigDecimal?,
    val Change: BigDecimal?,
    val Transaction: String
) : StockBaseItem, ClosingPriceItem