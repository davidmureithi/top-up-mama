package md.absa.makeup.topupmama.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import md.absa.makeup.topupmama.common.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RetrofitInstance @Inject constructor(
    private val myHttpClient: MyHttpClient
) {

    private var gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .create()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(myHttpClient.client)
            .baseUrl(Constants.MAIN_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val retrofitInterface: RetrofitInterface = getRetrofit().create(RetrofitInterface::class.java)
}
