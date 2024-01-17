package com.weit.presentation.ui.profile.lifeshot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.usecase.community.GetMyCommunitiesUseCase
import com.weit.domain.usecase.image.GetUserImageUseCase
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.ui.feed.FeedViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LifeShotPickerViewModel @Inject constructor(
    private val getUserImageUseCase: GetUserImageUseCase,
) : ViewModel(){

    private val _event = MutableEventFlow<LifeShotPickerViewModel.Event>()
    val event = _event.asEventFlow()

    private var getJob: Job = Job().apply {
        complete()
    }
    private var imageLastId: Long? = null
    private val _lifeshotImages = MutableStateFlow<List<UserImageResponseInfo>>(emptyList())
    val lifeshotImages: StateFlow<List<UserImageResponseInfo>> get() = _lifeshotImages

    private var imageInfo: SelectLifeShotImageDTO? = null
    init{
        onNextImages()
    }

    fun onNextImages() {
        if (getJob.isCompleted.not()) {
            return
        }
        loadNextImages()
    }

    private fun loadNextImages() {
        getJob = viewModelScope.launch {
            val result = getUserImageUseCase(
                DEFAULT_PAGE_SIZE, imageLastId
            )
            if (result.isSuccess) {
                val newImages = result.getOrThrow()
                imageLastId = newImages.lastOrNull()?.imageId
                _lifeshotImages.emit(lifeshotImages.value + newImages)

            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }

        }
    }

    fun selectLifeShotImage(selectLifeShotImage: SelectLifeShotImageDTO){
        imageInfo = selectLifeShotImage
    }

    fun selectLifeShotPlace(placeName: String){
        viewModelScope.launch {
            _event.emit(Event.onSelectCompleted(imageInfo,placeName))
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
        data class onSelectCompleted(
            val imageInfo: SelectLifeShotImageDTO?,
            val placeName: String
        ) : Event()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 18

    }
}
