package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo
import com.weit.domain.usecase.community.comment.DeleteCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommunityCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommunityCommentsUseCase
import com.weit.presentation.model.FeedDetail
import com.weit.presentation.ui.placereview.EditPlaceReviewViewModel
import com.weit.presentation.util.PlaceReviewContentData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class CommentDialogViewModel @AssistedInject constructor(
    private val registerCommunityCommentsUseCase: RegisterCommunityCommentsUseCase,
    private val getCommunityCommentsUseCase: GetCommunityCommentsUseCase,
    private val deleteCommunityCommentsUseCase: DeleteCommunityCommentsUseCase,
    private val updateCommunityCommentsUseCase: UpdateCommunityCommentsUseCase,
    @Assisted private val feedDetail: FeedDetail?,
) : ViewModel() {
    var feedId : Long = 0
    var _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    val writedComment = MutableStateFlow("")
    private var commentState = commentRegister

    private val _comments = MutableStateFlow<List<CommunityCommentContent>>(emptyList())
    val comments: StateFlow<List<CommunityCommentContent>> get() = _comments
    var commentList =  CopyOnWriteArrayList<CommunityCommentContent>()

    private var lastId :Long? = null
    private var currentPosition : Int = 0

    private var commentJob: Job = Job().apply {
        complete()
    }

    private var job: Job = Job().apply { cancel() }

    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedDetailFactory: FeedDetail?): CommentDialogViewModel
    }

    init{
        viewModelScope.launch {
            if (feedDetail != null) {
                feedId = feedDetail.feedId
                _feed.value = feedDetail
            }
        }
        onNextComments()
    }

    fun onNextComments() {
        if (commentJob.isCompleted.not()) {
            return
        }
        loadNextComments()
    }

    private fun loadNextComments() {
        commentJob = viewModelScope.launch {
            val result = getCommunityCommentsUseCase(
                CommunityCommentInfo(feedId,DEFAULT_PAGE_SIZE,lastId))
            if (result.isSuccess) {
                val newComments = result.getOrThrow()
                lastId = newComments[newComments.lastIndex].communityCommentId
                if (newComments.isEmpty()) {
                    loadNextComments()
                }
                commentList.clear()
                commentList.addAll(comments.value + newComments)
                _comments.emit(comments.value + newComments)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun registerAndUpdateComment() {
        job =  viewModelScope.launch {
            when(commentState){
                CommentDialogViewModel.commentRegister ->{
                    val result = registerCommunityCommentsUseCase(
                        CommunityCommentRegistrationInfo(
                            feedId, writedComment.value
                        )
                    )
                    if (result.isSuccess) {
                        lastId = null
                        _comments.value = emptyList()
                        writedComment.value = "" //이게 왜 변경이 안되는가
                        loadNextComments()
                    } else {
                        Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")

                        //TODO 에러
                    }
                }
                CommentDialogViewModel.commentUpdate ->{
                    commentState = CommentDialogViewModel.commentRegister
                    val commentId = commentList[currentPosition].communityCommentId
                    val result = updateCommunityCommentsUseCase(
                        CommunityCommentUpdateInfo(
                            feedId, commentId, writedComment.value
                        )
                    )
                    if (result.isSuccess) {
                        lastId = null
                        _comments.value = emptyList()
                        loadNextComments()
                    } else {
                        //TODO 에러
                    }

                }
            }
        }
    }

    fun updateComment(position: Int){
        commentState = CommentDialogViewModel.commentUpdate
        if (job.isCompleted) {
            currentPosition = position
        }
    }

    fun deleteComment(position : Int) {
        viewModelScope.launch {
            val commentId = commentList[position].communityCommentId
            Logger.t("MainTest").i("${commentId}")

            val result = deleteCommunityCommentsUseCase(
                CommunityCommentDeleteInfo(
                    feedId, commentId
                )
            )
            if (result.isSuccess) {
                lastId = null
                _comments.value = emptyList()
                loadNextComments()
            } else {
                //TODO 에러
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")

            }
        }
    }
    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        const val commentRegister = "register"
        const val commentUpdate = "update"

        fun provideFactory(
            assistedFactory: CommentDialogViewModel.FeedDetailFactory,
            feedDetail: FeedDetail?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(feedDetail) as T
            }
        }
    }
}
