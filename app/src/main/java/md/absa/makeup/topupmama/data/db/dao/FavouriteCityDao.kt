package md.absa.makeup.topupmama.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import md.absa.makeup.topupmama.model.FavouriteCity

@Dao
interface FavouriteCityDao : BaseDao<FavouriteCity> {

    @Query("SELECT * FROM favourite_city")
    fun getAll(): List<FavouriteCity>

    @Query("DELETE FROM favourite_city")
    fun nukeTable()
}
