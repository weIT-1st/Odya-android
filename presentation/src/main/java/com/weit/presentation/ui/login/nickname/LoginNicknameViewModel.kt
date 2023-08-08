package com.weit.presentation.ui.login.nickname

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.repository.login.GetUserNameRepository
import com.weit.domain.usecase.auth.IsDuplicateNicknameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.E

@HiltViewModel
class LoginNicknameViewModel @Inject constructor(
    private val isDuplicateNicknameUseCase: IsDuplicateNicknameUseCase,
    private val getUserNameRepository: GetUserNameRepository
) : ViewModel() {

    val nickname = MutableStateFlow("")
    val username = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            username.emit(getUserNameRepository.getUsername().toString())
            if (event.equals(Event.GoodNickname)){
                nickname.emit(username.value)
            }
        }
    }

    @MainThread
    fun setNickname(){
        viewModelScope.launch {
            isGoodNickname(nickname.value)
            if (event.equals(Event.GoodNickname)) {
                nickname.emit(nickname.value)
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