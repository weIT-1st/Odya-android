package com.weit.presentation.ui.login.input.userinfo

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.GenderType
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.domain.usecase.userinfo.GetBirthUseCase
import com.weit.domain.usecase.userinfo.GetGenderUseCase
import com.weit.domain.usecase.userinfo.GetNicknameUseCase
import com.weit.domain.usecase.userinfo.GetTermIdListUseCase
import com.weit.domain.usecase.userinfo.GetUsernameUseCase
import com.weit.domain.usecase.userinfo.SetBirthUseCase
import com.weit.domain.usecase.userinfo.SetGenderUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginInputUserInfoViewModel @Inject constructor(
    private val getNicknameUseCase: GetNicknameUseCase,
    private val setBirthUseCase: SetBirthUseCase,
    private val setGenderUseCase: SetGenderUseCase,
    ) : ViewModel() {

    private val _birth = MutableStateFlow<LocalDate?>(null)
    val birth: StateFlow<LocalDate?> get() = _birth

    private val _gender = MutableStateFlow<GenderType>(GenderType.IDLE)
    val gender: StateFlow<GenderType> get() = _gender

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> get() = _nickname

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getNickname()
    }

    private fun getNickname(){
        viewModelScope.launch {
            val result = getNicknameUseCase()
            if (result.isSuccess){
                val getNickname = result.getOrThrow()
                if (getNickname != null) {
                    _nickname.emit(getNickname)
                } else{
                    Log.d("getNickname", "getNickname fail")
                }
            } else {
                Log.d("getNickname", "getNickname fail")
            }
        }
    }

    fun setBirth(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val inputBirth = LocalDate.of(year, month + 1, day)
            val result = setBirthUseCase(inputBirth)
            if (result.isSuccess){
                _birth.emit(inputBirth)
            }
        }
    }

    fun setGender(position: Int) {
        viewModelScope.launch {
            val genderType = when (position) {
                1 -> GenderType.MALE
                2 -> GenderType.FEMALE
                else -> GenderType.IDLE
            }

            if (genderType != GenderType.IDLE){
                val result = setGenderUseCase(genderType)
                if (result.isSuccess){
                    _gender.emit(genderType)
                } else {
                    _event.emit(Event.GenderNotSelected)
                }
            }
        }
    }

    private suspend fun checkUserRegistrationCondition(): Boolean {
        val initGender = gender.value
        val initBirth = birth.value

        if (initGender == GenderType.IDLE) {
            _event.emit(Event.GenderNotSelected)
            return false
        }
        if (initBirth == null) {
            _event.emit(Event.BirthNotSelected)
            return false
        }
        return true
    }

    fun completeRegister(){
        viewModelScope.launch {
            val check = checkUserRegistrationCondition()

            if (check){
                _event.emit(Event.ReadyToRegister)
            } else {
                _event.emit(Event.NotReadyToRegister)
            }
        }
    }

    sealed class Event {
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
        object ReadyToRegister : Event()
        object NotReadyToRegister : Event()
    }
}
