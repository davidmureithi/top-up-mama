package md.absa.makeup.topupmama.data.api.response

import md.absa.makeup.topupmama.model.WeatherData

data class WeatherResponse(
    val cnt: Int,
    val list: ArrayList<WeatherData>
)
