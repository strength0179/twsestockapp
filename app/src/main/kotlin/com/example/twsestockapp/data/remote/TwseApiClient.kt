package com.example.twsestockapp.data.remote

import com.example.twsestockapp.data.remote.adapter.BigDecimalTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

object TwseApiClient {

    private const val BASE_URL =
        "https://openapi.twse.com.tw/v1/"

    /**
     * Creates the Gson configuration used by the TWSE API.
     *
     * The custom [BigDecimalTypeAdapter] converts TWSE numeric strings,
     * including comma-separated values, into nullable [BigDecimal] values.
     *
     * The function is internal so production code and module-level tests
     * can use the same Gson configuration without exposing it publicly.
     */
    internal fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                BigDecimal::class.java,
                BigDecimalTypeAdapter()
            )
            .create()
    }

    /**
     * Creates a [TwseApi] implementation using the supplied base URL.
     *
     * Production code uses the official TWSE URL. Unit tests can supply
     * a local MockWebServer URL while preserving the same Retrofit and
     * Gson configuration.
     *
     * @param baseUrl Base URL ending with a forward slash.
     */
    internal fun createApi(
        baseUrl: String
    ): TwseApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(createGson())
            )
            .build()
            .create(TwseApi::class.java)
    }

    /**
     * Shared production API implementation.
     */
    val api: TwseApi by lazy {
        createApi(BASE_URL)
    }
}