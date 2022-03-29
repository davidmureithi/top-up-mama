package md.absa.makeup.topupmama.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_city")
data class FavouriteCity(
    @PrimaryKey val id: Int?,
    val name: String?,
)

