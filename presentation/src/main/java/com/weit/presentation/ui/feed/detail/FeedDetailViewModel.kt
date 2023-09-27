package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.follow.FollowFollowingIdInfo
import com.weit.domain.usecase.follow.CreateFollowCreateUseCase
import com.weit.domain.usecase.follow.DeleteFollowUseCase
import com.weit.presentation.model.FeedComment
import com.weit.presentation.model.FeedDetail
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
) : ViewModel() {

    private val _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    private val _likeNum = MutableStateFlow<Int?>(null)
    val likeNum: StateFlow<Int?> get() = _likeNum

    private val _commentNum = MutableStateFlow<Int?>(null)
    val commentNum: StateFlow<Int?> get() = _commentNum

    private val _event = MutableEventFlow<FeedDetailViewModel.Event>()
    val event = _event.asEventFlow()
    var userId: Long = 0
    init {
        getFeed()
    }

    private fun getFeed() {
        // TODO feedId로 상세 정보 가져오기
        viewModelScope.launch {
            val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
            val travelLog = TravelLogInFeed(1, "ddd", "")

            val comments = listOf<FeedComment>(
                FeedComment(1, 1, profile, "dd", "wowwow"),
                FeedComment(1, 1, profile, "dd", "wowwow"),
                FeedComment(1, 1, profile, "dd", "wowwow"),
            )

            val feed = FeedDetail(1, 1, profile, "dd", true, "dd", null, "Dd", "dd", 100, 100, "dd", comments)
            userId = feed.userId
            val commentCount = minOf(comments.size, DEFAULT_COMMENT_COUNT)
            val defaultComments = comments
                .slice(0 until commentCount)
            val remainingCommentsCount = comments.size - defaultComments.size
            setFeedDetail(feed)
            _event.emit(Event.OnChangeFeed(feed.travelLog, defaultComments, remainingCommentsCount, comments))
        }
    }

    private fun setFeedDetail(feed: FeedDetail) {
        _feed.value = feed
        _likeNum.value = feed.likeNum
        _commentNum.value = feed.commentNum
    }

    fun registerComment() {
        viewModelScope.launch {
            // TODO 댓글 등록 API
        }
    }

    fun onFollowStateChange(isChecked: Boolean) {
        viewModelScope.launch {
            Logger.t("MainTest").i("feed like $userId")
            if (isChecked) {
                val result = createFollowCreateUseCase(FollowFollowingIdInfo(userId))
                if (result.isSuccess) {
                    _event.emit(Event.CreateFollowSuccess)
                } else {
                    handleError(result.exceptionOrNull() ?: UnKnownException())
                    Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                }
            } else {
                val result = deleteFollowUseCase(FollowFollowingIdInfo(userId))
                if (result.isSuccess) {
                    _event.emit(Event.DeleteFollowSuccess)
                } else {
                    handleError(result.exceptionOrNull() ?: UnKnownException())
                    Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                }
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
        ) : Event()
        object CreateFollowSuccess : Event()
        object DeleteFollowSuccess : Event()
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
