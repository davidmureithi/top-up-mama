package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.model.ext.Clouds

class CloudsConverter {

    @TypeConverter
    fun toString(clouds: Clouds): String = Gson().toJson(clouds)

    @TypeConverter
    fun fromString(string: String): Clouds = Gson().fromJson(string, Clouds::class.java)
}
