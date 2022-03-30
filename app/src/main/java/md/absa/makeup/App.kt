package md.absa.makeup

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import md.absa.makeup.topupmama.BuildConfig
import md.absa.makeup.topupmama.workers.NotificationWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Stetho.initializeWithDefaults(applicationContext)
        CoroutineScope(Dispatchers.Default).launch {
            scheduleWork()
        }
    }

    /**
     * Fire our worker to fetch data via [scheduleWork]
     */
    private fun scheduleWork() {
        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(false)
            .build()

        val data = Data.Builder().putString(ENDPOINT_REQUEST, endPoint)
        val inputData: Data = Data.Builder().putInt(DBEventIDTag, DBEventID).build()

//        val task = oneTimeRequest(constraints, data)

        val task = periodicWorkRequest(constraints, data)

        workManager.enqueue(task)
    }

    private fun periodicWorkRequest(constraints: Constraints, data: Data.Builder): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
//            .setInitialDelay(calculateDelay(event.getDate()), TimeUnit.MILLISECONDS)
            .setInputData(data.build())
            .addTag(periodicWorkRequestTag)
            .build()
    }

    private fun oneTimeRequest(constraints: Constraints, data: Data.Builder): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(constraints)
            .setInputData(data.build())
            .addTag(oneTimeWorkRequestTag)
            .build()
    }

    companion object {
        const val periodicWorkRequestTag = "periodicWorkRequestTag"
        const val oneTimeWorkRequestTag = "oneTimeWorkRequestTag"
        const val ENDPOINT_REQUEST = "ENDPOINT_REQUEST"
        const val endPoint = "endPoint"
        const val DBEventIDTag = "DBEventIDTag"
        const val DBEventID = 2002
    }
}
