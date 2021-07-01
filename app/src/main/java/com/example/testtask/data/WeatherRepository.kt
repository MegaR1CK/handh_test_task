package com.example.testtask.data

class WeatherRepository {
    suspend fun getWeather(query: String) = WeatherApi.weatherService.getCurrentWeather(query)
}