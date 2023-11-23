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
import com.weit.domain.model.user.User
import com.weit.domain.usecase.community.comment.DeleteCommentsUseCase
import com.weit.domain.usecase.community.comment.GetCommentsUseCase
import com.weit.domain.usecase.community.comment.RegisterCommentsUseCase
import com.weit.domain.usecase.community.comment.UpdateCommentsUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentDialogViewModel @AssistedInject constructor(
    private val registerCommentsUseCase: RegisterCommentsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val deleteCommentsUseCase: DeleteCommentsUseCase,
    private val updateCommentsUseCase: UpdateCommentsUseCase,
    private val getUserUseCase: GetUserUseCase,
    @Assisted private val feedId: Long,
) : ViewModel() {
    val user = MutableStateFlow<User?>(null)

    val writedComment = MutableStateFlow("")
    private var commentState = commentRegister

    private val _comments = MutableStateFlow<List<CommentContent>>(emptyList())
    val comments: StateFlow<List<CommentContent>> get() = _comments

    private var lastId :Long? = null
    private var currentPosition : Int = 0

    private var commentJob: Job = Job().apply {
        complete()
    }

    private var registerAndUpdateJob: Job = Job().apply { complete() }

    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedId: Long): CommentDialogViewModel
    }

    init{
        viewModelScope.launch {
            getUserUseCase().onSuccess {
                user.value = it
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
            val result = getCommentsUseCase(
                CommentInfo(feedId,DEFAULT_PAGE_SIZE,lastId))
            if (result.isSuccess) {
                val newComments = result.getOrThrow()
                lastId = newComments.last().communityCommentId
                if (newComments.isEmpty()) {
                    loadNextComments()
                }
                _comments.emit(comments.value + newComments)
            } else {
                // TODO 에러 처리
            }
        }
    }

    fun registerAndUpdateComment() {
        registerAndUpdateJob =  viewModelScope.launch {
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
                    //TODO 에러
                    }
                }
                CommentDialogViewModel.commentUpdate ->{
                    commentState = CommentDialogViewModel.commentRegister
                    val commentId = comments.value[currentPosition].communityCommentId
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
        commentJob.cancel()
        lastId = null
        loadNextComments()
    }

    fun updateComment(position: Int){
        commentState = CommentDialogViewModel.commentUpdate
        viewModelScope.launch {
            writedComment.value = comments.value[position].content
        }
        if (registerAndUpdateJob.isCompleted) {
            currentPosition = position
        }
    }

    fun deleteComment(position : Int) {
        viewModelScope.launch {
            val commentId = comments.value[position].communityCommentId
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
            feedId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(feedId) as T
            }
        }
    }
}
