package com.bhavya.weatherapp.viewmodel


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.bhavya.weatherapp.UiState
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.api.LocationApi
import com.bhavya.weatherapp.repository.WeatherRepository
import com.bhavya.weatherapp.util.TestDispatcherProvider
import com.example.weatherapp.model.WeatherInfo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import retrofit2.Response
import util.DefaultDispatcherProvider
import util.DispatcherProvider
import com.google.gson.Gson as Gson1

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest() {


    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    var weatherRepository: WeatherRepository = mock<WeatherRepository>()

    var locationApi: LocationApi = mock<LocationApi>()

    var dispatcherProvider: DispatcherProvider = mock<DefaultDispatcherProvider>()


    private val _uiState = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherInfo>> = _uiState

    private lateinit var viewModel: WeatherViewModel

    @Mock
    lateinit var application:WeatherApplication

    @Before
    fun setup() {
        dispatcherProvider = TestDispatcherProvider()
        /*MockitoAnnotations.initMocks(this)
        DaggerApplicationComponent
            .builder()
            .networkModule(NetworkModuleTest(application))
            .build().inject(application)*/
        viewModel = WeatherViewModel(weatherRepository,locationApi, dispatcherProvider)

    }

    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun fetchCurrentWeatherSuccessScenario() = testDispatcher.runBlockingTest {
        //creating stub
        val latitude = "37.5435"
        val longitude = "-121.9713"
        var jsonString = "{\"coord\":{\"lon\":-121.9713,\"lat\":37.5435},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\",\"main\":{\"temp\":294.98,\"feels_like\":294.71,\"temp_min\":288.81,\"temp_max\":300.2,\"pressure\":1011,\"humidity\":57},\"visibility\":10000,\"wind\":{\"speed\":6.69,\"deg\":340},\"clouds\":{\"all\":75},\"dt\":1697240810,\"sys\":{\"type\":2,\"id\":2036492,\"country\":\"US\",\"sunrise\":1697206430,\"sunset\":1697247247},\"timezone\":-25200,\"id\":5350734,\"name\":\"Fremont\",\"cod\":200}";
        var testModel = Gson1().fromJson(jsonString,WeatherInfo::class.java)

        val weatherViewModel = WeatherViewModel(weatherRepository,locationApi, dispatcherProvider)
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

    @Test
    fun givenRunTimeExceptionDuringHttpCall() = testDispatcher.runBlockingTest {
        runTest {
            val errorMessage = "Error Message For You"
            Mockito.doReturn(flow<WeatherInfo> {
                throw IllegalStateException(errorMessage)
            }).`when`(weatherRepository).getWeatherData("Fremont")

            val viewModel =
                WeatherViewModel(weatherRepository, locationApi, dispatcherProvider)
            viewModel.uiState.test {
                assertEquals(
                    UiState.Error("Error Message For You"),
                    awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
            Mockito.verify(weatherRepository).getWeatherData("Fremont")
        }
    }

    @Test
    fun fetchCurrentWeatherWithServerRepliedWithError() = testDispatcher.runBlockingTest {
        val weatherViewModel = WeatherViewModel(weatherRepository,locationApi, dispatcherProvider)
        val responseBody = "{\"cod\":\"404\",\"message\":\"city not found\"}".toResponseBody(null);
        val response = Response.error<WeatherInfo>(400,responseBody)
        val channel = Channel<Response<WeatherInfo>>()
        val flow = channel.consumeAsFlow()

        Mockito.`when`(weatherRepository.getWeatherData("abc")).thenReturn(flow)
        launch {
            channel.send(response)
        }
        weatherViewModel.getWeather("abc")
        Assert.assertEquals(UiState.Error("Not found"),weatherViewModel.uiState.value)
    }


}

