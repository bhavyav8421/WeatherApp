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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import util.DefaultDispatcherProvider
import util.DispatcherProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule (private val application: WeatherApplication){

    @ApplicationContext
    @Provides
    fun provideContext(): Context {
        return application
    }

    /**
     * Provides base url for the http client
     */
    @Provides
    fun provideBaseUrl(): String = BuildConfig.BASE_URL;

    /**
     * Provides httpLogInterceptor
     */
    @Provides
    fun provideHttpLogger(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(
        HttpLoggingInterceptor.Level.BODY)

    /**
     * Provides http client object
     */
    @Provides
    fun provideOKHttp(logger: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        with(okHttpClient) {
            addInterceptor(logger)
        }
        return okHttpClient.build()
    }


    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider();
    }

    /**
     * Provides gson factor object to convert json string to object
     */
    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    /**
     * Provides retrofit object
     */
    @Singleton
    @Provides
    fun provideRetrofit(baseUrl: String, gsonConverterFactory: GsonConverterFactory) : Retrofit {
        return Retrofit.Builder().addConverterFactory(gsonConverterFactory).baseUrl(baseUrl).client(provideOKHttp(
            provideHttpLogger())).build();
    }

    /**
     * Provides weather service instance to invoke weather api
     */
    @Singleton
    @Provides
    fun provideWeatherService(retrofit: Retrofit) : WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    /**
     * Provides Location instance to capture geo coodinates of user
     */
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