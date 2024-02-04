package com.weit.presentation.ui.splash

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.auth.NeedUserRegistrationException
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
) : ViewModel() {
    private var _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @MainThread
    fun onLoginWithKakako(loginWithKakaoUseCase: LoginWithKakaoUseCase){
        viewModelScope.launch {
            val result = loginWithKakaoUseCase()
            if (result.isSuccess){
                _event.emit(Event.LoginSuccess)
            } else {
                val error = result.exceptionOrNull()
                if (error is NeedUserRegistrationException) {
                    _event.emit(Event.UserRegistrationRequired(error.username))
                }
                _event.emit(Event.LoginFail)
            }
        }
    }

    sealed class Event{
        object LoginSuccess : Event()
        object LoginFail : Event()
        data class UserRegistrationRequired(
            val username: String
        ) : Event()
    }
}