package com.weit.presentation.ui.journal.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TravelJournalDetailViewModel @AssistedInject constructor(
    @Assisted val travelJournalInfo: TravelJournalInfo
)
: ViewModel() {
    @AssistedFactory
    interface TravelJournalInfoFactory{
        fun create(travelJournalInfo: TravelJournalInfo): TravelJournalDetailViewModel
    }

    fun handleFriendsCount(): List<TravelJournalCompanionsInfo>{
        val friendCount = travelJournalInfo.travelJournalCompanions.size
        return if (friendCount < MAX_ABLE_SHOW_FRIENDS_NUM) {
            travelJournalInfo.travelJournalCompanions
        } else {
            travelJournalInfo.travelJournalCompanions.slice(0 until  MAX_ABLE_SHOW_FRIENDS_NUM)
        }
    }

    companion object {
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
        fun provideFactory(
            assistedFactory: TravelJournalInfoFactory,
            travelJournalInfo: TravelJournalInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalInfo) as T
            }
        }
    }
}
