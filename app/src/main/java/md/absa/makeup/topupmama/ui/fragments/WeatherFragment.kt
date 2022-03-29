package md.absa.makeup.topupmama.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.ConnectionLiveData
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.data.api.resource.Status
import md.absa.makeup.topupmama.databinding.FragmentWeatherBinding
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.adapters.WeatherAdapter
import md.absa.makeup.topupmama.ui.adapters.listener.OnClickListener
import md.absa.makeup.topupmama.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var connectionLiveData: ConnectionLiveData

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

        connectionLiveData = ConnectionLiveData(requireActivity())

        observeConnection()
    }

    private fun observeConnection() {
        connectionLiveData.observe(viewLifecycleOwner) { isAvailable ->
            when (isAvailable) {
                true -> {
                    viewModel.onFragmentReady()
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.search.visibility = View.VISIBLE
                    observeWeatherData()
                }
                false -> {
                    Snackbar.make(
                        binding.root,
                        "You are not connected. View cached weather data",
                        LENGTH_LONG
                    ).show()
                    binding.recyclerView.visibility = View.GONE
                    binding.search.visibility = View.GONE
                    collectFlowData()
                }
            }
        }
    }

    private fun observeWeatherData() {
        viewModel.weatherLiveData.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.search.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.search.visibility = View.VISIBLE
                    response.data?.let { data ->
                        Snackbar.make(
                            binding.root,
                            response.message.toString(),
                            LENGTH_LONG
                        ).show()
                    }
                    collectFlowData()
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.search.visibility = View.GONE
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
                    delay(1000L)
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
        binding.search.addTextChangedListener(searchTextWatcher)

        val adapter = WeatherAdapter(
            OnClickListener { weatherData ->
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
        adapter.updateData(dataList!!)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = adapter

        viewModel.weatherMediatorData.observe(viewLifecycleOwner) {
            Timber.e("MEDIATOR : $")
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            viewModel.onSearchQuery(editable.toString())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }
}
