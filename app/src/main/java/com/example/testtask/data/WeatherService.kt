package com.example.testtask.data

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun getCurrentWeather(
        // Лучше давать переменным осмысленные имена. Например, query вместо q
        @Query("q") q: String,
        @Query("lang") lang: String = "ru",
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = "e35ff139f27685255e98d4ed735260a3"
    ) : WeatherResponse
}

object WeatherApi {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val weatherService: WeatherService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(WeatherService::class.java)
}