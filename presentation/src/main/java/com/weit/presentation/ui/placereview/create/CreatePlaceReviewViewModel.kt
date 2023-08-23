package com.weit.presentation.ui.placereview.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.usecase.place.RegisterPlaceReviewUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlaceReviewViewModel @Inject constructor(
    private val registerPlaceReviewUseCase: RegisterPlaceReviewUseCase,
) : ViewModel() {
    private val placeId: String = ""

    val rating = MutableStateFlow(0F)

    val review = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var job: Job = Job().apply { cancel() }

    fun registerPlaceReview(){
        if (job.isCompleted){
            registerReview()
        }
    }

    private fun registerReview() {
        job = viewModelScope.launch {
            if (checkRegistrationCondition()) {
                val placeReviewRegistrationInfo = PlaceReviewRegistrationInfo(placeId, (rating.value * 2).toInt(), review.value)
                val result = registerPlaceReviewUseCase(placeReviewRegistrationInfo)
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
