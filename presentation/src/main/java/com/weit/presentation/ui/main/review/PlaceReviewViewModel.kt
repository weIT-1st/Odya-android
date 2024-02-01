package com.weit.presentation.ui.main.review

import android.content.res.Resources.NotFoundException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.ForbiddenException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewInfo
import com.weit.domain.usecase.place.DeletePlaceReviewUseCase
import com.weit.domain.usecase.place.GetAverageRatingUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlaceReviewContentUseCase
import com.weit.domain.usecase.report.ReviewReportUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.util.PlaceReviewContentData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class PlaceReviewViewModel @AssistedInject constructor(
    private val getAverageRatingUseCase: GetAverageRatingUseCase,
    private val getPlaceReviewContentUseCase: GetPlaceReviewContentUseCase,
    private val deletePlaceReviewUseCase: DeletePlaceReviewUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    @Assisted private val placeId: String,
) : ViewModel() {

    val reviewRating = MutableStateFlow(initRating)

    private val _placeName = MutableStateFlow("")
    val placeName: StateFlow<String> get() = _placeName

    private val _placeReviewList = MutableStateFlow<List<PlaceReviewInfo>>(emptyList())
    val placeReviewList: StateFlow<List<PlaceReviewInfo>> get() = _placeReviewList

    private val _myPlaceReviewData = MutableStateFlow<PlaceReviewContentData?>(null)
    val myPlaceReviewData: StateFlow<PlaceReviewContentData?> get() = _myPlaceReviewData

    private var lastReviewId: Long? = null
    private var reviewJob: Job = Job().apply { complete() }

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface PlaceIdFactory {
        fun create(placeId: String): PlaceReviewViewModel
    }

    init {
        updateReviewInfo()
        getPlaceDetail()
    }

    fun updateReviewInfo() {
        viewModelScope.launch {
            lastReviewId = null
            getAverageRating()
            onNextReviews()
        }
    }

    private fun getPlaceDetail() {
        viewModelScope.launch {
            val placeDetail = getPlaceDetailUseCase(placeId)
            _placeName.emit(placeDetail.name ?: "")
        }
    }

    private suspend fun getAverageRating() {
        val result = getAverageRatingUseCase(placeId)
        if (result.isSuccess) {
            val averageRating = result.getOrThrow()
            _event.emit(Event.GetAverageRatingSuccess)
            reviewRating.emit((averageRating / 2))
        } else {
            handleError(result.exceptionOrNull() ?: UnknownError())
        }
    }

    fun onNextReviews() {
        if (reviewJob.isCompleted.not()) {
            return
        }
        loadNextReviews()
    }

    private fun loadNextReviews() {
        reviewJob = viewModelScope.launch {
            val result = getPlaceReviewContentUseCase(DEFAULT_REVIEW_SIZE, lastReviewId, placeId)

            if (result.isSuccess) {
                val newReviews = result.getOrThrow()
                newReviews.lastOrNull()?.let {
                    lastReviewId = it.placeReviewId
                    _placeReviewList.emit(makeMyReviewFirst(placeReviewList.value + newReviews))
                }
            } else {
                // todo 에러 처리
            }
        }
    }

    private fun makeMyReviewFirst(reviews: List<PlaceReviewInfo>) : List<PlaceReviewInfo> {
            val myReview = reviews.find { it.isMine }
            registrationMyReview(myReview)

            return if (myReview == null) {
                reviews
            } else {
                val reviewsWithMyReview = reviews.toMutableList()
                reviewsWithMyReview.remove(myReview)
                reviewsWithMyReview.add(0, myReview)
                reviewsWithMyReview
            }

    }

    private fun registrationMyReview(myReview: PlaceReviewInfo?) {
        viewModelScope.launch {
            if (myReview == null) {
                _myPlaceReviewData.emit(null)
            } else {
                _myPlaceReviewData.emit(
                    PlaceReviewContentData(
                        myReview.placeReviewId,
                        myReview.review,
                        (myReview.rating * 2).toInt(),
                    )
                )
            }
        }
    }

    fun deleteMyReview() {
        viewModelScope.launch {
            val myReview = myPlaceReviewData.value
            if (myReview == null) {
                _event.emit(Event.DoNotGetMyReviewData)
            } else {
                val result = deletePlaceReviewUseCase(myReview.placeReviewId)
                if (result.isSuccess) {
                    _event.emit(Event.DeleteMyReviewSuccess)
                    _myPlaceReviewData.emit(null)
                    getAverageRating()
                    val list = placeReviewList.value.filterNot { it.isMine }
                    _placeReviewList.emit(list.toList())
                } else {
                    Log.d("deleteReview", "delete fail : ${result.exceptionOrNull()}")
                    handleError(result.exceptionOrNull() ?: UnknownError())
                }
            }
        }
    }

    fun onClickCreateReview() {
        viewModelScope.launch {
            _event.emit(Event.PopUpEditReview(myPlaceReviewData.value))
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is NotFoundException -> _event.emit(Event.NotFoundException)
            is ForbiddenException -> _event.emit(Event.ForbiddenException)
            is UnKnownException -> _event.emit(Event.UnKnownException)
        }
    }

    sealed class Event {
        object GetAverageRatingSuccess : Event()
        object GetPlaceReviewWithMineSuccess : Event()
        object UpdateMyReviewSuccess : Event()
        object DeleteMyReviewSuccess : Event()
        object DoNotGetMyReviewData : Event()
        object GetPlaceReviewSuccess : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotFoundException : Event()
        object ForbiddenException : Event()
        object UnKnownException : Event()
        data class PopUpEditReview(val myReview: PlaceReviewContentData?) : Event()
    }

    companion object {
        const val initRating = 1.0F
        const val DEFAULT_REVIEW_SIZE = 15
        fun provideFactory(
            assistedFactory: PlaceIdFactory,
            placeId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeId) as T
            }
        }
    }
}
