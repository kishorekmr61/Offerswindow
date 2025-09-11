package com.customer.offerswindow.ui.mybookings.bookings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.customer.offerswindow.ui.mybookings.MyBookingsFragment


class BookingsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MyBookingsFragment()
            }

            1 -> {
                MyBookingsFragment()
            }

            else ->
                MyBookingsFragment()
        }
    }
}