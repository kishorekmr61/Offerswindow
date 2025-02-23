package com.customer.offerswindow.ui.onboarding.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null

    private val viewModel: IntroViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DotIndicatorPager2Adapter()
        _binding?.slideVP?.adapter = adapter
        _binding?.slideVP?.let { _binding?.dotsIndicator?.attachTo(it) }
    }
}