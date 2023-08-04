package com.weit.presentation.ui.login

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginStepZeroViewModel @Inject constructor(

) : ViewModel(){

    private var _isConsentApp: MutableLiveData<Boolean> = MutableLiveData()
    val isConsetApp: LiveData<Boolean> get() = _isConsentApp

    private var _isConsentPrivacy: MutableLiveData<Boolean> = MutableLiveData()
    val isConsentPrivacy: LiveData<Boolean> get() = _isConsentPrivacy

    private var _isConsentAll: MutableLiveData<Boolean> = MutableLiveData()
    val isConsentALl: LiveData<Boolean> get() = _isConsentAll

    init {
        _isConsentApp.value = false
        _isConsentPrivacy.value = false
        _isConsentAll.value = false
    }


    @MainThread
    fun setCheckApp(isCheck: Boolean){
        _isConsentApp.value = isCheck
        if(_isConsentApp.value!! && _isConsentPrivacy.value!!){
            _isConsentAll.value = true
        } else if(!(_isConsentApp.value!! && _isConsentPrivacy.value!!)){
            _isConsentAll.value = false
        }
    }

    @MainThread
    fun setCheckPrivacy(isCheck: Boolean){
        _isConsentPrivacy.value = isCheck
        if(_isConsentApp.value!! && _isConsentPrivacy.value!!){
            _isConsentAll.value = true
        } else if(!(_isConsentApp.value!! && _isConsentPrivacy.value!!)){
            _isConsentAll.value = false
        }
    }

    @MainThread
    fun setCheckAll(isCheck: Boolean){
        _isConsentAll.value = isCheck
        _isConsentApp.value = isCheck
        _isConsentPrivacy.value = isCheck
    }




}