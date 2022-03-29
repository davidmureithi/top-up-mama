package md.absa.makeup.topupmama.ui.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import md.absa.makeup.topupmama.R
import md.absa.makeup.topupmama.databinding.FragmentWelcomeBinding
import md.absa.makeup.topupmama.ui.adapters.WelcomeViewPagerAdapter
import md.absa.makeup.topupmama.ui.viewmodels.StoreViewModel

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private val storeViewModel by viewModels<StoreViewModel>()

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private var welcomeViewPagerAdapter: WelcomeViewPagerAdapter? = null

    private var layouts: IntArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeViewModel.welcomeScreenInfo().observe(viewLifecycleOwner) { welcomeIsScreenShown ->
            when (welcomeIsScreenShown) {
                true -> {
                    launchHomeScreen()
                }
                false -> {
                    launchWelcomeScreen()
                }
                else -> {
                    // How did we get here
                }
            }
        }
    }

    private fun launchHomeScreen() {
        lifecycleScope.launch {
            storeViewModel.setWelcomeScreenInfo(true)
        }
        findNavController().navigate(R.id.action_welcomeFragment_to_weatherFragment)
    }

    private fun launchWelcomeScreen() {
        binding.btnSkip.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
        // layouts for all intro screens
        layouts = intArrayOf(
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3,
            R.layout.welcome_slide4
        )

        // adding bottom dots
        addBottomDots(0)

        welcomeViewPagerAdapter = WelcomeViewPagerAdapter(layouts)
        binding.viewPager.adapter = welcomeViewPagerAdapter
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        binding.btnSkip.setOnClickListener { launchHomeScreen() }
        binding.btnNext.setOnClickListener { proceedWelcome() }
    }

    /**
     * viewpager change listener
     */
    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts?.size?.minus(1)) {
                binding.btnNext.text = getString(R.string.start)
                binding.btnSkip.visibility = View.GONE
            } else {
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun proceedWelcome() {
        // checking for last page - if last page, launch home screen
        val current = getItem(+1)
        if (current < layouts!!.size) {
            // move to next screen
            binding.viewPager.currentItem = current
        } else {
            launchHomeScreen()
        }
    }

    @Suppress("SameParameterValue")
    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(layouts!!.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        binding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(requireActivity())
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive[currentPage])
            binding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive[currentPage])
    }
}
