package com.weit.presentation.ui.placereview.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.usecase.place.GetPlaceReviewDetailUseCase
import com.weit.domain.usecase.place.RegisterPlaceReviewUseCase
import com.weit.domain.usecase.place.UpdatePlaceReviewUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlaceReviewViewModel @Inject constructor(
    private val updatePlaceReviewUseCase: UpdatePlaceReviewUseCase,
    private val getPlaceReviewDetailUseCase: GetPlaceReviewDetailUseCase
) : ViewModel() {
    private var placeReviewId: Long = 0L
    private var placeId: String = ""
    var existReview: String = ""

    val rating = MutableStateFlow(0F)

    val review = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var job: Job = Job().apply { cancel() }

    init {
        getPlaceId()
        getPlaceReviewId()
        viewModelScope.launch {
            existReview = getPlaceReviewDetailUseCase(placeId)
        }
    }

    private fun getPlaceId(){

    }

    private fun getPlaceReviewId(){

    }

    fun updatePlaceReview(){
        if (job.isCompleted){
            updateReview()
        }
    }

    private fun updateReview() {
        job = viewModelScope.launch {
            if (checkRegistrationCondition()) {
                val placeReviewRegistrationInfo = PlaceReviewUpdateInfo(placeReviewId, (rating.value * 2).toInt(), review.value)
                val result = updatePlaceReviewUseCase(placeReviewRegistrationInfo)
                handleRegistrationResult(result)
            }
        }
    }

    private suspend fun handleRegistrationResult(result: Result<Unit>) {
        if (result.isSuccess) {
            _event.emit(Event.RegistrationSuccess)
        } else {
            handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
        }
    }

    private suspend fun handleRegistrationError(error: Throwable) {
        when (error) {
            is InvalidTokenException -> _event.emit(Event.InvalidTokenError)
            is DuplicatedSomethingException -> _event.emit(Event.IsDuplicatedReviewError)
            else -> _event.emit(Event.UnknownError)
        }
    }

    suspend fun setStar(newRating: Float) {
        if (newRating < 0.5F) {
            rating.emit(0.5F)
        }
    }

    private suspend fun checkRegistrationCondition(): Boolean {
        val ratingValue = rating.value
        val reviewValue = review.value

        if (ratingValue.equals(0F)) {
            _event.emit(Event.NotEnoughStarError)
            return false
        }
        if (ratingValue > 5F) {
            _event.emit(Event.TooManyStarError)
            return false
        }

        if (reviewValue.isEmpty()) {
            _event.emit(Event.TooShortReviewError)
            return false
        }
        if (reviewValue.length > 30) {
            _event.emit(Event.TooLongReviewError)
            return false
        }
        return true
    }

    sealed class Event {
        object RegistrationSuccess : Event()
        object NotEnoughStarError : Event()
        object TooManyStarError : Event()
        object TooShortReviewError : Event()
        object TooLongReviewError : Event()
        object UnregisteredError : Event()
        object IsDuplicatedReviewError : Event()
        object InvalidTokenError : Event()
        object UnknownError : Event()
    }
}
