package com.customer.offerswindow.utils

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.customer.offerswindow.utils.resource.WidgetViewModel


class ViewPager2Adapter<T : WidgetViewModel>(
    recyclerView: ViewPager2,
    private val list: ArrayList<T>,
    private val onBind: (item: T, binder: ViewDataBinding, position: Int) -> Unit
) : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder<ViewDataBinding>>() {

    init {
        recyclerView.adapter = this
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].layoutId()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewDataBinding> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(viewType))!!)
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewDataBinding>, position: Int) {
        val item = list[position]
        onBind.invoke(item, holder.binder, position)
    }

    class ViewHolder<out V : ViewDataBinding>(val binder: V) : RecyclerView.ViewHolder(binder.root)
}

fun <T : WidgetViewModel> ViewPager2.setUpViewPagerAdapter(
    itemList: ArrayList<T>,
    onBind: ((item: T, binder: ViewDataBinding, position: Int) -> Unit)
) = ViewPager2Adapter(this, itemList, onBind)


