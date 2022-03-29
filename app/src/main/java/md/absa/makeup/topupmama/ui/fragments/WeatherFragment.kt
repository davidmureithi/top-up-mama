package md.absa.makeup.topupmama.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.data.api.resource.Status
import md.absa.makeup.topupmama.databinding.FragmentWeatherBinding
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.adapters.listener.OnClickListener
import md.absa.makeup.topupmama.ui.adapters.WeatherAdapter
import md.absa.makeup.topupmama.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setStatusBarColor(requireActivity().window, resources.getColor(R.color.purple_500, resources.newTheme()), true)
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Weather Data"

        if (Utils.isConnected()) {
            viewModel.fetchWeatherData(
                cities = viewModel.getCities(),
                unit = "metric",
                appId = Constants.appId
            )
            observeWeatherData()
        } else {
            collectFlowData()
        }
    }

    private fun observeWeatherData() {
        viewModel.weatherData.observe(viewLifecycleOwner) { response ->
            Timber.e("brands response", response.toString())

            when (response.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    Timber.e("... SUCCESS", response.toString())
                    binding.progressBar.visibility = View.GONE
//                    response.data?.let { data ->
//                        Snackbar.make(
//                            binding.root,
//                            response.message.toString(),
//                            LENGTH_LONG
//                        ).show()
//                        Timber.e("... success", data.toString())
//                    }
                    collectFlowData()
                }
                Status.ERROR -> {
                    Timber.e("... ERROR", response.toString())
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        response.message.toString(),
                        LENGTH_LONG
                    ).show()
                    Timber.e(response.toString())
                }
            }
        }
    }

    private fun collectFlowData() {
        viewModel.collectWeatherData()
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                if (it.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    setupRecyclerView(it)
                } else {
                    Snackbar.make(
                        binding.root,
                        "No data found",
                        LENGTH_LONG
                    ).show()
                    binding.recyclerView.visibility = View.GONE
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupRecyclerView(dataList: List<WeatherData?>?) {
        val adapter = WeatherAdapter(
            dataList!!,
            OnClickListener { weatherData ->
                Timber.e("WEATHER DATA ==== $weatherData")
                val favouriteCity = FavouriteCity(
                    id = weatherData.id,
                    name = weatherData.name
                )
                if (weatherData.isFavourite == 1) {
                    viewModel.unFavouriteCity(favouriteCity)
                } else {
                    viewModel.favouriteCity(favouriteCity)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
