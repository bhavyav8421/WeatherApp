package com.bhavya.weatherapp.repository

import android.location.Location
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.api.WeatherService
import com.bhavya.weatherapp.data.ParsedWeatherData
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import util.DispatcherProvider
import javax.inject.Singleton
import javax.inject.Inject


@Singleton
class WeatherRepository @Inject constructor(private val weatherService : WeatherService, private  val dispatcherProvider: DispatcherProvider){
    fun getWeatherData(lat: String, long:String): Flow<Response<WeatherInfo>> {
        return flow {dispatcherProvider.io
          //  val currentWeatherData =
           // currentWeatherData.currentTime = System.currentTimeMillis()
            emit(weatherService.getCurrentWeatherData(lat, long))
        }
    }

    fun getWeatherData(cityName: String): Flow<Response<WeatherInfo>> {
        return flow {dispatcherProvider.io
           val currentWeatherData = weatherService.getCurrentWeatherByCity(cityName);
           // currentWeatherData.currentTime = System.currentTimeMillis()
            emit(weatherService.getCurrentWeatherByCity(cityName))
        }
    }

}