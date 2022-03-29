package md.absa.makeup.topupmama.ui.viewholders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.adapters.listener.OnClickListener

class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var weatherData: WeatherData? = null
    private val name: TextView = itemView.findViewById(R.id.name)
    private val date: TextView = itemView.findViewById(R.id.date)
    private val time: TextView = itemView.findViewById(R.id.time)
    private val temperature: TextView = itemView.findViewById(R.id.temperature)
    private val image: ImageView = itemView.findViewById(R.id.image)
    private val favourite: ImageView = itemView.findViewById(R.id.favourite)
    private var clickListener: OnClickListener? = null

    init {
        itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("weatherData", Gson().toJson(weatherData))
            Navigation.findNavController(it).navigate(R.id.action_weatherFragment_to_weatherDetailsFragment, bundle)
        }
        favourite.setOnClickListener {
            clickListener?.onClick(weatherData!!)
        }
    }

    fun bind(data: WeatherData, clickListener: OnClickListener) {
        this.clickListener = clickListener
        weatherData = data
        val icon = Utils.getIcon(itemView.context, data.weather?.get(0)!!.main)
        image.setImageDrawable(icon)
        name.text = String.format(
            itemView.context.getString(R.string.currentLocation),
            data.name,
            data.sys?.country
        )
        date.text = Utils.getDate(data.dt!!.toLong())
        time.text = Utils.getTime(data.dt.toLong())
        temperature.text = String.format(
            itemView.context.getString(R.string.currentTemperature),
            data.main!!.temp
        )
        if (data.isFavourite == 1) {
            favourite.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_unfavorite))
        } else {
            favourite.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_favorite))
        }
    }

    companion object {
        fun create(parent: ViewGroup): WeatherViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_weather_list, parent, false)
            return WeatherViewHolder(view)
        }
    }
}
