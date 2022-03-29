package md.absa.makeup.topupmama.data.api

import md.absa.makeup.topupmama.data.api.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("/data/2.5/group?")
    suspend fun fetchWeatherData(
        @Query("id") id: String,
        @Query("units") units: String,
        @Query("appId") appId: String,
    ): Response<WeatherResponse>
}
