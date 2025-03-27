package com.customer.offerswindow.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.databinding.FragmentCategoriesBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.dashboard.CategoriesData
import com.customer.offerswindow.ui.dashboard.DashBoardViewModel
import com.customer.offerswindow.utils.setUpMultiViewRecyclerAdapter
import com.customer.offerswindow.utils.setWhiteToolBar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriesViewModel by viewModels()
    var categoryList = ArrayList<CategoriesData>()
    private val vm: DashBoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        activity?.setWhiteToolBar("Categories", true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = true
        viewModel.isloading.set(true)
        viewModel.getMstData()
    }

    private fun setObserver() {
        viewModel.masterdata.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { resposnes ->
                        viewModel.isloading.set(false)
                        response?.data?.data?.forEach {
                            if (it.MstType == "Service") {
                                categoryList.add(
                                    CategoriesData(
                                        it.Image_path,
                                        it.MstDesc,
                                        it.MstCode
                                    )
                                )
                            }

                        }
                        setRecyclervewData()
                    }
                }

                is NetworkResult.Error -> {
                    viewModel.isloading.set(false)
                }

                else -> {}
            }
        }

    }

    private fun setRecyclervewData() {
        binding.rvListdata.setUpMultiViewRecyclerAdapter(
            categoryList
        ) { item: CategoriesData, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.item, item)
            binder.setVariable(BR.onItemClick, View.OnClickListener {
                when(it.id){
                    R.id.category_item->{
                        var bundle = Bundle()
                        bundle.putString("Category",Gson().toJson(item))
                        bundle.putString("ISFROM","CATEGORY")
                        findNavController().navigate(R.id.nav_home,bundle)
                    }
                }
                binder.executePendingBindings()
            })
        }
    }


}