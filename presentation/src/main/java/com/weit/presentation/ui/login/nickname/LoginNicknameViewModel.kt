package com.weit.presentation.ui.login.nickname

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.auth.IsDuplicateNicknameUseCase
import com.weit.domain.usecase.userinfo.GetNicknameUsecase
import com.weit.domain.usecase.userinfo.GetUsernameUsecase
import com.weit.domain.usecase.userinfo.SetNicknameUsecase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginNicknameViewModel @Inject constructor(
    private val isDuplicateNicknameUseCase: IsDuplicateNicknameUseCase,
    private val getUsernameUsecase: GetUsernameUsecase,
    private val getNicknameUsecase: GetNicknameUsecase,
    private val setNicknameUsecase: SetNicknameUsecase
) : ViewModel() {

    val nickname = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            getUsernameUsecase.invoke().onSuccess { it ->
                if (it == null){
                    nickname.emit("닉네임을 입력하세요!!")
                } else {
                    nickname.emit(it.toString())
                }
            }
        }
    }

    @MainThread
    fun setNickname(){
        viewModelScope.launch {
            isGoodNickname(nickname.value)
            if (event.equals(Event.GoodNickname)) {
                nickname.emit(nickname.value)
                setNicknameUsecase.invoke(nickname.value)
            } else {
                nickname.emit("닉네임을 입력하세요.")
            }
        }
    }

    private fun isGoodNickname(newNickname: String){
        viewModelScope.launch {
            val result = isDuplicateNicknameUseCase(newNickname)
            handleIsDuplicateNickname(newNickname, result)
        }
    }


    private suspend fun handleIsDuplicateNickname(newNickname: String, result: Result<Unit>){
        if (newNickname.length < 2){
            _event.emit(Event.TooShortNickname)
        } else if (newNickname.length > 9) {
            _event.emit(Event.TooLongNickname)
        } else {
            if (result.isSuccess) {
                _event.emit(Event.GoodNickname)
            } else {
                _event.emit(Event.DuplicateNickname)
            }
        }
    }


    sealed class Event {
        object TooShortNickname : Event()
        object TooLongNickname : Event()
        object DuplicateNickname : Event()
        object GoodNickname : Event()
    }
}