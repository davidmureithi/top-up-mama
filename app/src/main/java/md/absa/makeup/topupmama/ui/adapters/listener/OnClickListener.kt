package md.absa.makeup.topupmama.ui.adapters.listener

import md.absa.makeup.topupmama.model.WeatherData

class OnClickListener(val clickListener: (weatherData: WeatherData) -> Unit) {
    fun onClick(weatherData: WeatherData) = clickListener(weatherData)
}
