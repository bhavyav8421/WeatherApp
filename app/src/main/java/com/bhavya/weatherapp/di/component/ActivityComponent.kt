package com.bhavya.weatherapp.di.component

import com.bhavya.weatherapp.di.ActivityScope
import com.bhavya.weatherapp.di.module.ActivityModule
import com.bhavya.weatherapp.ui.MainActivity
import com.bhavya.weatherapp.ui.MainFragment
import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)

}