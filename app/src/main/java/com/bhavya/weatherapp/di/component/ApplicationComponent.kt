package com.bhavya.weatherapp.di.component

import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.api.WeatherService
import com.bhavya.weatherapp.di.module.NetworkModule
import com.bhavya.weatherapp.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(application: WeatherApplication)

    fun getWeatherService(): WeatherService

    fun getWeatherRepository(): WeatherRepository

    fun getLocationProviderClient(): FusedLocationProviderClient

    fun getLocationApi(): LocationApi

}