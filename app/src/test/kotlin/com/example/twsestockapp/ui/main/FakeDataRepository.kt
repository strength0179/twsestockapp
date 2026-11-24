package com.example.twsestockapp.ui.main

import com.example.twsestockapp.data.DataRepository
import com.example.twsestockapp.data.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Deterministic fake stock-data repository.
 */
internal class FakeDataRepository(
    stocks: List<Stock>,
) : DataRepository {

    override val data:
        Flow<List<Stock>> =
        flowOf(
            stocks
        )
}
