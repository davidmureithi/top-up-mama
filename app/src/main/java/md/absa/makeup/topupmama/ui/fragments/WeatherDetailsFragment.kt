package md.absa.makeup.topupmama.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.Constants
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.databinding.FragmentWeatherDetailsBinding
import md.absa.makeup.topupmama.model.FavouriteCity
import md.absa.makeup.topupmama.model.WeatherData
import md.absa.makeup.topupmama.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class WeatherDetailsFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()
    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Utils.setStatusBarColor(requireActivity().window, resources.getColor(R.color.purple_500, resources.newTheme()), true)
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Weather Details"

        val cityId = Gson().fromJson(arguments?.getString("weatherData"), WeatherData::class.java).id

        collectWeatherDataForCity(cityId)
    }

    private fun collectWeatherDataForCity(cityId: Int?) {
        viewModel.fetchCityWeatherData(cityId!!).asLiveData().observe(viewLifecycleOwner) { weatherData ->
            setupWeatherData(weatherData)
        }
    }

    private fun setupWeatherData(weatherData: WeatherData?) {
        val icon = Utils.getIcon(requireActivity(), weatherData?.weather?.get(0)!!.main)
        binding.image.setImageDrawable(icon)
        binding.name.text = String.format(
            requireActivity().getString(R.string.currentLocation),
            weatherData.name,
            weatherData.sys?.country
        )
        binding.date.text = Utils.getDate(weatherData.dt!!.toLong())
        binding.time.text = Utils.getTime(weatherData.dt.toLong())
        binding.temperature.text = String.format(
            requireActivity().getString(R.string.currentTemperature),
            weatherData.main!!.temp
        )
        if (weatherData.isFavourite == 1) {
            binding.favourite.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_unfavorite))
        } else {
            binding.favourite.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_favorite))
        }
        binding.more.text = getString(R.string.moreAbout, weatherData.name)
        setupListeners(weatherData)
    }

    private fun setupListeners(weatherData: WeatherData) {
        binding.favourite.setOnClickListener {
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
        binding.more.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.EXPLORE_CITY, weatherData.name)
            findNavController().navigate(R.id.action_weatherDetailsFragment_to_webViewFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
