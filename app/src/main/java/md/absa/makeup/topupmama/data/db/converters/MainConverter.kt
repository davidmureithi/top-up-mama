package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.model.ext.Main

class MainConverter {

    @TypeConverter
    fun toString(main: Main): String = Gson().toJson(main)

    @TypeConverter
    fun fromString(string: String): Main = Gson().fromJson(string, Main::class.java)
}

