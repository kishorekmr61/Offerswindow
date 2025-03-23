package com.customer.offerswindow.Pagenation

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.customer.offerswindow.data.api.login.DashBoardHelperImpl
import com.customer.offerswindow.model.dashboard.DashboardData
import retrofit2.HttpException
import java.io.IOException

class DashBoardPagenation(
    private val lShowroomId: String,
    private val lLocationId: String,
    private val lServiceId: String,
    private val iCategoryId: String,
    private val iCityId: String,
    private val lCustomerId: String,
    private val defaultindex: String,
    private val dashBoardHelperImpl: DashBoardHelperImpl
) : PagingSource<Int, DashboardData>() {
    private var defaultPageIndex: String = "0"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DashboardData> {
        return try {
            val currentPage = params.key ?: 1
            var data: ArrayList<DashboardData>
            if (defaultindex == " 0") {
                defaultPageIndex = "0"
            }
            val response = dashBoardHelperImpl.getDashBoardOffersList(
                lShowroomId, lLocationId, lServiceId,
                iCategoryId,
                iCityId, lCustomerId, defaultPageIndex
            )
            data = response.body()?.dashboardData ?: arrayListOf()
            defaultPageIndex = data.lastOrNull()?.id ?: "0"

            val responseData = mutableListOf<DashboardData>()
            responseData.addAll(data)

            if (data.isEmpty()) {
                defaultPageIndex = "0"
                LoadResult.Page(
                    data = arrayListOf(),
                    prevKey = if (currentPage == 1) null else -1,
                    nextKey = null
                )
            } else {
                LoadResult.Page(
                    data = responseData,
                    prevKey = if (currentPage == 1) null else -1,
                    nextKey = currentPage.plus(1)
                )
            }
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            LoadResult.Error(ex)
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DashboardData>): Int? {
        Log.v("Refreshed", "Refreshed data")
        return 0
    }


}