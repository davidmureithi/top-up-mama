package md.absa.makeup.topupmama.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.data.db.converters.*
import md.absa.makeup.topupmama.data.db.dao.FavouriteCityDao
import md.absa.makeup.topupmama.data.db.dao.WeatherDataDao
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData

@Database(
    entities =
    [
        WeatherData::class,
        FavouriteCity::class
    ],
    exportSchema = false,
    version = 2
)
@TypeConverters(
    CoordinatesConverter::class,
    WeatherConverter::class,
    MainConverter::class,
    WindConverter::class,
    CloudsConverter::class,
    SysConverter::class,
    FavouriteConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao
    abstract fun favouriteCityDao(): FavouriteCityDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(
            context: Context
        ): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}
