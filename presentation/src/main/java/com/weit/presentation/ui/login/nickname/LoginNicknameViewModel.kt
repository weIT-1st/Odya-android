package com.weit.presentation.ui.login.nickname

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.InvalidNicknameReason
import com.weit.domain.usecase.auth.IsDuplicateNicknameUseCase
import com.weit.domain.usecase.userinfo.GetNicknameUsecase
import com.weit.domain.usecase.userinfo.GetUsernameUsecase
import com.weit.domain.usecase.userinfo.SetNicknameUsecase
import com.weit.domain.usecase.userinfo.ValidateNicknameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginNicknameViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUsecase,
    private val setNicknameUseCase: SetNicknameUsecase,
    private val validateNicknameUseCase: ValidateNicknameUseCase
) : ViewModel() {

    val nickname = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            getUsernameUseCase().onSuccess { it ->
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
            val newNickname = nickname.value
            if (handleIsGoodNickname(newNickname)){
                setNicknameUseCase(newNickname)
            }
        }
    }

    private suspend fun handleIsGoodNickname(newNickname: String): Boolean {
        var result = false

        when (validateNicknameUseCase(newNickname)){
            InvalidNicknameReason.GoodNickname -> { _event.emit(Event.GoodNickname)
                result = true
            }
            InvalidNicknameReason.IsDuplicateNickname -> _event.emit(Event.DuplicateNickname)
            InvalidNicknameReason.TooLongNickname -> _event.emit(Event.TooLongNickname)
            InvalidNicknameReason.TooShortNickname -> _event.emit(Event.TooShortNickname)
            InvalidNicknameReason.HasSpecialChar -> _event.emit(Event.HasSpecialChar)
            InvalidNicknameReason.UnknownReason -> _event.emit(Event.UnknownNickcname)
        }

        return result
    }


    sealed class Event {
        object NullDefaultNickname : Event()
        object TooShortNickname : Event()
        object TooLongNickname : Event()
        object HasSpecialChar : Event()
        object DuplicateNickname : Event()
        object GoodNickname : Event()
        object UnknownNickcname : Event()
    }

}
