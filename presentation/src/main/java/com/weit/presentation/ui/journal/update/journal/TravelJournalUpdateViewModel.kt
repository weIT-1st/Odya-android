package com.weit.presentation.ui.journal.update.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.follow.FollowUserContent
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.journal.TravelJournalUpdateCompanionsInfo
import com.weit.presentation.model.journal.TravelJournalUpdateDTO
import com.weit.presentation.model.post.travellog.FollowUserContentDTO
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.model.post.travellog.toDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.min

class TravelJournalUpdateViewModel @AssistedInject constructor(
    @Assisted val travelJournalUpdateDTO: TravelJournalUpdateDTO,
    private val getTravelJournalUseCase: GetTravelJournalUseCase
) : ViewModel() {
    @AssistedFactory
    interface TravelJournalUpdateFactory {
        fun create(travelJournalUpdateDTO: TravelJournalUpdateDTO): TravelJournalUpdateViewModel
    }

    val title = MutableStateFlow("")

    private val _travelPeriod = MutableStateFlow(TravelPeriod())
    val travelPeriod: StateFlow<TravelPeriod> get() = _travelPeriod

    private val friends = CopyOnWriteArrayList<FollowUserContent>()
    private val _travelFriendsInfo = MutableStateFlow(TravelFriendsInfo())
    val travelFriendsInfo: StateFlow<TravelFriendsInfo> get() = _travelFriendsInfo

    private val _visibility = MutableStateFlow(Visibility.PUBLIC)
    val visibility: StateFlow<Visibility> get() = _visibility

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
            val friendsDTO = friends.map { it.toDTO() }
            _event.emit(Event.OnEditTravelFriends(friendsDTO))
        }
    }

    fun showDatePicker() {
        viewModelScope.launch {
            _event.emit(Event.ShowDataPicker(travelPeriod.value))
        }
    }

    fun onChangePeriod(period: TravelPeriod) {
        viewModelScope.launch {
            _travelPeriod.emit(period)
        }
    }

    fun onDatePickerDismissed() {
        viewModelScope.launch {
            _event.emit(Event.ClearDatePickerDialog)
        }
    }

    private fun initTravelFriend(travelFriends: List<FollowUserContentDTO>) {
        friends.run {
            clear()
            addAll(travelFriends)
        }

        val friendsSummary = if (travelFriends.size >= DEFAULT_FRIENDS_SUMMARY_COUNT) {
            travelFriends
                .slice(0 until min(DEFAULT_FRIENDS_SUMMARY_COUNT, travelFriends.size))
                .map { it.profile }
        } else {
            travelFriends
                .map { it.profile }
        }

        val remainingFriendsCount = travelFriends.size - friendsSummary.size

        viewModelScope.launch {
            _travelFriendsInfo.emit(TravelFriendsInfo(friendsSummary, remainingFriendsCount))
        }
    }

    fun selectTravelLogVisibility(selectedVisibility: Visibility) {
        viewModelScope.launch {
            _visibility.emit(selectedVisibility)
        }
    }

    fun updateTravelJournal() {
        viewModelScope.launch {
            val newTitle = title.value
            val newPeriod = travelPeriod.value

            if (newTitle.isBlank()) {
                _event.emit(Event.IsBlankTitle)
                return@launch
            }
        }
    }

    sealed class Event {
        data class OnEditTravelFriends(
            val travelFriends: List<FollowUserContentDTO>
        ) : Event()

        data class ShowDataPicker(
            val currentPeriod: TravelPeriod
        ) : Event()
        object ClearDatePickerDialog : Event()
        object IsBlankTitle : Event()
    }

    data class TravelFriendsInfo(
        val friendsSummary: List<UserProfile> = emptyList(),
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
