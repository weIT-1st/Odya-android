package com.weit.presentation.ui.journal.update.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import com.weit.presentation.model.journal.TravelJournalUpdateDTO
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TravelJournalUpdateViewModel @AssistedInject constructor(
    @Assisted val travelJournalUpdateDTO: TravelJournalUpdateDTO,
    private val getTravelJournalUseCase: GetTravelJournalUseCase
) : ViewModel() {
    // todo 여행일지 공개 범위

    @AssistedFactory
    interface TravelJournalUpdateFactory {
        fun create(travelJournalUpdateDTO: TravelJournalUpdateDTO): TravelJournalUpdateViewModel
    }

    val title = MutableStateFlow("")

    private val _travelPeriod = MutableStateFlow(TravelPeriod())
    val travelPeriod: StateFlow<TravelPeriod> get() = _travelPeriod

    private val _travelFriendsInfo = MutableStateFlow(TravelFriendsInfo())
    val travelFriendsInfo: StateFlow<TravelFriendsInfo> get() = _travelFriendsInfo

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getTravelJournal()
    }

    private fun getTravelJournal() {
        viewModelScope.launch {
            title.emit(travelJournalUpdateDTO.title ?: "")

            _travelPeriod.emit(
                TravelPeriod(
                    travelJournalUpdateDTO.travelStartDate,
                    travelJournalUpdateDTO.travelEndDate
                )
            )

        }
    }

    fun onEditTravelFriends() {
        viewModelScope.launch {
            // todo
//            _event.emit(Event.OnEditTravelFriends())
        }
    }

    fun showDatePicker() {
        viewModelScope.launch {
//            _event.emit(Event.ShowDataPicker())
        }
    }

    sealed class Event {
        data class OnEditTravelFriends(
            val travelFriends: List<FollowUserContentDTO>
        ) : Event()

        data class ShowDataPicker(
            val currentPeriod: TravelPeriod
        ) : Event()
    }

    data class TravelFriendsInfo(
        val friendsSummary: List<TravelJournalCompanionsInfo> = emptyList(),
        val remainingFriendsCount: Int = 0,
    )

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalUpdateFactory,
            travelJournalUpdateDTO: TravelJournalUpdateDTO
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalUpdateDTO) as T
            }
        }

        private const val DEFAULT_FRIENDS_SUMMARY_COUNT = 3
    }
}
