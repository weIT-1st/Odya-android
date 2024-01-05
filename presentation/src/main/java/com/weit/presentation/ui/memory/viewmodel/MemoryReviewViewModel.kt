package com.weit.presentation.ui.memory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.domain.usecase.place.DeletePlaceReviewUseCase
import com.weit.domain.usecase.place.GetMyPlaceReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryReviewViewModel @Inject constructor(
    private val getPlaceMyReviewInfo: GetMyPlaceReviewUseCase,
    private val deletePlaceReviewUseCase: DeletePlaceReviewUseCase
) : ViewModel() {

    private val _myReviews = MutableStateFlow<List<PlaceMyReviewInfo>>(emptyList())
    val myReviews: StateFlow<List<PlaceMyReviewInfo>> get() = _myReviews

    init {
        getMyPlaceReview()
    }

    private fun getMyPlaceReview(){
        viewModelScope.launch {
            val result = getPlaceMyReviewInfo()

            if (result.isSuccess){
                val reviews = result.getOrThrow()
                _myReviews.emit(reviews)
            } else {
                // todo 에러처리
                Log.d("memory", "Get My Review Error : ${result.exceptionOrNull()}")
            }
        }
    }

    fun deleteReview(placeReviewId: Long) {
        viewModelScope.launch {
            val result = deletePlaceReviewUseCase(placeReviewId)

            if (result.isSuccess){
                Log.d("memory", "Delete Review Success")
                getMyPlaceReview()
            } else {
                // todo 에러처리
                Log.d("memory", "Delete Review Error : ${result.exceptionOrNull()}")
            }
        }
    }
}
