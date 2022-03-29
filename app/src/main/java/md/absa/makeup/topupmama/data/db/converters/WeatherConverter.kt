package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import md.absa.makeup.topupmama.model.ext.Weather
import java.lang.reflect.Type

class WeatherConverter {

    @TypeConverter
    fun toString(weather: ArrayList<Weather?>?): String {
        val gson = Gson()
        return gson.toJson(weather)
    }

    @TypeConverter
    fun fromString(string: String?): ArrayList<Weather> {
        val listType: Type = object : TypeToken<ArrayList<Weather?>?>() {}.type
        return Gson().fromJson(string, listType)
    }
}
