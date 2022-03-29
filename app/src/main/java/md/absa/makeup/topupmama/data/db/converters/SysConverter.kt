package md.absa.makeup.topupmama.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import md.absa.makeup.topupmama.model.ext.Sys

class SysConverter {

    @TypeConverter
    fun toString(sys: Sys): String = Gson().toJson(sys)

    @TypeConverter
    fun fromString(string: String): Sys = Gson().fromJson(string, Sys::class.java)
}
