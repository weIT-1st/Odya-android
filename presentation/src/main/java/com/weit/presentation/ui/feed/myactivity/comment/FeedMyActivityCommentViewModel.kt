package com.weit.presentation.ui.feed.myactivity.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.community.CommunityMyActivityCommentContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NoMoreItemException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.exception.topic.NotExistTopicIdException
import com.weit.domain.usecase.community.GetMyCommentCommunitiesUseCase
import com.weit.domain.usecase.community.GetMyLikeCommunitiesUseCase
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
class FeedMyActivityCommentViewModel @Inject constructor(
    private val getMyCommentCommunitiesUseCase: GetMyCommentCommunitiesUseCase,
) : ViewModel(){

    private val _event = MutableEventFlow<FeedViewModel.Event>()
    val event = _event.asEventFlow()

    private var getJob: Job = Job().apply {
        complete()
    }
    private var communityLastId: Long? = null
    private val _postContents = MutableStateFlow<List<CommunityMyActivityCommentContent>>(emptyList())
    val postContents: StateFlow<List<CommunityMyActivityCommentContent>> get() = _postContents
    init{
        onNextImages()
    }

    fun onNextImages() {
        if (getJob.isCompleted.not()) {
            return
        }
        loadNextFriends()
    }

    private fun loadNextFriends() {
        getJob = viewModelScope.launch {
            val result = getMyCommentCommunitiesUseCase(
                CommunityRequestInfo(DEFAULT_PAGE_SIZE, communityLastId)
            )
            if (result.isSuccess) {
                val newContents = result.getOrThrow()
                communityLastId = newContents.last().communityId
                if (newContents.isEmpty()) {
                    onNextImages()
                }
                _postContents.emit(postContents.value + newContents)

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
