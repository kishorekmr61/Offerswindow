package com.customer.offerswindow.repositry

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.customer.offerswindow.Pagenation.DashBoardPagenation
import com.customer.offerswindow.data.api.login.DashBoardHelperImpl
import com.customer.offerswindow.helper.BaseApiResponse
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerDataResponse
import com.customer.offerswindow.model.OfferWindowCommonResponse
import com.customer.offerswindow.model.customersdata.PostOfferBooking
import com.customer.offerswindow.model.customersdata.PostSlotBooking
import com.customer.offerswindow.model.dashboard.BookingsResponse
import com.customer.offerswindow.model.dashboard.DashBoardDataResponse
import com.customer.offerswindow.model.dashboard.DashboardData
import com.customer.offerswindow.model.dashboard.SlotsDataResponse
import com.customer.offerswindow.model.dashboard.WishListResponse
import com.customer.offerswindow.model.masters.ShowRoomsResponse
import com.customer.offerswindow.model.offerdetails.OfferDeatilsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DashBoardRepositry @Inject constructor(
    private val dashBoardHelperImpl: DashBoardHelperImpl
) : BaseApiResponse() {


    suspend fun getCustomerData(
        mobileno: String
    ): Flow<NetworkResult<CustomerDataResponse>> {
        return flow {
            emit(safeApiCall { dashBoardHelperImpl.getCustomerData(userid = mobileno) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDashBoardOffersList(
        lShowroomId: String,
        lLocationId: String,
        lServiceId: String,
        iCategoryId: String,
        iCityId: String,
        lCustomerId: String,
        lMaximumTransactionId: String
    ): Flow<NetworkResult<DashBoardDataResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getDashBoardOffersList(
                    lShowroomId,
                    lLocationId,
                    lServiceId,
                    iCategoryId,
                    iCityId, lCustomerId, lMaximumTransactionId
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    fun getDashBoardOffersListPagenation(
        lShowroomId: String, lLocationId: String, lServiceId: String,
        iCategoryId: String,
        iCityId: String, lCustomerId: String, defaultindex: String
    ): Flow<PagingData<DashboardData>> =
        Pager(
            PagingConfig(1),
            pagingSourceFactory = {
                DashBoardPagenation(
                    lShowroomId, lLocationId, lServiceId,
                    iCategoryId,
                    iCityId, lCustomerId, defaultindex, dashBoardHelperImpl
                )
            }
        ).flow.flowOn(Dispatchers.IO)

    suspend fun getShowRooms(
        lShowroomId: String, lLocationId: String, lServiceId: String,
    ): Flow<NetworkResult<ShowRoomsResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getShowRooms(
                    lShowroomId, lLocationId, lServiceId,
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getIndividualOfferDetails(
        lRecordId: String
    ): Flow<NetworkResult<OfferDeatilsResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getIndividualOfferDetails(
                    lRecordId
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getofferTimeSlots(
        lShowroomId: String, lLocationId: String, lServiceId: String, date: String
    ): Flow<NetworkResult<SlotsDataResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getofferTimeSlots(
                    lShowroomId, lLocationId, lServiceId, date
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getBookings(
        lCustomerID: String
    ): Flow<NetworkResult<BookingsResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getBooking(
                    lCustomerID
                )
            })
        }.flowOn(Dispatchers.IO)
    }
 suspend fun getOffersData(
        lCustomerID: String
    ): Flow<NetworkResult<BookingsResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getOffersData(
                    lCustomerID
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postSlotBooking(
        postSlotBooking: PostSlotBooking
    ): Flow<NetworkResult<OfferWindowCommonResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.postSlotBooking(
                    postSlotBooking
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getWishList(
        lCustomerID: String, iCategoryType: String
    ): Flow<NetworkResult<WishListResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.getWishList(
                    lCustomerID, iCategoryType
                )
            })
        }.flowOn(Dispatchers.IO)
    }


    suspend fun postOfferBooking(
        postOfferBooking: PostOfferBooking
    ): Flow<NetworkResult<OfferWindowCommonResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.postOfferBooking(
                    postOfferBooking
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun removeWishlist(lOfferId: String,lCustomerId:String
    ): Flow<NetworkResult<OfferWindowCommonResponse>> {
        return flow {
            emit(safeApiCall {
                dashBoardHelperImpl.removeWishListIteam(
                    lOfferId,lCustomerId
                )
            })
        }.flowOn(Dispatchers.IO)
    }

}