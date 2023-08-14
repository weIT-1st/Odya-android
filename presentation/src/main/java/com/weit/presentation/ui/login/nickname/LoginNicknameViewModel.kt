package com.weit.presentation.ui.login.nickname

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.auth.IsDuplicateNicknameUseCase
import com.weit.domain.usecase.userinfo.GetUsernameUsecase
import com.weit.domain.usecase.userinfo.SetNicknameUsecase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginNicknameViewModel @Inject constructor(
    private val isDuplicateNicknameUseCase: IsDuplicateNicknameUseCase,
    private val getUsernameUsecase: GetUsernameUsecase,
    private val setNicknameUsecase: SetNicknameUsecase,
) : ViewModel() {

    val nickname = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            getUsernameUsecase.invoke().onSuccess { it ->
                if (it == null) {
                    _event.emit(Event.NullDefaultNickname)
                } else {
                    nickname.emit(it.toString())
                }
            }
        }
    }

    @MainThread
    fun setNickname() {
        viewModelScope.launch {
            isGoodNickname(nickname.value)
            if (event.equals(Event.GoodNickname)) {
                nickname.emit(nickname.value)
                setNicknameUsecase.invoke(nickname.value)
            }
        }
    }

    private fun isGoodNickname(newNickname: String) {
        viewModelScope.launch {
            val result = isDuplicateNicknameUseCase(newNickname)
            handleIsGoodNickname(newNickname, result)
        }
    }

    private suspend fun handleIsGoodNickname(newNickname: String, isDuplicate: Boolean) {
        if (hasSpecialChar(newNickname)) {
            _event.emit(Event.HasSpecialChar)
        } else {
            if (newNickname.length < 2) {
                _event.emit(Event.TooShortNickname)
            } else if (newNickname.length > 9) {
                _event.emit(Event.TooLongNickname)
            } else {
                if (isDuplicate) {
                    _event.emit(Event.GoodNickname)
                } else {
                    _event.emit(Event.DuplicateNickname)
                }
            }
        }
    }

    private fun hasSpecialChar(newNickname: String): Boolean {
        return Pattern.matches(REGEX_SPECIALCHAR, newNickname)
    }

    sealed class Event {
        object NullDefaultNickname : Event()
        object TooShortNickname : Event()
        object TooLongNickname : Event()
        object HasSpecialChar : Event()
        object DuplicateNickname : Event()
        object GoodNickname : Event()
    }

    companion object {
        private const val REGEX_SPECIALCHAR = "/[`~!@#\$%^&*|\'\";:/?]/gi"
    }
}
