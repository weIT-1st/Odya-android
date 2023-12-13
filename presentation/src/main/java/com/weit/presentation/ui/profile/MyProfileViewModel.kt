package com.weit.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.usecase.user.DeleteUserUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<MyProfileViewModel.Event>()
    val event = _event.asEventFlow()

    init {
        getUserStatistics()
    }
    private fun getUserStatistics() {
        viewModelScope.launch {
            val result = getUserStatisticsUseCase(getUserIdUseCase())
            if (result.isSuccess) {
                _event.emit(Event.GetUserStatisticsSuccess(result.getOrThrow()))
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {

        data class GetUserStatisticsSuccess(
            val statistics : UserStatistics
        ) : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }
}

