package com.weit.presentation.ui.login.nickname

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.NicknameState
import com.weit.domain.usecase.userinfo.GetUsernameUsecase
import com.weit.domain.usecase.userinfo.SetNicknameUsecase
import com.weit.domain.usecase.userinfo.ValidateNicknameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginNicknameViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUsecase,
    private val setNicknameUseCase: SetNicknameUsecase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
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
            if (handleIsGoodNickname(newNickname)) {
                setNicknameUseCase(newNickname)
            }
        }
    }

    private suspend fun handleIsGoodNickname(newNickname: String): Boolean {
        val nicknameEvent = when (validateNicknameUseCase(newNickname)) {
            NicknameState.GoodNickname -> Event.GoodNickname
            NicknameState.IsDuplicateNickname -> Event.DuplicateNickname
            NicknameState.TooLongNickname -> Event.TooLongNickname
            NicknameState.TooShortNickname -> Event.TooShortNickname
            NicknameState.HasSpecialChar -> Event.HasSpecialChar
            NicknameState.UnknownReason -> Event.UnknownNickname
        }

        _event.emit(nicknameEvent)

        return event.equals(Event.GoodNickname)
    }

    sealed class Event {
        object NullDefaultNickname : Event()
        object TooShortNickname : Event()
        object TooLongNickname : Event()
        object HasSpecialChar : Event()
        object DuplicateNickname : Event()
        object GoodNickname : Event()
        object UnknownNickname : Event()
    }
}
