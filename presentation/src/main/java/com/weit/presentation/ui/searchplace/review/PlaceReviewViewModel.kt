package com.weit.presentation.ui.searchplace.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewDetail
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.usecase.place.GetAverageRatingUseCase
import com.weit.domain.usecase.place.GetPlaceReviewByPlaceIdUseCase
import com.weit.domain.usecase.place.GetPlaceReviewListUseCase
import com.weit.domain.usecase.user.GetUserByNicknameUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.presentation.ui.searchplace.SearchPlaceBottomSheetViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaceReviewViewModel @AssistedInject constructor(
    private val getAverageRatingUseCase: GetAverageRatingUseCase,
    private val getPlaceReviewByPlaceIdUseCase: GetPlaceReviewByPlaceIdUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val getPlaceReviewListUseCase: GetPlaceReviewListUseCase,
    @Assisted private val placeId: String
) : ViewModel() {

    val reviewRating = MutableStateFlow(initRating)
    val reviewNum = reviewCount
    private val _placeReviewList = MutableStateFlow<List<PlaceReviewInfo>>(emptyList())
    val placeReviewList: StateFlow<List<PlaceReviewInfo>> get() = _placeReviewList

    private val _myId = MutableStateFlow<Long>(0L)
    val myId: StateFlow<Long> get() = _myId

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): PlaceReviewViewModel
    }

    init {
        viewModelScope.launch {
            getAverageRating()
        }
    }

//    fun initMyId(){
//        viewModelScope.launch {
//            val result = getUserIdUseCase()
//            if (result.isSuccess){
//                val getId = result.getOrNull()
//                if (getId != null){
//                    _myId.emit(getId)
//                }
//            }
//        }
//    }

    private suspend fun getAverageRating() {
        val result = getAverageRatingUseCase(placeId)
        if (result.isSuccess) {
            val averageRating = result.getOrNull()
            if (averageRating != null) {
                reviewRating.emit((averageRating / 2).toFloat())
            }
        }
    }

//    fun getPlaceReview() {
//        viewModelScope.launch {
//            val result = getPlaceReviewListUseCase(placeId)
//            if (result.isSuccess) {
//                var placeReview = result.getOrNull()
//                if (placeReview != null) {
//                    _placeReviewList.emit(placeReview)
//                }
//            }
//            Log.d("placeReview", result.exceptionOrNull().toString())
//            Log.d("placeReview", "${result.isSuccess}")
//        }
//    }

    fun getPlaceReview() {
        viewModelScope.launch {
            val tempList: List<PlaceReviewInfo> = emptyList()
            val placeReviewResult =
                getPlaceReviewByPlaceIdUseCase(PlaceReviewByPlaceIdInfo(placeId, 20))
            if (placeReviewResult.isSuccess) {
                val review = placeReviewResult.getOrNull()
                if (review != null) {
                    _placeReviewList.emit(
(                   review.map {
                        PlaceReviewInfo(
                            it.writerNickname,
                            (it.starRating / 2).toFloat(),
                            it.review,
                            it.createdAt.substring(0,9),
                            it.userId,
                            getUserByNicknameUseCase(UserByNicknameInfo(null,null, it.writerNickname)).getOrNull()?.firstOrNull()!!.profile
                        )
                    }))
                }
            }
        }
    }

    sealed class Event {

    }

    companion object {
        const val initRating = 1.0F
        const val reviewCount = 20

        fun provideFactory(
            assistedFactory: PlaceIdFactory,
            placeId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeId) as T
            }
        }
    }
}
