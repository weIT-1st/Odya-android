package com.weit.presentation.ui.profile.preferences.update

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.weit.domain.model.user.User
import com.weit.domain.usecase.auth.LogoutUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.domain.usecase.user.UpdateUserEmailUseCase
import com.weit.domain.usecase.user.UpdateUserInformationUseCase
import com.weit.domain.usecase.user.UpdateUserPhoneNumberUseCase
import com.weit.domain.usecase.userinfo.SetNicknameUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UpdateUserInfoViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val setNicknameUseCase: SetNicknameUseCase,
    private val updateUserInformationUseCase: UpdateUserInformationUseCase,
    private val updateUserPhoneNumberUseCase: UpdateUserPhoneNumberUseCase,
    private val updateUserEmailUseCase: UpdateUserEmailUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    val newNickname = MutableStateFlow("")
    val newPhoneNum = MutableStateFlow("")
    val newEmail = MutableStateFlow("")
    val phoneAuthNum = MutableStateFlow("")

    private val _isSameId = MutableStateFlow(false)
    val isSameId : StateFlow<Boolean> get() =  _isSameId

    private var phoneVerificationId = MutableStateFlow("")

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


    fun updateNickname() {
        viewModelScope.launch {
            val newNickname = newNickname.value

            val result = updateUserInformationUseCase(newNickname)
            if (result.isSuccess) {
                _event.emit(Event.SuccessUpdateNickname)
                setNicknameUseCase(newNickname)
            } else {
                // todo 에러 처리
            }
        }
    }

    fun sendPhoneAuth() {
        viewModelScope.launch {
            val isGood = checkIsGoodPhoneNum()
            if (isGood) {
                _event.emit(Event.SendPhoneAuth(changeInternationalPhoneNum()))
            } else {
                _event.emit(Event.IsNotGoodPhoneNum)
            }
        }
    }

    fun updateUserPhoneNum() {
        viewModelScope.launch {
            Log.d("jomi", "phoneUpdate")

            val phoneNum = newPhoneNum.value

            val result = updateUserPhoneNumberUseCase(phoneNum)
            if (result.isSuccess) {
                _event.emit(Event.SuccessUpdatePhone)
            } else {
                Log.d("jomi", "phoneUpdate fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun checkPhoneAuthCredential() {
        viewModelScope.launch {
            val phoneAuthNum = phoneAuthNum.value
            val verificationId = phoneVerificationId.value

            val credentials = PhoneAuthProvider.getCredential(verificationId, phoneAuthNum)
            _event.emit(Event.CheckPhoneAuth(credentials))
        }
    }
    fun sendEmailAuth() {
        viewModelScope.launch {
            val inputEmail = newEmail.value
            _event.emit(Event.SendEmailAuth(inputEmail))
        }
    }

    fun checkEmailAuth(credentials: AuthCredential) {
        viewModelScope.launch {
            _event.emit(Event.CheckEmailAuth(credentials))
        }
    }

    private fun saveUserNickname(newNickname: String) {
        viewModelScope.launch {
            val result = setNicknameUseCase(newNickname)

            if (result.isSuccess) {
                _event.emit(Event.SuccessUpdateNickname)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            if (result.isSuccess) {
                _event.emit(Event.SuccessLogout)
            }
        }
    }

    fun withdraw() {

    }

    fun checkIsGoodNickname(): Boolean {
        val inputNickname = newNickname.value

        val pattern = Pattern.compile(NICKNAME_REGEX)
        val matcher = pattern.matcher(inputNickname)

        return matcher.matches()
    }

    fun checkIsGoodPhoneNum(): Boolean {
        val inputPhone = newPhoneNum.value

        val pattern = Pattern.compile(PHONE_REGEX)
        val matcher = pattern.matcher(inputPhone)

        return matcher.matches()
    }

    private fun changeInternationalPhoneNum(): String {
        val inputPhone = newPhoneNum.value
        val firstNumber : String = inputPhone.substring(0,3)
        var phoneEdit = inputPhone.replace("-", "").substring(3)

        when(firstNumber){
            "010" -> phoneEdit = "+8210$phoneEdit"
            "011" -> phoneEdit = "+8211$phoneEdit"
            "016" -> phoneEdit = "+8216$phoneEdit"
            "017" -> phoneEdit = "+8217$phoneEdit"
            "018" -> phoneEdit = "+8218$phoneEdit"
            "019" -> phoneEdit = "+8219$phoneEdit"
            "106" -> phoneEdit = "+82106$phoneEdit"
        }

        return phoneEdit
    }

    fun settingPhonePhoneAuthCredential(verificationId: String) {
        viewModelScope.launch {
            phoneVerificationId.emit(verificationId)
        }
    }

    fun checkIsSameId() {
        viewModelScope.launch {
            val inputAuth = phoneAuthNum.value
            val id = phoneVerificationId.value

            _isSameId.emit(inputAuth == id)

        }
    }

    sealed class Event {
        object SuccessUpdateNickname : Event()
        object SuccessUpdatePhone : Event()
        object SuccessUpdateEmail : Event()
        object SuccessLogout : Event()
        object SuccessWithdraw : Event()
        object IsNotGoodPhoneNum: Event()

        data class SendPhoneAuth(
            val phoneNum: String
        ) : Event()
        data class CheckPhoneAuth(
            val credentials: PhoneAuthCredential
        ) : Event()
        data class SendEmailAuth(
            val email: String
        ) : Event()
        data class CheckEmailAuth(
            val credentials: AuthCredential
        ) : Event()
    }

    companion object {
        private const val NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{2,8}$"
        private const val PHONE_REGEX = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
    }
}
