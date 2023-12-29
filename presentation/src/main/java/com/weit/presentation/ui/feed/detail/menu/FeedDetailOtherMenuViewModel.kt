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


class FeedDetailOtherMenuViewModel @AssistedInject constructor(
    @Assisted private val feedId: Long,
) : ViewModel() {
    @AssistedFactory
    interface FeedDetailFactory {
        fun create(feedId: Long): FeedDetailOtherMenuViewModel
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
