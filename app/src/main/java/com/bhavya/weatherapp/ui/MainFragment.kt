package com.bhavya.weatherapp.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.databinding.MainFragment3Binding
import com.bhavya.weatherapp.di.component.DaggerActivityComponent
import com.bhavya.weatherapp.di.module.ActivityModule
import com.bhavya.weatherapp.viewmodel.WeatherViewModel
import com.bumptech.glide.Glide
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import util.SharedPrefs
import util.hasPermission
import javax.inject.Inject

class MainFragment :PermissionFragment(){
    private lateinit var binding: MainFragment3Binding;
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
        }
    }

    fun navigateToLocationWeather() {
        if (getApplicationContext()?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)!!) {
            requestFineLocationPermission()
        } else {
            onPermissionGrantedAlready()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragment3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWeatherObserver()
        setUpLocationObserver()
        setUpSearchView()
        fetchWeather()
        setUpNavigateButton()
    }

    private fun setUpNavigateButton() {
        binding.navigateImageView.setOnClickListener(View.OnClickListener {
            navigateToLocationWeather()
        })
    }

    private fun fetchWeather() {
        showProgressView()
        val sharedPrefs = getApplicationContext()?.let { SharedPrefs.getInstance(it) }
        val city = sharedPrefs?.getValueOrNull("city")
        Log.d("Prefs", city.toString())
        if (city!=null){
            weatherViewModel.getWeather(city)
        } else {
            navigateToLocationWeather();
        }
    }

    fun showProgressView(){
        binding.progressBar.visibility = View.VISIBLE
        binding.weatherLayout.visibility = View.GONE
    }

    fun showWeatherView(){
        binding.progressBar.visibility = View.GONE
        binding.weatherLayout.visibility = View.VISIBLE
    }


    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val sharedPrefs =
                    SharedPrefs.getInstance((activity?.application as WeatherApplication).applicationContext)
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
        val job = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.uiState.collect {
                    when (it) {
                        is UiState.Success -> {
                            updateUI(it.data)
                            showWeatherView();
                        }

                        is UiState.Loading -> {
                            showProgressView()
                        }

                        is UiState.Error -> {
                            //Handle Error
                            //callback?.onError(it.message)
                            showError(it.message)
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
                           // callback?.onError(it.message)
                            showError(it.message)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun showError(msg:String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("RETRY") { _,_ ->
                    fetchWeather();
                }.setNegativeButton("CLOSE") { _,_ ->
                    activity?.finish()
                }
                .create().show()
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
        callback?.onError("Location permission is  granted")
        fetchLocationWeather();
    }

    fun fetchLocationWeather() {
        weatherViewModel.getCurrentAddress();
    }


    override
    fun onPermissionDenied() {
        callback?.onError("Location permission is not granted")
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

        fun onError(msg:String)
    }

    fun getApplicationContext() : Context? {
        return (activity?.application as WeatherApplication).applicationContext
    }


    override fun onPause() {
        super.onPause()
       // job.cancel() ;
    }
}