package com.weit.presentation.ui.login.input.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.follow.MayknowUserSearchInfo
import com.weit.domain.usecase.follow.ChangeFollowStateUseCase
import com.weit.domain.usecase.follow.GetMayknowUsersUseCase
import com.weit.presentation.ui.feed.FeedViewModel
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
class LoginFriendViewModel @Inject constructor(
    private val getMayknowUsersUseCase: GetMayknowUsersUseCase,
    private val changeFollowStateUseCase: ChangeFollowStateUseCase
): ViewModel() {

    private val _mayKnowFriends = MutableStateFlow<List<FollowUserContent>>(emptyList())
    val mayKnowFriends : StateFlow<List<FollowUserContent>> get() = _mayKnowFriends

    private val friends = CopyOnWriteArrayList<FollowUserContent>()

    private var pageJob: Job = Job().apply {
        complete()
    }
    private var friendLastId: Long? = null

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        onNextFriends()
    }

    fun onNextFriends() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextFriends()
    }

    private fun loadNextFriends() {
        pageJob = viewModelScope.launch {
            val result = getMayknowUsersUseCase(
                MayknowUserSearchInfo(
                    DEFAULT_PAGE_SIZE, friendLastId
                )
            )
            if (result.isSuccess) {
                val newFriends = result.getOrThrow()

                if (newFriends.isNotEmpty()) {
                    friendLastId = newFriends.last().userId
                }
                if (newFriends.isEmpty()) {
                    loadNextFriends()
                }
                val originalFriends = friends
                friends.clear()
                friends.addAll(originalFriends + newFriends)
                _mayKnowFriends.emit(friends)
            }

        }
    }

    fun createFollowState(userId: Long) {
        viewModelScope.launch {
            val user = mayKnowFriends.value.find { it.userId == userId}

            val result = changeFollowStateUseCase(userId, false)

            if (result.isSuccess) {
                _event.emit(Event.CreateFollowSuccess(user?.nickname ?: ""))
                onNextFriends()
            } else {
                Log.d("createFollow", "Create Follow Fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun moveToBack(){
        viewModelScope.launch {
            _event.emit(Event.MoveToBack)
        }
    }

    fun startOdya() {
        viewModelScope.launch{
            _event.emit(Event.StartOdya)
        }
    }

    sealed class Event {
        object MoveToBack : Event()
        object StartOdya : Event()
        data class CreateFollowSuccess(
            val nickname : String
        ) : Event()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
