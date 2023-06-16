package com.weit.presentation.ui.login

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.auth.LoginWithKakaoUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _loginEvent = MutableEventFlow<Unit>()
    val loginEvent = _loginEvent.asEventFlow()

    private val _errorEvent = MutableEventFlow<Throwable>()
    val errorEvent = _errorEvent.asEventFlow()

    @MainThread
    fun onLoginWithKakao(loginWithKakaoUseCase: LoginWithKakaoUseCase) {
        viewModelScope.launch {
            val result = loginWithKakaoUseCase()
            if (result.isSuccess) {
                _loginEvent.emit(Unit)
            } else {
                _errorEvent.emit(result.exceptionOrNull() ?: Exception())
            }
        }
    }
}
