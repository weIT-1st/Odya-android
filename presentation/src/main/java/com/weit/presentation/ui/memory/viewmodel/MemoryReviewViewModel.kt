package com.weit.presentation.ui.memory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.domain.usecase.place.GetMyPlaceReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryReviewViewModel @Inject constructor(
    private val getPlaceMyReviewInfo: GetMyPlaceReviewUseCase
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
                Log.d("jomi", "get review : ${result.exceptionOrNull()}")
            }
        }
    }

    fun deleteReview() {

    }
}
