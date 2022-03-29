package md.absa.makeup.topupmama.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.common.Utils
import md.absa.makeup.topupmama.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Color status bar
        Utils.setStatusBarColor(requireActivity().window, Color.TRANSPARENT, false)
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
