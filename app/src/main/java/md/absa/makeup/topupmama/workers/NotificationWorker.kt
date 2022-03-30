package md.absa.makeup.topupmama.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.common.notifications.DEFAULT_CHANNEL_ID
import md.absa.makeup.topupmama.common.notifications.NotificationUtils
import md.absa.makeup.topupmama.data.api.response.WeatherResponse
import md.absa.makeup.topupmama.data.repository.MainRepositoryImpl
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.activities.MainActivity
import retrofit2.Response
import timber.log.Timber

/**
 * Work Manager Arch Component
 * 1. To Demo the component
 * 2. To Demo push notifications
 */
@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repositoryImpl: MainRepositoryImpl
) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
//        val eventId = inputData.getInt(DBEventIDTag, ERROR_VALUE)
//        val outputData = Data.Builder()
//            .putString("NOTIFICATION_DATA", "Success")
//            .build()
//            return Result.success(outputData)
            val response = repositoryImpl.fetchWeatherData(
                cities = Utils.getCities(),
                unit = "metric",
                appId = Constants.appId
            )
            prepareNotificationToSend(response)
            Timber.tag(TAG).e("Success fetching data")
            Result.success()
        } catch (e: Exception) {
            Timber.tag(TAG).e("Error fetching data")
            Result.failure()
        }
    }

    private suspend fun prepareNotificationToSend(response: Response<WeatherResponse>) {
        NotificationUtils.createNotificationChannel(applicationContext)
        if (response.isSuccessful) {
            var ourFavouriteCityData: WeatherData? = null
            // Get favourite cities
            val cities = repositoryImpl.getFavouriteCities()
            if (cities.isNotEmpty()) {
                val city = cities[0]
                for (item in response.body()!!.list) {
                    if (item.id == city.id) {
                        ourFavouriteCityData = item
                    }
                }
                sendNotification(ourFavouriteCityData)
            }
        }
        // else no need to push a notification
    }

    private fun sendNotification(ourFavouriteCityData: WeatherData?) {
        val notifyIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notifyIntent.putExtra(NOTIFICATION_EXTRA, true)
        notifyIntent.putExtra(NOTIFICATION_ID_KEY, NOTIFICATION_ID)
        val notifyPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat
            .Builder(applicationContext, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.launcher)
            .setContentTitle(ourFavouriteCityData!!.name)
            .setContentText(ourFavouriteCityData.main?.temp.toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val TAG = "NotificationWorker"
        const val NOTIFICATION_EXTRA = "NOTIFICATION_EXTRA"
        const val NOTIFICATION_TITLE = "Daily Weather Reminder"
        const val NOTIFICATION_TEXT = "Hi, We have some new news for you."
        const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
        private const val NOTIFICATION_ID = 41412
    }
}
