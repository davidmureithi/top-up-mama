package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.data.api.response.WeatherResponse

class FavouriteConverter {

    @TypeConverter
    fun toString(weatherResponse: WeatherResponse): String = Gson().toJson(weatherResponse)

    @TypeConverter
    fun fromString(string: String): WeatherResponse = Gson().fromJson(string, WeatherResponse::class.java)
}
