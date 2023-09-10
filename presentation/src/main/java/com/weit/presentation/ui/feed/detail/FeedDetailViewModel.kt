package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.CreateFollowCreateUseCase
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.presentation.model.FeedComment
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.model.TopicDTO
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val createFollowCreateUseCase: CreateFollowCreateUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
) : ViewModel() {

    private val _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    private val _likeNum = MutableStateFlow<Int>(0)
    val likeNum: StateFlow<Int> get() = _likeNum

    private val _commentNum = MutableStateFlow<Int>(0)
    val commentNum: StateFlow<Int> get() = _commentNum

    private val _followState = MutableStateFlow<Boolean>(false)
    val followState: StateFlow<Boolean> get() = _followState

    private val _event = MutableEventFlow<FeedDetailViewModel.Event>()
    val event = _event.asEventFlow()
    private var userId: Long = 0
    init {
        getFeed()
    }

    private fun getFeed() {
        // TODO feedId로 상세 정보 가져오기
        viewModelScope.launch {
            val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
            val travelLog = TravelLogInFeed(1, "ddd", "")
            val topics = listOf(TopicDTO(1,"바다여행"),TopicDTO(2,"우주여행"))
            val comments = listOf<FeedComment>(
                FeedComment(1, 3, profile, "dd", "wowwow"),
                FeedComment(1, 1, profile, "dd", "wowwow"),
                FeedComment(1, 1, profile, "dd", "wowwow"),
            )

            val feed = FeedDetail(1, 4, profile, "dd", true, "dd", null, "Dd", "dd", 100, 100, "dd", comments,topics)
            userId = feed.userId
            val commentCount = minOf(comments.size, DEFAULT_COMMENT_COUNT)
            val defaultComments = comments
                .slice(0 until commentCount)
            val remainingCommentsCount = comments.size - defaultComments.size
            setFeedDetail(feed)
            _event.emit(Event.OnChangeFeed(feed.travelLog, defaultComments, remainingCommentsCount, comments, feed.topics))
        }
    }

    private fun setFeedDetail(feed: FeedDetail) {
        _feed.value = feed
        _likeNum.value = feed.likeNum
        _commentNum.value = feed.commentNum
        _followState.value = feed.followState
    }

    fun registerComment() {
        viewModelScope.launch {
            // TODO 댓글 등록 API
        }
    }

    fun onFollowStateChange() {
        viewModelScope.launch {
            _followState.value = !_followState.value
            val result = changeFollowStateUseCase(userId, _followState.value)
            if (result.isSuccess) {
                _event.emit(Event.CreateAndDeleteFollowSuccess)
            } else {
                _followState.value = !_followState.value
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is ExistedFollowingIdException -> _event.emit(Event.ExistedFollowingIdException)
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is InvalidPermissionException -> _event.emit(Event.NotHavePermissionException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {
        data class OnChangeFeed(
            val travelLog: TravelLogInFeed?,
            val defaultComments: List<FeedComment>,
            val remainingCommentsCount: Int,
            val comments: List<FeedComment>,
            val topics: List<TopicDTO>
        ) : Event()
        object CreateAndDeleteFollowSuccess : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object ExistedFollowingIdException : Event()
        object UnknownException : Event()
    }
    companion object {
        private const val DEFAULT_COMMENT_COUNT = 2
    }
}
