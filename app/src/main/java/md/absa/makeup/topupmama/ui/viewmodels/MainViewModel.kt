package md.absa.makeup.topupmama.ui.viewmodels

import android.text.TextUtils
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.data.api.resource.NetworkResource
import md.absa.makeup.topupmama.data.repository.MainRepositoryImpl
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repositoryImpl: MainRepositoryImpl
) : ViewModel() {

    private var searchJob: Job? = null
    private var debouncePeriod: Long = 500

    private val _searchFieldTextLiveData = MutableLiveData<String>()

    private var _searchWeatherLiveData: MutableLiveData<NetworkResource<List<WeatherData>>> = MutableLiveData()
    private var searchWeatherLiveData: LiveData<NetworkResource<List<WeatherData>>> = _searchWeatherLiveData

    private var _weatherLiveData: MutableLiveData<NetworkResource<List<WeatherData>>> = MutableLiveData()
    private val weatherLiveData: LiveData<NetworkResource<List<WeatherData>>> = _weatherLiveData

    val weatherMediatorData = MediatorLiveData<NetworkResource<List<WeatherData>>>()

    init {

        searchWeatherLiveData = Transformations.switchMap(_searchFieldTextLiveData) {
            fetchWeatherByQuery(it)
        }

        weatherMediatorData.addSource(searchWeatherLiveData) {
            weatherMediatorData.value = it
        }

        weatherMediatorData.addSource(weatherLiveData) {
            weatherMediatorData.value = it
        }
    }

    private fun fetchWeatherData(cities: String, unit: String, appId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _weatherLiveData.postValue(NetworkResource.loading(message = "Loading"))
            kotlin.runCatching {
                repositoryImpl.fetchWeatherData(cities = cities, unit = unit, appId = appId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    fetchCachedData()
                }
            }.onFailure { error ->
                _weatherLiveData.postValue(NetworkResource.error(message = error.message ?: "Some error occurred"))
            }
        }

    private fun fetchWeatherByQuery(query: String): LiveData<NetworkResource<List<WeatherData>>> {
        viewModelScope.launch(Dispatchers.IO) {
            _searchWeatherLiveData.postValue(NetworkResource.loading(message = "Loading"))
            kotlin.runCatching {
                repositoryImpl.fetchDataByQuery(query)
            }.onSuccess { response ->
                _searchWeatherLiveData.postValue(NetworkResource.success(data = response))
            }.onFailure { error ->
                _searchWeatherLiveData.postValue(NetworkResource.error(message = error.message ?: "Some error occurred"))
            }
        }
        return _searchWeatherLiveData
    }

    fun favouriteCity(favouriteCity: FavouriteCity) =
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.favouriteCity(favouriteCity)
        }

    fun unFavouriteCity(favouriteCity: FavouriteCity) =
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.unFavouriteCity(favouriteCity)
        }

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
                fetchCachedData()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    fun fetchLatestData() {
        if (weatherLiveData.value?.data.isNullOrEmpty()) {
            fetchWeatherData(
                cities = Utils.getCities(),
                unit = "metric",
                appId = Constants.appId
            )
        }
    }

    fun fetchCachedData() {
        viewModelScope.launch {
            repositoryImpl.collectWeatherData().collect {
                _weatherLiveData.postValue(NetworkResource.success(data = it))
            }
        }
    }
}
