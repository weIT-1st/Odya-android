package com.weit.presentation.ui.placereview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.place.PlaceReviewRegistrationInfo
import com.weit.domain.model.place.PlaceReviewUpdateInfo
import com.weit.domain.usecase.place.RegisterPlaceReviewUseCase
import com.weit.domain.usecase.place.UpdatePlaceReviewUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.util.PlaceReviewContentData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class EditPlaceReviewViewModel @AssistedInject constructor(
    private val registerPlaceReviewUseCase: RegisterPlaceReviewUseCase,
    private val updatePlaceReviewUseCase: UpdatePlaceReviewUseCase,
    @Assisted private val placeReviewContentData: PlaceReviewContentData?
) : ViewModel() {

    private var placeReviewId: Long? = null
    private var reviewState = register
    val rating = MutableStateFlow(initRating)
    val review = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var job: Job = Job().apply { cancel() }

    @AssistedFactory
    interface PlaceReviewContentFactory{
        fun create(placeReviewContentData: PlaceReviewContentData?): EditPlaceReviewViewModel
    }

    init {
        viewModelScope.launch {
            if (placeReviewContentData != null){
                reviewState = update
                placeReviewId = placeReviewContentData.placeReviewId
                rating.emit((placeReviewContentData.myRating / 2).toFloat())
                review.emit(placeReviewContentData.myReview)
            }
        }
    }

    fun updatePlaceReview(placeId: String) {
        if (job.isCompleted) {
            updateReview(placeId)
        }
    }

    private fun updateReview(placeId: String) {
        job = viewModelScope.launch {
            if (checkRegistrationCondition()) {
                when (reviewState) {
                    register -> {
                        val placeReviewRegistrationInfo = PlaceReviewRegistrationInfo(
                            placeId,
                            (rating.value * 2).toInt(),
                            review.value,
                        )
                        val result = registerPlaceReviewUseCase(placeReviewRegistrationInfo)
                        handleRegistrationResult(result)
                    }

                    update -> {
                        val placeReviewUpdateInfo = PlaceReviewUpdateInfo(
                            placeReviewId!!,
                            (rating.value * 2).toInt(),
                            review.value,
                        )
                        val result = updatePlaceReviewUseCase(placeReviewUpdateInfo)
                        handleRegistrationResult(result)
                    }
                }
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
            else -> error
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
    }

    companion object {
        const val initRating = 3.0F
        const val register = "register"
        const val update = "update"

        fun provideFactory(
            assistedFactory: PlaceReviewContentFactory,
            placeReviewContentData: PlaceReviewContentData?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeReviewContentData) as T
            }
        }
    }
}
