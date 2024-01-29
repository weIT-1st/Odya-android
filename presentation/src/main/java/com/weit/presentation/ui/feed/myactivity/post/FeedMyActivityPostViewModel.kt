package com.weit.presentation.ui.feed.myactivity.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.usecase.community.GetMyCommunitiesUseCase
import com.weit.presentation.model.Feed
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.ui.feed.FeedViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class FeedMyActivityPostViewModel @Inject constructor(
    private val getMyCommunitiesUseCase: GetMyCommunitiesUseCase
) : ViewModel(){

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private var getJob: Job = Job().apply {
        complete()
    }
    private var communityLastId: Long? = null
    private val _postImages = MutableStateFlow<List<CommunityMyActivityContent>>(emptyList())
    val postImages: StateFlow<List<CommunityMyActivityContent>> get() = _postImages
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
            val result = getMyCommunitiesUseCase(
                CommunityRequestInfo(DEFAULT_PAGE_SIZE, communityLastId)
            )
            if (result.isSuccess) {
                val newImages = result.getOrThrow()
                communityLastId = newImages.last().communityId
                if (newImages.isEmpty()) {
                    onNextImages()
                }
                _postImages.emit(postImages.value + newImages)

            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }

        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is ExistedFollowingIdException -> _event.emit(FeedViewModel.Event.ExistedFollowingIdException)
            is NotExistTopicIdException -> _event.emit(FeedViewModel.Event.NotExistTopicIdException)
            is InvalidRequestException -> _event.emit(FeedViewModel.Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(FeedViewModel.Event.InvalidTokenException)
            is InvalidPermissionException -> _event.emit(FeedViewModel.Event.NotHavePermissionException)
            else -> _event.emit(FeedViewModel.Event.UnknownException)
        }
    }
    companion object {
        private const val DEFAULT_PAGE_SIZE = 18

    }
}
