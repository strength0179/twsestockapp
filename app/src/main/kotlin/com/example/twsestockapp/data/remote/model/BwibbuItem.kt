package com.example.twsestockapp.data.remote.model

import java.math.BigDecimal

data class BwibbuItem(
    override val Code: String,
    override val Name: String,
    val PEratio: BigDecimal?,
    val DividendYield: BigDecimal?,
    val PBratio: BigDecimal?
) : StockBaseItem