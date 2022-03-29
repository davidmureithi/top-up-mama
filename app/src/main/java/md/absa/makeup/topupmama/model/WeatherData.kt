package md.absa.makeup.topupmama.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import md.absa.makeup.topupmama.model.ext.*

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey val id: Int?,
    val coord: Coord?,
    val weather: ArrayList<Weather>?,
    val base: String?,
    val main: Main?,
    val visibility: Int?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: Sys?,
    val timezone: Int?,
    val name: String?,
    val cod: Int?,
    var isFavourite: Int?
)
