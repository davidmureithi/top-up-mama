package md.absa.makeup.topupmama.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.databinding.ActivityMainBinding
import md.absa.makeup.topupmama.ui.viewmodels.StoreViewModel
import md.absa.makeup.topupmama.workers.NotificationWorker

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var nightModeActive = false

    private val storeViewModel by viewModels<StoreViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?
        navHostFragment?.let {
            val navController = it.navController

            appBarConfiguration = AppBarConfiguration.Builder(
                R.id.weatherFragment,
                R.id.weatherDetailsFragment,
                R.id.webViewFragment,
            ).build()

            setupActionBarWithNavController(navController, appBarConfiguration)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.splashFragment -> {
                        binding.toolbar.visibility = View.GONE
                    }
                    R.id.welcomeFragment -> {
                        binding.toolbar.visibility = View.GONE
                    }
                    R.id.weatherFragment -> {
                        binding.toolbar.setTitle(R.string.weather)
                        binding.toolbar.visibility = View.VISIBLE
                    }
                    R.id.weatherDetailsFragment -> {
                        binding.toolbar.setTitle(R.string.favourites)
                        binding.toolbar.visibility = View.VISIBLE
                    }
                    R.id.webViewFragment -> {
                        binding.toolbar.setTitle(R.string.more)
                        binding.toolbar.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.toolbar.visibility = View.VISIBLE
                    }
                }
            }
        }
        storeViewModel.darkThemeEnabled.observe(this) { nightModeActive ->
            this.nightModeActive = nightModeActive
            val defaultMode = if (nightModeActive) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(defaultMode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.dayNightMode) {
            storeViewModel.toggleNightMode()
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (nightModeActive) {
            menu?.findItem(R.id.dayNightMode)?.setIcon(R.drawable.icn_light_mode)
        } else {
            menu?.findItem(R.id.dayNightMode)?.setIcon(R.drawable.icn_night_mode)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        intent?.let {
            if (it.hasExtra(NotificationWorker.NOTIFICATION_EXTRA)) {
                val id = it.getStringExtra(NotificationWorker.NOTIFICATION_ID_KEY)
            }
        }
    }
}
