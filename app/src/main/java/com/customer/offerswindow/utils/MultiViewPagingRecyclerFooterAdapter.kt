package com.customer.offerswindow.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.customer.offerswindow.R

class MultiViewPagingRecyclerFooterAdapter: LoadStateAdapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        val view = holder.itemView.findViewById<ConstraintLayout>(R.id.progress_bar_layout)
        if (loadState == LoadState.Loading) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_multi_view_paging_recycler_footer, parent, false)
        )
    }
}