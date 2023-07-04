package com.weit.presentation.ui.login.user.registration

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.presentation.model.GenderType
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    val name = MutableStateFlow("")
    val nickname = MutableStateFlow("")
    private var gender = GenderType.IDLE
    // TODO 이렇게 받는게 맞냐
    private var birth = listOf<Int>()

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        birth = listOf(year, month, day)
    }

    @MainThread
    fun setGender(genderType: GenderType) {
        gender = genderType
    }

    @MainThread
    fun registerUser() {
        viewModelScope.launch {
            if (checkUserRegistrationCondition()) {
                registerUserUseCase(
                    UserRegistrationInfo(
                        name = name.value,
                        nickname = nickname.value,
                        gender = gender.type,
                        birthday = birth
                    )
                )
            }
        }
    }

    private suspend fun checkUserRegistrationCondition(): Boolean {
        if (name.value.isEmpty()) {
            _event.emit(Event.NameIsEmpty)
            return false
        }
        if (nickname.value.isEmpty()) {
            _event.emit(Event.NicknameIsEmpty)
            return false
        }
        if (gender == GenderType.IDLE) {
            _event.emit(Event.GenderNotSelected)
            return false
        }
        if (birth.isEmpty()) {
            _event.emit(Event.BirthNotSelected)
            return false
        }
        return true
    }

    sealed class Event {
        object NameIsEmpty : Event()
        object NicknameIsEmpty : Event()
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
        object RegistrationSuccess : Event()
        object RegistrationFailed : Event()
    }
}
