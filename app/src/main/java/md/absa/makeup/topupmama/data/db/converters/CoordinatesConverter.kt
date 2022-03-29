package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.model.ext.Coord

class CoordinatesConverter {
    @TypeConverter
    fun toString(coord: Coord?): String? = Gson().toJson(coord)
    @TypeConverter
    fun fromString(string: String?): Coord? = Gson().fromJson(string, Coord::class.java)
}
