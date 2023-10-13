package com.bhavya.weatherapp.data

import com.example.weatherapp.model.WeatherInfo
data class ParsedWeatherData(val weatherInfo: WeatherInfo, val sunRiseTime : String, val sunSetTime : String, val currentDateTime : String )