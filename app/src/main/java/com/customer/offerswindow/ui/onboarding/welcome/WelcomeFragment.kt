package com.customer.offerswindow.ui.onboarding.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentWelcomeBinding
import com.customer.offerswindow.ui.dashboard.DashboardActivity
import com.customer.offerswindow.utils.hideOnBoardingToolbar
import com.customer.offerswindow.utils.hideStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment() {
    private val welcomViewModel: WelcomeViewModel by viewModels()
    var _binding: FragmentWelcomeBinding? = null
    val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private var progressInt = 0
    private var mHandler: Handler? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        init()

        timer = object : CountDownTimer(5500, 50) {
            override fun onTick(millisUntilFinished: Long) {
                progressInt += 1

            }

            override fun onFinish() {
                startActivity(Intent(requireContext(), DashboardActivity::class.java))
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

        }.start()
        return root
    }

    fun init() {
        binding.setVariable(BR.vm, welcomViewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
    }


    override fun onResume() {
        super.onResume()
        activity?.hideOnBoardingToolbar()
        activity?.hideStatusBar()
        setHasOptionsMenu(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                findNavController().navigate(R.id.nav_notification_info)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this, onBackPressedCallback
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

    override fun onDetach() {
        super.onDetach()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}