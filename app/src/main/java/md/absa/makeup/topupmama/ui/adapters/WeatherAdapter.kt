package md.absa.makeup.topupmama.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.adapters.listener.OnClickListener
import md.absa.makeup.topupmama.ui.viewholders.WeatherViewHolder

class WeatherAdapter(
    private val clickListener: OnClickListener
) : RecyclerView.Adapter<WeatherViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder.create(parent)
    }

//    private val dataList: List<WeatherData?> =
    private var dataList = mutableListOf<WeatherData?>()

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val data: WeatherData? = dataList[position]
        if (data != null) {
            holder.bind(data, clickListener)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(data: List<WeatherData?>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }
}
