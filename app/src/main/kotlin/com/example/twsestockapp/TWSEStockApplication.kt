package com.example.twsestockapp

import android.app.Application

/**
 * Application object owning process-wide production dependencies.
 */
class TWSEStockApplication : Application() {

    /**
     * Lazily initialized application dependency container.
     */
    val container: AppContainer by lazy {
        AppContainer(
            context = this
        )
    }
}