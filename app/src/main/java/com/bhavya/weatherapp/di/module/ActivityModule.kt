package com.bhavya.weatherapp.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.di.ActivityContext
import com.bhavya.weatherapp.repository.WeatherRepository
import com.bhavya.weatherapp.viewmodel.ViewModelProviderFactory
import com.bhavya.weatherapp.viewmodel.WeatherViewModel
import dagger.Module
import dagger.Provides
import util.DispatcherProvider

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context {
        return activity
    }

    /**
     * Provides view model object activity and main fragment
     */
    @Provides
    fun provideWeatherViewModel(repo: WeatherRepository, locationApi:LocationApi, dispatcherProvider: DispatcherProvider): WeatherViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(WeatherViewModel::class) {
                WeatherViewModel(repo,locationApi, dispatcherProvider)
            })[WeatherViewModel::class.java]
    }

}