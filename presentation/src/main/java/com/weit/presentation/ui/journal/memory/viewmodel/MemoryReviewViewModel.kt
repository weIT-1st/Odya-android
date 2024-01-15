package com.weit.presentation.ui.journal.memory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.domain.usecase.place.DeletePlaceReviewUseCase
import com.weit.domain.usecase.place.GetMyPlaceReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class MemoryReviewViewModel @Inject constructor(
    private val getPlaceMyReviewInfo: GetMyPlaceReviewUseCase,
    private val deletePlaceReviewUseCase: DeletePlaceReviewUseCase
) : ViewModel() {

    private val _myReviews = MutableStateFlow<List<PlaceMyReviewInfo>>(emptyList())
    val myReviews: StateFlow<List<PlaceMyReviewInfo>> get() = _myReviews

    private val reviews = CopyOnWriteArrayList<PlaceMyReviewInfo>()

    private var reviewLastId: Long? = null
    private var pageJob: Job = Job().apply {
        complete()
    }

    init {
        onNextReview()
    }

    fun onNextReview() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextReviews()
    }

    private fun loadNextReviews() {
        pageJob = viewModelScope.launch {
            val result = getPlaceMyReviewInfo(lastId = reviewLastId)

            if (result.isSuccess) {
                val newReviews = result.getOrThrow()

                if (newReviews.isNotEmpty()) {
                    reviewLastId = newReviews.last().placeReviewId
                }

                val originalReviews = reviews
                reviews.clear()
                reviews.addAll(originalReviews + newReviews)
                _myReviews.emit(reviews)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }


    fun deleteReview(placeReviewId: Long) {
        viewModelScope.launch {
            val result = deletePlaceReviewUseCase(placeReviewId)

            if (result.isSuccess){
                Log.d("memory", "Delete Review Success")
            } else {
                // todo 에러처리
                Log.d("memory", "Delete Review Error : ${result.exceptionOrNull()}")
            }
        }
    }
}
