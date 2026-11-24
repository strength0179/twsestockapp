package com.example.twsestockapp.data.remote.model

import java.math.BigDecimal

data class StockDayAvgItem(
    override val Code: String,
    override val Name: String,
    override val ClosingPrice: BigDecimal?,
    val MonthlyAveragePrice: BigDecimal?
) : StockBaseItem, ClosingPriceItem