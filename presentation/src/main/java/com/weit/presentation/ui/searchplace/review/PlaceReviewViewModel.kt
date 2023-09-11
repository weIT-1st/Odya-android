package com.weit.presentation.ui.searchplace.review

import android.content.res.Resources.NotFoundException
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
import com.weit.domain.usecase.place.GetPlaceReviewContentUseCase
import com.weit.presentation.ui.searchplace.SearchPlaceBottomSheetViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.util.PlaceReviewContentData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceReviewViewModel @AssistedInject constructor(
    private val getAverageRatingUseCase: GetAverageRatingUseCase,
    private val getPlaceReviewContentUseCase: GetPlaceReviewContentUseCase,
    private val deletePlaceReviewUseCase: DeletePlaceReviewUseCase,
    @Assisted private val placeId: String
) : ViewModel() {

    val reviewRating = MutableStateFlow(initRating)
    val reviewNum = reviewCount

    private val _placeReviewList = MutableStateFlow<List<PlaceReviewInfo>>(emptyList())
    val placeReviewList: StateFlow<List<PlaceReviewInfo>> get() = _placeReviewList

    private val _myPlaceReviewData = MutableStateFlow<PlaceReviewContentData?>(null)
    val myPlaceReviewData: StateFlow<PlaceReviewContentData?> get() = _myPlaceReviewData

    private val _review = MutableStateFlow<PlaceReviewInfo?>(null)
    val review : StateFlow<PlaceReviewInfo?> get() = _review

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

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
            val averageRating = result.getOrThrow()
            _event.emit(Event.GetAverageRatingSuccess)
            reviewRating.emit((averageRating / 2))
        } else {
            handleError(result.exceptionOrNull() ?: UnknownError())
        }
    }

    fun getPlaceReview() {
        viewModelScope.launch {
            val result = getPlaceReviewContentUseCase(placeId)
            if (result.isSuccess){
                val list = result.getOrThrow()
                _placeReviewList.emit(list)

                val myReview = list.find { it.isMine }
                if (myReview != null ){
                    _event.emit(Event.GetPlaceReviewWithMineSuccess)
                    _myPlaceReviewData.emit(PlaceReviewContentData(
                        myReview.placeReviewId,
                        myReview.review,
                        (myReview.rating * 2).toInt()))
                    if (myPlaceReviewData.value == null){
                        _event.emit(Event.DoNotGetMyReviewData)
                    }
                } else {
                    _event.emit(Event.GetPlaceReviewSuccess)
                }
            } else {
                handleError(result.exceptionOrNull() ?: UnknownError())
            }
        }
    }


    fun deleteMyReview(){
        viewModelScope.launch {
            if (myPlaceReviewData.value == null) {
                _event.emit(Event.GetPlaceReviewSuccess)
            }else {
                val result = deletePlaceReviewUseCase(myPlaceReviewData.value!!.placeReviewId)
                if (result.isSuccess){
                    _event.emit(Event.DeleteMyReviewSuccess)
                    getPlaceReview()
                } else {
                    handleError(result.exceptionOrNull() ?: UnknownError())
                }
            }
        }
    }

    private suspend fun handleError(error: Throwable){
        when (error ){
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is NotFoundException -> _event.emit(Event.NotFoundException)
            is ForbiddenException -> _event.emit(Event.ForbiddenException)
            is UnKnownException -> _event.emit(Event.UnKnownException)
        }
    }

    sealed class Event {
        object GetAverageRatingSuccess: Event()
        object GetPlaceReviewWithMineSuccess: Event()
        object UpdateMyReviewSuccess: Event()
        object DeleteMyReviewSuccess: Event()
        object DoNotGetMyReviewData: Event()
        object GetPlaceReviewSuccess: Event()
        object InvalidRequestException: Event()
        object InvalidTokenException : Event()
        object NotFoundException: Event()
        object ForbiddenException: Event()
        object UnKnownException: Event()
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
