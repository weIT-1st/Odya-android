package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.community.comment.CommentDeleteInfo
import com.weit.domain.model.community.comment.CommentInfo
import com.weit.domain.model.community.comment.CommentRegistrationInfo
import com.weit.domain.model.community.comment.CommentUpdateInfo
import com.weit.domain.model.exception.InvalidPermissionException
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.model.exception.follow.ExistedFollowingIdException
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.community.DeleteCommunityUseCase
import com.weit.domain.usecase.community.GetDetailCommunityUseCase
import com.weit.domain.usecase.community.comment.DeleteCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommentsUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class FeedDetailViewModel @AssistedInject constructor(
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val registerCommentsUseCase: RegisterCommentsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val deleteCommentsUseCase: DeleteCommentsUseCase,
    private val updateCommentsUseCase: UpdateCommentsUseCase,
    private val deleteCommunityUseCase: DeleteCommunityUseCase,
    private val getDetailCommunityUseCase: GetDetailCommunityUseCase,
    @Assisted private val feedId: Long,
) : ViewModel() {

    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedId: Long): FeedDetailViewModel
    }

    var writedComment = MutableStateFlow("")

    private val _feed = MutableStateFlow<CommunityContent?>(null)
    val feed: StateFlow<CommunityContent?> get() = _feed

    private val _likeNum = MutableStateFlow<Int>(0)
    val likeNum: StateFlow<Int> get() = _likeNum

    private val _commentNum = MutableStateFlow<Int>(0)
    val commentNum: StateFlow<Int> get() = _commentNum

    private val _followState = MutableStateFlow<Boolean>(false)
    val followState: StateFlow<Boolean> get() = _followState

    private val _createdDate = MutableStateFlow<String>("")
    val creadtedDate: StateFlow<String> get() = _createdDate

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var userId: Long = 0

    private var commentState = commentRegister

    private var currentPosition : Int = 0

    var commentList =  CopyOnWriteArrayList<CommentContent>()


    private var job: Job = Job().apply { complete() }

    init {
        getFeed()
        getFeedDetailComments(feedId)
    }

    private fun getFeed() {
        viewModelScope.launch {
            val result = getDetailCommunityUseCase(feedId)
            if (result.isSuccess) {
                val feed = result.getOrThrow()
                Logger.t("MainTest").i("${feed}")

//                userId = feed.
                setFeedDetail(feed)
                _event.emit(Event.OnChangeFeed(feed, listOf(feed.topic)))
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun setFeedDetail(feed: CommunityContent) {
        _feed.value = feed
        _likeNum.value = feed.communityLikeCount
        _commentNum.value = feed.communityCommentCount
        _followState.value = feed.writer?.isFollowing ?: true
        //TODO 피드 삭제, CREATEDDATE, WRITER NON-NULL CHANGE

        //LocalDateTimeConverter를 여기서 쓸 수가 있는건가?
        //_createdDate.value = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(feed.createdDate)
    }


    private fun getFeedDetailComments(feedId: Long) {
        viewModelScope.launch {
            val result = getCommentsUseCase(
                CommentInfo(feedId)
            )
            if (result.isSuccess) {
                commentList.clear()
                val comments = result.getOrThrow()
                commentList.addAll(comments)
                val commentCount = minOf(comments.size, DEFAULT_COMMENT_COUNT)
                val defaultComments = comments
                    .slice(0 until commentCount)

                var remainingCommentsCount = 0
                if(_feed.value?.communityCommentCount != null){
                    remainingCommentsCount = _feed.value?.communityCommentCount?.minus(
                        defaultComments.size
                    ) as Int
                }else{
                    remainingCommentsCount = -1
                }
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
                   val result = registerCommentsUseCase(
                       CommentRegistrationInfo(
                           feedId, writedComment.value
                       )
                   )
                   if (result.isSuccess) {
                       writedComment.emit("")
                       getFeedDetailComments(feedId)

                   } else {
                       Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
                   }
               }
               commentUpdate ->{
                   commentState = commentRegister
                   val commentId = commentList[currentPosition].communityCommentId
                   val result = updateCommentsUseCase(
                           CommentUpdateInfo(
                               feedId, commentId, writedComment.value
                           )
                   )
                   if (result.isSuccess) {
                       getFeedDetailComments(feedId)
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
            val result = deleteCommentsUseCase(
                CommentDeleteInfo(
                    feedId, commentId
                )
            )
            if (result.isSuccess) {
                getFeedDetailComments(feedId)
            } else {
                //TODO 에러
            }
        }
    }

    fun deleteFeed(){
        viewModelScope.launch {
            val result = deleteCommunityUseCase(feedId)
            if (result.isSuccess) {
                _event.emit(Event.DeleteCommunitySuccess)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
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
            val feed: CommunityContent,
            val topics: List<TopicDetail>,
        ) : Event()
        data class OnChangeComments(
            val feed: CommunityContent?,
            val defaultComments: List<CommentContent>,
            val remainingCommentsCount: Int,
        ) : Event()
        data class OnChangeFollowState(
            val followState: Boolean
        ) : Event()
        object DeleteCommunitySuccess : Event()

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
        fun provideFactory(
            assistedFactory: FeedDetailViewModel.FeedDetailFactory,
            feedId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(feedId) as T
            }
        }

    }
}
