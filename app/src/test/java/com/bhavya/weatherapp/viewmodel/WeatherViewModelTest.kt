package com.bhavya.weatherapp.viewmodel


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.di.component.DaggerApplicationComponent
import com.bhavya.weatherapp.di.module.NetworkModule
import com.bhavya.weatherapp.repository.WeatherRepository
import com.example.weatherapp.model.WeatherInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import javax.inject.Inject
import com.google.gson.Gson as Gson1

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest(
    @Inject
    val weatherRepository: WeatherRepository,
    @Inject val locationApi: LocationApi
) {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    private val _uiState = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherInfo>> = _uiState

    private lateinit var viewModel: WeatherViewModel
    @Mock
    val application = mock(WeatherApplication::class.java)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        DaggerApplicationComponent
            .builder()
            .networkModule(NetworkModule(application))
            .build().inject(application)
    }

    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun fetchCurrentWeather() = testDispatcher.runBlockingTest {
        //creating stub
        val latitude = "37.5435"
        val longitude = "-121.9713"
        var jsonString = "{\"coord\":{\"lon\":-121.9713,\"lat\":37.5435},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":294.98,\"feels_like\":294.71,\"temp_min\":288.81,\"temp_max\":300.2,\"pressure\":1011,\"humidity\":57},\"visibility\":10000,\"wind\":{\"speed\":6.69,\"deg\":340},\"clouds\":{\"all\":75},\"dt\":1697240810,\"sys\":{\"type\":2,\"id\":2036492,\"country\":\"US\",\"sunrise\":1697206430,\"sunset\":1697247247},\"timezone\":-25200,\"id\":5350734,\"name\":\"Fremont\",\"cod\":200}";
        var testModel = Gson1().fromJson(jsonString,WeatherInfo::class.java)

        val weatherViewModel = WeatherViewModel(weatherRepository,locationApi)
        val response = Response.success(testModel)
        val channel = Channel<Response<WeatherInfo>>()
        val flow = channel.consumeAsFlow()

        Mockito.`when`(weatherRepository.getWeatherData(latitude, longitude)).thenReturn(flow)
        launch {
            channel.send(response)
        }
        weatherViewModel.getWeather("37.5435","-121.9713")
        Assert.assertEquals(UiState.Success(testModel),weatherViewModel.uiState.value)
    }
}