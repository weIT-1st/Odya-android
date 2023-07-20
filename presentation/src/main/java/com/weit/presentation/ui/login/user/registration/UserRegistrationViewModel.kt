package com.weit.presentation.ui.login.user.registration

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.presentation.model.GenderType
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserRegistrationViewModel @AssistedInject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    @Assisted private val username: String,
) : ViewModel() {

    val nickname = MutableStateFlow("")
    private var gender = GenderType.IDLE
    private lateinit var birth: LocalDate

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface UsernameFactory {
        fun create(username: String): UserRegistrationViewModel
    }

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        birth = LocalDate.of(year, month + 1, day)
    }

    @MainThread
    fun setGender(genderType: GenderType) {
        gender = genderType
    }

    @MainThread
    fun registerUser() {
        viewModelScope.launch {
            if (checkUserRegistrationCondition()) {
                val result = registerUserUseCase(
                    UserRegistrationInfo(
                        name = username,
                        nickname = nickname.value,
                        gender = gender.type,
                        birthday = birth,
                    ),
                )
                handleRegistrationResult(result)
            }
        }
    }

    private suspend fun handleRegistrationResult(result: Result<Unit>) {
        if (result.isSuccess) {
            _event.emit(Event.RegistrationSuccess)
        } else {
            handleRegistrationError(result.exceptionOrNull() ?: UnKnownException())
        }
    }

    private suspend fun handleRegistrationError(error: Throwable) {
        // TODO 에러 코드가 추가 되면 에러 처리 세분화
        when (error) {
            is DuplicatedSomethingException -> _event.emit(Event.DuplicatedNickname)
            else -> _event.emit(Event.RegistrationFailed)
        }
    }

    private suspend fun checkUserRegistrationCondition(): Boolean {
        if (nickname.value.isEmpty()) {
            _event.emit(Event.NicknameIsEmpty)
            return false
        }
        if (gender == GenderType.IDLE) {
            _event.emit(Event.GenderNotSelected)
            return false
        }
        if (::birth.isInitialized.not()) {
            _event.emit(Event.BirthNotSelected)
            return false
        }
        return true
    }

    sealed class Event {
        object NicknameIsEmpty : Event()
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
        object RegistrationSuccess : Event()
        object DuplicatedNickname : Event()
        object RegistrationFailed : Event()
    }
}
