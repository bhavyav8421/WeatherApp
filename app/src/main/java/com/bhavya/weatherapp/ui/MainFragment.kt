package com.bhavya.weatherapp.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.collectLatest
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
            //Navigated user to the current location
            navigateToLocationWeather()

        } else {
            throw RuntimeException("$context must implement LocationUpdateFragment.Callbacks")
        }
    }

    /**
     * Request permission from the user if not granted already
     */
    fun navigateToLocationWeather() {
        if (getApplicationContext()?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)!!) {
            onPermissionGrantedAlready()
        } else {
            requestFineLocationPermission()
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
        setUpSearchView()
        setUpNavigateButton()
        makeViewInVisible()
    }

    private fun setUpNavigateButton() {
        binding.navigateImageView.setOnClickListener(View.OnClickListener {
            navigateToLocationWeather()
        })
    }

    /**
     * Fetches the weather data of last searched city
     */
    private fun fetchWeatherForSavedCity() {
        val sharedPrefs = getApplicationContext()?.let { SharedPrefs.getInstance(it) }
        val city = sharedPrefs?.getValueOrNull("city")
        Log.d("Prefs", city.toString())
        if (city!=null){
            weatherViewModel.getWeather(city)
        }
    }

    /**
     * sets up search function based on city, country and zipcode
     */
    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val sharedPrefs =
                    SharedPrefs.getInstance((activity?.application as WeatherApplication).applicationContext)
                sharedPrefs.setValueOrNull("city", query!!)
                if (!query.isNullOrEmpty()) {
                    fetchWeatherofCity(query)
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

    /**
     * Fetch the weather of city user has searched
     */
    fun fetchWeatherofCity(value : String){
        weatherViewModel.getWeather(value)
    }

    /**
     * Observe the value in weatherview model and update the view accordingly
     */
    private fun setUpWeatherObserver() {
        val job = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.uiState.collectLatest {
                    when (it) {
                        is UiState.Success -> {
                            updateUI(it.data)

                        }

                        is UiState.Loading -> {
                        }

                        is UiState.Error -> {
                            //Handle Error
                            callback?.onError(it.message)
                            //showError(it.message)
                        }

                        else -> {}
                    }
                }
            }
        }

    }

    /**
     * @param weatherInfo - Weather info object received from the last http call
     * Updated the view with value received
     */
    fun updateUI(weatherInfo: WeatherInfo) {
        // val weatherInfo: WeatherInfo = data.weatherInfo;
        val temperatureCelsius = (weatherInfo.main.temp - 273.15)
        val temperatureFormatted = String.format("%.0f", temperatureCelsius)
        binding.humidityMain.text = weatherInfo.main.humidity.toString();
        binding.windSpeed.text = weatherInfo.wind.speed.toString();
        binding.tempMain.text = "${temperatureFormatted.toString()} °C"
        binding.pressure.text = weatherInfo.main.pressure.toString()
        binding.cityName.text = weatherInfo.name
        Glide.with(this).load("https://openweathermap.org/img/wn/${weatherInfo.weather[0].icon}@2x.png").into(binding.imageMain);
        binding.descMain.text = weatherInfo.weather[0].description
        binding.dateDayMain.text = util.getFormatedDateTime(weatherInfo.dt)
        binding.sunriseTextView.text = util.getFormatedTime(weatherInfo.sys.sunrise)
        binding.sunsetTextView.text = util.getFormatedTime(weatherInfo.sys.sunset)
        makeViewVisible();
    }

    fun makeViewVisible() {
        binding.humidityMain.visibility = View.VISIBLE;
        binding.windSpeed.visibility = View.VISIBLE;
        binding.tempMain.visibility = View.VISIBLE;
        binding.pressure.visibility = View.VISIBLE;
        binding.cityName.visibility = View.VISIBLE;
        binding.descMain.visibility = View.VISIBLE;
        binding.dateDayMain.visibility = View.VISIBLE;
        binding.sunriseTextView.visibility = View.VISIBLE;
        binding.sunsetTextView.visibility = View.VISIBLE;
        binding.imageMain.visibility = View.VISIBLE;
        binding.humidityLabel.visibility = View.VISIBLE;
        binding.windSpeedLabel.visibility = View.VISIBLE;
        binding.pressureLabel.visibility = View.VISIBLE;
    }

    fun makeViewInVisible() {
        binding.humidityMain.visibility = View.GONE;
        binding.windSpeed.visibility = View.GONE;
        binding.tempMain.visibility = View.GONE;
        binding.pressure.visibility = View.GONE;
        binding.cityName.visibility = View.GONE;
        binding.descMain.visibility = View.GONE;
        binding.dateDayMain.visibility = View.GONE;
        binding.sunriseTextView.visibility = View.GONE;
        binding.sunsetTextView.visibility = View.GONE;
        binding.imageMain.visibility = View.GONE;
        binding.humidityLabel.visibility = View.GONE;
        binding.windSpeedLabel.visibility = View.GONE;
        binding.pressureLabel.visibility = View.GONE;
    }

    /**
     * Call back when permission is granted by the user
     */
    override
    fun onPermissionGranted() {
        callback?.onError("Location permission is  granted")
        fetchLocationWeather();
    }

    /**
     * fetch the geo cordinates of user and subsequently calls the weather api for cordinates
     */
    fun fetchLocationWeather() {
        weatherViewModel.getCurrentAddress();
    }

    /**
     * Call back when permission is denied by the user
     * When location permission is denied by user, app will look for weather of last searched city if available
     */
    override
    fun onPermissionDenied() {
        fetchWeatherForSavedCity();
        callback?.onError("Location permission is not granted. Currently showing last searched city")
    }

    /**
     * Call back when permission is granted by the user already
     */
    override fun onPermissionGrantedAlready() {
        fetchLocationWeather();
    }


    private fun injectDependencies() {
        val appComponent = (activity?.application as WeatherApplication).appComponent;
        DaggerActivityComponent.builder()
            .applicationComponent(appComponent)
            .activityModule(ActivityModule(activity as AppCompatActivity)).build().inject(this)
    }

    /**
     * Interface to communicate from Fragment to another component eg: Fragment to ACtivity
     */
    interface Callbacks {

        fun onError(msg:String)
    }

    fun getApplicationContext() : Context? {
        return (activity?.application as WeatherApplication).applicationContext
    }

}