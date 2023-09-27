package com.weit.presentation.ui.login.inputuserinfo

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.GenderType
import com.weit.domain.model.auth.UserRegistrationInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.auth.DuplicatedSomethingException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.domain.usecase.userinfo.GetBirthUseCase
import com.weit.domain.usecase.userinfo.GetGenderUseCase
import com.weit.domain.usecase.userinfo.GetNicknameUseCase
import com.weit.domain.usecase.userinfo.GetTermIdListUseCase
import com.weit.domain.usecase.userinfo.GetUsernameUseCase
import com.weit.domain.usecase.userinfo.SetBirthUseCase
import com.weit.domain.usecase.userinfo.SetGenderUseCase
import com.weit.presentation.ui.feed.FeedViewModel
import com.weit.presentation.ui.login.user.registration.UserRegistrationViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LoginInputUserInfoViewModel @Inject constructor(
    private val getNicknameUseCase: GetNicknameUseCase,
    private val setBirthUseCase: SetBirthUseCase,
    private val setGenderUseCase: SetGenderUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getBirthUseCase: GetBirthUseCase,
    private val getGenderUseCase: GetGenderUseCase,
    private val getTermIdListUseCase: GetTermIdListUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    ) : ViewModel() {
    private var gender = GenderType.IDLE
    private lateinit var birth: LocalDate

    var nickname: String = ""

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            getNicknameUseCase().onSuccess { nickname = it.toString() }
        }
    }

    @MainThread
    fun setBirth(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            birth = LocalDate.of(year, month + 1, day)
            setBirthUseCase(birth)

        }
    }

    @MainThread
    fun setGender(position: Int) {
        viewModelScope.launch {
            var genderType = when (position) {
                0 -> GenderType.MALE
                1 -> GenderType.FEMALE
                else -> GenderType.IDLE
            }
            gender = genderType
            setGenderUseCase(genderType)

        }
    }

    private suspend fun checkUserRegistrationCondition() : Boolean {
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

    @MainThread
    fun registerUser() {
        viewModelScope.launch {
            if (checkUserRegistrationCondition()) {
                var username = ""
                var birth = LocalDate.of(2000,1,1)

                val usernameResult = getUsernameUseCase()
                if(usernameResult.isSuccess){
                    username = usernameResult.getOrThrow().toString()
                }else{
                    _event.emit(Event.GetStoredUsernameFaild)
                    Logger.t("MainTest").i("실패 ${usernameResult.exceptionOrNull()?.javaClass?.name}")
                }


                val birthResult = getBirthUseCase()
                if(birthResult.isSuccess){
                    birth = birthResult.getOrThrow()
                }else{
                    _event.emit(Event.GetStoredBirthFaild)
                    Logger.t("MainTest").i("실패 ${usernameResult.exceptionOrNull()?.javaClass?.name}")
                }

                val gender = getGenderUseCase()
                val termsIdList = getTermIdListUseCase()


                Logger.t("MainTest").i("${gender} ${birth} ${username} ${nickname} ${termsIdList}")

                val result = registerUserUseCase(
                    UserRegistrationInfo(
                        name = username,
                        nickname = nickname,
                        gender = gender.type,
                        birthday = birth,
                        termsIdList = termsIdList,
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

    sealed class Event {
        object GenderNotSelected : Event()
        object BirthNotSelected : Event()
        object RegistrationSuccess : Event()
        object DuplicatedNickname : Event()
        object RegistrationFailed : Event()
        object GetStoredUsernameFaild : Event()
        object GetStoredBirthFaild : Event()
    }
}
