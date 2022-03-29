package md.absa.makeup.topupmama.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import md.absa.makeup.topupmama.model.WeatherData

class TumDiffUtils(
    private val oldList: List<WeatherData>,
    private val newList: List<WeatherData>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return when {
           oldList[oldItemPosition].id != newList[newItemPosition].id -> {
               false
           }
           oldList[oldItemPosition].name != newList[newItemPosition].name -> {
               false
           }
           oldList[oldItemPosition].isFavourite != newList[newItemPosition].isFavourite -> {
               false
           }
           // Skipped the rest coz I don't expect them to change
           else -> true
       }
    }
}
