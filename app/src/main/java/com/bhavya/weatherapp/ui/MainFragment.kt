package com.bhavya.weatherapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.bhavya.weatherapp.data.ParsedWeatherData
import android.view.View
import androidx.appcompat.widget.SearchView
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.databinding.MainFragment2Binding
import com.bhavya.weatherapp.databinding.MainFragmentBinding
import com.bhavya.weatherapp.di.component.DaggerActivityComponent
import com.bhavya.weatherapp.di.module.ActivityModule
import com.bhavya.weatherapp.viewmodel.WeatherViewModel
import com.bumptech.glide.Glide
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.launch
import util.getFormatedDateTime
import util.getFormatedTime
import util.hasPermission
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import util.SharedPrefs

class MainFragment :PermissionFragment() {
    private lateinit var binding: MainFragmentBinding;

    @Inject
    lateinit var weatherViewModel: WeatherViewModel
    private var callback: Callbacks? = null;
    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectDependencies()
        if (context is Callbacks) {
            callback = context

            // If fine location permission isn't approved, instructs the parent Activity to replace
            // this fragment with the permission request fragment.
            if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestFineLocationPermission()
            } else {
                onPermissionGrantedAlready()
            }
        } else {
            throw RuntimeException("$context must implement LocationUpdateFragment.Callbacks")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWeatherObserver()
        setUpLocationObserver()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                val sharedPrefs = SharedPrefs.getInstance((activity?.application as WeatherApplication).applicationContext)
                sharedPrefs.setValueOrNull("city", query!!)


                if (!query.isNullOrEmpty()) {

                    weatherViewModel.getWeather(query)



                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true
                }


                return true


            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false


            }


        })
    }

    private fun setUpWeatherObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.uiState.collect {
                    when (it) {
                        is UiState.Success -> {
                            updateUI(it.data)
                        }
                        is UiState.Loading -> {
                        }
                        is UiState.Error -> {
                            //Handle Error
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setUpLocationObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.locationState.collect {
                    when (it) {
                        is UiState.Success -> {
                            val location = it.data;
                            weatherViewModel.getWeather(location.latitude.toString(), location.longitude.toString())
                        }
                        is UiState.Loading -> {
                        }
                        is UiState.Error -> {
                            //Handle Error
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun updateUI(weatherInfo: WeatherInfo) {
        // val weatherInfo: WeatherInfo = data.weatherInfo;
        val temperatureCelsius = (weatherInfo.main.temp - 273.15)
        val temperatureFormatted = String.format("%.2f", temperatureCelsius)
        binding.humidityMain.text = weatherInfo.main.humidity.toString();
        binding.windSpeed.text = weatherInfo.wind.speed.toString();
        binding.tempMain.text = "${temperatureFormatted.toString()} °C"

        //     binding.chanceofrainTextView.text = weatherInfo.rain.toString()
//        val sunrise = getDateFormatedTime(weatherInfo.sys.sunrise.toString())
//            val sunset = getDateFormatedTime(weatherInfo.sys.sunset.toString())
        //          val currentDateTime = getDateTime(weatherInfo.dt.toString())
        //      binding.sunsetTextView.text = sunrise
        //     binding.sunsetTextView.text = sunset
        //    binding.date.text = currentDateTime
        binding.pressure.text = weatherInfo.main.pressure.toString()
        binding.cityName.text = weatherInfo.name
        Glide.with(this).load("https://openweathermap.org/img/wn/${weatherInfo.weather[0].icon}@2x.png").into(binding.imageMain);
        binding.descMain.text = weatherInfo.weather[0].description
        binding.dateDayMain.text = util.getFormatedDateTime(weatherInfo.dt)
        binding.sunriseTextView.text = util.getFormatedTime(weatherInfo.sys.sunrise)
        binding.sunsetTextView.text = util.getFormatedTime(weatherInfo.sys.sunset)
    }

    override
    fun onPermissionGranted() {
        callback?.showMessage("Location permission is  granted")
        fetchLocationWeather();
    }

    fun fetchLocationWeather() {
        weatherViewModel.getCurrentAddress();
    }


    override
    fun onPermissionDenied() {
        callback?.showMessage("Location permission is not granted")
    }

    override fun onPermissionGrantedAlready() {
        fetchLocationWeather();
    }


    private fun injectDependencies() {
        val appComponent = (activity?.application as WeatherApplication).appComponent;
        DaggerActivityComponent.builder()
            .applicationComponent(appComponent)
            .activityModule(ActivityModule(activity as AppCompatActivity)).build().inject(this)
    }

    interface Callbacks {
        fun showMessage(msg:String)
    }

}