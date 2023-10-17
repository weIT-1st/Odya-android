package com.weit.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.usecase.user.DeleteUserUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<MyPageViewModel.Event>()
    val event = _event.asEventFlow()

    fun deleteUser() {
        viewModelScope.launch {
            val result = deleteUserUseCase()
            if (result.isSuccess) {
                Logger.t("MainTest").i("계정 삭제 성공")
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
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
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }
}
