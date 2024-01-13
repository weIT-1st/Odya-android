package com.weit.presentation.ui.profile.reptraveljournal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.community.CommunityContent
import com.weit.domain.model.community.CommunityRegistrationInfo
import com.weit.domain.model.community.CommunityUpdateInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalVisibilityInfo
import com.weit.domain.usecase.community.GetDetailCommunityUseCase
import com.weit.domain.usecase.community.RegisterCommunityUseCase
import com.weit.domain.usecase.community.UpdateCommunityUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.journal.UpdateTravelJournalVisibilityUseCase
import com.weit.domain.usecase.repjournal.CreateRepTravelJournalUseCase
import com.weit.domain.usecase.topic.GetTopicListUseCase
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotPlaceDTO
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.feed.post.FeedSelectPlaceViewModel
import com.weit.presentation.ui.feed.post.traveljournal.FeedTravelJournalEntity
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerViewModel
import com.weit.presentation.ui.util.Constants.DEFAULT_REACTION_COUNT
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class RepTravelJournalViewModel @Inject constructor(
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase,
    private val updateTravelJournalVisibilityUseCase: UpdateTravelJournalVisibilityUseCase,
    private val createRepTravelJournalUseCase : CreateRepTravelJournalUseCase
) : ViewModel() {

    private val _event = MutableEventFlow<RepTravelJournalViewModel.Event>()
    val event = _event.asEventFlow()

    private val _journals = MutableStateFlow<List<FeedTravelJournalEntity?>>(emptyList())
    val journals: StateFlow<List<FeedTravelJournalEntity?>> get() = _journals

    private var getJob: Job = Job().apply {
        complete()
    }
    private var journalLastId: Long? = null

    private var selectedTravelJournal : TravelJournalListInfo? = null

    init {
        onNextMyTravelJournals()
    }

    fun onNextMyTravelJournals() {
        if (getJob.isCompleted.not()) {
            return
        }
        getMyTravelJournals()
    }

    private fun getMyTravelJournals() {
        getJob = viewModelScope.launch {
            val result = getMyTravelJournalListUseCase(
                DEFAULT_REACTION_COUNT, journalLastId, null
            )
            if (result.isSuccess) {
                val newJournals = result.getOrThrow().map {
                    FeedTravelJournalEntity(it, false)
                }
                newJournals.lastOrNull()?.let {
                    journalLastId = it.travelJournal.travelJournalId
                    _journals.emit(journals.value + newJournals)
                }
            } else {
                // TODO 에러 처리
            }
        }
    }


    fun updateTravelJournalVisibility(selectedJournal: FeedTravelJournalEntity) {
        viewModelScope.launch {
            val result = updateTravelJournalVisibilityUseCase(
                TravelJournalVisibilityInfo(
                    selectedJournal.travelJournal.travelJournalId,
                    Visibility.PUBLIC.name
                )
            )
            if (result.isSuccess) {
                val newJournals = _journals.value.map { journal ->
                    if(selectedJournal.travelJournal.travelJournalId == journal?.travelJournal?.travelJournalId){
                        journal.copy(travelJournal = journal.travelJournal.copy(visibility = "PUBLIC"))
                    }else{
                        journal
                    }
                }
                _journals.emit(newJournals)
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun onClickPublicJournal(selectedJournal: FeedTravelJournalEntity) {
        viewModelScope.launch {
            selectedTravelJournal = selectedJournal.travelJournal
            val newJournals = _journals.value.map { journal ->
                journal?.copy(isSelected = journal.travelJournal.travelJournalId == selectedJournal.travelJournal.travelJournalId)
            }
            _journals.emit(newJournals)
            _event.emit(Event.OnClickPublicJournalSuccess)
        }
    }

    fun onCreateRepTravelJournal(){
        viewModelScope.launch {
            val result = createRepTravelJournalUseCase(
                selectedTravelJournal?.travelJournalId ?:0
            )
            if (result.isSuccess) {
                _event.emit(Event.OnCompleted)
            } else {
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }


    sealed class Event {
        object OnClickPublicJournalSuccess : Event()

        object OnCompleted : Event()
    }
}
