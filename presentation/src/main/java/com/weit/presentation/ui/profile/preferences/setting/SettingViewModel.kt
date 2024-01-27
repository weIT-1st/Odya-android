package com.weit.presentation.ui.profile.preferences.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.userinfo.GetIsAlarmAllUseCase
import com.weit.domain.usecase.userinfo.GetIsAlarmCommentUseCase
import com.weit.domain.usecase.userinfo.GetIsAlarmLikeUseCase
import com.weit.domain.usecase.userinfo.GetIsAlarmMarketingUseCase
import com.weit.domain.usecase.userinfo.GetIsLocationInfoUseCase
import com.weit.domain.usecase.userinfo.SetIsAlarmAllUseCase
import com.weit.domain.usecase.userinfo.SetIsAlarmCommentUseCase
import com.weit.domain.usecase.userinfo.SetIsAlarmLikeUseCase
import com.weit.domain.usecase.userinfo.SetIsAlarmMarketingUseCase
import com.weit.domain.usecase.userinfo.SetIsLocationInfoUseCase
import com.weit.presentation.ui.profile.preferences.detail.DetailType
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getIsAlarmAllUseCase: GetIsAlarmAllUseCase,
    private val getIsAlarmLikeUseCase: GetIsAlarmLikeUseCase,
    private val getIsAlarmCommentUseCase: GetIsAlarmCommentUseCase,
    private val getIsAlarmMarketingUseCase: GetIsAlarmMarketingUseCase,
    private val getIsLocationInfoUseCase: GetIsLocationInfoUseCase,
    private val setIsAlarmAllUseCase: SetIsAlarmAllUseCase,
    private val setIsAlarmLikeUseCase: SetIsAlarmLikeUseCase,
    private val setIsAlarmCommentUseCase: SetIsAlarmCommentUseCase,
    private val setIsAlarmMarketingUseCase: SetIsAlarmMarketingUseCase,
    private val setIsLocationInfoUseCase: SetIsLocationInfoUseCase
): ViewModel() {
    val isAlarmAll = MutableStateFlow(true)

    val isAlarmLike = MutableStateFlow(true)

    val isAlarmComment = MutableStateFlow(true)

    val isAlarmMarketing = MutableStateFlow(true)

    val isLocationInfo = MutableStateFlow(true)

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getIsAlarmAll()
        getIsAlarmLike()
        getIsAlarmComment()
        getIsAlarmMarketing()
        getIsLocationInfo()
    }

    private fun getIsAlarmAll() {
        viewModelScope.launch {
            val result = getIsAlarmAllUseCase()
            if (result.isSuccess) {
                result.getOrThrow()?.let { isAlarmAll.emit(it) }
            } else {
                // todo 에러 처리
            }
        }
    }

    private fun getIsAlarmLike() {
        viewModelScope.launch {
            val result = getIsAlarmLikeUseCase()
            if (result.isSuccess) {
                result.getOrThrow()?.let { isAlarmLike.emit(it) }
            } else {
                // todo 에러 처리
            }
        }
    }

    private fun getIsAlarmComment() {
        viewModelScope.launch {
            val result = getIsAlarmCommentUseCase()
            if (result.isSuccess) {
                result.getOrThrow()?.let { isAlarmComment.emit(it) }
            } else {
                // todo 에러 처리
            }
        }
    }

    private fun getIsAlarmMarketing() {
        viewModelScope.launch {
            val result = getIsAlarmMarketingUseCase()
            if (result.isSuccess) {
                result.getOrThrow()?.let { isAlarmMarketing.emit(it) }
            } else {
                // todo 에러 처리
            }
        }
    }

    private fun getIsLocationInfo() {
        viewModelScope.launch {
            val result = getIsLocationInfoUseCase()

            if (result.isSuccess) {
                result.getOrThrow()?.let { isLocationInfo.emit(it) }
            } else {
                // todo 에러 처리
            }
        }
    }

    fun setIsAlarmAll(isAlarm : Boolean, isAll: Boolean) {
        viewModelScope.launch{
            val result = setIsAlarmAllUseCase(isAlarm)
            if (result.isSuccess) {
                getIsAlarmAll()

                if (isAll) {
                    setIsAlarmLike(isAlarm, true)
                    setIsAlarmComment(isAlarm, true)
                    setIsAlarmMarketing(isAlarm, true)
                }
            } else {
                // todo 에러처리
            }
        }
    }

    fun setIsAlarmLike(isAlarm : Boolean, isAll : Boolean) {
        viewModelScope.launch{
            val result = setIsAlarmLikeUseCase(isAlarm)
            if (result.isSuccess) {
                getIsAlarmLike()

                if (isAll.not()) {
                    checkIsAlarmAll()
                }
            } else {
                // todo 에러처리
            }
        }
    }

    fun setIsAlarmComment(isAlarm : Boolean, isAll : Boolean) {
        viewModelScope.launch{
            val result = setIsAlarmCommentUseCase(isAlarm)

            if (result.isSuccess) {
                getIsAlarmComment()

                if (isAll.not()) {
                    checkIsAlarmAll()
                }
            } else {
                // todo 에러처리
            }
        }
    }

    fun setIsAlarmMarketing(isAlarm : Boolean, isAll : Boolean) {
        viewModelScope.launch{
            val result = setIsAlarmMarketingUseCase(isAlarm)

            if (result.isSuccess) {
                getIsAlarmMarketing()

                if (isAll.not()) {
                    checkIsAlarmAll()
                }
            } else {
                // todo 에러처리
            }
        }
    }

    fun setIsLocationInfo(isAlarm : Boolean) {
        viewModelScope.launch{
            val result = setIsLocationInfoUseCase(isAlarm)

            if (result.isSuccess) {
                getIsLocationInfo()
            } else {
                // todo 에러처리
            }
        }
    }

    private fun checkIsAlarmAll() {
        viewModelScope.launch {
            val like = isAlarmLike.value
            val comment = isAlarmComment.value
            val marketing = isAlarmMarketing.value

            if (like && comment && marketing) {
                setIsAlarmAll(isAlarm = true, isAll = false)
            } else {
                setIsAlarmAll(isAlarm = false, isAll = false)
            }
        }
    }

    fun moveToUpdateInfo() {
        viewModelScope.launch {
            _event.emit(Event.MoveToUpdateInfo)
        }
    }

    fun moveToSettingDetail(type : Int) {
        viewModelScope.launch{
            val detail = DetailType.fromPosition(type)
            _event.emit(Event.MoveToSettingDetail(detail))
        }
    }

    sealed class Event {
        object MoveToUpdateInfo : Event()
        data class MoveToSettingDetail(
            val detailType : String
        ) : Event()
    }
}
