package com.weit.presentation.ui.login.user.login

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.auth.NeedUserRegistrationException
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @MainThread
    fun onLoginWithKakao(loginWithKakaoUseCase: LoginWithKakaoUseCase) {
        viewModelScope.launch {
            val result = loginWithKakaoUseCase()
            if (result.isSuccess) {
                _event.emit(Event.LoginSuccess)
            } else {
                if (result.exceptionOrNull() is NeedUserRegistrationException) {
                    _event.emit(Event.UserRegistrationRequired)
                } else {
                    _event.emit(Event.LoginFailed)
                }
            }
        }
    }

    sealed class Event {
        object LoginSuccess : Event()
        object LoginFailed : Event()
        object UserRegistrationRequired : Event()
    }
}
