package com.weit.presentation.ui.feed.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.comment.CommentContent
import com.weit.domain.model.community.comment.CommentDeleteInfo
import com.weit.domain.model.community.comment.CommentInfo
import com.weit.domain.model.community.comment.CommentRegistrationInfo
import com.weit.domain.model.community.comment.CommentUpdateInfo
import com.weit.domain.usecase.community.comment.DeleteCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommentsUseCase
import com.weit.presentation.model.FeedDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class CommentDialogViewModel @AssistedInject constructor(
    private val registerCommentsUseCase: RegisterCommentsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val deleteCommentsUseCase: DeleteCommentsUseCase,
    private val updateCommentsUseCase: UpdateCommentsUseCase,
    @Assisted private val feedDetail: FeedDetail,
) : ViewModel() {
    var feedId : Long = 0
    var _feed = MutableStateFlow<FeedDetail?>(null)
    val feed: StateFlow<FeedDetail?> get() = _feed

    val writedComment = MutableStateFlow("")
    private var commentState = commentRegister

    private val _comments = MutableStateFlow<List<CommentContent>>(emptyList())
    val comments: StateFlow<List<CommentContent>> get() = _comments
    private val commentList =  CopyOnWriteArrayList<CommentContent>()

    private var lastId :Long? = null
    private var currentPosition : Int = 0

    private val _changedComment = MutableStateFlow<String>("")
    val changedComment: StateFlow<String> get() = _changedComment

    private var commentJob: Job = Job().apply {
        complete()
    }

    private var job: Job = Job().apply { complete() }

    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedDetailFactory: FeedDetail): CommentDialogViewModel
    }

    init{
        viewModelScope.launch {
            feedId = feedDetail.feedId
            _feed.value = feedDetail
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
            val result = getCommentsUseCase(
                CommentInfo(feedId,DEFAULT_PAGE_SIZE,lastId))
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
                    val result = registerCommentsUseCase(
                        CommentRegistrationInfo(
                            feedId, writedComment.value
                        )
                    )
                    if (result.isSuccess) {
                        beforeGetComments()
                        writedComment.value = ""
                    } else {
                        Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")

                        //TODO 에러
                    }
                }
                CommentDialogViewModel.commentUpdate ->{
                    commentState = CommentDialogViewModel.commentRegister
                    val commentId = commentList[currentPosition].communityCommentId
                    val result = updateCommentsUseCase(
                        CommentUpdateInfo(
                            feedId, commentId, writedComment.value
                        )
                    )
                    if (result.isSuccess) {
                        beforeGetComments()
                    } else {
                        //TODO 에러
                    }

                }
            }
        }
    }

    private fun beforeGetComments(){
        lastId = null
        _comments.value = emptyList()
        commentJob.cancel()
        loadNextComments()
    }

    fun updateComment(position: Int){
        commentState = CommentDialogViewModel.commentUpdate
        viewModelScope.launch {
            _changedComment.emit(commentList[position].content)
        }
        if (job.isCompleted) {
            currentPosition = position
        }
    }

    fun deleteComment(position : Int) {
        viewModelScope.launch {
            val commentId = commentList[position].communityCommentId
            Logger.t("MainTest").i("${commentId}")

            val result = deleteCommentsUseCase(
                CommentDeleteInfo(
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
            feedDetail: FeedDetail,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(feedDetail) as T
            }
        }
    }
}
