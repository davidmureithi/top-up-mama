package md.absa.makeup.topupmama.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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
    private lateinit var weatherAdapter: WeatherAdapter

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

        initializeAdapter()

        connectionLiveData = ConnectionLiveData(requireActivity())

        connectionLiveData.observe(
            viewLifecycleOwner,
            Observer { isAvailable ->
                observeConnection(isAvailable)
            }
        )

        observeWeatherData()
    }

    private fun initializeAdapter() {
        weatherAdapter = WeatherAdapter(
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
    }

    private fun observeConnection(isAvailable: Boolean) {
        when (isAvailable) {
            true -> {
                viewModel.fetchLatestData()
            }
            false -> {
                Snackbar.make(
                    binding.root,
                    R.string.no_internet,
                    LENGTH_INDEFINITE
                ).setTextColor(Color.RED).show()
                viewModel.fetchCachedData()
            }
        }
    }

    private fun observeWeatherData() {
        viewModel.weatherMediatorData.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.search.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        setupRecyclerView(response.data!!)
                        Timber.e("MEDIATOR : $response")
                        response.data.let {
                            Snackbar.make(
                                binding.root,
                                response.message.toString(),
                                LENGTH_LONG
                            ).show()
                        }
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
        )
    }

    private fun setupRecyclerView(dataList: List<WeatherData>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.search.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        binding.search.addTextChangedListener(searchTextWatcher)
        weatherAdapter.updateData(dataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = weatherAdapter
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
