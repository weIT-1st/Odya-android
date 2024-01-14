package com.weit.presentation.ui.journal.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class TravelJournalFriendsViewModel @AssistedInject constructor(
    @Assisted val friends: List<TravelJournalCompanionsInfo>
): ViewModel(){

    @AssistedFactory
    interface TravelJournalFriendsFactory {
        fun create(friends: List<TravelJournalCompanionsInfo>) : TravelJournalFriendsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalFriendsFactory,
            friends: List<TravelJournalCompanionsInfo>
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(friends) as T
            }
        }
    }
}
