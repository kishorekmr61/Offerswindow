package com.customer.offerswindow.ui.onboarding.intro

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentIntroBinding
import com.customer.offerswindow.databinding.FragmentSignInBinding
import com.customer.offerswindow.model.intro.IntroModel
import com.customer.offerswindow.utils.setUpViewPagerAdapter


class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null

    private val viewModel: IntroViewModel by viewModels()
    private val binding get() = _binding!!
    var introList = arrayListOf<IntroModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        introList.add(
            IntroModel(
                getString(R.string.app_name),
                getString(R.string.grab_the_best_deal),
                "Track order !!",
                "Lorem ipsum dolor sit amet, consectetur adipiscing, sed do eiusmod tempor ut labore",
                R.drawable.ic_gift_img,
                R.drawable.ic_leftcurve,
                Gravity.LEFT
            )
        )
        introList.add(
            IntroModel(
                getString(R.string.app_name),
                getString(R.string.grab_the_best_deal),
                "Track order !!",
                "Lorem ipsum dolor sit amet, consectetur adipiscing, sed do eiusmod tempor ut labore",
                R.drawable.ic_offer_tag,
                R.drawable.ic_leftcurve,
                Gravity.LEFT
            )
        )
        introList.add(
            IntroModel(
                getString(R.string.app_name),
                getString(R.string.grab_the_best_deal),
                "Track order !!",
                "Lorem ipsum dolor sit amet, consectetur adipiscing, sed do eiusmod tempor ut labore",
                R.drawable.ic_announcment,
                R.drawable.ic_leftcurve,
                Gravity.LEFT
            )
        )
        _binding?.slideVP?.setUpViewPagerAdapter(
            introList
        ) { item: IntroModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {

            })
        }
        _binding?.slideVP?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            // This method is triggered when there is any scrolling activity for the current page
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            // triggered when you select a new page
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position >= 3){
                    findNavController().navigate(R.id.nav_sign_in)
                }
            }

            // triggered when there is
            // scroll state will be changed
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        _binding?.slideVP?.setAdapter(_binding?.slideVP?.adapter);
    }
}