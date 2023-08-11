package com.weit.presentation.ui.login.inputuserinfo

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.userinfo.GetNicknameUsecase
import com.weit.domain.usecase.userinfo.SetBirthUsecase
import com.weit.domain.model.GenderType
import com.weit.domain.usecase.userinfo.SetGenderUsecase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginInputUserInfoViewModel @Inject constructor(
    private val getNicknameUsecase: GetNicknameUsecase,
    private val setBirthUsecase: SetBirthUsecase,
    private val setGenderUsecase: SetGenderUsecase
): ViewModel() {
    private var gender = GenderType.IDLE
    private lateinit var birth: LocalDate

    lateinit var nickname: String

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            getNicknameUsecase.invoke().onSuccess { it
            nickname = it.toString()}
        }
    }

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            birth = LocalDate.of(year, month + 1, day)
            setBirthUsecase.invoke(birth)
        }
    }

    @MainThread
    fun setGender(genderType: GenderType) {
        viewModelScope.launch {
            gender = genderType
            setGenderUsecase.invoke(gender)
        }
    }

    private suspend fun checkInputGenderBirth(){
        if (gender == GenderType.IDLE){
            _event.emit(Event.GenderNotSelected)
        }

        if (::birth.isInitialized.not()){
            _event.emit(Event.BirthNotSelected)
        }
    }

    sealed class Event{
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
    }
}
