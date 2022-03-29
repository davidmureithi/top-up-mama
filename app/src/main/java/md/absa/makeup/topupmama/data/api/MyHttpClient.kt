package md.absa.makeup.topupmama.data.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import md.absa.makeup.topupmama.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyHttpClient @Inject constructor(
    context: Context
) {

    private fun buildOkhttpClient(
        context: Context
    ): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = when (BuildConfig.BUILD_TYPE) {
            "release" -> HttpLoggingInterceptor.Level.NONE
            else -> HttpLoggingInterceptor.Level.BODY
        }

        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        val chuckerInterceptor =
            ChuckerInterceptor.Builder(context)
                .collector(chuckerCollector)
                .maxContentLength(length = 250000L)
                .redactHeaders("Auth-Token", "Bearer")
                .alwaysReadResponseBody(true)
                .build()

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(chuckerInterceptor)
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(timeout = 60, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = 60, unit = TimeUnit.SECONDS)
            .build()
    }

    val client: OkHttpClient = buildOkhttpClient(context)
}
