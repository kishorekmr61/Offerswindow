package com.customer.offerswindow.ui.onboarding.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentIntroBinding
import com.customer.offerswindow.model.intro.IntroModel
import com.customer.offerswindow.ui.dashboard.DashboardActivity
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
                "Never Miss a Deal Again!",
                "OffersWindow brings you the best discounts, promo codes, and special offers from your favorite brands",
                R.drawable.ic_gift_img,
                R.drawable.ic_leftcurve,
                1
            )
        )
        introList.add(
            IntroModel(
                getString(R.string.app_name),
                "Exclusive Discounts Just for You –\n Don’t Miss Out!",
                "Quick, easy, and hassle-free!",
                "You can even book slots or grab exclusive offers right from your mobile.",
                R.drawable.ic_offer_tag,
                R.drawable.ic_leftcurve,
                1
            )
        )
        introList.add(
            IntroModel(
                getString(R.string.app_name),
                "Unbeatable Offers Inside –\n Shop & Save Now!",
                "Live Notifications!!",
                "Get instant alerts the moment a new offer is released.",
                R.drawable.ic_announcment,
                R.drawable.ic_rightcurve,
                0
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
                if (position == 2) {
                    binding.dotsIndicator.visibility = View.INVISIBLE
                    binding.finishTxt.visibility = View.VISIBLE
                } else {
                    binding.dotsIndicator.visibility = View.VISIBLE
                    binding.finishTxt.visibility = View.INVISIBLE
                }
            }

            // triggered when there is
            // scroll state will be changed
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        AppPreference.read(Constants.LOGINUSERNAME, Constants.DEFAULTUSERMOBILE)
            ?: Constants.DEFAULTUSERMOBILE
        AppPreference.read(Constants.LOGINPASSWORD, Constants.DEFAULTUSERKEY)
            ?: Constants.DEFAULTUSERKEY
        _binding?.slideVP?.setAdapter(_binding?.slideVP?.adapter);
        _binding?.slideVP?.let { binding.dotsIndicator.attachTo(it) }
        binding.finishTxt.setOnClickListener {
            val intent = Intent(requireActivity(), DashboardActivity::class.java)
            AppPreference.write(Constants.SKIPSIGNIN, true)
            startActivity(intent)
        }
    }
}