package com.bhavya.weatherapp.api

import com.bhavya.weatherapp.BuildConfig
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.WeatherInfo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather?")
    suspend fun getCurrentWeatherData(@Query("lat")
                                      lat: String,
                                      @Query("lon")
                                      lon: String,
                                      @Query("appid")
                                      appid: String = BuildConfig.API_KEY) : WeatherInfo

    @GET("weather?")
    suspend fun getCurrentWeatherByCity(
        @Query("q")
        city: String,
        @Query("appid")
        appid: String = BuildConfig.API_KEY) : WeatherInfo

    @GET("forecast?")
    suspend fun getForecastByCity(
        @Query("q")
        city: String,
        @Query("appid")
        appid: String = BuildConfig.API_KEY) : Forecast
}