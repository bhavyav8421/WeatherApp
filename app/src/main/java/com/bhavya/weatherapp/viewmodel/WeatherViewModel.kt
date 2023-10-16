package com.bhavya.weatherapp.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.repository.WeatherRepository
import com.example.weatherapp.model.WeatherInfo
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.Response
import util.DispatcherProvider
import javax.inject.Inject

class WeatherViewModel @Inject constructor(private val weatherRepository : WeatherRepository, private val locationApi: LocationApi, private  val dispatcherProvider: DispatcherProvider) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherInfo>> = _uiState

    private val _locationState = MutableStateFlow<UiState<Location>>(UiState.Loading)
    val locationState: StateFlow<UiState<Location>> = _locationState


    /**
     * @param lat - geo location latitude
     * @param long - geo location longitude
     * Retrieves weather based on coordinates
     */
    fun getWeather(lat:String, long: String) {
        viewModelScope.launch {dispatcherProvider.main
            weatherRepository.getWeatherData(lat, long)
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect{
                    print("********${it}")
                    onResponseCollected(it)
                }
        }
    }

    /**
     * @param lat - geo location latitude
     * Retrieves weather based on value (city, state,zipcode)
     */
    fun getWeather(city:String) {
        viewModelScope.launch {dispatcherProvider.main
            weatherRepository.getWeatherData(city)
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect{
                    onResponseCollected(it)
                }
        }
    }

    /**
     * invokes when http response is not null. response can be success or error
     */
    private fun onResponseCollected(it: Response<WeatherInfo>) {
        if (it.isSuccessful) {
            val weatherInfo = it.body();
            weatherInfo?.let { model ->
                model.currentTime = System.currentTimeMillis()
                _uiState.value = UiState.Success(model)
            }
        } else {
            _uiState.value = UiState.Error(it.message())
        }
    }

    /**
     * Retrieves current weather for latitude and longitude
     */
    fun getCurrentAddress() {
        //viewModelScope.launch {
        locationApi.getCurrentLocation(){ location ->
            // Log latitude and longitude here
            val latitude = location.latitude
            val longitude = location.longitude
            getWeather(latitude.toString(),longitude.toString());
        }
        // }
    }
}