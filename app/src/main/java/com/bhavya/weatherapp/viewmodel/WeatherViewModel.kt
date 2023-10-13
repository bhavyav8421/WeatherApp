package com.bhavya.weatherapp.viewmodel

import android.app.SearchManager
import android.location.Location
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.data.ParsedWeatherData
import com.bhavya.weatherapp.repository.WeatherRepository
import com.bhavya.weatherapp.state.LocationState
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import util.SharedPrefs
import javax.inject.Inject

class WeatherViewModel @Inject constructor(private val weatherRepository : WeatherRepository, private val locationApi: LocationApi) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherInfo>> = _uiState

    private val _locationState = MutableStateFlow<UiState<Location>>(UiState.Loading)
    val locationState: StateFlow<UiState<Location>> = _locationState


    fun getWeather(lat:String, long: String) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(lat, long)
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect{
                    _uiState.value = UiState.Success(it)
                }
        }
    }

    fun getWeather(city:String) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(city)
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect{
                    _uiState.value = UiState.Success(it)
                }
        }
    }

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