package com.weit.presentation.ui.feed.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.notification.NotificationInfo
import com.weit.domain.usecase.notification.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
 ) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationInfo>>(emptyList())
    val notifications: StateFlow<List<NotificationInfo>> get() = _notifications

    fun initData(){
        viewModelScope.launch {
            val result = getNotificationsUseCase()
            _notifications.emit(result)
            Logger.t("MainTest").i("$result")

        }
    }


}

