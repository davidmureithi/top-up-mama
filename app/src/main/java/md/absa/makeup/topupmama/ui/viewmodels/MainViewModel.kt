package md.absa.makeup.topupmama.ui.viewmodels

import android.text.TextUtils
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.data.api.resource.NetworkResource
import md.absa.makeup.topupmama.data.repository.MainRepositoryImpl
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repositoryImpl: MainRepositoryImpl
) : ViewModel() {

    private var searchJob: Job? = null
    private var debouncePeriod: Long = 500
    private val _searchFieldTextLiveData = MutableLiveData<String>()
    private var _searchWeatherLiveData: LiveData<List<WeatherData>>

    private val _weatherLiveData: MutableLiveData<NetworkResource<List<WeatherData>>> = MutableLiveData()
    val weatherLiveData: LiveData<NetworkResource<List<WeatherData>>> = _weatherLiveData

    val weatherMediatorData = MediatorLiveData<List<WeatherData>>()

    init {
        _searchWeatherLiveData = Transformations.switchMap(_searchFieldTextLiveData) {
            fetchWeatherByQuery(it)
        }

        weatherMediatorData.addSource(_searchWeatherLiveData) {
            weatherMediatorData.value = it
        }
        /**
         * Harmonize Mediator
         */
//        weatherMediatorData.addSource(_weatherLiveData) {
//            weatherMediatorData.value = it.data!!
//        }
    }

    private fun fetchWeatherData(cities: String, unit: String, appId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(NetworkResource.loading(message = "Loading"))
            kotlin.runCatching {
                repositoryImpl.fetchWeatherData(cities = cities, unit = unit, appId = appId)
            }.onSuccess { response ->
                _weatherLiveData.postValue(NetworkResource.success(data = response.body()!!.list))
            }.onFailure { error ->
                _weatherLiveData.postValue(NetworkResource.error(message = error.message ?: "Some error occurred"))
            }
        }

    private fun getCities(): String {
        val cities = Constants.CITY_LIST
        val citiesString = TextUtils.join(",", cities)
        Timber.e("Cities String $citiesString")
        return citiesString
    }

    fun favouriteCity(favouriteCity: FavouriteCity) =
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.favouriteCity(favouriteCity)
        }

    fun unFavouriteCity(favouriteCity: FavouriteCity) =
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.unFavouriteCity(favouriteCity)
        }

    fun collectWeatherData() =
        repositoryImpl.collectWeatherData()

    fun fetchCityWeatherData(id: Int) =
        repositoryImpl.fetchCityWeatherData(id)

    fun onSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            if (query.length > 2) {
                _searchFieldTextLiveData.value = query
            }
            if (query.isEmpty()) {
                fetchWeatherData(
                    cities = getCities(),
                    unit = "metric",
                    appId = Constants.appId
                )
            }
        }
    }

    private fun fetchWeatherByQuery(query: String): LiveData<List<WeatherData>> {
        val liveData = MutableLiveData<List<WeatherData>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherData = repositoryImpl.fetchDataByQuery(query)
                liveData.postValue(weatherData)
            } catch (e: Exception) {
            }
        }
        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    fun onFragmentReady() {
        if (_weatherLiveData.value?.data.isNullOrEmpty()) {
            fetchWeatherData(
                cities = getCities(),
                unit = "metric",
                appId = Constants.appId
            )
        }
    }
}
