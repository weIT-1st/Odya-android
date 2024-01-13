package com.weit.presentation.ui.profile.lifeshot.detail

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.usecase.image.GetUserImageUseCase
import com.weit.domain.usecase.user.GetUserLifeshotUseCase
import com.weit.domain.usecase.user.GetUserStatisticsUseCase
import com.weit.presentation.model.profile.lifeshot.LifeShotImageDetailDTO
import com.weit.presentation.model.profile.lifeshot.LifeShotRequestDTO
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.profile.lifeshot.detail.menu.LifeShotDetailMenuViewModel
import com.weit.presentation.ui.profile.myprofile.MyProfileViewModel
import com.weit.presentation.ui.util.Constants
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LifeShotDetailViewModel @AssistedInject constructor(
    private val getUserLifeshotUseCase: GetUserLifeshotUseCase,
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase,
    @Assisted private val images: List<LifeShotImageDetailDTO>,
    @Assisted private val position: Int,
    @Assisted private val lifeshotRequestInfo: LifeShotRequestDTO,
) : ViewModel() {

    @AssistedFactory
    interface LifeShotDetailFactory {
        fun create(
            images: List<LifeShotImageDetailDTO>,
            position: Int,
            lifeshotRequestInfo: LifeShotRequestDTO
        ): LifeShotDetailViewModel
    }

    private val _event = MutableEventFlow<LifeShotDetailViewModel.Event>()
    val event = _event.asEventFlow()

    private val _lifeshots = MutableStateFlow<List<UserImageResponseInfo>>(emptyList())
    val lifeshots: StateFlow<List<UserImageResponseInfo>> get() = _lifeshots

    private var lastImageId: Long? = null
    private var totalLifeshotCount: Int = 0
    init {
        lastImageId = lifeshotRequestInfo.lastId
        viewModelScope.launch {
            _lifeshots.emit(
                images.map {
                    UserImageResponseInfo(
                        it.imageId,
                        it.imageUrl,
                        it.placeId,
                        it.isLifeShot,
                        it.placeName,
                        it.journalId,
                        it.communityId
                    )
                }
            )
        }
        getUserStatistics()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserStatistics() {
        viewModelScope.launch {
            val result = getUserStatisticsUseCase(lifeshotRequestInfo.userId)
            if (result.isSuccess) {
                totalLifeshotCount = result.getOrThrow().lifeShotCount
                _event.emit(
                    LifeShotDetailViewModel.Event.GetUserStatisticsSuccess(
                        position,
                        result.getOrThrow().lifeShotCount
                    )
                )
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun loadNextLifeShots() {
        viewModelScope.launch {
            val result = getUserLifeshotUseCase(
                LifeshotRequestInfo(DEFAULT_PAGE_SIZE, lastImageId,lifeshotRequestInfo.userId)
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

    fun transformImage(position: Int){
        viewModelScope.launch {
            _event.emit(
                LifeShotDetailViewModel.Event.OnTransformImage(
                    position,
                    totalLifeshotCount,
                    _lifeshots.value.get(position).imageId
                )
            )
        }
    }

    fun onDeleteCompeleted(currentPosition: Int){
        viewModelScope.launch {
            totalLifeshotCount -= 1
            val currentLifeShots = _lifeshots.value.toMutableList().apply {
                removeAt(currentPosition)
            }
            _lifeshots.emit(currentLifeShots)
            _event.emit(
                LifeShotDetailViewModel.Event.OnDeleteSuccess(
                    totalLifeshotCount,
                )
            )
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is NoMoreItemException -> {
            }
            else -> {
            }
        }
    }

    sealed class Event {

        data class GetUserStatisticsSuccess(
            val currentPosition: Int,
            val totalCount: Int
        ) : Event()

        data class OnTransformImage(
            val currentPosition : Int,
            val totalCount: Int,
            val imageId: Long
        ) : Event()

        data class OnDeleteSuccess(
            val totalCount: Int,
        ) : Event()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20

        fun provideFactory(
            assistedFactory: LifeShotDetailViewModel.LifeShotDetailFactory,
            images: List<LifeShotImageDetailDTO>,
            position: Int,
            lifeshotRequestInfo: LifeShotRequestDTO
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(images, position, lifeshotRequestInfo) as T
            }
        }
    }
}
