package com.customer.offerswindow.utils

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.customer.offerswindow.utils.inflate
import com.customer.offerswindow.utils.resource.WidgetViewModel

class MultiViewPagingRecyclerAdapter<T: WidgetViewModel>(
    recyclerView: RecyclerView,
    private val onBind: (item: T, binder: ViewDataBinding, position: Int) -> Unit
): PagingDataAdapter<T, MultiViewPagingRecyclerAdapter.ViewHolder<ViewDataBinding>>(BaseItemCallback()) {

    class BaseItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.toString() == newItem.toString()

        override fun areContentsTheSame(oldItem: T, newItem: T) = false
    }

    init {
        recyclerView.adapter = this
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.layoutId() ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewDataBinding> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(viewType))!!)
    }

    class ViewHolder<out V : ViewDataBinding>(val binder: V) : RecyclerView.ViewHolder(binder.root)

    override fun onBindViewHolder(holder: ViewHolder<ViewDataBinding>, position: Int) {
        getItem(position)?.let {
            onBind.invoke(it, holder.binder, position)
        }
    }
}

fun <T: WidgetViewModel> RecyclerView.setUpPagingMultiViewRecyclerAdapter(
    onBind: ((item: T, binder: ViewDataBinding, position: Int) -> Unit)
) = MultiViewPagingRecyclerAdapter(this, onBind)
