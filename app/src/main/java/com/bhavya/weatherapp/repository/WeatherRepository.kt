package com.bhavya.weatherapp.repository

import android.location.Location
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.api.WeatherService
import com.bhavya.weatherapp.data.ParsedWeatherData
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Singleton
import javax.inject.Inject


@Singleton
class WeatherRepository @Inject constructor(private val weatherService : WeatherService){
    fun getWeatherData(lat: String, long:String): Flow<WeatherInfo> {
        return flow {
            val currentWeatherData = weatherService.getCurrentWeatherData(lat, long);
            currentWeatherData.currentTime = System.currentTimeMillis()
            emit(currentWeatherData)
        }
    }

    fun getWeatherData(cityName: String): Flow<WeatherInfo> {
        return flow {
            val currentWeatherData = weatherService.getCurrentWeatherByCity(cityName);
            currentWeatherData.currentTime = System.currentTimeMillis()
            emit(currentWeatherData)
        }
    }

}