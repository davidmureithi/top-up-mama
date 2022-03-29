package md.absa.makeup.topupmama.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import md.absa.makeup.topupmama.R
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private const val conveter: Double = 273.15

object Utils {

    /**
     * Coloring the status bar
     */
    fun setStatusBarColor(window: Window, color: Int, fitWindow: Boolean) {
        window.apply {
            when {
                Build.VERSION.SDK_INT in 21..29 -> {
                    WindowCompat.setDecorFitsSystemWindows(window, fitWindow)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                }
                Build.VERSION.SDK_INT >= 30 -> {
                    WindowCompat.setDecorFitsSystemWindows(window, fitWindow)
                }
                else -> {
                }
            }
            statusBarColor = color
        }
    }

    fun convertKelvinToCelsius(double: Double) = roundOff(double.minus(conveter))

    private fun roundOffDecimal(number: Double): Double? {
        // To get two decimal places
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun roundOff(number: Double): String? {
        val df = DecimalFormat("#")
        return df.format(number).toString()
    }

    fun getDayFromEpochTime(timestamp: Long): String? {
        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        val date = Date(calendar.timeInMillis)
        val formatter: DateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        return formatter.format(date)
    }

    fun getDate(timestamp: Long): CharSequence? {
        val c = Calendar.getInstance()
        c.timeInMillis = (timestamp * 1000L)
        val d = c.time
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(d)
    }

    fun getTime(timestamp: Long): CharSequence? {
        val c = Calendar.getInstance()
        c.timeInMillis = timestamp * 1000L
        val d = c.time
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(d)
    }

    fun isLocationEnabled(context: Context): Boolean {
        var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getIcon(context: Context, name: String): Drawable? {
        return when {
            name.lowercase(Locale.getDefault()).contains("rain") -> {
                context.resources.getDrawable(R.drawable.rain_3x)
            }
            name.lowercase(Locale.getDefault()).contains("sunny") -> {
                context.resources.getDrawable(R.drawable.clear_3x)
            }
            name.lowercase(Locale.getDefault()).contains("cloudy") -> {
                context.resources.getDrawable(R.drawable.partlysunny_3x)
            }
            else -> { // For unhandled weather groups > Let us return sunny :(
                context.resources.getDrawable(R.drawable.clear_3x)
            }
        }
    }

    fun isConnected(): Boolean {
//        return false
        return true
    }
}
