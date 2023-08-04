package com.weit.presentation.ui.login

import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.auth.RegisterUserUseCase
import com.weit.domain.usecase.user.UpdateUserInformationUseCase
import com.weit.presentation.ui.login.user.registration.UserRegistrationViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginStepOneViewModel @Inject constructor(
): ViewModel() {


    @MainThread
    fun setNewNickname(newNickname: String){
        if (!isDuplicateNickname(newNickname)){
        }
    }

    @MainThread
    fun createRandomNickname(){

    }

    @MainThread
    fun registerUser(){
        viewModelScope.launch {

        }
    }

    fun randomNickname(){

    }

    private fun isDuplicateNickname(newNickname: String): Boolean{
        return false
    }

}