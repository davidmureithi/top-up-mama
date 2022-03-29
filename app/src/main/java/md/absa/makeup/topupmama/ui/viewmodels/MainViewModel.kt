package md.absa.makeup.topupmama.ui.viewmodels

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    /**
     * Using livedata api
     */
    private val _weatherData: MutableLiveData<NetworkResource<List<WeatherData?>>> = MutableLiveData()
    val weatherData: LiveData<NetworkResource<List<WeatherData?>>> = _weatherData

    fun fetchWeatherData(cities: String, unit: String, appId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.postValue(NetworkResource.loading(message = "Loading"))
            kotlin.runCatching {
                repositoryImpl.fetchWeatherData(cities = cities, unit = unit, appId = appId)
            }.onSuccess { response ->
                _weatherData.postValue(NetworkResource.success(data = response.body()!!.list))
            }.onFailure { error ->
                _weatherData.postValue(NetworkResource.error(message = error.message ?: "Some error occurred"))
            }
        }

    fun getCities(): String {
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
}
