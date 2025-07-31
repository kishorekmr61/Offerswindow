package com.customer.offerswindow.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.SlideItemContainerBinding


class SliderAdapter(private val imageUrlList: List<String>) :
    RecyclerView.Adapter<SliderAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: SlideItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(imageUrl: String) {
            binding.imageSlide.load(imageUrl) {
                placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
            }
        }

    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = SlideItemContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.setData(imageUrlList[position])
    }

}