package com.weit.presentation.ui.profile.preferences.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.user.User
import com.weit.domain.usecase.auth.LogoutUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.domain.usecase.user.UpdateUserInformationUseCase
import com.weit.domain.usecase.userinfo.SetNicknameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUserInfoViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val setNicknameUseCase: SetNicknameUseCase,
    private val updateUserInformationUseCase: UpdateUserInformationUseCase,
    private val logoutUseCase: LogoutUseCase,
): ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> get() = _user

    val newNickname = MutableStateFlow("")
    val newPhoneNum = MutableStateFlow("")
    val newEmail = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        initUser()
    }

    private fun initUser() {
        viewModelScope.launch {
            val result = getUserUseCase()

            if (result.isSuccess) {
                _user.emit(result.getOrThrow())
            } else {
                // todo 에러 처리
            }
        }
    }

    fun updatePhone() {
//        viewModelScope.launch {
//            val newPhoneNum = newPhoneNum.value
//            val originalUser = user.value
//
//            if (originalUser != null) {
//                originalUser.phoneNumber = newPhoneNum
//
//                val result = updateUserInformationUseCase(originalUser)
//
//                if (result.isSuccess) {
//                    _event.emit(Event.SuccessUpdatePhone)
//                } else {
//                    // todo 에러 처리
//                }
//            }
//        }
    }

    fun updateNickname() {
//        viewModelScope.launch {
//            val newNickname = newNickname.value
//            val originalUser = user.value
//
//            if (originalUser != null) {
//                originalUser.nickname = newNickname
//
//                val result = updateUserInformationUseCase(originalUser)
//
//                if (result.isSuccess) {
//                    saveUserNickname(newNickname)
//                    _event.emit(Event.SuccessUpdateNickname)
//                } else {
//                    // todo 에러 처리
//                }
//            }
//        }
    }

    private fun saveUserNickname(newNickname: String) {
        viewModelScope.launch{
            val result = setNicknameUseCase(newNickname)

            if (result.isSuccess) {
                _event.emit(Event.SuccessUpdateNickname)
            }
        }
    }

    fun updateEmail() {
//        viewModelScope.launch {
//            val newEmail = newEmail.value
//            val originalUser = user.value
//
//            if (originalUser != null) {
//                originalUser.email = newEmail
//
//                val result = updateUserInformationUseCase(originalUser)
//
//                if (result.isSuccess) {
//                    _event.emit(Event.SuccessUpdateEmail)
//                } else {
//                    // todo 에러 처리
//                }
//            }
//        }
    }


    fun logout() {
        viewModelScope.launch{
            val result = logoutUseCase()
            if (result.isSuccess) {
                _event.emit(Event.SuccessLogout)
            }
        }
    }

    fun withdraw() {

    }

    sealed class Event {
        object SuccessUpdateNickname: Event()
        object SuccessUpdatePhone: Event()
        object SuccessUpdateEmail: Event()
        object SuccessLogout: Event()
        object SuccessWithdraw: Event()
    }

}
