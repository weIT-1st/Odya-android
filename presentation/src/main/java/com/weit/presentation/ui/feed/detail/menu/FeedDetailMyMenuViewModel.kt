package com.weit.presentation.ui.feed.detail.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.usecase.community.DeleteCommunityUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch


class FeedDetailMyMenuViewModel @AssistedInject constructor(
    private val deleteCommunityUseCase: DeleteCommunityUseCase,
    @Assisted private val feedId: Long,
) : ViewModel() {
    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedId: Long): FeedDetailMyMenuViewModel
    }
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    fun deleteFeed(){
        viewModelScope.launch {
            val result = deleteCommunityUseCase(feedId)
            if (result.isSuccess) {
                _event.emit(Event.FeedDeleteSuccess)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }
    sealed class Event {
        object FeedDeleteSuccess : Event()
        object UnknownException : Event()
    }
    companion object {
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
