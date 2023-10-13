package com.bhavya.weatherapp.di.module


import android.app.Application
import android.content.Context
import com.bhavya.weatherapp.BuildConfig
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.api.WeatherService
import com.bhavya.weatherapp.di.ApplicationContext
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class NetworkModule (private val application: WeatherApplication){

    @ApplicationContext
    @Provides
    fun provideContext(): Context {
        return application
    }

    @Provides
    fun provideBaseUrl(): String = BuildConfig.BASE_URL;

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(baseUrl: String, gsonConverterFactory: GsonConverterFactory) : Retrofit {
        return Retrofit.Builder().addConverterFactory(gsonConverterFactory).baseUrl(baseUrl).build();
    }

    @Singleton
    @Provides
    fun provideWeatherService(retrofit: Retrofit) : WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Singleton
    @Provides
    fun provideLocationApi(locationProvider: FusedLocationProviderClient): LocationApi{
        return LocationApi(locationProvider);
    }

    @Provides
    @Singleton
    fun provideLocationProviderClient() =
        LocationServices.getFusedLocationProviderClient(application)

}