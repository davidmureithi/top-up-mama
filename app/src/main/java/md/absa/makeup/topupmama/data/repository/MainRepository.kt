package md.absa.makeup.topupmama.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import md.absa.makeup.topupmama.data.api.response.WeatherResponse
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import retrofit2.Response

interface MainRepository {

    suspend fun fetchWeatherData(cities: String, unit: String, appId: String): Response<WeatherResponse>

    suspend fun addToRoom(response: List<WeatherData>)

    fun collectWeatherData(): Flow<List<WeatherData>>

    fun fetchCityWeatherData(id: Int): Flow<WeatherData>

    suspend fun setAsFavourite(id: Int)

    suspend fun setNotFavourite(id: Int)

    suspend fun favouriteCity(favouriteCity: FavouriteCity)

    suspend fun unFavouriteCity(favouriteCity: FavouriteCity)

}