package md.absa.makeup.topupmama.data.prefs_datastore

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import md.absa.makeup.topupmama.common.Constants
import java.io.IOException
import javax.inject.Inject

class PrefsStoreImpl @Inject constructor(
    @ApplicationContext context: Context
) : PrefsStore {

    private val dataStore = context.createDataStore(name = Constants.DATASTORE_NAME)

    /**
     * Store dark mode state
     */
    override suspend fun toggleNightMode() {
        dataStore.edit {
            it[PreferencesKeys.NIGHT_MODE_KEY] = !(it[PreferencesKeys.NIGHT_MODE_KEY] ?: false)
        }
    }
    override fun isNightMode() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[PreferencesKeys.NIGHT_MODE_KEY] ?: false }

    /**
     * Store state of welcome screen
     */
    override suspend fun setWelcomeScreenShown(value: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.WELCOME_SCREEN_KEY] = value
        }
    }
    override fun isWelcomeScreenShown() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[PreferencesKeys.WELCOME_SCREEN_KEY] ?: false }

    /**
     * Keys to access our specific data store values
     */
    private object PreferencesKeys {
        val NIGHT_MODE_KEY = preferencesKey<Boolean>(Constants.DARK_THEME_ENABLED)
        val WELCOME_SCREEN_KEY = preferencesKey<Boolean>(Constants.WELCOME_SCREEN_SHOWN)
    }
}
