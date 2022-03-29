package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.model.ext.Wind

class WindConverter {

    @TypeConverter
    fun toString(wind: Wind): String = Gson().toJson(wind)

    @TypeConverter
    fun fromString(string: String): Wind = Gson().fromJson(string, Wind::class.java)
}
