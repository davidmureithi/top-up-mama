package md.absa.makeup.topupmama.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import md.absa.makeup.topupmama.model.WeatherData

@Dao
interface WeatherDataDao : BaseDao<WeatherData> {

    @Query("SELECT * FROM weather_data")
    fun getAll(): LiveData<List<WeatherData>>

    @Query("SELECT * FROM weather_data ORDER BY isFavourite DESC")
    fun collectWeatherData(): Flow<List<WeatherData>>

    @Query("SELECT * FROM weather_data WHERE id=:id LIMIT 1")
    fun fetchCityWeatherData(id: Int): Flow<WeatherData>

    @Query("DELETE FROM weather_data")
    fun nukeTable()

    @Query("UPDATE weather_data SET isFavourite = 1 WHERE id=:id")
    fun setAsFavourite(id: Int?)

    @Query("UPDATE weather_data SET isFavourite = 0 WHERE id=:id")
    fun setNotFavourite(id: Int?)
}
