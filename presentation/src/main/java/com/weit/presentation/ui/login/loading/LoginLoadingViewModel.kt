package com.weit.presentation.ui.login.loading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.GenderType
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.domain.usecase.userinfo.GetBirthUseCase
import com.weit.domain.usecase.userinfo.GetGenderUseCase
import com.weit.domain.usecase.userinfo.GetNicknameUseCase
import com.weit.domain.usecase.userinfo.GetTermIdListUseCase
import com.weit.domain.usecase.userinfo.GetUsernameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginLoadingViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getNicknameUseCase: GetNicknameUseCase,
    private val getBirthUseCase: GetBirthUseCase,
    private val getGenderUseCase: GetGenderUseCase,
    private val getTermIdListUseCase: GetTermIdListUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> get() = _nickname

    private val _birth = MutableStateFlow<LocalDate?>(null)
    val birth: StateFlow<LocalDate?> get() = _birth

    private val _gender = MutableStateFlow<GenderType>(GenderType.IDLE)
    val gender: StateFlow<GenderType> get() = _gender

    private val _termList = MutableStateFlow<Set<Long>>(emptySet())
    val termList: StateFlow<Set<Long>> get() = _termList


    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getRegisterInfo()
        register()
    }

    private fun getRegisterInfo() {
        viewModelScope.launch {
            val userNameResult = getUsernameUseCase()
            val nicknameResult = getNicknameUseCase()
            val birthResult = getBirthUseCase()
            val genderResult = getGenderUseCase()
            val termListResult = getTermIdListUseCase()

            if (userNameResult.isSuccess) {
                _userName.emit(userNameResult.getOrThrow())
            }

            if (nicknameResult.isSuccess) {
                _nickname.emit(nicknameResult.getOrThrow())
            }

            if (birthResult.isSuccess) {
                _birth.emit(birthResult.getOrThrow())
            }

            if (genderResult == GenderType.IDLE) {

            } else {
                _gender.emit(genderResult)
            }

            _termList.emit(termListResult)
        }
    }

    private fun register() {
        viewModelScope.launch {
            val initUserName = userName.value ?: ""
            val initNickname = nickname.value ?: ""
            val initBirth = birth.value
            val initGender = gender.value
            val initTermList = termList.value

            if (initUserName.isBlank().not() && initNickname.isBlank()
                    .not() && initBirth != null
            ) {
                val result = registerUserUseCase(
                    UserRegistrationInfo(
                        initUserName,
                        null,
                        null,
                        initNickname,
                        initGender.type,
                        initBirth,
                        initTermList
                    )
                )

                if (result.isSuccess) {
                    _event.emit(Event.SuccessRegister)
                } else {
                    handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
                }

            }
        }
    }

    private fun handleRegistrationError(error: Throwable){
        viewModelScope.launch {
            when (error){
                is InvalidRequestException -> {
                    _event.emit(Event.InvalidRequestException)
                }
                is DuplicatedSomethingException -> {
                    _event.emit(Event.DuplicatedSomethingException)
                }
                is UnKnownException -> {
                    _event.emit(Event.UnKnownException)
                }
            }
        }
    }

    sealed class Event {
        object SuccessRegister : Event()
        object InvalidRequestException : Event()
        object DuplicatedSomethingException : Event()
        object UnKnownException : Event()
    }
}
