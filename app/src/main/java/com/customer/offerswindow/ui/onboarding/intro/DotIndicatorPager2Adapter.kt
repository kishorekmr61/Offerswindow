package com.customer.offerswindow.ui.onboarding.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.customer.offerswindow.R

class DotIndicatorPager2Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    data class Card(val id: Int)

    val items = mutableListOf<Card>().apply {
        repeat(10) { add(Card(it)) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.intro_row, parent, false)
        ) {}
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Empty
    }
}