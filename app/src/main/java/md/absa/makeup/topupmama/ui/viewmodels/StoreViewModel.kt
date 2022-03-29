package md.absa.makeup.topupmama.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import md.absa.makeup.topupmama.data.prefs_datastore.PrefsStore
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val prefsStore: PrefsStore
) : ViewModel() {

    fun toggleNightMode() {
        viewModelScope.launch {
            prefsStore.toggleNightMode()
        }
    }

    val darkThemeEnabled = prefsStore.isNightMode().asLiveData()

    fun welcomeScreenInfo() = prefsStore.isWelcomeScreenShown().asLiveData()

    suspend fun setWelcomeScreenInfo(value: Boolean) = prefsStore.setWelcomeScreenShown(value)
}
