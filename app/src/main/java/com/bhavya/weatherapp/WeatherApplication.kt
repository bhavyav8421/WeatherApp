package com.bhavya.weatherapp

import android.app.Application
import com.bhavya.weatherapp.di.component.ApplicationComponent
import com.bhavya.weatherapp.di.component.DaggerApplicationComponent
import com.bhavya.weatherapp.di.module.NetworkModule

class WeatherApplication : Application() {
    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        initDagger();
    }

    fun initDagger() {
        appComponent = DaggerApplicationComponent
            .builder()
            .networkModule(NetworkModule(this))
            .build()
        appComponent.inject(this)
       // DaggerApp
    }
}