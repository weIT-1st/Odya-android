package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.usecase.community.comment.DeleteCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommunityCommentsUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.model.TopicDTO
import com.weit.presentation.model.TravelLogInFeed
import com.weit.presentation.model.user.UserProfileColorDTO
import com.weit.presentation.model.user.UserProfileDTO
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
class FeedDetailViewModel @Inject constructor(
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val registerCommunityCommentsUseCase: RegisterCommunityCommentsUseCase,
    private val getCommunityCommentsUseCase: GetCommunityCommentsUseCase,
    private val deleteCommunityCommentsUseCase: DeleteCommunityCommentsUseCase,
    private val updateCommunityCommentsUseCase: UpdateCommunityCommentsUseCase,
) : ViewModel() {

    var feedId : Long = 0
    var writedComment = MutableStateFlow("")
    private val _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    private val _likeNum = MutableStateFlow<Int>(0)
    val likeNum: StateFlow<Int> get() = _likeNum

    private val _commentNum = MutableStateFlow<Int>(0)
    val commentNum: StateFlow<Int> get() = _commentNum

    private val _followState = MutableStateFlow<Boolean>(false)
    val followState: StateFlow<Boolean> get() = _followState

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var userId: Long = 0

    private var commentState = commentRegister

    private var currentPosition : Int = 0

    var commentList =  CopyOnWriteArrayList<CommunityCommentContent>()


    private var job: Job = Job().apply { cancel() }

    init {
        getFeed()
        getFeedDetailComments()
    }

    private fun getFeed() {
        // TODO feedId로 상세 정보 가져오기
        viewModelScope.launch {

            val profile = UserProfileDTO("testProfileUrl", UserProfileColorDTO("#ffd42c", 255, 212, 44))
            val travelLog = TravelLogInFeed(1, "ddd", "")
            val topics = listOf(TopicDTO(1, "바다여행"), TopicDTO(2, "우주여행"))
//            val comments = listOf<FeedComment>(
//                FeedComment(1, 3, profile, "dd", "wowwow"),
//                FeedComment(1, 1, profile, "dd", "wowwow"),
//                FeedComment(1, 1, profile, "dd", "wowwow"),
//            )

            val feed = FeedDetail(2, 5, profile, "dd", true, "dd", null, "Dd", "dd", 100, 100, "dd", topics)
            userId = feed.userId

            setFeedDetail(feed)
            _event.emit(Event.OnChangeFeed(feed, feed.topics))
        }
    }

    private fun setFeedDetail(feed: FeedDetail) {
        _feed.value = feed
        _likeNum.value = feed.likeNum
        _commentNum.value = feed.commentNum
        _followState.value = feed.followState
    }


    private fun getFeedDetailComments() {
        viewModelScope.launch {
            val result = getCommunityCommentsUseCase(
                CommunityCommentInfo(2,null,null) //스크롤로 가져오는거면 댓글 총갯수는 어떻게 파악?
            )
            if (result.isSuccess) {
                 Logger.t("MainTest").i("${result.getOrThrow()}")

                commentList.clear()
                commentList = CopyOnWriteArrayList(result.getOrThrow())
                val comments = result.getOrThrow()
                val commentCount = minOf(comments.size, DEFAULT_COMMENT_COUNT)
                val defaultComments = comments
                    .slice(0 until commentCount)
                val remainingCommentsCount = comments.size - defaultComments.size
                _event.emit(Event.OnChangeComments(_feed.value,defaultComments, remainingCommentsCount))
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun registerAndUpdateComment() {
       job =  viewModelScope.launch {
           when(commentState){
               commentRegister ->{
                   Logger.t("MainTest").i(writedComment.value)

                   val result = registerCommunityCommentsUseCase(
                       CommunityCommentRegistrationInfo(
                           2, writedComment.value
                       )
                   )
                   if (result.isSuccess) {
                       writedComment.emit("")
                       getFeedDetailComments()

                   } else {
                       Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                   }
               }
               commentUpdate ->{
                   commentState = commentRegister
                   val commentId = commentList[currentPosition].communityCommentId
                   val result = updateCommunityCommentsUseCase(
                           CommunityCommentUpdateInfo(
                               2, commentId, writedComment.value
                           )
                   )
                   if (result.isSuccess) {
                       getFeedDetailComments()
                   } else {
                       //TODO 에러
                   }

               }
           }
        }
    }

    fun updateComment(position: Int){
        commentState = commentUpdate
        if (job.isCompleted) {
            currentPosition = position
        }
    }

    fun deleteComment(position : Int) {
        viewModelScope.launch {
            val commentId = commentList[position].communityCommentId
            val result = deleteCommunityCommentsUseCase(
                CommunityCommentDeleteInfo(
                    2, commentId
                )
            )
            if (result.isSuccess) {
                getFeedDetailComments()
            } else {
                //TODO 에러
            }
        }
    }

    fun deleteFeed(){

    }

    fun onFollowStateChange(followState : Boolean) {
        viewModelScope.launch {
            var changeState = !followState
            val result = changeFollowStateUseCase(userId, changeState)
            if (result.isSuccess) {
                _event.emit(Event.OnChangeFollowState(changeState))
            } else {
                changeState = !followState
                _event.emit(Event.OnChangeFollowState(changeState))
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
            val feed: FeedDetail,
            val topics: List<TopicDTO>,
        ) : Event()
        data class OnChangeComments(
            val feed: FeedDetail?,
            val defaultComments: List<CommunityCommentContent>,
            val remainingCommentsCount: Int,
        ) : Event()
        data class OnChangeFollowState(
            val followState: Boolean
        ) : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object ExistedFollowingIdException : Event()
        object UnknownException : Event()
    }
    companion object {
        private const val DEFAULT_COMMENT_COUNT = 2
        const val commentRegister = "register"
        const val commentUpdate = "update"
    }
}
