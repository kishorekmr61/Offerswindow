package com.customer.offerswindow.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.customer.offerswindow.BR
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
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
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.nav_home)
                }
            }
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        vm.hidetoolbar.value = true
        viewModel.isloading.set(true)
        categoryList.clear()
        viewModel.getOfferServiceDetails("0")
    }

    private fun setObserver() {


        viewModel.serviceResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    categoryList.clear()
                    viewModel.isloading.set(false)
                    response.data?.Data?.forEach {
                        categoryList.add(
                            CategoriesData(
                                false,
                                it.Image_path,
                                it.Mst_Desc,
                                it.Mst_Code,
                            ))
                    }
                    setRecyclervewData()
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
                when (it.id) {
                    R.id.category_item -> {
                        var bundle = Bundle()
                        bundle.putString("Category", Gson().toJson(item))
                        bundle.putString(Constants.ISFROM, "CATEGORY")
                        bundle.putString("CategoryID", item.category_id)
                        bundle.putInt("POSITION", position)
                        findNavController().navigate(R.id.nav_home, bundle)
                    }
                }
                binder.executePendingBindings()
            })
        }
    }


}