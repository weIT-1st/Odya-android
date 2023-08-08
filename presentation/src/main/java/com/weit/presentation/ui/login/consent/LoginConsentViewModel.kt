package com.weit.presentation.ui.login.consent

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginConsentViewModel @Inject constructor(

) : ViewModel(){

    private var _isConsentApp =  MutableStateFlow(false)
    val isConsentApp: StateFlow<Boolean> get() = _isConsentApp

    private var _isConsentPrivacy = MutableStateFlow(false)
    val isConsentPrivacy: StateFlow<Boolean> get() = _isConsentPrivacy

    private val _isConsentAll : MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isConsentALl: MediatorLiveData<Boolean> = _isConsentAll

    init {
        if (_isConsentApp.value && _isConsentPrivacy.value) {
            _isConsentAll.postValue(true)
        } else {
            _isConsentAll.postValue(false)
        }
    }


    @MainThread
    fun setCheckApp(isCheck: Boolean){
        _isConsentApp.value = isCheck
    }

    @MainThread
    fun setCheckPrivacy(isCheck: Boolean){
        _isConsentPrivacy.value = isCheck
    }

}