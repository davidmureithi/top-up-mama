package md.absa.makeup.topupmama.data.api

import md.absa.makeup.topupmama.data.api.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    // http://api.openweathermap.org/
    // data/2.5/group?id=
    // 2643743,184742,909137,344979,373303,927967,3369157,266826,2538474,3352136,2538474,360630,2401325,2306104,2332459,7303939,202061,160196,232422,184742
    // &units=metric
    // &appid=
    // 1304fcde9f2e9c1b0c1c81d999bdd919

    // ?id={cities}
    // &units=metric
    // &appid={appId}
// ?={id}&units={units}&appid={appId}

    @GET("/data/2.5/group?")
    suspend fun fetchWeatherData(
        @Query("id") id: String,
        @Query("units") units: String,
        @Query("appId") appId: String,
    ): Response<WeatherResponse>
}
