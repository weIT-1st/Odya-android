package com.weit.presentation.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.usecase.place.RegisterPlaceReviewUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val registerPlaceReviewUseCase: RegisterPlaceReviewUseCase,
) : ViewModel() {
    private val placeId: String = ""

    val rating = MutableStateFlow(0F)

    val review = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun registerPlaceReview() {
        val job = viewModelScope.launch {
            if (checkRegistrationCondition()) {
                val placeReviewRegistrationInfo = PlaceReviewRegistrationInfo(placeId, (rating.value * 2).toInt(), review.value)
                val result = registerPlaceReviewUseCase(placeReviewRegistrationInfo)
                handleRegistrationResult(result)
            }
        }

        runBlocking {
            job.join()
        }
    }

    private suspend fun handleRegistrationResult(result: Result<Unit>) {
        if (result.isSuccess) {
            _event.emit(Event.RegisrtationScuccess)
        } else {
            handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
        }
    }

    private suspend fun handleRegistrationError(error: Throwable) {
        when (error.message) {
            "-10006" -> _event.emit(Event.IsDuplicatedReviewError)
            "-11000" -> _event.emit(Event.InvalidTokenError)
        }
    }

    suspend fun setStar() {
        if (rating.value < 0.5F) {
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
        object RegisrtationScuccess : Event()
        object NotEnoughStarError : Event()
        object TooManyStarError : Event()
        object TooShortReviewError : Event()
        object TooLongReviewError : Event()
        object UnregisteredError : Event()
        object IsDuplicatedReviewError : Event()
        object InvalidTokenError : Event()
    }
}
