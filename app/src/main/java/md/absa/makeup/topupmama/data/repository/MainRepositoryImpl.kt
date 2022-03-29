package md.absa.makeup.topupmama.data.repository

import md.absa.makeup.topupmama.data.api.RetrofitInterface
import md.absa.makeup.topupmama.data.db.AppDatabase
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import timber.log.Timber
import javax.inject.Inject

open class MainRepositoryImpl @Inject constructor(
    private val retrofitInterface: RetrofitInterface,
    private val appDatabase: AppDatabase
) : MainRepository {

    override suspend fun fetchWeatherData(cities: String, unit: String, appId: String) =
        retrofitInterface.fetchWeatherData(id = cities, units = unit, appId = appId).also {
            if (it.isSuccessful) {
                val list = mutableListOf<WeatherData>()
                val favourites = appDatabase.favouriteCityDao().getAll()
                if (favourites.isNotEmpty()) {
                    for (favourite in favourites) {
                        for (item in it.body()?.list!!) {
                            if (favourite.id == item.id) {
                                item.isFavourite = 1
                            }
                            list.add(item)
                        }
                    }
                } else {
                    for (item in it.body()?.list!!) {
                        item.isFavourite = 0
                        list.add(item)
                    }
                }
                addToRoom(list)
            } else {
                Timber.e("ERROR > $it")
            }
        }

    override suspend fun addToRoom(response: List<WeatherData>) {
        appDatabase.weatherDataDao().nukeTable()
        appDatabase.weatherDataDao().insert(response)
    }

    override suspend fun favouriteCity(favouriteCity: FavouriteCity) {
        appDatabase.favouriteCityDao().insert(favouriteCity)
        setAsFavourite(favouriteCity.id!!)
    }

    override suspend fun unFavouriteCity(favouriteCity: FavouriteCity) {
        appDatabase.favouriteCityDao().delete(favouriteCity)
        setNotFavourite(favouriteCity.id!!)
    }

    override suspend fun setAsFavourite(id: Int) =
        appDatabase.weatherDataDao().setAsFavourite(id)

    override suspend fun setNotFavourite(id: Int) =
        appDatabase.weatherDataDao().setNotFavourite(id)

    override fun collectWeatherData() =
        appDatabase.weatherDataDao().collectWeatherData()

    override fun fetchCityWeatherData(id: Int) =
        appDatabase.weatherDataDao().fetchCityWeatherData(id)

    override suspend fun fetchDataByQuery(query: String) =
        appDatabase.weatherDataDao().fetchDataByQueryAsync(query)
}
