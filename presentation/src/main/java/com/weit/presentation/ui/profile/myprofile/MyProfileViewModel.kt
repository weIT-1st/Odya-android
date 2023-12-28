package com.weit.presentation.ui.profile.myprofile

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.usecase.user.GetUserLifeshotUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.util.Constants.DEFAULT_DATA_SIZE
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserLifeshotUseCase: GetUserLifeshotUseCase,
    ) : ViewModel() {
    private val _lifeshots = MutableStateFlow<List<UserImageResponseInfo>>(emptyList())
    val lifeshots: StateFlow<List<UserImageResponseInfo>> get() = _lifeshots
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private lateinit var user : User
    private var lifeShotJob: Job = Job().apply {
        complete()
    }
    private var lastImageId :Long? = null

    init {
        viewModelScope.launch {
            getUserUseCase().onSuccess {
                user = it
                getUserStatistics()
                onNextLifeShots()
            }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserStatistics() {
        viewModelScope.launch {
            val result = getUserStatisticsUseCase(user.userId)
                if (result.isSuccess) {
                    _event.emit(Event.GetUserStatisticsSuccess(result.getOrThrow(), user))
                } else {
                    handleError(result.exceptionOrNull() ?: UnKnownException())
                }
            }
        }
    fun onNextLifeShots() {
        if (lifeShotJob.isCompleted.not()) {
            return
        }
        loadNextLifeShots()
    }

    private fun loadNextLifeShots() {
        lifeShotJob = viewModelScope.launch {
            val result = getUserLifeshotUseCase(
                LifeshotRequestInfo(DEFAULT_DATA_SIZE,lastImageId,user.userId)
            )
            if (result.isSuccess) {
                val newLifeShots = result.getOrThrow()
                newLifeShots.lastOrNull()?.let {
                    lastImageId = it.imageId
                    _lifeshots.emit(lifeshots.value + newLifeShots)
                }
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())

            }
        }
    }


    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {

        data class GetUserStatisticsSuccess(
            val statistics : UserStatistics,
            val user: User
        ) : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object UnknownException : Event()
    }
}

