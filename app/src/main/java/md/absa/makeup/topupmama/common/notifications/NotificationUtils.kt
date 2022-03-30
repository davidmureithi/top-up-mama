package md.absa.makeup.topupmama.common.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

const val CHANNEL_NAME: String = "reminders"
const val CHANNEL_DESC: String = "A channel that reminds users to open the app"
const val DEFAULT_CHANNEL_ID: String = "DEFAULT_CHANNEL_ID4R2352_001001001"

object NotificationUtils {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DEFAULT_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Set notification LED colour
            channel.lightColor = Color.MAGENTA
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
