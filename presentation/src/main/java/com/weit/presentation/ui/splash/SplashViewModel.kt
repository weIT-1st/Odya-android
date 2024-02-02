package com.weit.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private var _splashEvent = MutableEventFlow<Boolean>()
    val splashEvent = _splashEvent.asEventFlow()

    init {
        splashDelay()
    }

    private fun splashDelay() {
        viewModelScope.launch {
            delay(1000)
            _splashEvent.emit(true)
        }
    }
}
