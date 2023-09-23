package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.comment.CommunityCommentContent
import com.weit.domain.model.community.comment.CommunityCommentDeleteInfo
import com.weit.domain.model.community.comment.CommunityCommentInfo
import com.weit.domain.model.community.comment.CommunityCommentRegistrationInfo
import com.weit.domain.model.community.comment.CommunityCommentUpdateInfo
import com.weit.domain.usecase.community.DeleteCommunityCommentsUseCase
import com.weit.domain.usecase.community.GetCommunityCommentsUseCase
import com.weit.domain.usecase.community.RegisterCommunityCommentsUseCase
import com.weit.domain.usecase.community.UpdateCommunityCommentsUseCase
import com.weit.presentation.model.FeedDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class CommentDialogViewModel @Inject constructor(
    private val registerCommunityCommentsUseCase: RegisterCommunityCommentsUseCase,
    private val getCommunityCommentsUseCase: GetCommunityCommentsUseCase,
    private val deleteCommunityCommentsUseCase: DeleteCommunityCommentsUseCase,
    private val updateCommunityCommentsUseCase: UpdateCommunityCommentsUseCase,
    ) : ViewModel() {
    var feedId : Long = 0
    var _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    val writedComment = MutableStateFlow("")
    private var commentState = commentRegister

    private val _comments = MutableStateFlow<List<CommunityCommentContent>>(emptyList())
    val comments: StateFlow<List<CommunityCommentContent>> get() = _comments
    var commentList =  CopyOnWriteArrayList<CommunityCommentContent>()

    private var commentPage = 0
    private var currentPosition : Int = 0

    private var pageJob: Job = Job().apply {
        complete()
    }

    private var job: Job = Job().apply { cancel() }

    init{
        loadNextComments(commentPage)
    }

    fun loadNextComments() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextComments(commentPage)
    }

    private fun loadNextComments(page: Int) {
        pageJob = viewModelScope.launch {
            val result = getCommunityCommentsUseCase(
                CommunityCommentInfo(feedId, DEFAULT_PAGE_SIZE))
            if (result.isSuccess) {
                val newComments = result.getOrThrow()
                commentPage = page + 1
                if (newComments.isEmpty()) {
                    loadNextComments(page + 1)
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
//            val feedId = feed.value?.feedId as Long
            when(commentState){
                CommentDialogViewModel.commentRegister ->{
                    val result = registerCommunityCommentsUseCase(
                        CommunityCommentRegistrationInfo(
                            feedId, writedComment.value
                        )
                    )
                    if (result.isSuccess) {
                        commentPage = 0
                        _comments.value = emptyList()
                        loadNextComments()

                    } else {
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
                        commentPage = 0
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
            val result = deleteCommunityCommentsUseCase(
                CommunityCommentDeleteInfo(
                    feedId, commentId
                )
            )
            if (result.isSuccess) {
                commentPage = 0
                _comments.value = emptyList()
                loadNextComments()
            } else {
                //TODO 에러
            }
        }
    }
    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        const val commentRegister = "register"
        const val commentUpdate = "update"
    }
}
