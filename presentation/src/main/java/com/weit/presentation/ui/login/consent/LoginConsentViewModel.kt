package com.weit.presentation.ui.login.consent

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class LoginConsentViewModel @Inject constructor(

) : ViewModel(){

    private var _isConsentApp =  MutableStateFlow(false)
    val isConsentApp: StateFlow<Boolean> get() = _isConsentApp

    private var _isConsentPrivacy = MutableStateFlow(false)
    val isConsentPrivacy: StateFlow<Boolean> get() = _isConsentPrivacy

    val isConsentAll: StateFlow<Boolean> = combine(_isConsentApp, _isConsentPrivacy) {app, privacy ->
        app && privacy
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    @MainThread
    fun checkConsentApp(){
        viewModelScope.launch(){
            val consentApp = _isConsentApp.value
            if (consentApp){_isConsentApp.emit(!consentApp)}
        }
    }

    @MainThread
    fun checkConsentPrivacy(){
        viewModelScope.launch {
            val consentPrivacy = _isConsentPrivacy.value
            if (consentPrivacy) {_isConsentPrivacy.emit(!consentPrivacy)}
        }
    }

    @MainThread
    fun checkConsentAll(){
        viewModelScope.launch {
            changeConsetAll()
        }
    }

    private suspend fun changeConsetAll(){
        var consentAll: Boolean = isConsentAll.value
        if (consentAll){
            _isConsentApp.emit(!consentAll)
            _isConsentPrivacy.emit(!consentAll)
        }
    }

}
