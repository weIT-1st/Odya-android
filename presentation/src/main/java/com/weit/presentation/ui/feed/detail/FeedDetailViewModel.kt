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
import com.weit.domain.model.user.User
import com.weit.domain.usecase.community.ChangeLikeStateUseCase
import com.weit.domain.usecase.community.GetDetailCommunityUseCase
import com.weit.domain.usecase.community.comment.DeleteCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommentsUseCase
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.user.GetUserUseCase
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
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase,
    private val registerCommentsUseCase: RegisterCommentsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val deleteCommentsUseCase: DeleteCommentsUseCase,
    private val updateCommentsUseCase: UpdateCommentsUseCase,
    private val getDetailCommunityUseCase: GetDetailCommunityUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val changeLikeStateUseCase: ChangeLikeStateUseCase,
    @Assisted private val feedId: Long=0,
    ) : ViewModel() {
    private var communityId: Long = 0

    fun initialize(savedFeedId : Long?) {
        communityId = if(savedFeedId == null){
            feedId
        }else{
            savedFeedId.toLong()
        }
        Logger.t("MainTest").i("communityId $communityId")

        getFeed()
        getFeedDetailComments(communityId)
    }
    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedId: Long): FeedDetailViewModel
    }


    val user = MutableStateFlow<User?>(null)
    private val _isWriter = MutableStateFlow<Boolean>(false)
    val isWriter: StateFlow<Boolean> get() = _isWriter


    var writedComment = MutableStateFlow("")

    private val _feed = MutableStateFlow<CommunityContent?>(null)
    val feed: StateFlow<CommunityContent?> get() = _feed

    private val _likeNum = MutableStateFlow<Int>(0)
    val likeNum: StateFlow<Int> get() = _likeNum

    private val _commentNum = MutableStateFlow<Int>(0)
    val commentNum: StateFlow<Int> get() = _commentNum

    private val _placeName = MutableStateFlow<String>("0")
    val placeName: StateFlow<String> get() = _placeName

    private val _followState = MutableStateFlow<Boolean>(false)
    val followState: StateFlow<Boolean> get() = _followState

    private val _createdDate = MutableStateFlow<String>("")
    val creadtedDate: StateFlow<String> get() = _createdDate

    private val _likeState = MutableStateFlow<Boolean>(false)
    val likeState: StateFlow<Boolean> get() = _likeState

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    private var userId: Long = -1

    private var commentState = commentRegister

    private var currentPosition : Int = 0

    var commentList =  CopyOnWriteArrayList<CommentContent>()


    private var registerAndUpdateJob: Job = Job().apply { complete() }

    init {
        viewModelScope.launch {
            getUserUseCase().onSuccess {
                user.value = it
            }
        }

    }

    private fun setPlaceName(placeId: String?){
        viewModelScope.launch {
            if(placeId.isNullOrEmpty().not()){
                val placeInfo = getPlaceDetailUseCase(placeId.toString())
                if (placeInfo.name.isNullOrBlank().not()) {
                    _placeName.emit(placeInfo.name.toString())
                }
            }
        }
    }

    private fun getFeed() {
        viewModelScope.launch {
            val result = getDetailCommunityUseCase(communityId)
            if (result.isSuccess) {
                val feed = result.getOrThrow()
                Logger.t("MainTest").i("${feed}")

                userId = feed.writer.userId
                setPlaceName(feed.placeId)
                setFeedDetail(feed)
                _isWriter.emit(feed.isWriter)
                val uris = feed.communityContentImages.map{
                    it.imageUrl
                }
                _event.emit(Event.OnChangeFeed(feed,uris))
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
                _event.emit(Event.OnChangeComments(defaultComments))
            } else {
                //TODO 에러
            }
        }
    }

    fun registerAndUpdateComment() {
        registerAndUpdateJob =  viewModelScope.launch {
           when(commentState){
               commentRegister ->{
                   val result = registerCommentsUseCase(
                       CommentRegistrationInfo(
                           communityId, writedComment.value
                       )
                   )
                   if (result.isSuccess) {
                       writedComment.emit("")
                       getFeedDetailComments(communityId)

                   } else {
                        //TODO 에러
                   }
               }
               commentUpdate ->{
                   commentState = commentRegister
                   val commentId = commentList[currentPosition].communityCommentId
                   val result = updateCommentsUseCase(
                           CommentUpdateInfo(
                               communityId, commentId, writedComment.value
                           )
                   )
                   if (result.isSuccess) {
                       getFeedDetailComments(communityId)
                   } else {
                       //TODO 에러
                   }

               }
           }
        }
    }

    fun updateComment(position: Int){
        viewModelScope.launch {
            writedComment.emit(commentList[position].content)
        }

        commentState = commentUpdate
        if (registerAndUpdateJob.isCompleted) {
            currentPosition = position
        }
    }

    fun deleteComment(position : Int) {
        viewModelScope.launch {
            val commentId = commentList[position].communityCommentId
            val result = deleteCommentsUseCase(
                CommentDeleteInfo(
                    communityId, commentId
                )
            )
            if (result.isSuccess) {
                getFeedDetailComments(communityId)
            } else {
                //TODO 에러
            }
        }
    }

    fun onFollowStateChange() {
        viewModelScope.launch {
            val currentFollowState = _feed.value?.writer?.isFollowing ?: return@launch
            val result = changeFollowStateUseCase(userId, !currentFollowState)
            if (result.isSuccess) {
                getFeed()
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
            }
        }
    }

    fun onLikeStateChange() {
        viewModelScope.launch {
            val currentLikeState = _feed.value?.isUserLiked ?: return@launch
            val result = changeLikeStateUseCase(communityId, !currentLikeState)
            if (result.isSuccess) {
                getFeed()
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
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
            val feedImages: List<String>,
        ) : Event()
        data class OnChangeComments(
            val defaultComments: List<CommentContent>,
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
            assistedFactory: FeedDetailFactory,
            feedId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(feedId) as T
            }
        }
    }
}
