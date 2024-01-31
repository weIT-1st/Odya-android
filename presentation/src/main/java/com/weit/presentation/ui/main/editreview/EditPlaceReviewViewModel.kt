package com.weit.presentation.ui.main.editreview

import android.content.res.Resources.NotFoundException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.exception.UnKnownException
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditPlaceReviewViewModel @AssistedInject constructor(
    private val registerPlaceReviewUseCase: RegisterPlaceReviewUseCase,
    private val updatePlaceReviewUseCase: UpdatePlaceReviewUseCase,
    @Assisted private val placeReviewContentData: PlaceReviewContentData?,
) : ViewModel() {

    private var placeReviewId: Long? = null
    private var reviewState = register
    private val _rating = MutableStateFlow(initRating)
    val rating: StateFlow<Float> get() = _rating
    val review = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var job: Job = Job().apply { cancel() }

    @AssistedFactory
    interface PlaceReviewContentFactory {
        fun create(placeReviewContentData: PlaceReviewContentData?): EditPlaceReviewViewModel
    }

    init {
        viewModelScope.launch {
            if (placeReviewContentData != null) {
                reviewState = update
                placeReviewId = placeReviewContentData.placeReviewId
                _rating.emit((placeReviewContentData.myRating.toFloat() / 2))
                review.emit(placeReviewContentData.myReview)
            }
        }
    }

    fun updatePlaceReview(placeId: String) {
        if (job.isCompleted) {
            updateReview(placeId)
        }
    }

    fun setRating(rating: Float) {
        viewModelScope.launch {
            _rating.emit(rating)
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
            is InvalidRequestException -> _event.emit(Event.InvalidRequestError)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenError)
            is RequestResourceAlreadyExistsException -> _event.emit(Event.AlreadyRegisterReviewError)
            is NotFoundException -> _event.emit(Event.NotExistPlaceReview)
            else -> error
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
        object InvalidRequestError : Event()
        object InvalidTokenError : Event()
        object AlreadyRegisterReviewError : Event()
        object NotExistPlaceReview : Event()
    }

    companion object {
        const val initRating = 3.0F
        const val register = "register"
        const val update = "update"

        fun provideFactory(
            assistedFactory: PlaceReviewContentFactory,
            placeReviewContentData: PlaceReviewContentData?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeReviewContentData) as T
            }
        }
    }
}
