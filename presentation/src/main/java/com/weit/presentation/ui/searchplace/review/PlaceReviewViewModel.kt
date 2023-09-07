package com.weit.presentation.ui.searchplace.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlaceReviewByPlaceIdInfo
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.place.DeletePlaceReviewUseCase
import com.weit.domain.usecase.place.GetAverageRatingUseCase
import com.weit.domain.usecase.place.GetPlaceReviewByPlaceIdUseCase
import com.weit.domain.usecase.place.UpdatePlaceReviewUseCase
import com.weit.domain.usecase.user.GetUserByNicknameUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceReviewViewModel @AssistedInject constructor(
    private val getAverageRatingUseCase: GetAverageRatingUseCase,
    private val getPlaceReviewByPlaceIdUseCase: GetPlaceReviewByPlaceIdUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val deletePlaceReviewUseCase: DeletePlaceReviewUseCase,
    @Assisted private val placeId: String
) : ViewModel() {

    val reviewRating = MutableStateFlow(initRating)
    val reviewNum = reviewCount

    private val _placeReviewList = MutableStateFlow<List<PlaceReviewInfo>>(emptyList())
    val placeReviewList: StateFlow<List<PlaceReviewInfo>> get() = _placeReviewList

    private val _myPlaceReviewID = MutableStateFlow(0L)
    val myPlaceReviewID : StateFlow<Long> get() = _myPlaceReviewID

    private val _myReview = MutableStateFlow("")
    val myReview : StateFlow<String> get() = _myReview

    private val _myRating = MutableStateFlow(initRating)
    val myRating : StateFlow<Float> get() = _myRating

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): PlaceReviewViewModel
    }

    init {
        viewModelScope.launch {
            getAverageRating()
            getPlaceReview()
        }
    }

    private suspend fun getAverageRating() {
        val result = getAverageRatingUseCase(placeId)
        if (result.isSuccess) {
            val averageRating = result.getOrNull()
            if (averageRating != null) {
                reviewRating.emit((averageRating / 2).toFloat())
            }
        }
    }

    fun getPlaceReview() {
        viewModelScope.launch {
            val placeReviewResult =
                getPlaceReviewByPlaceIdUseCase(PlaceReviewByPlaceIdInfo(placeId, 20))
            if (placeReviewResult.isSuccess) {
                val review = placeReviewResult.getOrNull()
                if (review != null) {
                    val list = (review.map {
                        PlaceReviewInfo(
                            it.writerNickname,
                            (it.starRating / 2).toFloat(),
                            it.review,
                            it.createdAt.substring(0,10),
                            it.userId,
                            false,
                            it.id,
                            getUserByNicknameUseCase(UserByNicknameInfo(null,null, it.writerNickname)).getOrNull()?.firstOrNull()!!.profile
                        )
                    })
                    val myReview = list.find { it.userId == getUserIdUseCase() }
                    if (myReview != null){
                        myReview.isMine = true
                        val mutableList = list.toMutableList()
                        mutableList.remove(myReview)
                        mutableList.add(0, myReview)
                        _placeReviewList.emit(mutableList)
                        _myPlaceReviewID.emit(myReview.placeReviewId)
                        _myReview.emit(myReview.review)
                        _myRating.emit(myReview.rating)
                    } else {
                        _placeReviewList.emit(list)
                    }
                }
            }
        }
    }


    fun deleteMyReview(){
        viewModelScope.launch {
            val result = deletePlaceReviewUseCase(myPlaceReviewID.value)
            Log.d("delete review", "id : ${myPlaceReviewID.value}")
            if (result.isSuccess){
                Log.d("delete review", "isSuccess : ${result.isSuccess}")
            } else {
                Log.d("delete review", "isSuccess : ${result.isSuccess}")
            }
        }
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
